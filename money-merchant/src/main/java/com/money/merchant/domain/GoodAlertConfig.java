package com.money.merchant.domain;

import java.math.BigDecimal;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 商品预警配置对象 tb_good_alert_config
 */
public class GoodAlertConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long goodId;

    /** 价格波动预警阈值(%) */
    @Excel(name = "价格波动预警阈值(%)")
    private BigDecimal priceChangeThreshold;

    /** 利润预警阈值(%) */
    @Excel(name = "利润预警阈值(%)")
    private BigDecimal profitThreshold;

    /** 销售异常预警阈值(天) */
    @Excel(name = "销售异常预警阈值(天)")
    private Integer salesAbnormalThreshold;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private String enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public BigDecimal getPriceChangeThreshold() {
        return priceChangeThreshold;
    }

    public void setPriceChangeThreshold(BigDecimal priceChangeThreshold) {
        this.priceChangeThreshold = priceChangeThreshold;
    }

    public BigDecimal getProfitThreshold() {
        return profitThreshold;
    }

    public void setProfitThreshold(BigDecimal profitThreshold) {
        this.profitThreshold = profitThreshold;
    }

    public Integer getSalesAbnormalThreshold() {
        return salesAbnormalThreshold;
    }

    public void setSalesAbnormalThreshold(Integer salesAbnormalThreshold) {
        this.salesAbnormalThreshold = salesAbnormalThreshold;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }
} 