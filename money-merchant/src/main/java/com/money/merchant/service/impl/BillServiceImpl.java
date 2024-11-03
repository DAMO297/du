package com.money.merchant.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.money.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.money.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.money.merchant.domain.Good;
import com.money.merchant.mapper.BillMapper;
import com.money.merchant.domain.Bill;
import com.money.merchant.service.IBillService;

/**
 * 账单Service业务层处理
 * 
 * @author dm
 * @date 2024-10-27
 */
@Service
public class BillServiceImpl implements IBillService 
{
    @Autowired
    private BillMapper billMapper;

    /**
     * 查询账单
     * 
     * @param id 账单主键
     * @return 账单
     */
    @Override
    public Bill selectBillById(Long id)
    {
        return billMapper.selectBillById(id);
    }

    /**
     * 查询账单列表
     * 
     * @param bill 账单
     * @return 账单
     */
    @Override
    public List<Bill> selectBillList(Bill bill)
    {
        return billMapper.selectBillList(bill);
    }

    /**
     * 新增账单
     * 
     * @param bill 账单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBill(Bill bill)
    {
        bill.setCreateTime(DateUtils.getNowDate());
        int rows = billMapper.insertBill(bill);
        insertGood(bill);
        return rows;
    }

    /**
     * 修改账单
     * 
     * @param bill 账单
     * @return 结果
     */
    @Transactional
    @Override
    public int updateBill(Bill bill)
    {
        billMapper.deleteGoodByProfit(bill.getId());
        insertGood(bill);
        return billMapper.updateBill(bill);
    }

    /**
     * 批量删除账单
     * 
     * @param ids 需要删除的账单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBillByIds(Long[] ids)
    {
        billMapper.deleteGoodByProfits(ids);
        return billMapper.deleteBillByIds(ids);
    }

    /**
     * 删除账单信息
     * 
     * @param id 账单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBillById(Long id)
    {
        billMapper.deleteGoodByProfit(id);
        return billMapper.deleteBillById(id);
    }

    /**
     * 新增仓库信息
     * 
     * @param bill 账单对象
     */
    public void insertGood(Bill bill)
    {
        List<Good> goodList = bill.getGoodList();
        Long id = bill.getId();
        if (StringUtils.isNotNull(goodList))
        {
            List<Good> list = new ArrayList<Good>();
            for (Good good : goodList)
            {
                good.setProfit(BigDecimal.valueOf(id));
                list.add(good);
            }
            if (list.size() > 0)
            {
                billMapper.batchGood(list);
            }
        }
    }
}
