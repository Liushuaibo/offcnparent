package com.offcn.project.config;

import com.offcn.util.OssTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProjectConfig {

    @Bean
    public OssTemplate ossTemplate(){
        return new OssTemplate();
    }

}
