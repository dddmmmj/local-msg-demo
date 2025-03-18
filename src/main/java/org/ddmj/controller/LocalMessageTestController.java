package org.ddmj.controller;

import com.alibaba.fastjson.JSON;
import lombok.*;
import org.ddmj.localmsg.LocalMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@RestController
public class LocalMessageTestController {

    private static final Logger logger = LoggerFactory.getLogger(LocalMessageTestController.class);


    @GetMapping("/test")
    public void testMethod() {
        try {
            // NOTE: Spring AOP 限制
            LocalMessageTestController demo = (LocalMessageTestController) AopContext.currentProxy();
            HashMap<String, Object> map = new HashMap<>();
            map.put("kfc", UUID.randomUUID().toString());
            demo.testRetryMethod(map, new ModelData("yoyocraft", 18));
        } catch (Exception e) {
            logger.error("testRetryMethod error", e);
        }
    }

    @LocalMessage
    public void testRetryMethod(Map<String,Object> map, ModelData modelData) {
        //百分之50概率执行失败
        if (Math.random() > 0.5) {
            logger.error("testRetryMethod error");
            throw new RuntimeException("testRetryMethod error");
        }
        logger.info("testRetryMethod success map: {}, modelData: {}", JSON.toJSONString(map), JSON.toJSONString(modelData));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelData {
        private String name;
        private Integer age;
    }
}
