package com.money.merchant.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 账单对象 tb_bill
 * 
 * @author dm
 * @date 2024-10-27
 */
public class Bill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账单主键ID */
    private Long id;

    /** 关联的商品ID */
    @Excel(name = "关联的商品ID")
    private Long goodId;

    /** 关联的邮费ID */
    @Excel(name = "关联的邮费ID")
    private Long costId;

    /** 每日利润(售出价 - 成本 - 邮费) */
    @Excel(name = "每日利润(售出价 - 成本 - 邮费)")
    private BigDecimal dailyProfit;

    /** 每月利润 */
    @Excel(name = "每月利润")
    private BigDecimal monthlyProfit;

    /** 每年利润 */
    @Excel(name = "每年利润")
    private BigDecimal yearlyProfit;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    /** 仓库信息 */
    private List<Good> goodList;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGoodId(Long goodId) 
    {
        this.goodId = goodId;
    }

    public Long getGoodId() 
    {
        return goodId;
    }
    public void setCostId(Long costId) 
    {
        this.costId = costId;
    }

    public Long getCostId() 
    {
        return costId;
    }
    public void setDailyProfit(BigDecimal dailyProfit) 
    {
        this.dailyProfit = dailyProfit;
    }

    public BigDecimal getDailyProfit() 
    {
        return dailyProfit;
    }
    public void setMonthlyProfit(BigDecimal monthlyProfit) 
    {
        this.monthlyProfit = monthlyProfit;
    }

    public BigDecimal getMonthlyProfit() 
    {
        return monthlyProfit;
    }
    public void setYearlyProfit(BigDecimal yearlyProfit) 
    {
        this.yearlyProfit = yearlyProfit;
    }

    public BigDecimal getYearlyProfit() 
    {
        return yearlyProfit;
    }
    public void setDateTime(Date dateTime) 
    {
        this.dateTime = dateTime;
    }

    public Date getDateTime() 
    {
        return dateTime;
    }

    public List<Good> getGoodList()
    {
        return goodList;
    }

    public void setGoodList(List<Good> goodList)
    {
        this.goodList = goodList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("goodId", getGoodId())
            .append("costId", getCostId())
            .append("dailyProfit", getDailyProfit())
            .append("monthlyProfit", getMonthlyProfit())
            .append("yearlyProfit", getYearlyProfit())
            .append("createTime", getCreateTime())
            .append("dateTime", getDateTime())
            .append("goodList", getGoodList())
            .toString();
    }
}
