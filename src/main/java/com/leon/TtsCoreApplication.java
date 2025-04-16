package com.leon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Scan for MyBatis mappers in the infrastructure layer
@MapperScan("com.leon.infrastructure.persistence.mybatis.mapper") 
public class TtsCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsCoreApplication.class, args);
    }

} 