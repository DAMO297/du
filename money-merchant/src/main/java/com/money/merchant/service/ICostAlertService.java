package com.money.merchant.service;

import java.util.List;
import java.math.BigDecimal;
import com.money.merchant.domain.CostAlert;
import com.money.merchant.domain.Cost;

/**
 * 邮费异常提醒Service接口
 * 
 * @author dm
 * @date 2025-04-03
 */
public interface ICostAlertService 
{
    /**
     * 查询邮费异常提醒
     * 
     * @param id 邮费异常提醒主键
     * @return 邮费异常提醒
     */
    public CostAlert selectCostAlertById(Long id);

    /**
     * 查询邮费异常提醒列表
     * 
     * @param costAlert 邮费异常提醒
     * @return 邮费异常提醒集合
     */
    public List<CostAlert> selectCostAlertList(CostAlert costAlert);

    /**
     * 新增邮费异常提醒
     * 
     * @param costAlert 邮费异常提醒
     * @return 结果
     */
    public int insertCostAlert(CostAlert costAlert);

    /**
     * 修改邮费异常提醒
     * 
     * @param costAlert 邮费异常提醒
     * @return 结果
     */
    public int updateCostAlert(CostAlert costAlert);

    /**
     * 批量删除邮费异常提醒
     * 
     * @param ids 需要删除的邮费异常提醒主键集合
     * @return 结果
     */
    public int deleteCostAlertByIds(Long[] ids);

    /**
     * 删除邮费异常提醒信息
     * 
     * @param id 邮费异常提醒主键
     * @return 结果
     */
    public int deleteCostAlertById(Long id);
    
    /**
     * 检查邮费记录是否触发异常提醒
     * 
     * @param cost 邮费记录
     * @return 触发的提醒列表
     */
    public List<CostAlert> checkCostAlerts(Cost cost);
    
    /**
     * 发送邮费异常提醒
     * 
     * @param costAlert 异常提醒配置
     * @param cost 触发提醒的邮费记录
     * @return 结果
     */
    public boolean sendAlert(CostAlert costAlert, Cost cost);
    
    /**
     * 检查日累计/周累计/月累计邮费是否超过阈值
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 触发的提醒列表
     */
    public List<CostAlert> checkAggregatedCostAlerts(Long userId, Long deptId);
    
    /**
     * 生成邮费节约建议
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 节约建议
     */
    public String generateCostSavingSuggestions(Long userId, Long deptId);
} 