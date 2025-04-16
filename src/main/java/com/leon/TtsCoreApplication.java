package com.leon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leon.infrastructure.mapper")
public class TtsCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsCoreApplication.class, args);
    }

} 