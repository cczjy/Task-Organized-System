package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径 (/**)
                .allowedOrigins("*")   // 允许所有来源。在生产环境中，应该替换为你的前端域名，例如 "http://yourfrontend.com"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许所有主流的HTTP方法
                .allowedHeaders("*")   // 允许所有请求头
                .allowCredentials(false); // 如果不需要 cookies, session 等，设为 false
    }
}