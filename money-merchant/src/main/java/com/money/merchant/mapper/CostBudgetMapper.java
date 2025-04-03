package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.CostBudget;

/**
 * 邮费预算Mapper接口
 * 
 * @author dm
 * @date 2025-04-03
 */
public interface CostBudgetMapper 
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
     * 删除邮费预算
     * 
     * @param id 邮费预算主键
     * @return 结果
     */
    public int deleteCostBudgetById(Long id);

    /**
     * 批量删除邮费预算
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostBudgetByIds(Long[] ids);
    
    /**
     * 查询活跃的邮费预算
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 邮费预算列表
     */
    public List<CostBudget> selectActiveCostBudgets(Long userId, Long deptId);
    
    /**
     * 更新预算使用金额
     * 
     * @param id 预算ID
     * @param usedAmount 已使用金额
     * @return 结果
     */
    public int updateBudgetUsedAmount(Long id, java.math.BigDecimal usedAmount);
} 