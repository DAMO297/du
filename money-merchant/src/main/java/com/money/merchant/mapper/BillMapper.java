package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.Bill;
import com.money.merchant.domain.Good;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单Mapper接口
 * 
 * @author dm
 * @date 2024-10-27
 */
@Mapper
public interface BillMapper 
{
    /**
     * 查询账单
     * 
     * @param id 账单主键
     * @return 账单
     */
    public Bill selectBillById(Long id);

    /**
     * 查询账单列表
     * 
     * @param bill 账单
     * @return 账单集合
     */
    public List<Bill> selectBillList(Bill bill);

    /**
     * 新增账单
     * 
     * @param bill 账单
     * @return 结果
     */
    public int insertBill(Bill bill);

    /**
     * 修改账单
     * 
     * @param bill 账单
     * @return 结果
     */
    public int updateBill(Bill bill);

    /**
     * 删除账单
     * 
     * @param id 账单主键
     * @return 结果
     */
    public int deleteBillById(Long id);

    /**
     * 批量删除账单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBillByIds(Long[] ids);

    /**
     * 批量删除仓库
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGoodByProfits(Long[] ids);
    
    /**
     * 批量新增仓库
     * 
     * @param goodList 仓库列表
     * @return 结果
     */
    public int batchGood(List<Good> goodList);
    

    /**
     * 通过账单主键删除仓库信息
     * 
     * @param id 账单ID
     * @return 结果
     */
    public int deleteGoodByProfit(Long id);
}
