package com.money.merchant.service.impl;

import java.util.List;
import com.money.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.money.merchant.mapper.CostMapper;
import com.money.merchant.domain.Cost;
import com.money.merchant.service.ICostService;

/**
 * 邮费Service业务层处理
 * 
 * @author dm
 * @date 2024-10-27
 */
@Service
public class CostServiceImpl implements ICostService 
{
    @Autowired
    private CostMapper costMapper;

    /**
     * 查询邮费
     * 
     * @param id 邮费主键
     * @return 邮费
     */
    @Override
    public Cost selectCostById(Long id)
    {
        return costMapper.selectCostById(id);
    }

    /**
     * 查询邮费列表
     * 
     * @param cost 邮费
     * @return 邮费
     */
    @Override
    public List<Cost> selectCostList(Cost cost)
    {
        return costMapper.selectCostList(cost);
    }

    /**
     * 新增邮费
     * 
     * @param cost 邮费
     * @return 结果
     */
    @Override
    public int insertCost(Cost cost)
    {
        cost.setCreateTime(DateUtils.getNowDate());
        return costMapper.insertCost(cost);
    }

    /**
     * 修改邮费
     * 
     * @param cost 邮费
     * @return 结果
     */
    @Override
    public int updateCost(Cost cost)
    {
        return costMapper.updateCost(cost);
    }

    /**
     * 批量删除邮费
     * 
     * @param ids 需要删除的邮费主键
     * @return 结果
     */
    @Override
    public int deleteCostByIds(Long[] ids)
    {
        return costMapper.deleteCostByIds(ids);
    }

    /**
     * 删除邮费信息
     * 
     * @param id 邮费主键
     * @return 结果
     */
    @Override
    public int deleteCostById(Long id)
    {
        return costMapper.deleteCostById(id);
    }
}
