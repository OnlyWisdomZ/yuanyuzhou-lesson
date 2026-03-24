package com.ming;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/** @author Ming */
@EnableScheduling
@MapperScan("com.ming.mapper")
@SpringBootApplication
@EnableDiscoveryClient
public class SaleApp {  
    public static void main(String[] args) {  
        SpringApplication.run(SaleApp.class,args);
    }  
}