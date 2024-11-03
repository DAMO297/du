package com.money.merchant.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 邮费对象 tb_cost
 * 
 * @author dm
 * @date 2024-10-27
 */
public class Cost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 邮费主键ID */
    private Long id;

    /** 物流公司名称 */
    @Excel(name = "物流公司名称")
    private String logisticsCompany;

    /** 单号后四位 */
    @Excel(name = "单号后四位")
    private String lastFourDigits;

    /** 花费金额 */
    @Excel(name = "花费金额")
    private BigDecimal costAmount;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLogisticsCompany(String logisticsCompany) 
    {
        this.logisticsCompany = logisticsCompany;
    }

    public String getLogisticsCompany() 
    {
        return logisticsCompany;
    }
    public void setLastFourDigits(String lastFourDigits) 
    {
        this.lastFourDigits = lastFourDigits;
    }

    public String getLastFourDigits() 
    {
        return lastFourDigits;
    }
    public void setCostAmount(BigDecimal costAmount) 
    {
        this.costAmount = costAmount;
    }

    public BigDecimal getCostAmount() 
    {
        return costAmount;
    }
    public void setDateTime(Date dateTime) 
    {
        this.dateTime = dateTime;
    }

    public Date getDateTime() 
    {
        return dateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("logisticsCompany", getLogisticsCompany())
            .append("lastFourDigits", getLastFourDigits())
            .append("costAmount", getCostAmount())
            .append("createTime", getCreateTime())
            .append("dateTime", getDateTime())
            .toString();
    }
}
