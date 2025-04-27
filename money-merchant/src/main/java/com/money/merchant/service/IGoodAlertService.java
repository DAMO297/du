package com.money.merchant.service;

import java.util.List;
import com.money.merchant.domain.GoodAlertConfig;
import com.money.merchant.domain.GoodAlertRecord;

/**
 * 商品预警Service接口
 */
public interface IGoodAlertService {
    /**
     * 查询预警配置
     */
    public GoodAlertConfig selectGoodAlertConfigById(Long id);

    /**
     * 查询预警配置列表
     */
    public List<GoodAlertConfig> selectGoodAlertConfigList(GoodAlertConfig config);

    /**
     * 新增预警配置
     */
    public int insertGoodAlertConfig(GoodAlertConfig config);

    /**
     * 修改预警配置
     */
    public int updateGoodAlertConfig(GoodAlertConfig config);

    /**
     * 批量删除预警配置
     */
    public int deleteGoodAlertConfigByIds(Long[] ids);

    /**
     * 查询预警记录
     */
    public GoodAlertRecord selectGoodAlertRecordById(Long id);

    /**
     * 查询预警记录列表
     */
    public List<GoodAlertRecord> selectGoodAlertRecordList(GoodAlertRecord record);

    /**
     * 新增预警记录
     */
    public int insertGoodAlertRecord(GoodAlertRecord record);

    /**
     * 修改预警记录
     */
    public int updateGoodAlertRecord(GoodAlertRecord record);

    /**
     * 批量删除预警记录
     */
    public int deleteGoodAlertRecordByIds(Long[] ids);

    /**
     * 处理预警记录
     */
    public int handleAlert(Long id, String handler, String handleRemark);

    /**
     * 检查商品价格预警
     */
    public void checkPriceAlert();

    /**
     * 检查商品销售预警
     */
    public void checkSalesAlert();

    /**
     * 检查商品利润预警
     */
    public void checkProfitAlert();
    
    /**
     * 创建默认预警配置
     * 
     * @param goodId 商品ID
     * @return 结果
     */
    public int createDefaultAlertConfig(Long goodId);
    
    /**
     * 为所有未配置预警的商品创建默认预警配置
     * 
     * @return 创建的配置数量
     */
    public int createDefaultAlertConfigForAllGoods();
} 