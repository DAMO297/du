package com.money.merchant.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.money.common.utils.DateUtils;
import com.money.merchant.domain.Good;
import com.money.merchant.domain.GoodAlertConfig;
import com.money.merchant.domain.GoodAlertRecord;
import com.money.merchant.mapper.GoodAlertConfigMapper;
import com.money.merchant.mapper.GoodAlertRecordMapper;
import com.money.merchant.mapper.GoodMapper;
import com.money.merchant.service.IGoodAlertService;

/**
 * 商品预警Service业务层处理
 */
@Service
public class GoodAlertServiceImpl implements IGoodAlertService {
    @Autowired
    private GoodAlertConfigMapper configMapper;

    @Autowired
    private GoodAlertRecordMapper recordMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Override
    public GoodAlertConfig selectGoodAlertConfigById(Long id) {
        return configMapper.selectGoodAlertConfigById(id);
    }

    @Override
    public List<GoodAlertConfig> selectGoodAlertConfigList(GoodAlertConfig config) {
        return configMapper.selectGoodAlertConfigList(config);
    }

    @Override
    public int insertGoodAlertConfig(GoodAlertConfig config) {
        // 检查是否已存在配置
        GoodAlertConfig existingConfig = configMapper.selectGoodAlertConfigByGoodId(config.getGoodId());
        if (existingConfig != null) {
            // 如果已存在，则更新现有配置
            config.setId(existingConfig.getId());
            return updateGoodAlertConfig(config);
        }
        
        // 不存在则新增
        config.setCreateTime(DateUtils.getNowDate());
        return configMapper.insertGoodAlertConfig(config);
    }

    @Override
    public int updateGoodAlertConfig(GoodAlertConfig config) {
        config.setUpdateTime(DateUtils.getNowDate());
        return configMapper.updateGoodAlertConfig(config);
    }

    @Override
    public int deleteGoodAlertConfigByIds(Long[] ids) {
        return configMapper.deleteGoodAlertConfigByIds(ids);
    }

    @Override
    public GoodAlertRecord selectGoodAlertRecordById(Long id) {
        return recordMapper.selectGoodAlertRecordById(id);
    }

    @Override
    public List<GoodAlertRecord> selectGoodAlertRecordList(GoodAlertRecord record) {
        return recordMapper.selectGoodAlertRecordList(record);
    }

    @Override
    public int insertGoodAlertRecord(GoodAlertRecord record) {
        record.setCreateTime(DateUtils.getNowDate());
        return recordMapper.insertGoodAlertRecord(record);
    }

    @Override
    public int updateGoodAlertRecord(GoodAlertRecord record) {
        record.setUpdateTime(DateUtils.getNowDate());
        return recordMapper.updateGoodAlertRecord(record);
    }

    @Override
    public int deleteGoodAlertRecordByIds(Long[] ids) {
        return recordMapper.deleteGoodAlertRecordByIds(ids);
    }

    @Override
    public int handleAlert(Long id, String handler, String handleRemark) {
        GoodAlertRecord record = recordMapper.selectGoodAlertRecordById(id);
        if (record != null) {
            record.setStatus("1");
            record.setHandler(handler);
            record.setHandleTime(DateUtils.getNowDate());
            record.setHandleRemark(handleRemark);
            return updateGoodAlertRecord(record);
        }
        return 0;
    }

    @Override
    public void checkPriceAlert() {
        // 创建查询条件，只查询status为returned的商品
        Good goodQuery = new Good();
        goodQuery.setStatus("returned");
        
        List<Good> goods = goodMapper.selectGoodList(goodQuery);
        for (Good good : goods) {
            GoodAlertConfig config = configMapper.selectGoodAlertConfigByGoodId(good.getId());
            if (config != null && "1".equals(config.getEnabled()) && config.getPriceChangeThreshold() != null) {
                if (good.getSalePrice() != null && good.getCost() != null && good.getCost().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal priceChange = good.getSalePrice().subtract(good.getCost())
                        .divide(good.getCost(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                    if (priceChange.abs().compareTo(config.getPriceChangeThreshold()) > 0) {
                        createAlertRecord(good.getId(), "PRICE", 
                            "商品价格波动超过预警阈值", 
                            priceChange, 
                            config.getPriceChangeThreshold());
                    }
                }
            }
        }
    }

    @Override
    public void checkSalesAlert() {
        // 创建查询条件，只查询status为returned的商品
        Good goodQuery = new Good();
        goodQuery.setStatus("returned");
        
        List<Good> goods = goodMapper.selectGoodList(goodQuery);
        for (Good good : goods) {
            GoodAlertConfig config = configMapper.selectGoodAlertConfigByGoodId(good.getId());
            if (config != null && "1".equals(config.getEnabled()) && config.getSalesAbnormalThreshold() != null) {
                if (good.getCreateTime() != null) {
                    long days = (DateUtils.getNowDate().getTime() - good.getCreateTime().getTime()) / (1000 * 60 * 60 * 24);
                    if (days >= config.getSalesAbnormalThreshold()) {
                        createAlertRecord(good.getId(), "SALES", 
                            "商品长时间未销售", 
                            new BigDecimal(days), 
                            new BigDecimal(config.getSalesAbnormalThreshold()));
                    }
                }
            }
        }
    }

    @Override
    public void checkProfitAlert() {
        // 创建查询条件，查询status为returned或sold的商品
        Good goodQuery = new Good();
        goodQuery.setStatus("returned");
        List<Good> returnedGoods = goodMapper.selectGoodList(goodQuery);
        
        goodQuery.setStatus("sold");
        List<Good> soldGoods = goodMapper.selectGoodList(goodQuery);
        
        // 合并两个列表
        List<Good> goods = new ArrayList<>();
        goods.addAll(returnedGoods);
        goods.addAll(soldGoods);
        
        for (Good good : goods) {
            GoodAlertConfig config = configMapper.selectGoodAlertConfigByGoodId(good.getId());
            if (config != null && "1".equals(config.getEnabled()) && config.getProfitThreshold() != null) {
                if (good.getProfit() != null && good.getCost() != null && good.getCost().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal profitRate = good.getProfit()
                        .divide(good.getCost(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                    if (profitRate.compareTo(config.getProfitThreshold()) < 0) {
                        createAlertRecord(good.getId(), "PROFIT", 
                            "商品利润率低于预警阈值", 
                            profitRate, 
                            config.getProfitThreshold());
                    }
                }
            }
        }
    }
    
    @Override
    public int createDefaultAlertConfig(Long goodId) {
        System.out.println("Creating default alert config for goodId: " + goodId);
        
        // 检查是否已存在配置
        GoodAlertConfig existingConfig = configMapper.selectGoodAlertConfigByGoodId(goodId);
        System.out.println("Existing config check result: " + (existingConfig != null ? "exists" : "not exists"));
        
        if (existingConfig != null) {
            System.out.println("Alert config already exists for goodId: " + goodId);
            return 0; // 已存在配置，不再创建
        }
        
        // 检查商品状态是否为returned
        Good good = goodMapper.selectGoodById(goodId);
        System.out.println("Good check result: " + (good != null ? "exists" : "not exists") + 
                         ", status: " + (good != null ? good.getStatus() : "null"));
        
        // 如果商品不存在或状态不是returned，则不创建配置
        if (good == null || !"returned".equals(good.getStatus())) {
            System.out.println("Good does not exist or status is not 'returned', skipping alert config creation");
            return 0;
        }
        
        // 创建默认配置
        GoodAlertConfig config = new GoodAlertConfig();
        config.setGoodId(goodId);
        config.setPriceChangeThreshold(new BigDecimal("20"));  // 默认价格波动阈值20%
        config.setProfitThreshold(new BigDecimal("5"));       // 默认利润率阈值5%
        config.setSalesAbnormalThreshold(30);                  // 默认销售异常阈值30天
        config.setEnabled("1");                                // 默认启用
        config.setCreateTime(DateUtils.getNowDate());
        
        System.out.println("Inserting alert config with values: " +
                         "goodId=" + config.getGoodId() +
                         ", priceChangeThreshold=" + config.getPriceChangeThreshold() +
                         ", profitThreshold=" + config.getProfitThreshold() +
                         ", salesAbnormalThreshold=" + config.getSalesAbnormalThreshold() +
                         ", enabled=" + config.getEnabled());
        
        int result = configMapper.insertGoodAlertConfig(config);
        System.out.println("Insert alert config SQL result: " + result);
        return result;
    }
    
    @Override
    public int createDefaultAlertConfigForAllGoods() {
        int count = 0;
        
        // 创建查询条件，只查询status为returned的商品
        Good goodQuery = new Good();
        goodQuery.setStatus("returned");
        
        List<Good> goods = goodMapper.selectGoodList(goodQuery);
        for (Good good : goods) {
            count += createDefaultAlertConfig(good.getId());
        }
        return count;
    }

    private void createAlertRecord(Long goodId, String alertType, String content, BigDecimal value, BigDecimal threshold) {
        GoodAlertRecord record = new GoodAlertRecord();
        record.setGoodId(goodId);
        record.setAlertType(alertType);
        record.setAlertContent(content);
        record.setAlertValue(value);
        record.setThreshold(threshold);
        record.setAlertTime(DateUtils.getNowDate());
        record.setStatus("0");
        insertGoodAlertRecord(record);
    }
} 