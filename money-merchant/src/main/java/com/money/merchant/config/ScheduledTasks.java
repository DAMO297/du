package com.money.merchant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.money.merchant.service.IGoodAlertService;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class ScheduledTasks {
    
    @Autowired
    private IGoodAlertService goodAlertService;
    
    /**
     * 每天凌晨1点执行未动销商品检查
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkUnsoldGoods() {
        goodAlertService.checkSalesAlert();
    }
} 