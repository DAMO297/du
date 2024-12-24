package com.money.merchant.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 仓库对象 tb_good
 * 
 * @author dm
 * @date 2024-10-27
 */
public class Good extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品主键ID */
    private Long id;

    public Long getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Long displayId) {
        this.displayId = displayId;
    }

    private Long displayId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String productName;

    /** 货号 */
    @Excel(name = "货号")
    private String productCode;

    /** 码数 */
    @Excel(name = "码数")
    private String sizeCode;

    /** 尺寸 */
    @Excel(name = "尺寸")
    private String dimensions;

    /** 商品成本 */
    @Excel(name = "商品成��")
    private BigDecimal cost;

    /** 状态: 卖出或退掉 */
    @Excel(name = "状态: 卖出或退掉")
    private String status;

    /** 售出价格 */
    @Excel(name = "售出价格")
    private BigDecimal salePrice;

    /** 商品总值 */
    @Excel(name = "商品总值")
    private BigDecimal totalValue;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    /** 商品利润(售出价 - 成本) */
    @Excel(name = "商品利润(售出价 - 成本)")
    private BigDecimal profit;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }
    public void setProductCode(String productCode) 
    {
        this.productCode = productCode;
    }

    public String getProductCode() 
    {
        return productCode;
    }
    public void setSizeCode(String sizeCode) 
    {
        this.sizeCode = sizeCode;
    }

    public String getSizeCode() 
    {
        return sizeCode;
    }
    public void setDimensions(String dimensions) 
    {
        this.dimensions = dimensions;
    }

    public String getDimensions() 
    {
        return dimensions;
    }
    public void setCost(BigDecimal cost) 
    {
        this.cost = cost;
    }

    public BigDecimal getCost() 
    {
        return cost;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setSalePrice(BigDecimal salePrice) 
    {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePrice() 
    {
        return salePrice;
    }
    public void setTotalValue(BigDecimal totalValue) 
    {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalValue() 
    {
        return totalValue;
    }
    public void setDateTime(Date dateTime) 
    {
        this.dateTime = dateTime;
    }

    public Date getDateTime() 
    {
        return dateTime;
    }
    public void setProfit(BigDecimal profit) 
    {
        this.profit = profit;
    }

    public BigDecimal getProfit() 
    {
        return profit;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productName", getProductName())
            .append("productCode", getProductCode())
            .append("sizeCode", getSizeCode())
            .append("dimensions", getDimensions())
            .append("cost", getCost())
            .append("status", getStatus())
            .append("salePrice", getSalePrice())
            .append("totalValue", getTotalValue())
            .append("createTime", getCreateTime())
            .append("dateTime", getDateTime())
            .append("profit", getProfit())
            .toString();
    }
}
