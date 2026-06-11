package com.example.devlogjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.devlogjava.mapper")
public class DevLogJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevLogJavaApplication.class, args);
    }

}
