package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护。
                .csrf(csrf -> csrf.disable())

                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(
            "/", 
            "/favicon.ico",

            // 静态资源目录
            "/assets/**",

            // 根目录所有静态文件（html/js/css/json/png/jpg）
            "/*.html",
            "/*.js",
            "/*.css",
            "/*.json",
            "/*.png",
            "/*.jpg",
            "/*.svg",

            // API 如果要放行
            "/api/users/**",
            "/api/workspaces/**",
            "/api/invitations/**",
            "/api/tasks/**",
            "/api/files/**"
        ).permitAll()


                        // 对于任何其他未匹配的请求，都要求用户必须已经认证
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}