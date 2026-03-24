package com.ming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @author Ming */
@EnableDiscoveryClient
@SpringBootApplication
public class SearchApp {  
    public static void main(String[] args) {  
        SpringApplication.run(SearchApp.class, args);
    }  
}
