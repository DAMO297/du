package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.Cost;

/**
 * 邮费Mapper接口
 *
 * @author dm
 * @date 2025-04-01
 */
public interface CostMapper
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
     * 删除邮费
     *
     * @param id 邮费主键
     * @return 结果
     */
    public int deleteCostById(Long id);

    /**
     * 批量删除邮费
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostByIds(Long[] ids);
}
