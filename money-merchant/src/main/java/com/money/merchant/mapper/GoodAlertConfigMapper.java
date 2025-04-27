package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.GoodAlertConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品预警配置Mapper接口
 */
@Mapper
public interface GoodAlertConfigMapper {
    /**
     * 查询预警配置
     */
    public GoodAlertConfig selectGoodAlertConfigById(Long id);

    /**
     * 查询预警配置列表
     */
    public List<GoodAlertConfig> selectGoodAlertConfigList(GoodAlertConfig config);

    /**
     * 新增预警配置
     */
    public int insertGoodAlertConfig(GoodAlertConfig config);

    /**
     * 修改预警配置
     */
    public int updateGoodAlertConfig(GoodAlertConfig config);

    /**
     * 删除预警配置
     */
    public int deleteGoodAlertConfigById(Long id);

    /**
     * 批量删除预警配置
     */
    public int deleteGoodAlertConfigByIds(Long[] ids);

    /**
     * 根据商品ID查询预警配置
     */
    public GoodAlertConfig selectGoodAlertConfigByGoodId(Long goodId);
} 