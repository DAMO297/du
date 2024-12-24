package com.money.merchant.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "express")
@PropertySource("classpath:application.yml")
public class ExpressConfig {
    private static final Logger log = LoggerFactory.getLogger(ExpressConfig.class);

    /**
     * 阿里云AppCode
     */
    private String appCode;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
        log.debug("设置 appCode: {}", appCode);
    }
} 