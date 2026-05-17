package com.example.aifomo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.aifomo.mapper")
public class AiFomoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiFomoApplication.class, args);
    }
}
