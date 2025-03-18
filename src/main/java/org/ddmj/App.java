package org.ddmj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan(basePackages = {"org.ddmj.dao"})
@EnableScheduling
@EnableAsync
@ServletComponentScan
@EnableAspectJAutoProxy(exposeProxy = true)
public class App {

    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
    }
}
