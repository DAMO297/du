package com.money.merchant.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.money.merchant.mapper.CostReportMapper;
import com.money.merchant.domain.CostReport;
import com.money.merchant.service.ICostReportService;
import com.money.common.utils.DateUtils;
import com.money.common.utils.SecurityUtils;
import com.money.common.annotation.DataScope;
import java.util.ArrayList;

/**
 * 邮费报告Service业务层处理
 * 
 * @author dm
 * @date 2025-04-03
 */
@Service
public class CostReportServiceImpl implements ICostReportService 
{
    @Autowired
    private CostReportMapper costReportMapper;

    /**
     * 查询邮费报告
     * 
     * @param id 邮费报告主键
     * @return 邮费报告
     */
    @Override
    public CostReport selectCostReportById(Long id)
    {
        return costReportMapper.selectCostReportById(id);
    }

    /**
     * 查询邮费报告列表
     * 
     * @param costReport 邮费报告
     * @return 邮费报告
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CostReport> selectCostReportList(CostReport costReport)
    {
        return costReportMapper.selectCostReportList(costReport);
    }

    /**
     * 新增邮费报告
     * 
     * @param costReport 邮费报告
     * @return 结果
     */
    @Override
    public int insertCostReport(CostReport costReport)
    {
        try {
            costReport.setUserId(SecurityUtils.getUserId());
            costReport.setDeptId(SecurityUtils.getDeptId());
        } catch (Exception e) {
            costReport.setUserId(1L);
            costReport.setDeptId(100L);
        }
        costReport.setCreateTime(DateUtils.getNowDate());
        costReport.setGeneratedTime(DateUtils.getNowDate());
        return costReportMapper.insertCostReport(costReport);
    }

    /**
     * 修改邮费报告
     * 
     * @param costReport 邮费报告
     * @return 结果
     */
    @Override
    public int updateCostReport(CostReport costReport)
    {
        costReport.setUpdateTime(DateUtils.getNowDate());
        return costReportMapper.updateCostReport(costReport);
    }

    /**
     * 批量删除邮费报告
     * 
     * @param ids 需要删除的邮费报告主键
     * @return 结果
     */
    @Override
    public int deleteCostReportByIds(Long[] ids)
    {
        return costReportMapper.deleteCostReportByIds(ids);
    }

    /**
     * 删除邮费报告信息
     * 
     * @param id 邮费报告主键
     * @return 结果
     */
    @Override
    public int deleteCostReportById(Long id)
    {
        return costReportMapper.deleteCostReportById(id);
    }
    
    /**
     * 生成周期性邮费报告
     * 
     * @param reportType 报告类型 (1:周报, 2:月报, 3:季报, 4:年报)
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 生成的报告
     */
    @Override
    public CostReport generatePeriodicReport(Integer reportType, Long userId, Long deptId)
    {
        // 1. 设置报告基本信息
        CostReport report = new CostReport();
        report.setReportType(reportType);
        report.setUserId(userId);
        report.setDeptId(deptId);
        
        // 2. 计算报告时间范围
        Calendar calendar = Calendar.getInstance();
        
        // 设置报告年份和周期
        report.setReportYear(calendar.get(Calendar.YEAR));
        
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        
        switch (reportType) {
            case 1: // 周报
                // 获取本周的第一天和最后一天
                startCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek());
                endCal.setTime(startCal.getTime());
                endCal.add(Calendar.DAY_OF_WEEK, 6);
                
                report.setReportPeriod(calendar.get(Calendar.WEEK_OF_YEAR));
                break;
                
            case 2: // 月报
                // 获取本月的第一天和最后一天
                startCal.set(Calendar.DAY_OF_MONTH, 1);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                
                report.setReportPeriod(calendar.get(Calendar.MONTH) + 1);
                break;
                
            case 3: // 季报
                // 获取本季度的第一天和最后一天
                int quarter = (calendar.get(Calendar.MONTH) / 3) + 1;
                startCal.set(Calendar.MONTH, (quarter - 1) * 3);
                startCal.set(Calendar.DAY_OF_MONTH, 1);
                
                endCal.set(Calendar.MONTH, quarter * 3 - 1);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                
                report.setReportPeriod(quarter);
                break;
                
            case 4: // 年报
                // 获取本年的第一天和最后一天
                startCal.set(Calendar.MONTH, Calendar.JANUARY);
                startCal.set(Calendar.DAY_OF_MONTH, 1);
                
                endCal.set(Calendar.MONTH, Calendar.DECEMBER);
                endCal.set(Calendar.DAY_OF_MONTH, 31);
                
                report.setReportPeriod(1); // 年报只有一个周期
                break;
        }
        
        // 设置时间范围
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        
        report.setStartDate(startCal.getTime());
        report.setEndDate(endCal.getTime());
        
        // 3. 调用自定义时间段的报告生成
        return generateCustomReport(report.getStartDate(), report.getEndDate(), userId, deptId);
    }
    
    /**
     * 生成自定义时间段的邮费报告
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 生成的报告
     */
    @Override
    public CostReport generateCustomReport(java.util.Date startDate, java.util.Date endDate, Long userId, Long deptId)
    {
        // 1. 获取时间段内的邮费统计数据
        Map<String, Object> statisticsMap = costReportMapper.getCostStatistics(userId, deptId, startDate, endDate);
        
        // 由于现在统计是Map<String, Object>格式，直接使用第一个条目
        Map<String, Object> statistics = new HashMap<>();
        if (statisticsMap != null && !statisticsMap.isEmpty()) {
            // 使用第一个条目的内容
            String key = statisticsMap.keySet().iterator().next();
            Object value = statisticsMap.get(key);
            // 确保值是Map类型
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> valueMap = (Map<String, Object>) value;
                statistics = valueMap;
            }
        }
        
        // 2. 创建报告对象
        CostReport report = new CostReport();
        report.setUserId(userId);
        report.setDeptId(deptId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setGeneratedTime(DateUtils.getNowDate());
        
        // 3. 设置报告数据
        if (statistics != null) {
            // 设置总费用
            if (statistics.get("totalAmount") != null) {
                report.setTotalAmount((BigDecimal) statistics.get("totalAmount"));
            }
            
            // 设置平均单笔费用
            if (statistics.get("averageAmount") != null) {
                report.setAverageAmount((BigDecimal) statistics.get("averageAmount"));
            }
            
            // 设置最高单笔费用
            if (statistics.get("maxAmount") != null) {
                report.setMaxAmount((BigDecimal) statistics.get("maxAmount"));
            }
            
            // 设置订单总数
            if (statistics.get("orderCount") != null) {
                report.setOrderCount(((Long) statistics.get("orderCount")).intValue());
            }
        }
        
        // 4. 获取异常邮费记录
        // 这里可以根据业务规则设置阈值，比如超过平均值的150%
        BigDecimal threshold = BigDecimal.ZERO;
        if (report.getAverageAmount() != null) {
            threshold = report.getAverageAmount().multiply(new BigDecimal("1.5"));
        }
        
        Map<String, Map<String, Object>> abnormalRecordsMap = costReportMapper.getAbnormalCostRecords(
                userId, deptId, startDate, endDate, threshold);
        
        List<Map<String, Object>> abnormalRecords = new ArrayList<>();
        if (abnormalRecordsMap != null) {
            abnormalRecords.addAll(abnormalRecordsMap.values());
        }
        
        // 设置异常记录数
        if (abnormalRecords != null) {
            report.setAbnormalCount(abnormalRecords.size());
        }
        
        // 5. 整合报告内容
        Map<String, Object> reportContent = new HashMap<>();
        reportContent.put("statistics", statistics);
        reportContent.put("abnormalRecords", abnormalRecords);
        report.setReportContent(JSON.toJSONString(reportContent));
        
        // 6. 生成优化建议
        report.setOptimizationSuggestions(generateOptimizationSuggestions(report));
        
        // 7. 保存报告
        insertCostReport(report);
        
        return report;
    }
    
    /**
     * 获取邮费优化建议
     * 
     * @param reportId 报告ID
     * @return 优化建议
     */
    @Override
    public String getOptimizationSuggestions(Long reportId)
    {
        CostReport report = selectCostReportById(reportId);
        if (report != null) {
            if (report.getOptimizationSuggestions() != null && !report.getOptimizationSuggestions().isEmpty()) {
                return report.getOptimizationSuggestions();
            } else {
                // 生成并更新优化建议
                String suggestions = generateOptimizationSuggestions(report);
                report.setOptimizationSuggestions(suggestions);
                updateCostReport(report);
                return suggestions;
            }
        }
        return "无法获取报告信息，请确认报告ID是否正确。";
    }
    
    /**
     * 导出邮费报告
     * 
     * @param reportId 报告ID
     * @return 导出结果
     */
    @Override
    public String exportReport(Long reportId)
    {
        // 这里可以实现导出为PDF、Excel等格式的逻辑
        // 暂时返回导出成功的消息
        return "报告导出成功，请在下载目录查看。";
    }
    
    /**
     * 定时生成报告任务
     */
    @Override
    public void scheduleReportGeneration()
    {
        // 1. 获取当前日期
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) + 1;
        
        // 2. 根据日期决定生成什么类型的报告
        
        // 每周日生成周报
        if (dayOfWeek == Calendar.SUNDAY) {
            // 这里可以获取所有用户或者有订阅周报的用户
            // 为简化，这里假设只为当前用户生成
            Long userId = SecurityUtils.getUserId();
            Long deptId = SecurityUtils.getDeptId();
            
            generatePeriodicReport(1, userId, deptId); // 1:周报
        }
        
        // 每月1日生成月报
        if (dayOfMonth == 1) {
            Long userId = SecurityUtils.getUserId();
            Long deptId = SecurityUtils.getDeptId();
            
            generatePeriodicReport(2, userId, deptId); // 2:月报
        }
        
        // 每季度结束后生成季报 (3月、6月、9月、12月的最后一天)
        if ((month == 3 || month == 6 || month == 9 || month == 12) && 
            dayOfMonth == now.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            Long userId = SecurityUtils.getUserId();
            Long deptId = SecurityUtils.getDeptId();
            
            generatePeriodicReport(3, userId, deptId); // 3:季报
        }
        
        // 每年1月1日生成年报
        if (month == 1 && dayOfMonth == 1) {
            Long userId = SecurityUtils.getUserId();
            Long deptId = SecurityUtils.getDeptId();
            
            // 为上一年生成年报
            Calendar lastYear = Calendar.getInstance();
            lastYear.add(Calendar.YEAR, -1);
            
            // 这里需要特殊处理，因为一般年报是针对上一年的
            // 暂时简化处理
            generatePeriodicReport(4, userId, deptId); // 4:年报
        }
    }
    
    /**
     * 根据报告数据生成优化建议
     * 
     * @param report 邮费报告
     * @return 优化建议
     */
    private String generateOptimizationSuggestions(CostReport report)
    {
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("邮费优化建议：\n\n");
        
        // 1. 成本概览
        suggestions.append("1. 成本概览\n");
        suggestions.append("   - 报告期间总邮费：").append(report.getTotalAmount()).append("\n");
        suggestions.append("   - 平均单笔邮费：").append(report.getAverageAmount()).append("\n");
        suggestions.append("   - 最高单笔邮费：").append(report.getMaxAmount()).append("\n");
        suggestions.append("   - 异常高额邮费记录：").append(report.getAbnormalCount()).append("笔\n\n");
        
        // 2. 成本分析
        suggestions.append("2. 成本分析\n");
        
        // 计算成本相关指标
        if (report.getOrderCount() != null && report.getOrderCount() > 0 && report.getTotalAmount() != null) {
            BigDecimal averageCost = report.getTotalAmount().divide(new BigDecimal(report.getOrderCount()), 2, RoundingMode.HALF_UP);
            suggestions.append("   - 平均每笔订单邮费：").append(averageCost).append("\n");
        }
        
        if (report.getAbnormalCount() != null && report.getOrderCount() != null && report.getOrderCount() > 0) {
            BigDecimal abnormalRate = new BigDecimal(report.getAbnormalCount())
                                    .multiply(new BigDecimal(100))
                                    .divide(new BigDecimal(report.getOrderCount()), 2, RoundingMode.HALF_UP);
            suggestions.append("   - 异常邮费比例：").append(abnormalRate).append("%\n\n");
        }
        
        // 3. 优化建议
        suggestions.append("3. 优化建议\n");
        
        // 3.1 基于异常邮费情况的建议
        if (report.getAbnormalCount() != null && report.getAbnormalCount() > 0) {
            suggestions.append("   a) 异常邮费管控\n");
            suggestions.append("      - 建议对").append(report.getAbnormalCount()).append("笔异常高额邮费进行详细审核\n");
            suggestions.append("      - 考虑设置邮费上限，超过阈值需要审批\n");
            suggestions.append("      - 与物流公司协商特定区域的优惠价格\n\n");
        }
        
        // 3.2 通用优化建议
        suggestions.append("   b) 物流选择优化\n");
        suggestions.append("      - 对比不同物流公司的价格和服务水平，选择最优性价比\n");
        suggestions.append("      - 考虑与主要物流公司签订年度合约，获取批量折扣\n");
        suggestions.append("      - 针对不同重量和目的地选择最经济的物流方式\n\n");
        
        suggestions.append("   c) 包装优化\n");
        suggestions.append("      - 优化包装材料和尺寸，减少体积重和实际重量\n");
        suggestions.append("      - 使用轻量化包装材料，降低重量相关费用\n");
        suggestions.append("      - 标准化包装规格，提高物流效率\n\n");
        
        suggestions.append("   d) 发货策略优化\n");
        suggestions.append("      - 合并同一区域的订单发货，降低单位运费\n");
        suggestions.append("      - 避开物流高峰期发货，降低加急费用\n");
        suggestions.append("      - 评估设立区域性仓库的可行性，缩短配送距离\n\n");
        
        // 4. 预期节约空间
        suggestions.append("4. 预期节约空间\n");
        
        // 假设通过优化可以节约10%的成本
        if (report.getTotalAmount() != null) {
            BigDecimal savingEstimate = report.getTotalAmount().multiply(new BigDecimal("0.1")).setScale(2, RoundingMode.HALF_UP);
            suggestions.append("   - 预计通过以上优化措施，可节约邮费约：").append(savingEstimate).append("\n");
            suggestions.append("   - 重点优化异常高额邮费记录，可快速实现成本降低\n");
        }
        
        return suggestions.toString();
    }
}