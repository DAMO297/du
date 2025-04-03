package com.money.merchant.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 邮费报告对象 tb_cost_report
 *
 * @author dm
 * @date 2025-04-03
 */
public class CostReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 报告类型 (1:周报, 2:月报, 3:季报, 4:年报) */
    @Excel(name = "报告类型", readConverterExp = "1=周报,2=月报,3=季报,4=年报")
    private Integer reportType;

    /** 报告名称 */
    @Excel(name = "报告名称")
    private String reportName;

    /** 报告年份 */
    @Excel(name = "报告年份")
    private Integer reportYear;

    /** 报告期间 (周:1-53, 月:1-12, 季:1-4) */
    @Excel(name = "报告期间")
    private Integer reportPeriod;

    /** 总费用 */
    @Excel(name = "总费用")
    private BigDecimal totalAmount;
    
    /** 总费用（兼容旧版本） */
    private BigDecimal totalCost;

    /** 平均单笔费用 */
    @Excel(name = "平均单笔费用")
    private BigDecimal averageAmount;
    
    /** 平均费用（兼容旧版本） */
    private BigDecimal averageCost;

    /** 最高单笔费用 */
    @Excel(name = "最高单笔费用")
    private BigDecimal maxAmount;

    /** 异常记录数 */
    @Excel(name = "异常记录数")
    private Integer abnormalCount;

    /** 订单总数 */
    @Excel(name = "订单总数")
    private Integer orderCount;
    
    /** 项目数量（兼容旧版本） */
    private Integer itemCount;
    
    /** 报告状态 (0:草稿, 1:已生成, 2:已发送) */
    @Excel(name = "报告状态", readConverterExp = "0=草稿,1=已生成,2=已发送")
    private Integer reportStatus;

    /** 报告生成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报告生成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date generatedTime;

    /** 报告内容 (JSON格式) */
    private String reportContent;

    /** 优化建议 */
    private String optimizationSuggestions;

    /** 起始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "起始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

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
    
    public void setReportType(Integer reportType)
    {
        this.reportType = reportType;
    }

    public Integer getReportType()
    {
        return reportType;
    }
    
    public void setReportName(String reportName)
    {
        this.reportName = reportName;
    }

    public String getReportName()
    {
        return reportName;
    }
    
    public void setReportYear(Integer reportYear)
    {
        this.reportYear = reportYear;
    }

    public Integer getReportYear()
    {
        return reportYear;
    }
    
    public void setReportPeriod(Integer reportPeriod)
    {
        this.reportPeriod = reportPeriod;
    }

    public Integer getReportPeriod()
    {
        return reportPeriod;
    }
    
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }
    
    public void setTotalCost(BigDecimal totalCost)
    {
        this.totalCost = totalCost;
        // 同步设置totalAmount，保持两个字段一致
        this.totalAmount = totalCost;
    }

    public BigDecimal getTotalCost()
    {
        return totalCost != null ? totalCost : totalAmount;
    }
    
    public void setAverageAmount(BigDecimal averageAmount)
    {
        this.averageAmount = averageAmount;
    }

    public BigDecimal getAverageAmount()
    {
        return averageAmount;
    }
    
    public void setAverageCost(BigDecimal averageCost)
    {
        this.averageCost = averageCost;
        // 同步设置averageAmount，保持两个字段一致
        this.averageAmount = averageCost;
    }

    public BigDecimal getAverageCost()
    {
        return averageCost != null ? averageCost : averageAmount;
    }
    
    public void setMaxAmount(BigDecimal maxAmount)
    {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getMaxAmount()
    {
        return maxAmount;
    }
    
    public void setAbnormalCount(Integer abnormalCount)
    {
        this.abnormalCount = abnormalCount;
    }

    public Integer getAbnormalCount()
    {
        return abnormalCount;
    }
    
    public void setOrderCount(Integer orderCount)
    {
        this.orderCount = orderCount;
    }

    public Integer getOrderCount()
    {
        return orderCount;
    }
    
    public void setItemCount(Integer itemCount)
    {
        this.itemCount = itemCount;
        // 同步设置orderCount，保持两个字段一致
        this.orderCount = itemCount;
    }

    public Integer getItemCount()
    {
        return itemCount != null ? itemCount : orderCount;
    }
    
    public void setReportStatus(Integer reportStatus)
    {
        this.reportStatus = reportStatus;
    }

    public Integer getReportStatus()
    {
        return reportStatus;
    }
    
    public void setGeneratedTime(Date generatedTime)
    {
        this.generatedTime = generatedTime;
    }

    public Date getGeneratedTime()
    {
        return generatedTime;
    }
    
    public void setReportContent(String reportContent)
    {
        this.reportContent = reportContent;
    }

    public String getReportContent()
    {
        return reportContent;
    }
    
    public void setOptimizationSuggestions(String optimizationSuggestions)
    {
        this.optimizationSuggestions = optimizationSuggestions;
    }

    public String getOptimizationSuggestions()
    {
        return optimizationSuggestions;
    }
    
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }
    
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }
} 