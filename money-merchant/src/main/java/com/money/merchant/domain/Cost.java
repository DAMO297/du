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
 * @date 2025-04-01
 */
public class Cost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 邮费主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 物流公司名称 */
    @Excel(name = "物流公司名称")
    private String logisticsCompany;

    /** 单号后四位 */
    private String lastFourDigits;

    /** 完整运单号 */
    @Excel(name = "完整运单号")
    private String fullTrackingNumber;

    /** 花费金额 */
    @Excel(name = "花费金额")
    private BigDecimal costAmount;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    /** 发货日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发货日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shippingDate;

    /** 收件省份 */
    private String recipientProvince;

    /** 收件城市 */
    private String recipientCity;

    /** 收件区县 */
    private String recipientDistrict;

    /** 收件详细地址 */
    @Excel(name = "收件详细地址")
    private String recipientAddress;

    /** 包裹重量(kg) */
    @Excel(name = "包裹重量(kg)")
    private BigDecimal weight;

    /** 运单图片存储路径 */
    @Excel(name = "运单图片存储路径")
    private String receiptImageUrl;

    /** 图片上传时间 */
    @Excel(name = "图片上传时间", width = 30)
    private String imageUploadTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
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
    public void setFullTrackingNumber(String fullTrackingNumber)
    {
        this.fullTrackingNumber = fullTrackingNumber;
    }

    public String getFullTrackingNumber()
    {
        return fullTrackingNumber;
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
    public void setShippingDate(Date shippingDate)
    {
        this.shippingDate = shippingDate;
    }

    public Date getShippingDate()
    {
        return shippingDate;
    }
    public void setRecipientProvince(String recipientProvince)
    {
        this.recipientProvince = recipientProvince;
    }

    public String getRecipientProvince()
    {
        return recipientProvince;
    }
    public void setRecipientCity(String recipientCity)
    {
        this.recipientCity = recipientCity;
    }

    public String getRecipientCity()
    {
        return recipientCity;
    }
    public void setRecipientDistrict(String recipientDistrict)
    {
        this.recipientDistrict = recipientDistrict;
    }

    public String getRecipientDistrict()
    {
        return recipientDistrict;
    }
    public void setRecipientAddress(String recipientAddress)
    {
        this.recipientAddress = recipientAddress;
    }

    public String getRecipientAddress()
    {
        return recipientAddress;
    }
    public void setWeight(BigDecimal weight)
    {
        this.weight = weight;
    }

    public BigDecimal getWeight()
    {
        return weight;
    }
    public void setReceiptImageUrl(String receiptImageUrl)
    {
        this.receiptImageUrl = receiptImageUrl;
    }

    public String getReceiptImageUrl()
    {
        return receiptImageUrl;
    }
    public void setImageUploadTime(String imageUploadTime)
    {
        this.imageUploadTime = imageUploadTime;
    }

    public String getImageUploadTime()
    {
        return imageUploadTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("deptId", getDeptId())
                .append("logisticsCompany", getLogisticsCompany())
                .append("lastFourDigits", getLastFourDigits())
                .append("fullTrackingNumber", getFullTrackingNumber())
                .append("costAmount", getCostAmount())
                .append("createTime", getCreateTime())
                .append("dateTime", getDateTime())
                .append("shippingDate", getShippingDate())
                .append("recipientProvince", getRecipientProvince())
                .append("recipientCity", getRecipientCity())
                .append("recipientDistrict", getRecipientDistrict())
                .append("recipientAddress", getRecipientAddress())
                .append("weight", getWeight())
                .append("receiptImageUrl", getReceiptImageUrl())
                .append("imageUploadTime", getImageUploadTime())
                .toString();
    }
}
