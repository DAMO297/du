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
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 邮费报告Service业务层处理
 * 
 * @author dm
 * @date 2025-04-03
 */
@Service
public class CostReportServiceImpl implements ICostReportService 
{
    private static final Logger logger = LoggerFactory.getLogger(CostReportServiceImpl.class);

    @Autowired
    private CostReportMapper costReportMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        
        // 检查是否已存在相同周期的报告
        CostReport existingReport = costReportMapper.selectCostReportByPeriod(
            userId, deptId, reportType, report.getReportPeriod(), 
            report.getStartDate(), report.getEndDate()
        );
        
        if (existingReport != null) {
            return existingReport;
        }
        
        // 3. 调用自定义时间段的报告生成
        return generateCustomReport(report.getStartDate(), report.getEndDate(), userId, deptId);
    }
    
    /**
     * 生成自定义报告
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param userId    用户ID
     * @param deptId    部门ID
     * @return 成本报告
     */
    @Override
    public CostReport generateCustomReport(Date startDate, Date endDate, Long userId, Long deptId) {
        logger.info("生成自定义报告，开始日期: {}, 结束日期: {}, 用户ID: {}, 部门ID: {}", startDate, endDate, userId, deptId);
        
        try {
            // 确保时间范围包含完整的一天
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            
            startDate = startCal.getTime();
            endDate = endCal.getTime();
            
            logger.info("调整后的查询时间范围: {} 至 {}", startDate, endDate);
            
            // 先查询数据库中是否真的有记录，并显示详细日期
            try {
                String diagSql = "SELECT id, user_id, DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as formatted_time, " +
                               "create_time as raw_time FROM tb_cost WHERE user_id = ? " +
                               "ORDER BY create_time";
                List<Map<String, Object>> diagResults = jdbcTemplate.queryForList(diagSql, userId);
                
                logger.info("数据库中此用户的记录总数: {} 条", diagResults.size());
                if (diagResults.size() > 0) {
                    logger.info("数据库记录示例 (最多显示5条):");
                    int count = 0;
                    for (Map<String, Object> row : diagResults) {
                        if (count++ < 5) {
                            logger.info("ID: {}, 用户ID: {}, 格式化时间: {}, 原始时间: {}", 
                                      row.get("id"), row.get("user_id"), 
                                      row.get("formatted_time"), row.get("raw_time"));
                        }
                    }
                    
                    // 检查是否有记录在指定日期范围内
                    String countSql = "SELECT COUNT(*) FROM tb_cost WHERE user_id = ? AND " +
                                     "DATE(create_time) BETWEEN DATE(?) AND DATE(?)";
                    Integer dateOnlyCount = jdbcTemplate.queryForObject(countSql, Integer.class, 
                                          userId, startDate, endDate);
                    
                    logger.info("仅使用日期部分查询范围内记录数: {}", dateOnlyCount);
                    
                    // 尝试使用其他表达式
                    String exactSql = "SELECT COUNT(*) FROM tb_cost WHERE user_id = ? AND " +
                                    "create_time BETWEEN ? AND ?";
                    Integer exactCount = jdbcTemplate.queryForObject(exactSql, Integer.class, 
                                       userId, startDate, endDate);
                    
                    logger.info("使用精确时间戳查询范围内记录数: {}", exactCount);
                    
                    // 使用字符串格式比较
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startDateStr = sdf.format(startDate);
                    String endDateStr = sdf.format(endDate);
                    
                    String stringSql = "SELECT COUNT(*) FROM tb_cost WHERE user_id = ? AND " +
                                     "DATE_FORMAT(create_time, '%Y-%m-%d') BETWEEN ? AND ?";
                    Integer stringCount = jdbcTemplate.queryForObject(stringSql, Integer.class, 
                                        userId, startDateStr, endDateStr);
                    
                    logger.info("使用字符串格式日期查询范围内记录数: {}", stringCount);
                }
            } catch (Exception e) {
                logger.error("诊断查询失败", e);
            }
            
            // 获取时间段内的邮费统计数据
            Map<String, Object> statistics = costReportMapper.getCostStatistics(userId, deptId, startDate, endDate);
            logger.info("统计查询结果: {}", statistics);
            
            // 检查是否有数据
            if (statistics == null || 
                statistics.isEmpty() || 
                !statistics.containsKey("orderCount") || 
                statistics.get("orderCount") == null || 
                (statistics.get("orderCount") instanceof Number && ((Number)statistics.get("orderCount")).intValue() == 0)) {
                
                // 尝试直接执行SQL语句查询，跳过MyBatis
                try {
                    // 使用字符串格式比较
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startDateStr = sdf.format(startDate);
                    String endDateStr = sdf.format(endDate);
                    
                    String sql = "SELECT COUNT(*) FROM tb_cost WHERE " + 
                               "DATE_FORMAT(create_time, '%Y-%m-%d') BETWEEN ? AND ? " + 
                               "AND user_id = ?";
                    
                    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
                                   startDateStr, endDateStr, userId);
                    
                    logger.info("直接SQL查询结果(使用字符串日期): count = {}", count);
                    
                    if (count != null && count > 0) {
                        logger.info("数据库中有数据，但MyBatis映射返回空结果，使用字符串查询方法获取统计");
                        // 直接执行SQL获取统计数据
                        String statsSql = 
                            "SELECT " +
                            "  COUNT(*) as orderCount, " +
                            "  IFNULL(SUM(cost_amount), 0) as totalAmount, " +
                            "  IFNULL(AVG(cost_amount), 0) as averageAmount, " +
                            "  IFNULL(MAX(cost_amount), 0) as maxAmount, " +
                            "  IFNULL(MIN(cost_amount), 0) as minAmount " +
                            "FROM tb_cost " +
                            "WHERE DATE_FORMAT(create_time, '%Y-%m-%d') BETWEEN ? AND ? " +
                            "AND user_id = ?";
                        
                        statistics = jdbcTemplate.queryForMap(statsSql, startDateStr, endDateStr, userId);
                        logger.info("直接SQL查询统计结果: {}", statistics);
                    } else {
                        logger.info("指定时间范围内没有数据，返回null");
                        return null; // 没有数据则返回null
                    }
                } catch (Exception e) {
                    logger.error("直接执行SQL查询时出错", e);
                    logger.info("指定时间范围内没有数据，返回null");
                    return null;
                }
            }
            
            // 创建报告对象
            CostReport report = new CostReport();
            report.setUserId(userId);
            report.setDeptId(deptId);
            report.setReportType(1); // 1表示正常报告
            report.setReportPeriod(1); // 1表示自定义周期
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setCreateTime(new Date());
            report.setReportStatus(0); // 0表示正常状态
            
            // 设置报告名称
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            report.setReportName("成本报告 - " + dateFormat.format(startDate) + " 至 " + dateFormat.format(endDate));
            
            // 设置统计数据（使用安全方法获取值）
            report.setTotalCost(getSafeDecimal(statistics, "totalAmount", BigDecimal.ZERO));
            report.setAverageCost(getSafeDecimal(statistics, "averageAmount", BigDecimal.ZERO));
            report.setItemCount(getSafeInt(statistics, "orderCount", 0));
            
            // 其他统计数据可以存储在report_content中
            if (statistics.containsKey("totalWeight") || 
                statistics.containsKey("averageWeight") || 
                statistics.containsKey("logisticsCompanyCount") || 
                statistics.containsKey("provinceCount")) {
                try {
                    Map<String, Object> extraStats = new HashMap<>();
                    if (statistics.containsKey("totalWeight")) {
                        extraStats.put("totalWeight", statistics.get("totalWeight"));
                    }
                    if (statistics.containsKey("averageWeight")) {
                        extraStats.put("averageWeight", statistics.get("averageWeight"));
                    }
                    if (statistics.containsKey("logisticsCompanyCount")) {
                        extraStats.put("logisticsCompanyCount", statistics.get("logisticsCompanyCount"));
                    }
                    if (statistics.containsKey("provinceCount")) {
                        extraStats.put("provinceCount", statistics.get("provinceCount"));
                    }
                    report.setReportContent(JSON.toJSONString(extraStats));
                } catch (Exception e) {
                    logger.warn("设置额外统计数据到reportContent时出错", e);
                }
            }
            
            // 保存报告
            int rows = costReportMapper.insertCostReport(report);
            if (rows <= 0) {
                logger.error("保存报告失败");
                throw new RuntimeException("保存报告失败");
            }
            
            logger.info("报告生成成功: {}", report);
            return report;
        } catch (Exception e) {
            logger.error("生成报告出错", e);
            throw new RuntimeException("生成报告时发生错误: " + e.getMessage(), e);
        }
    }
    
    /**
     * 安全获取BigDecimal值
     */
    private BigDecimal getSafeDecimal(Map<String, Object> map, String key, BigDecimal defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return defaultValue;
        }
        
        Object value = map.get(key);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else {
            try {
                return new BigDecimal(value.toString());
            } catch (Exception e) {
                logger.warn("无法将值转换为BigDecimal: " + value, e);
                return defaultValue;
            }
        }
    }
    
    /**
     * 安全获取Integer值
     */
    private Integer getSafeInt(Map<String, Object> map, String key, Integer defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return defaultValue;
        }
        
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (Exception e) {
                logger.warn("无法将值转换为Integer: " + value, e);
                return defaultValue;
            }
        }
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
        suggestions.append("   - 报告期间总邮费：").append(report.getTotalCost()).append("\n");
        suggestions.append("   - 平均单笔邮费：").append(report.getAverageCost()).append("\n");
        suggestions.append("   - 异常高额邮费记录：").append(report.getAbnormalCount()).append("笔\n\n");
        
        // 2. 成本分析
        suggestions.append("2. 成本分析\n");
        
        // 计算成本相关指标
        if (report.getItemCount() != null && report.getItemCount() > 0 && report.getTotalCost() != null) {
            BigDecimal averageCost = report.getTotalCost().divide(new BigDecimal(report.getItemCount()), 2, RoundingMode.HALF_UP);
            suggestions.append("   - 平均每笔订单邮费：").append(averageCost).append("\n");
        }
        
        if (report.getAbnormalCount() != null && report.getItemCount() != null && report.getItemCount() > 0) {
            BigDecimal abnormalRate = new BigDecimal(report.getAbnormalCount())
                                    .multiply(new BigDecimal(100))
                                    .divide(new BigDecimal(report.getItemCount()), 2, RoundingMode.HALF_UP);
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
        suggestions.append("      - 针对不同重量和目的地选择最经济的物流方式\n");
        suggestions.append("      - 优化包装材料和尺寸，减少体积重和实际重量\n");
        suggestions.append("      - 使用轻量化包装材料，降低重量相关费用\n");
        suggestions.append("      - 标准化包装规格，提高物流效率\n\n");
        
        suggestions.append("   c) 包装优化\n");
        suggestions.append("      - 标准化包装规格，提高物流效率\n\n");
        
        suggestions.append("   d) 发货策略优化\n");
        suggestions.append("      - 合并同一区域的订单发货，降低单位运费\n");
        suggestions.append("      - 避开物流高峰期发货，降低加急费用\n");
        suggestions.append("      - 评估设立区域性仓库的可行性，缩短配送距离\n\n");
        
        // 4. 预期节约空间
        suggestions.append("4. 预期节约空间\n");
        
        // 假设通过优化可以节约10%的成本
        if (report.getTotalCost() != null) {
            BigDecimal savingEstimate = report.getTotalCost().multiply(new BigDecimal("0.1")).setScale(2, RoundingMode.HALF_UP);
            suggestions.append("   - 预计通过以上优化措施，可节约邮费约：").append(savingEstimate).append("\n");
            suggestions.append("   - 重点优化异常高额邮费记录，可快速实现成本降低\n");
        }
        
        return suggestions.toString();
    }

    /**
     * 测试指定日期范围内是否有数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @return 测试结果
     */
    @Override
    public String testDataAvailability(java.util.Date startDate, java.util.Date endDate, Long userId) 
    {
        StringBuilder result = new StringBuilder();
        
        // 调整日期时间，确保包含完整的天
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        
        startDate = startCal.getTime();
        endDate = endCal.getTime();
        
        result.append("测试时间范围: ").append(startDate).append(" 到 ").append(endDate).append("\n");
        result.append("用户ID: ").append(userId).append("\n\n");
        
        try {
            // 按时间范围和用户ID查询
            BigDecimal totalByBoth = costReportMapper.calculateTotalCost(userId, null, startDate, endDate);
            result.append("按时间范围和用户ID查询总成本: ").append(totalByBoth).append("\n\n");
            
            // 使用统计查询
            Map<String, Object> statistics = costReportMapper.getCostStatistics(userId, null, startDate, endDate);
            if (statistics != null) {
                result.append("统计查询结果:\n");
                for (Map.Entry<String, Object> entry : statistics.entrySet()) {
                    result.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            } else {
                result.append("统计查询结果: null\n");
            }
            
            // 获取所有记录
            List<Map<String, Object>> allRecords = getAllCostRecords();
            result.append("\n所有成本记录 (").append(allRecords.size()).append(" 条):\n");
            
            if (allRecords != null && allRecords.size() > 0) {
                int count = 0;
                for (Map<String, Object> record : allRecords) {
                    result.append("记录 ").append(++count).append(":\n");
                    for (Map.Entry<String, Object> entry : record.entrySet()) {
                        result.append("  ").append(entry.getKey()).append(": ").append(entry.getValue())
                            .append(entry.getValue() != null ? " (类型: " + entry.getValue().getClass().getName() + ")" : "")
                            .append("\n");
                    }
                    result.append("\n");
                    
                    if (count >= 5) {
                        result.append("... 更多记录省略 ...\n");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            result.append("执行查询出错: ").append(e.getMessage()).append("\n");
            result.append("错误详情: ").append(e.toString()).append("\n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                result.append("  at ").append(stackTrace[i]).append("\n");
            }
        }
        
        return result.toString();
    }

    /**
     * 检查表结构
     * 
     * @return 表结构信息
     */
    public String checkTableStructure() {
        StringBuilder result = new StringBuilder("表结构信息:");
        
        try {
            // 使用一个简单的方法来查询表信息
            Map<String, Object> tableInfo = costReportMapper.getCostTableInfo(1L);
            if (tableInfo != null && !tableInfo.isEmpty()) {
                result.append("\n可用字段 (来自示例记录):\n");
                for (Map.Entry<String, Object> entry : tableInfo.entrySet()) {
                    result.append("  ").append(entry.getKey());
                    if (entry.getValue() != null) {
                        result.append(" (类型: ").append(entry.getValue().getClass().getSimpleName()).append(")");
                    }
                    result.append("\n");
                }
            } else {
                result.append("\n未找到表记录，无法获取字段信息。");
            }
        } catch (Exception e) {
            result.append("\n获取表结构失败: ").append(e.getMessage());
        }
        
        return result.toString();
    }

    @Override
    public List<Map<String, Object>> getAllCostRecords() {
        return costReportMapper.getAllCostRecords();
    }
}