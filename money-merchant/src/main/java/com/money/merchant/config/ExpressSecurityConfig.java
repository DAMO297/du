package com.money.merchant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ExpressSecurityConfig {

    @Bean(name = "expressFilterChain")
    @Order(1)
    public SecurityFilterChain expressFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .requestMatchers()
            .antMatchers("/merchant/express/**")
            .and()
            .authorizeRequests()
            .antMatchers("/merchant/express/**").permitAll()
            .and()
            .formLogin().disable()
            .httpBasic().disable();
        
        return http.build();
    }
} 