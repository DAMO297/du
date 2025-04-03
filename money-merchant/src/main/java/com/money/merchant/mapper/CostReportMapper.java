package com.money.merchant.mapper;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import com.money.merchant.domain.CostReport;

/**
 * 邮费报告Mapper接口
 * 
 * @author dm
 * @date 2025-04-03
 */
public interface CostReportMapper 
{
    /**
     * 查询邮费报告
     * 
     * @param id 邮费报告主键
     * @return 邮费报告
     */
    public CostReport selectCostReportById(Long id);

    /**
     * 查询邮费报告列表
     * 
     * @param costReport 邮费报告
     * @return 邮费报告集合
     */
    public List<CostReport> selectCostReportList(@Param("costReport") CostReport costReport);

    /**
     * 新增邮费报告
     * 
     * @param costReport 邮费报告
     * @return 结果
     */
    public int insertCostReport(CostReport costReport);

    /**
     * 修改邮费报告
     * 
     * @param costReport 邮费报告
     * @return 结果
     */
    public int updateCostReport(CostReport costReport);

    /**
     * 删除邮费报告
     * 
     * @param id 邮费报告主键
     * @return 结果
     */
    public int deleteCostReportById(Long id);

    /**
     * 批量删除邮费报告
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostReportByIds(Long[] ids);
    
    /**
     * 统计特定时间段的邮费总额
     *
     * @param userId 用户ID
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 邮费总额
     */
    public BigDecimal calculateTotalCost(
        @Param("userId") Long userId, 
        @Param("deptId") Long deptId, 
        @Param("startDate") Date startDate, 
        @Param("endDate") Date endDate
    );
    
    /**
     * 获取特定时间段的邮费统计
     *
     * @param userId 用户ID
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果，包含总额、平均值、最大值、订单数等
     */
    public Map<String, Object> getCostStatistics(
        @Param("userId") Long userId,
        @Param("deptId") Long deptId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
    
    /**
     * 获取异常邮费记录
     *
     * @param userId 用户ID
     * @param deptId 部门ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param threshold 最大阈值金额
     * @param minThreshold 最小阈值金额
     * @return 异常邮费记录列表
     */
    @MapKey("id")
    public Map<String, Map<String, Object>> getAbnormalCostRecords(
        @Param("userId") Long userId,
        @Param("deptId") Long deptId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        @Param("threshold") BigDecimal threshold,
        @Param("minThreshold") BigDecimal minThreshold
    );

    /**
     * 根据周期查询邮费报告
     *
     * @param userId 用户ID
     * @param deptId 部门ID
     * @param reportType 报告类型
     * @param reportPeriod 报告周期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 邮费报告
     */
    public CostReport selectCostReportByPeriod(
        @Param("userId") Long userId,
        @Param("deptId") Long deptId,
        @Param("reportType") Integer reportType,
        @Param("reportPeriod") Integer reportPeriod,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    /**
     * 获取cost表记录数和示例记录
     * 
     * @param userId 用户ID
     * @return 表信息
     */
    @MapKey("id")
    public Map<String, Object> getCostTableInfo(@Param("userId") Long userId);

    /**
     * 获取所有成本记录，用于测试
     * 
     * @return 成本记录列表
     */
    public List<Map<String, Object>> getAllCostRecords();
} 