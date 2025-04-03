package com.money.merchant.service;

import java.util.List;
import java.util.Map;
import com.money.merchant.domain.CostReport;

/**
 * 邮费报告Service接口
 * 
 * @author dm
 * @date 2025-04-03
 */
public interface ICostReportService 
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
    public List<CostReport> selectCostReportList(CostReport costReport);

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
     * 批量删除邮费报告
     * 
     * @param ids 需要删除的邮费报告主键集合
     * @return 结果
     */
    public int deleteCostReportByIds(Long[] ids);

    /**
     * 删除邮费报告信息
     * 
     * @param id 邮费报告主键
     * @return 结果
     */
    public int deleteCostReportById(Long id);
    
    /**
     * 生成周期性邮费报告
     * 
     * @param reportType 报告类型 (1:周报, 2:月报, 3:季报, 4:年报)
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 生成的报告
     */
    public CostReport generatePeriodicReport(Integer reportType, Long userId, Long deptId);
    
    /**
     * 生成自定义时间段的邮费报告
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 生成的报告
     */
    public CostReport generateCustomReport(java.util.Date startDate, java.util.Date endDate, Long userId, Long deptId);
    
    /**
     * 获取邮费优化建议
     * 
     * @param reportId 报告ID
     * @return 优化建议
     */
    public String getOptimizationSuggestions(Long reportId);
    
    /**
     * 导出邮费报告
     * 
     * @param reportId 报告ID
     * @return 导出结果
     */
    public String exportReport(Long reportId);
    
    /**
     * 定时生成报告任务
     * 
     * @return 处理结果
     */
    public void scheduleReportGeneration();
    
    /**
     * 测试指定日期范围内是否有数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @return 测试结果
     */
    public String testDataAvailability(java.util.Date startDate, java.util.Date endDate, Long userId);
    
    /**
     * 检查表结构
     * 
     * @return 表结构信息
     */
    public String checkTableStructure();

    /**
     * 获取所有成本记录
     * 
     * @return 成本记录列表
     */
    public List<Map<String, Object>> getAllCostRecords();
} 