package org.ddmj.localmsg;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ddmj.dao.LocalMessageDAO;
import org.ddmj.entity.LocalMessageDO;
import org.ddmj.support.TransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



@Component
@RequiredArgsConstructor
public class LocalMessageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalMessageService.class);
    public static final int RETRY_INTERVAL_MINUTES = 2;
    private static final long RETRY_TASK_INTERVAL = 2L * 60 * 1000;
    private static final long INIT_DELAY_TIME = 5L * 1000;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> needRetryTaskStatus = Lists.newArrayList(
        TaskStatus.INIT.name(),
        TaskStatus.RETRY.name()
    );

    private final ApplicationContext applicationContext;

    private final LocalMessageDAO localMessageDAO;

    private final ExecutorService localMessageThreadPool;

    @Scheduled(initialDelay = INIT_DELAY_TIME, fixedRate = RETRY_TASK_INTERVAL)
    public void compensation() {
        logger.info("[local message]compensation start");
        loadWaitRetryRecords().forEach(this::doAsyncInvoke);
    }

    public void invoke(LocalMessageDO localMessageDO, boolean async) {
        save(localMessageDO);
        boolean inTx = TransactionSynchronizationManager.isActualTransactionActive();
        if (inTx) {
            // 解决分布式事务基于此即可
            // 事务中，延迟到事务提交后再执行
            TransactionUtils.doAfterTransaction(() -> execute(localMessageDO, async));
        } else {
            // 非事务中，立即执行
            execute(localMessageDO, async);
        }
    }

    private void execute(LocalMessageDO localMessageDO, boolean async) {
        // TODO pre check 做幂等
        if (async) {
            doAsyncInvoke(localMessageDO);
        } else {
            doInvoke(localMessageDO);
        }
    }

    public void doAsyncInvoke(LocalMessageDO localMessageDO) {
        localMessageThreadPool.execute(() -> doInvoke(localMessageDO));
    }

    public void doInvoke(LocalMessageDO localMessageDO) {
        String snapshot = localMessageDO.getReqSnapshot();
        if (StringUtils.isBlank(snapshot)) {
            logger.warn("Request snapshot is blank, recordId: {}", localMessageDO.getId());
            invokeFail(localMessageDO, "Request snapshot is blank");
            return;
        }

        InvokeCtx ctx = JSON.parseObject(snapshot, InvokeCtx.class);
        try {
            InvokeStatusHolder.startInvoke();

            Class<?> target = Class.forName(ctx.getClassName());
            Object bean = applicationContext.getBean(target);

            List<Class<?>> paramTypes = getParamTypes(JSON.parseArray(ctx.getParamTypes(), String.class));
            Method method = ReflectUtil.getMethod(target, ctx.getMethodName(), paramTypes.toArray(new Class[0]));
            Object[] args = getArgs(paramTypes, ctx.getArgs());

            method.invoke(bean, args);

            invokeSuccess(localMessageDO);
        } catch (ClassNotFoundException e) {
            logger.error("Class not found for invocation, className: {}, recordId: {}", ctx.getClassName(), localMessageDO.getId(), e);
            invokeFail(localMessageDO, e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("argument illegal for invocation, methodName: {}, recordId: {}", ctx.getMethodName(), localMessageDO.getId(), e);
            invokeFail(localMessageDO, e.getMessage());
        } catch (Throwable e) {
            logger.error("Invocation failed, recordId: {}, error: {}", localMessageDO.getId(), e.getMessage(), e);
            retry(localMessageDO, e.getMessage());
        } finally {
            InvokeStatusHolder.endInvoke();
        }
    }

    public void save(LocalMessageDO localMessageDO) {
        localMessageDAO.insert(localMessageDO);
    }

    private List<LocalMessageDO> loadWaitRetryRecords() {
        // 查询 RETRY_INTERVAL_MINUTES 分钟之前需要重试的任务
        // 加入 create_time 查询条件，避免刚入库的数据被查询出来
        return localMessageDAO.loadWaitRetryRecords(
            needRetryTaskStatus,
            System.currentTimeMillis(),
            RETRY_INTERVAL_MINUTES
            // 0 // for test
        );
    }

    private void retry(LocalMessageDO localMessageDO, String errorMsg) {
        Integer retryTimes = localMessageDO.getRetryTimes() + 1;
        LocalMessageDO updateDO = new LocalMessageDO();
        updateDO.setId(localMessageDO.getId());
        updateDO.setFailReason(errorMsg);
        updateDO.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes >= localMessageDO.getMaxRetryTimes()) {
            updateDO.setStatus(TaskStatus.FAIL.name());
        } else {
            updateDO.setRetryTimes(retryTimes);
            updateDO.setStatus(TaskStatus.RETRY.name());
        }
        localMessageDAO.updateById(updateDO);
    }

    public void invokeSuccess(LocalMessageDO localMessageDO) {
        localMessageDO.setStatus(TaskStatus.SUCCESS.name());
        localMessageDAO.updateById(localMessageDO);
    }

    public void invokeFail(LocalMessageDO localMessageDO, String errorMsg) {
        localMessageDO.setStatus(TaskStatus.FAIL.name());
        localMessageDO.setFailReason(errorMsg);
        localMessageDAO.updateById(localMessageDO);
    }

    private List<Class<?>> getParamTypes(List<String> params) {
        return params.stream()
            .map(name -> {
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    logger.warn("Parameter class not found: {}", name, e);
                    throw new IllegalArgumentException("Parameter class not found: " + name, e);
                }
            })
            .collect(Collectors.toList());
    }

    private Object[] getArgs(List<Class<?>> paramTypes, String argsJson) throws IOException {
        List<Object> args = JSON.parseArray(argsJson, Object.class);
        return IntStream.range(0, paramTypes.size())
            .mapToObj(i -> convertArg(paramTypes.get(i), args.get(i)))
            .toArray();
    }

    private Object convertArg(Class<?> targetType, Object arg) {
        if (targetType.isAssignableFrom(arg.getClass())) {
            return arg;
        }
        try {
            byte[] paramBytes = objectMapper.writeValueAsBytes(arg);
            return objectMapper.readValue(paramBytes, targetType);
        } catch (IOException e) {
            logger.error("Failed to convert argument: {}", arg, e);
            throw new IllegalArgumentException("Argument conversion failed", e);
        }
    }

    /**
     * 根据重试次数计算下一次重试的时间
     * 本方法通过重试次数计算出需要等待的分钟数，从而确定下一次重试的具体时间
     * 这是为了避免短时间内多次重试，通过增加等待时间来逐步减少重试频率
     *
     * @param retryTimes 重试次数，用于计算等待时间
     * @return 下一次重试的时间
     */
    private Long getNextRetryTime(Integer retryTimes) {
        // 计算等待分钟数，使用重试间隔的指数增长来增加等待时间
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);
        return offsetTimestamp((long) waitMinutes);
    }

    public static Long offsetTimestamp(long minutes) {
        return System.currentTimeMillis() + minutes * 60 * 1000;
    }
}
