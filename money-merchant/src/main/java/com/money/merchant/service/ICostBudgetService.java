package com.money.merchant.service;

import java.util.List;
import java.math.BigDecimal;
import com.money.merchant.domain.CostBudget;

/**
 * 邮费预算Service接口
 * 
 * @author dm
 * @date 2025-04-03
 */
public interface ICostBudgetService 
{
    /**
     * 查询邮费预算
     * 
     * @param id 邮费预算主键
     * @return 邮费预算
     */
    public CostBudget selectCostBudgetById(Long id);

    /**
     * 查询邮费预算列表
     * 
     * @param costBudget 邮费预算
     * @return 邮费预算集合
     */
    public List<CostBudget> selectCostBudgetList(CostBudget costBudget);

    /**
     * 新增邮费预算
     * 
     * @param costBudget 邮费预算
     * @return 结果
     */
    public int insertCostBudget(CostBudget costBudget);

    /**
     * 修改邮费预算
     * 
     * @param costBudget 邮费预算
     * @return 结果
     */
    public int updateCostBudget(CostBudget costBudget);

    /**
     * 批量删除邮费预算
     * 
     * @param ids 需要删除的邮费预算主键集合
     * @return 结果
     */
    public int deleteCostBudgetByIds(Long[] ids);

    /**
     * 删除邮费预算信息
     * 
     * @param id 邮费预算主键
     * @return 结果
     */
    public int deleteCostBudgetById(Long id);
    
    /**
     * 检查预算使用情况
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 预算使用情况列表，包含预算信息和使用进度
     */
    public List<CostBudget> checkBudgetUsage(Long userId, Long deptId);
    
    /**
     * 更新预算使用金额
     * 
     * @param id 预算ID
     * @param amount 新增使用金额
     * @return 结果
     */
    public int updateBudgetUsage(Long id, BigDecimal amount);
    
    /**
     * 创建周期性预算
     * 
     * @param costBudget 预算基本信息
     * @return 创建结果
     */
    public int createPeriodicBudget(CostBudget costBudget);
} 