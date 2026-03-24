package com.ming;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @author Ming */
@MapperScan("com.ming.mapper")
@EnableDiscoveryClient
@SpringBootApplication
public class UserApp {  
    public static void main(String[] args) {  
        SpringApplication.run(UserApp.class, args);
    }  
}