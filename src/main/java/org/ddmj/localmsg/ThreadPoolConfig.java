package org.ddmj.localmsg;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
public class ThreadPoolConfig {


    @Bean
    public ExecutorService localMessageThreadPool() {
        return new ThreadPoolExecutor(2, 4,
                1L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(200),
                new ThreadFactoryBuilder().setNameFormat("async-local-message-call-%s").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
