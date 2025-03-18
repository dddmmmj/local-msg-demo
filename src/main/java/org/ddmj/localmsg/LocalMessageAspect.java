package org.ddmj.localmsg;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.ddmj.entity.LocalMessageDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.ddmj.localmsg.LocalMessageService.RETRY_INTERVAL_MINUTES;
import static org.ddmj.localmsg.LocalMessageUtil.offsetTimestamp;


@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocalMessageAspect {

    private static final Logger logger = LoggerFactory.getLogger(LocalMessageAspect.class);
    private final LocalMessageService localMessageService;

    @Around("@annotation(org.ddmj.localmsg.LocalMessage)")
    public Object doAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        LocalMessage localMessage = AnnotationUtils.findAnnotation(method, LocalMessage.class);
        if (Objects.isNull(localMessage)) {
            return joinPoint.proceed();
        }
        // 正在执行
        if (InvokeStatusHolder.inInvoke()) {
            return joinPoint.proceed();
        }
        boolean async = localMessage.async();
        // 方法参数
        List<String> params = Arrays.stream(method.getParameterTypes())
            .map(Class::getName)
            .collect(Collectors.toList());
        // 记录执行上下文
        InvokeCtx ctx = InvokeCtx.builder()
            .className(method.getDeclaringClass().getName())
            .methodName(method.getName())
            .paramTypes(JSON.toJSONString(params))
            .args(JSON.toJSONString(joinPoint.getArgs()))
            .build();
        LocalMessageDO localMessageDO = new LocalMessageDO(
            JSON.toJSONString(ctx),
            localMessage.maxRetryTimes(),
            offsetTimestamp(RETRY_INTERVAL_MINUTES)
        );
        logger.info("record local message, record: {}, async: {}", JSON.toJSONString(localMessageDO), async);
        localMessageService.invoke(localMessageDO, async);
        // 被切方法的逻辑交给 Task 去执行，这里返回 null
        return null;
    }
}
