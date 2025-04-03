package com.money.merchant.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 邮费预算对象 tb_cost_budget
 *
 * @author dm
 * @date 2025-04-03
 */
public class CostBudget extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预算ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 预算周期类型 (1:月度, 2:季度, 3:年度) */
    @Excel(name = "预算周期类型", readConverterExp = "1=月度,2=季度,3=年度")
    private Integer budgetType;

    /** 预算年份 */
    @Excel(name = "预算年份")
    private Integer budgetYear;

    /** 预算期间 (月度:1-12, 季度:1-4) */
    @Excel(name = "预算期间")
    private Integer budgetPeriod;

    /** 预算金额 */
    @Excel(name = "预算金额")
    private BigDecimal budgetAmount;

    /** 已使用金额 */
    @Excel(name = "已使用金额")
    private BigDecimal usedAmount;

    /** 预算状态 (1:进行中, 2:已完成, 3:已超出) */
    @Excel(name = "预算状态", readConverterExp = "1=进行中,2=已完成,3=已超出")
    private Integer status;

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
    
    public void setBudgetType(Integer budgetType)
    {
        this.budgetType = budgetType;
    }

    public Integer getBudgetType()
    {
        return budgetType;
    }
    
    public void setBudgetYear(Integer budgetYear)
    {
        this.budgetYear = budgetYear;
    }

    public Integer getBudgetYear()
    {
        return budgetYear;
    }
    
    public void setBudgetPeriod(Integer budgetPeriod)
    {
        this.budgetPeriod = budgetPeriod;
    }

    public Integer getBudgetPeriod()
    {
        return budgetPeriod;
    }
    
    public void setBudgetAmount(BigDecimal budgetAmount)
    {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getBudgetAmount()
    {
        return budgetAmount;
    }
    
    public void setUsedAmount(BigDecimal usedAmount)
    {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getUsedAmount()
    {
        return usedAmount;
    }
    
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
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