package com.money.merchant.service.impl;

import java.util.List;
import com.money.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.money.merchant.mapper.GoodMapper;
import com.money.merchant.domain.Good;
import com.money.merchant.service.IGoodService;
import com.money.common.annotation.DataScope;

/**
 * 仓库Service业务层处理
 * 
 * @author dm
 * @date 2024-10-27
 */
@Service
public class GoodServiceImpl implements IGoodService 
{
    @Autowired
    private GoodMapper goodMapper;

    /**
     * 查询仓库
     * 
     * @param id 仓库主键
     * @return 仓库
     */
    @Override
    public Good selectGoodById(Long id)
    {
        return goodMapper.selectGoodById(id);
    }

    /**
     * 查询仓库列表
     * 
     * @param good 仓库
     * @return 仓库
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<Good> selectGoodList(Good good)
    {
        return goodMapper.selectGoodList(good);
    }

    /**
     * 新增仓库
     * 
     * @param good 仓库
     * @return 结果
     */
    @Override
    public int insertGood(Good good)
    {
        good.setCreateTime(DateUtils.getNowDate());
        return goodMapper.insertGood(good);
    }

    /**
     * 修改仓库
     * 
     * @param good 仓库
     * @return 结果
     */
    @Override
    public int updateGood(Good good)
    {
        return goodMapper.updateGood(good);
    }

    /**
     * 批量删除仓库
     * 
     * @param ids 需要删除的仓库主键
     * @return 结果
     */
    @Override
    public int deleteGoodByIds(Long[] ids)
    {
        return goodMapper.deleteGoodByIds(ids);
    }

    /**
     * 删除仓库信息
     * 
     * @param id 仓库主键
     * @return 结果
     */
    @Override
    public int deleteGoodById(Long id)
    {
        return goodMapper.deleteGoodById(id);
    }
}
