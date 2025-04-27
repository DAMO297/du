package com.money.merchant.mapper;

import java.util.List;
import com.money.merchant.domain.GoodAlertRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品预警记录Mapper接口
 */
@Mapper
public interface GoodAlertRecordMapper {
    /**
     * 查询预警记录
     */
    public GoodAlertRecord selectGoodAlertRecordById(Long id);

    /**
     * 查询预警记录列表
     */
    public List<GoodAlertRecord> selectGoodAlertRecordList(GoodAlertRecord record);

    /**
     * 新增预警记录
     */
    public int insertGoodAlertRecord(GoodAlertRecord record);

    /**
     * 修改预警记录
     */
    public int updateGoodAlertRecord(GoodAlertRecord record);

    /**
     * 删除预警记录
     */
    public int deleteGoodAlertRecordById(Long id);

    /**
     * 批量删除预警记录
     */
    public int deleteGoodAlertRecordByIds(Long[] ids);

    /**
     * 查询未处理的预警记录
     */
    public List<GoodAlertRecord> selectUnhandledAlerts();

    /**
     * 根据商品ID查询预警记录
     */
    public List<GoodAlertRecord> selectAlertsByGoodId(Long goodId);
} 