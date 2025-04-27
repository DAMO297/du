package com.money.merchant.service.impl;

import java.util.List;
import com.money.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import com.money.merchant.mapper.GoodMapper;
import com.money.merchant.domain.Good;
import com.money.merchant.service.IGoodService;
import com.money.merchant.service.IGoodAlertService;
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
    
    @Autowired
    private IGoodAlertService goodAlertService;

    /**
     * 查询仓库
     * 
     * @param id 仓库主键
     * @return 仓库
     */
    @Override
    @Cacheable(value = "good", key = "#id")
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
    @Cacheable(value = "goodList", key = "#good.toString()")
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
    @CacheEvict(value = {"good", "goodList"}, allEntries = true)
    public int insertGood(Good good)
    {
        good.setCreateTime(DateUtils.getNowDate());
        // 设置默认状态为returned
        if (good.getStatus() == null) {
            good.setStatus("returned");
        }
        int rows = goodMapper.insertGood(good);
        System.out.println("Insert good result: rows=" + rows + ", id=" + good.getId() + ", status=" + good.getStatus());
        
        // 只有当商品状态为returned时才创建预警配置
        if (rows > 0 && good.getId() != null && "returned".equals(good.getStatus())) {
            // 直接调用服务方法创建默认预警配置
            int configResult = goodAlertService.createDefaultAlertConfig(good.getId());
            System.out.println("Create alert config result: " + configResult);
        }
        return rows;
    }

    /**
     * 修改仓库
     * 
     * @param good 仓库
     * @return 结果
     */
    @Override
    @CacheEvict(value = {"good", "goodList"}, allEntries = true)
    public int updateGood(Good good)
    {
        // 获取更新前的商品信息
        Good oldGood = goodMapper.selectGoodById(good.getId());
        
        // 更新商品信息
        int result = goodMapper.updateGood(good);
        
        // 如果商品状态从returned变为sold，执行利润预警检查
        if (result > 0 && oldGood != null && "returned".equals(oldGood.getStatus()) && "sold".equals(good.getStatus())) {
            goodAlertService.checkProfitAlert();
        }
        
        return result;
    }

    /**
     * 批量删除仓库
     * 
     * @param ids 需要删除的仓库主键
     * @return 结果
     */
    @Override
    @CacheEvict(value = {"good", "goodList"}, allEntries = true)
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
    @CacheEvict(value = {"good", "goodList"}, allEntries = true)
    public int deleteGoodById(Long id)
    {
        return goodMapper.deleteGoodById(id);
    }
}
