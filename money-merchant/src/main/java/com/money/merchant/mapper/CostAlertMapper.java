package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.CostAlert;

/**
 * 邮费异常提醒Mapper接口
 * 
 * @author dm
 * @date 2025-04-03
 */
public interface CostAlertMapper 
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
     * 删除邮费异常提醒
     * 
     * @param id 邮费异常提醒主键
     * @return 结果
     */
    public int deleteCostAlertById(Long id);

    /**
     * 批量删除邮费异常提醒
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostAlertByIds(Long[] ids);
    
    /**
     * 查询活跃的邮费异常提醒
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 邮费异常提醒列表
     */
    public List<CostAlert> selectActiveAlerts(Long userId, Long deptId);
    
    /**
     * 更新提醒触发时间
     * 
     * @param id 提醒ID
     * @param time 触发时间
     * @return 结果
     */
    public int updateAlertTriggeredTime(Long id, java.util.Date time);
} 