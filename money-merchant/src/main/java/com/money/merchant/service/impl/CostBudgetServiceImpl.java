package com.money.merchant.service.impl;

import java.util.List;
import java.util.Calendar;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.money.merchant.mapper.CostBudgetMapper;
import com.money.merchant.domain.CostBudget;
import com.money.merchant.service.ICostBudgetService;
import com.money.common.utils.DateUtils;
import com.money.common.utils.SecurityUtils;
import com.money.common.annotation.DataScope;

/**
 * 邮费预算Service业务层处理
 * 
 * @author dm
 * @date 2025-04-03
 */
@Service
public class CostBudgetServiceImpl implements ICostBudgetService 
{
    @Autowired
    private CostBudgetMapper costBudgetMapper;

    /**
     * 查询邮费预算
     * 
     * @param id 邮费预算主键
     * @return 邮费预算
     */
    @Override
    public CostBudget selectCostBudgetById(Long id)
    {
        return costBudgetMapper.selectCostBudgetById(id);
    }

    /**
     * 查询邮费预算列表
     * 
     * @param costBudget 邮费预算
     * @return 邮费预算
     */
    @Override
    public List<CostBudget> selectCostBudgetList(CostBudget costBudget)
    {
        // 如果没有指定用户ID，设置默认值
        if (costBudget.getUserId() == null) {
            try {
                costBudget.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 如果获取当前用户失败，设置默认用户ID为1
                costBudget.setUserId(1L);
            }
        }
        return costBudgetMapper.selectCostBudgetList(costBudget);
    }

    /**
     * 新增邮费预算
     * 
     * @param costBudget 邮费预算
     * @return 结果
     */
    @Override
    public int insertCostBudget(CostBudget costBudget)
    {
        costBudget.setCreateTime(DateUtils.getNowDate());
        
        // 如果没有设置用户ID，使用默认值
        if (costBudget.getUserId() == null) {
            try {
                costBudget.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 如果获取当前用户失败，设置默认用户ID
                costBudget.setUserId(1L);
            }
        }
        
        // 设置预算期间的起止日期
        setPeriodDates(costBudget);
        
        // 初始化已使用金额为0
        if (costBudget.getUsedAmount() == null) {
            costBudget.setUsedAmount(BigDecimal.ZERO);
        }
        
        // 初始化预算状态为进行中
        costBudget.setStatus(1);
        
        return costBudgetMapper.insertCostBudget(costBudget);
    }

    /**
     * 修改邮费预算
     * 
     * @param costBudget 邮费预算
     * @return 结果
     */
    @Override
    public int updateCostBudget(CostBudget costBudget)
    {
        // 设置更新时间
        costBudget.setUpdateTime(DateUtils.getNowDate());
        
        // 如果没有设置用户ID，使用默认值
        if (costBudget.getUserId() == null) {
            try {
                costBudget.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 如果获取当前用户失败，设置默认用户ID
                costBudget.setUserId(1L);
            }
        }
        
        // 获取数据库中现有的预算记录，以保留那些未被传入的字段
        CostBudget existingBudget = costBudgetMapper.selectCostBudgetById(costBudget.getId());
        if (existingBudget == null) {
            return 0; // 记录不存在
        }
        
        // 如果没有提供起止日期，保留原记录中的值
        if (costBudget.getStartDate() == null) {
            costBudget.setStartDate(existingBudget.getStartDate());
        }
        if (costBudget.getEndDate() == null) {
            costBudget.setEndDate(existingBudget.getEndDate());
        }
        
        // 如果需要重新计算起止日期（预算类型或周期发生变化）
        if ((costBudget.getBudgetType() != null && !costBudget.getBudgetType().equals(existingBudget.getBudgetType())) ||
            (costBudget.getBudgetPeriod() != null && !costBudget.getBudgetPeriod().equals(existingBudget.getBudgetPeriod()))) {
            setPeriodDates(costBudget);
        }
        
        // 如果已使用金额超过预算金额，则设置状态为已超出
        if (costBudget.getUsedAmount() != null && costBudget.getBudgetAmount() != null &&
            costBudget.getUsedAmount().compareTo(costBudget.getBudgetAmount()) > 0) {
            costBudget.setStatus(3); // 已超出
        } else {
            costBudget.setStatus(1); // 进行中
        }
        
        return costBudgetMapper.updateCostBudget(costBudget);
    }

    /**
     * 批量删除邮费预算
     * 
     * @param ids 需要删除的邮费预算主键
     * @return 结果
     */
    @Override
    public int deleteCostBudgetByIds(Long[] ids)
    {
        return costBudgetMapper.deleteCostBudgetByIds(ids);
    }

    /**
     * 删除邮费预算信息
     * 
     * @param id 邮费预算主键
     * @return 结果
     */
    @Override
    public int deleteCostBudgetById(Long id)
    {
        return costBudgetMapper.deleteCostBudgetById(id);
    }
    
    /**
     * 检查预算使用情况
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 预算使用情况列表
     */
    @Override
    public List<CostBudget> checkBudgetUsage(Long userId, Long deptId)
    {
        CostBudget query = new CostBudget();
        query.setUserId(userId);
        query.setDeptId(deptId);
        List<CostBudget> budgets = costBudgetMapper.selectActiveCostBudgets(userId, deptId);
        
        // 计算使用进度并更新状态
        for (CostBudget budget : budgets) {
            // 计算使用百分比
            if (budget.getBudgetAmount() != null && budget.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usagePercentage = budget.getUsedAmount().multiply(new BigDecimal(100))
                                            .divide(budget.getBudgetAmount(), 2, BigDecimal.ROUND_HALF_UP);
                
                // 更新预算状态
                if (budget.getUsedAmount().compareTo(budget.getBudgetAmount()) >= 0) {
                    budget.setStatus(3); // 已超出
                    updateCostBudget(budget);
                } else if (DateUtils.getNowDate().after(budget.getEndDate())) {
                    budget.setStatus(2); // 已完成
                    updateCostBudget(budget);
                }
            }
        }
        
        return budgets;
    }
    
    /**
     * 更新预算使用金额
     * 
     * @param id 预算ID
     * @param amount 新增使用金额
     * @return 结果
     */
    @Override
    public int updateBudgetUsage(Long id, BigDecimal amount)
    {
        CostBudget budget = selectCostBudgetById(id);
        if (budget != null) {
            BigDecimal newUsedAmount = budget.getUsedAmount().add(amount);
            budget.setUsedAmount(newUsedAmount);
            
            // 检查预算状态
            if (newUsedAmount.compareTo(budget.getBudgetAmount()) >= 0) {
                budget.setStatus(3); // 已超出
            }
            
            return updateCostBudget(budget);
        }
        return 0;
    }
    
    /**
     * 创建周期性预算
     * 
     * @param costBudget 预算基本信息
     * @return 创建结果
     */
    @Override
    public int createPeriodicBudget(CostBudget costBudget)
    {
        // 如果没有设置用户ID，使用默认值
        if (costBudget.getUserId() == null) {
            try {
                costBudget.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 如果获取当前用户失败，设置默认用户ID
                costBudget.setUserId(1L);
            }
        }
        
        // 设置预算期间的起止日期
        setPeriodDates(costBudget);
        
        return insertCostBudget(costBudget);
    }
    
    /**
     * 设置预算期间的起止日期
     * 
     * @param costBudget 邮费预算
     */
    private void setPeriodDates(CostBudget costBudget)
    {
        Calendar calendar = Calendar.getInstance();
        
        // 设置年份
        if (costBudget.getBudgetYear() == null) {
            costBudget.setBudgetYear(calendar.get(Calendar.YEAR));
        }
        
        // 根据预算类型和周期设置起止日期
        calendar.set(Calendar.YEAR, costBudget.getBudgetYear());
        
        switch (costBudget.getBudgetType()) {
            case 1: // 月度
                if (costBudget.getBudgetPeriod() == null) {
                    costBudget.setBudgetPeriod(calendar.get(Calendar.MONTH) + 1);
                }
                calendar.set(Calendar.MONTH, costBudget.getBudgetPeriod() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                costBudget.setStartDate(calendar.getTime());
                
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                costBudget.setEndDate(calendar.getTime());
                break;
                
            case 2: // 季度
                if (costBudget.getBudgetPeriod() == null) {
                    costBudget.setBudgetPeriod((calendar.get(Calendar.MONTH) / 3) + 1);
                }
                calendar.set(Calendar.MONTH, (costBudget.getBudgetPeriod() - 1) * 3);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                costBudget.setStartDate(calendar.getTime());
                
                calendar.add(Calendar.MONTH, 3);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                costBudget.setEndDate(calendar.getTime());
                break;
                
            case 3: // 年度
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                costBudget.setStartDate(calendar.getTime());
                
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                costBudget.setEndDate(calendar.getTime());
                break;
        }
    }
} 