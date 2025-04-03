package com.money.merchant.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.money.common.annotation.Excel;
import com.money.common.core.domain.BaseEntity;

/**
 * 邮费异常提醒对象 tb_cost_alert
 *
 * @author dm
 * @date 2025-04-03
 */
public class CostAlert extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提醒ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 阈值类型 (1:单笔金额, 2:日累计, 3:周累计, 4:月累计) */
    @Excel(name = "阈值类型", readConverterExp = "1=单笔金额,2=日累计,3=周累计,4=月累计")
    private Integer thresholdType;

    /** 阈值金额 */
    @Excel(name = "阈值金额")
    private BigDecimal thresholdAmount;

    /** 提醒方式 (1:系统消息, 2:邮件, 3:短信) */
    @Excel(name = "提醒方式", readConverterExp = "1=系统消息,2=邮件,3=短信")
    private Integer alertMethod;

    /** 状态 (0:禁用, 1:启用) */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private Integer status;
    
    /** 最近触发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最近触发时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastTriggeredTime;

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
    
    public void setThresholdType(Integer thresholdType)
    {
        this.thresholdType = thresholdType;
    }

    public Integer getThresholdType()
    {
        return thresholdType;
    }
    
    public void setThresholdAmount(BigDecimal thresholdAmount)
    {
        this.thresholdAmount = thresholdAmount;
    }

    public BigDecimal getThresholdAmount()
    {
        return thresholdAmount;
    }
    
    public void setAlertMethod(Integer alertMethod)
    {
        this.alertMethod = alertMethod;
    }

    public Integer getAlertMethod()
    {
        return alertMethod != null ? alertMethod : 1;
    }
    
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }
    
    public void setLastTriggeredTime(Date lastTriggeredTime)
    {
        this.lastTriggeredTime = lastTriggeredTime;
    }

    public Date getLastTriggeredTime()
    {
        return lastTriggeredTime;
    }
} 