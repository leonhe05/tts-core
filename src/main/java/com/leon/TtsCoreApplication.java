package com.leon;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@MapperScan("com.leon.infrastructure.mapper")
public class TtsCoreApplication {

    public static void main(String[] args) {
        MDC.put("parentId", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        SpringApplication.run(TtsCoreApplication.class, args);
    }

} 