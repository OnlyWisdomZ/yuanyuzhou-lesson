package com.ming.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author Ming */
@Configuration
public class SpringDocConfig {    

	private final String AUTHOR = "ming";
	private final String URL = "http://localhost:24106";
	private final String TITLE = "ml-search";
	private final String DESCRIPTION = "<em><em>MyLesson - 搜索微服务</em></em>";
	private final String VERSION = "v1.0.0";

    /** 通用信息Bean */  
    @Bean
    public OpenAPI commonInfo() {
		return new OpenAPI()
				.info(new Info()
				.title(TITLE)
				.description(DESCRIPTION)
				.version(VERSION)
				.contact(new Contact().name(AUTHOR).url(URL)));
    }    
}
