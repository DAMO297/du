package com.money.merchant.service;

import java.util.List;
import com.money.merchant.domain.Cost;

/**
 * 邮费Service接口
 * 
 * @author dm
 * @date 2024-10-27
 */
public interface ICostService 
{
    /**
     * 查询邮费
     * 
     * @param id 邮费主键
     * @return 邮费
     */
    public Cost selectCostById(Long id);

    /**
     * 查询邮费列表
     * 
     * @param cost 邮费
     * @return 邮费集合
     */
    public List<Cost> selectCostList(Cost cost);

    /**
     * 新增邮费
     * 
     * @param cost 邮费
     * @return 结果
     */
    public int insertCost(Cost cost);

    /**
     * 修改邮费
     * 
     * @param cost 邮费
     * @return 结果
     */
    public int updateCost(Cost cost);

    /**
     * 批量删除邮费
     * 
     * @param ids 需要删除的邮费主键集合
     * @return 结果
     */
    public int deleteCostByIds(Long[] ids);

    /**
     * 删除邮费信息
     * 
     * @param id 邮费主键
     * @return 结果
     */
    public int deleteCostById(Long id);
}
