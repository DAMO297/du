package com.money.merchant.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 商户模块Mybatis配置
 * 
 * @author dm
 */
@Configuration
@MapperScan("com.money.merchant.mapper")
public class MerchantMyBatisConfig {
    // 配置类仅用于扫描Mapper接口
} 