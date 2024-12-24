package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.Good;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库Mapper接口
 * 
 * @author dm
 * @date 2024-10-27
 */
@Mapper
public interface GoodMapper 
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
     * 删除仓库
     * 
     * @param id 仓库主键
     * @return 结果
     */
    public int deleteGoodById(Long id);

    /**
     * 批量删除仓库
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGoodByIds(Long[] ids);

    /**
     * 查询邮费信息
     * 
     * @param good 商品信息（包含尺寸代码和尺寸）
     * @return 邮费信息
     */
    public Good selectShippingFee(Good good);
}
