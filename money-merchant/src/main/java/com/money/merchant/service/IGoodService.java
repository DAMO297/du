package com.money.merchant.service;

import java.util.List;
import com.money.merchant.domain.Good;

/**
 * 仓库Service接口
 * 
 * @author dm
 * @date 2024-10-27
 */
public interface IGoodService 
{
    /**
     * 查询仓库
     * 
     * @param id 仓库主键
     * @return 仓库
     */
    public Good selectGoodById(Long id);

    /**
     * 查询仓库列表
     * 
     * @param good 仓库
     * @return 仓库集合
     */
    public List<Good> selectGoodList(Good good);

    /**
     * 新增仓库
     * 
     * @param good 仓库
     * @return 结果
     */
    public int insertGood(Good good);

    /**
     * 修改仓库
     * 
     * @param good 仓库
     * @return 结果
     */
    public int updateGood(Good good);

    /**
     * 批量删除仓库
     * 
     * @param ids 需要删除的仓库主键集合
     * @return 结果
     */
    public int deleteGoodByIds(Long[] ids);

    /**
     * 删除仓库信息
     * 
     * @param id 仓库主键
     * @return 结果
     */
    public int deleteGoodById(Long id);
}
