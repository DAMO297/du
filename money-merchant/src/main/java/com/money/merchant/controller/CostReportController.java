package com.money.merchant.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.money.common.annotation.Log;
import com.money.common.core.controller.BaseController;
import com.money.common.core.domain.AjaxResult;
import com.money.common.enums.BusinessType;
import com.money.merchant.domain.CostReport;
import com.money.merchant.service.ICostReportService;
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;
import java.text.SimpleDateFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;

/**
 * 邮费报告Controller
 * 
 * @author dm
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/merchant/costreport")
public class CostReportController extends BaseController
{
    @Autowired
    private ICostReportService costReportService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询邮费报告列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:list')")
    @GetMapping("/list")
    public TableDataInfo list(CostReport costReport)
    {
        startPage();
        List<CostReport> list = costReportService.selectCostReportList(costReport);
        return getDataTable(list);
    }

    /**
     * 导出邮费报告列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:export')")
    @Log(title = "邮费报告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CostReport costReport)
    {
        List<CostReport> list = costReportService.selectCostReportList(costReport);
        ExcelUtil<CostReport> util = new ExcelUtil<CostReport>(CostReport.class);
        util.exportExcel(response, list, "邮费报告数据");
    }

    /**
     * 获取邮费报告详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(costReportService.selectCostReportById(id));
    }

    /**
     * 新增邮费报告
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:add')")
    @Log(title = "邮费报告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CostReport costReport)
    {
        return toAjax(costReportService.insertCostReport(costReport));
    }

    /**
     * 修改邮费报告
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:edit')")
    @Log(title = "邮费报告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CostReport costReport)
    {
        return toAjax(costReportService.updateCostReport(costReport));
    }

    /**
     * 删除邮费报告
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:remove')")
    @Log(title = "邮费报告", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(costReportService.deleteCostReportByIds(ids));
    }
    
    /**
     * 生成周期性邮费报告
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:add')")
    @Log(title = "生成周期性邮费报告", businessType = BusinessType.INSERT)
    @PostMapping("/generate-periodic")
    public AjaxResult generatePeriodicReport(@RequestParam Integer reportType)
    {
        CostReport report = costReportService.generatePeriodicReport(reportType, getUserId(), getDeptId());
        return success(report);
    }
    
    /**
     * 生成自定义时间段的邮费报告
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:add')")
    @Log(title = "生成自定义邮费报告", businessType = BusinessType.INSERT)
    @PostMapping("/generate-custom")
    public AjaxResult generateCustomReport(@RequestBody Map<String, Object> params)
    {
        try {
            // 检查必要参数
            if (!params.containsKey("startDate") || !params.containsKey("endDate")) {
                return error("必须提供开始日期和结束日期参数");
            }
            
            // 输出原始参数信息，用于调试
            logger.info("接收到的参数: {}", params);
            
            // 日期参数处理
            Date startDate = null;
            Date endDate = null;
            
            try {
                Object startDateObj = params.get("startDate");
                Object endDateObj = params.get("endDate");
                
                logger.info("原始开始日期参数: {} (类型: {})", startDateObj, 
                            startDateObj != null ? startDateObj.getClass().getName() : "null");
                logger.info("原始结束日期参数: {} (类型: {})", endDateObj, 
                            endDateObj != null ? endDateObj.getClass().getName() : "null");
                
                if (startDateObj instanceof Number) {
                    long timestamp = ((Number) startDateObj).longValue();
                    startDate = new Date(timestamp);
                    logger.info("从数字转换开始日期: {} -> {}", timestamp, startDate);
                } else if (startDateObj instanceof String) {
                    String dateStr = (String) startDateObj;
                    try {
                        // 先尝试解析为长整数时间戳
                        long timestamp = Long.parseLong(dateStr);
                        startDate = new Date(timestamp);
                        logger.info("从字符串转换开始日期(时间戳): {} -> {}", dateStr, startDate);
                    } catch (NumberFormatException e) {
                        // 尝试按字符串解析日期
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        startDate = sdf.parse(dateStr);
                        logger.info("从字符串转换开始日期(日期格式): {} -> {}", dateStr, startDate);
                    }
                }
                
                if (endDateObj instanceof Number) {
                    long timestamp = ((Number) endDateObj).longValue();
                    endDate = new Date(timestamp);
                    logger.info("从数字转换结束日期: {} -> {}", timestamp, endDate);
                } else if (endDateObj instanceof String) {
                    String dateStr = (String) endDateObj;
                    try {
                        // 先尝试解析为长整数时间戳
                        long timestamp = Long.parseLong(dateStr);
                        endDate = new Date(timestamp);
                        logger.info("从字符串转换结束日期(时间戳): {} -> {}", dateStr, endDate);
                    } catch (NumberFormatException e) {
                        // 尝试按字符串解析日期
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        endDate = sdf.parse(dateStr);
                        logger.info("从字符串转换结束日期(日期格式): {} -> {}", dateStr, endDate);
                    }
                }
            } catch (Exception e) {
                logger.error("日期解析失败", e);
                return error("日期格式不正确: " + e.getMessage());
            }
            
            if (startDate == null || endDate == null) {
                return error("无法解析日期参数，请确保提供了正确的日期格式");
            }
            
            // 用户ID处理
            Long userId = null;
            
            if (params.containsKey("userId") && params.get("userId") != null) {
                if (params.get("userId") instanceof Number) {
                    userId = ((Number) params.get("userId")).longValue();
                } else if (params.get("userId") instanceof String) {
                    try {
                        userId = Long.parseLong((String) params.get("userId"));
                    } catch (NumberFormatException e) {
                        return error("用户ID格式不正确");
                    }
                }
            }
            
            // 如果未提供用户ID，则使用当前登录用户ID
            if (userId == null) {
                userId = getUserId();
            }
            
            Long deptId = getDeptId();
            
            logger.info("开始生成报告: 开始日期={}, 结束日期={}, 用户ID={}, 部门ID={}", 
                startDate, endDate, userId, deptId);
            
            // 先检查数据库中是否有记录，并输出一些诊断信息
            logger.info("检查数据库中的记录：");
            
            try {
                // 1. 获取所有记录总数
                Integer totalCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tb_cost WHERE user_id = ?", 
                    Integer.class, userId);
                logger.info("用户 {} 在数据库中的总记录数: {} 条", userId, totalCount);
                
                if (totalCount != null && totalCount > 0) {
                    // 2. 获取时间范围信息
                    Map<String, Object> timeRangeInfo = jdbcTemplate.queryForMap(
                        "SELECT MIN(create_time) as min_time, MAX(create_time) as max_time " +
                        "FROM tb_cost WHERE user_id = ?", userId);
                    logger.info("数据库记录的时间范围: 最早 {}, 最晚 {}", 
                              timeRangeInfo.get("min_time"), timeRangeInfo.get("max_time"));
                    
                    // 3. 使用不同的方式查询时间范围内的记录
                    // 3.1 使用精确时间戳
                    Integer exactCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tb_cost WHERE user_id = ? AND " +
                        "create_time BETWEEN ? AND ?", 
                        Integer.class, userId, startDate, endDate);
                    logger.info("使用精确时间范围查询结果: {} 条记录", exactCount);
                    
                    // 3.2 仅使用日期部分
                    Integer dateOnlyCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tb_cost WHERE user_id = ? AND " +
                        "DATE(create_time) BETWEEN DATE(?) AND DATE(?)", 
                        Integer.class, userId, startDate, endDate);
                    logger.info("仅使用日期部分查询结果: {} 条记录", dateOnlyCount);
                    
                    // 3.3 使用格式化字符串比较
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startStr = sdf.format(startDate);
                    String endStr = sdf.format(endDate);
                    
                    Integer strCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tb_cost WHERE user_id = ? AND " +
                        "DATE_FORMAT(create_time, '%Y-%m-%d') BETWEEN ? AND ?", 
                        Integer.class, userId, startStr, endStr);
                    logger.info("使用格式化日期字符串查询结果: {} 条记录", strCount);
                    
                    // 4. 如果字符串查询有结果但精确查询没有，使用字符串查询方式
                    if ((exactCount == null || exactCount == 0) && (strCount != null && strCount > 0)) {
                        logger.info("精确查询无结果但字符串查询有结果，使用字符串查询方式");
                        
                        // 使用字符串方式直接生成报告统计
                        Map<String, Object> statistics = jdbcTemplate.queryForMap(
                            "SELECT COUNT(*) as orderCount, " +
                            "IFNULL(SUM(cost_amount), 0) as totalAmount, " +
                            "IFNULL(AVG(cost_amount), 0) as averageAmount " +
                            "FROM tb_cost WHERE user_id = ? AND " +
                            "DATE_FORMAT(create_time, '%Y-%m-%d') BETWEEN ? AND ?", 
                            userId, startStr, endStr);
                        
                        logger.info("直接SQL统计结果: {}", statistics);
                        
                        // 创建报告对象
                        CostReport report = new CostReport();
                        report.setUserId(userId);
                        report.setDeptId(deptId);
                        report.setReportType(1); // 1表示自定义报告
                        report.setReportPeriod(1); // 1表示自定义周期
                        report.setStartDate(startDate);
                        report.setEndDate(endDate);
                        report.setCreateTime(new Date());
                        report.setReportStatus(0); // 0表示正常状态
                        
                        // 设置报告名称
                        report.setReportName("成本报告 - " + startStr + " 至 " + endStr);
                        
                        // 设置统计数据
                        Object totalAmount = statistics.get("totalAmount");
                        if (totalAmount instanceof Number) {
                            report.setTotalCost(new BigDecimal(totalAmount.toString()));
                        }
                        
                        Object avgAmount = statistics.get("averageAmount");
                        if (avgAmount instanceof Number) {
                            report.setAverageCost(new BigDecimal(avgAmount.toString()));
                        }
                        
                        Object count = statistics.get("orderCount");
                        if (count instanceof Number) {
                            report.setItemCount(((Number) count).intValue());
                        }
                        
                        // 保存报告
                        int result = costReportService.insertCostReport(report);
                        if (result > 0) {
                            logger.info("报告直接创建成功: {}", report);
                            return success(report);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("诊断查询出错", e);
            }
            
            // 使用正常方式生成报告
            CostReport report = costReportService.generateCustomReport(startDate, endDate, userId, deptId);
            
            // 处理无数据情况
            if (report == null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return error(String.format("在 %s 至 %s 的日期范围内没有找到成本数据", 
                    dateFormat.format(startDate), dateFormat.format(endDate)));
            }
            
            return success(report);
        } catch (Exception e) {
            logger.error("生成报告失败", e);
            return error("生成报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取邮费优化建议
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:query')")
    @GetMapping("/optimization-suggestions/{reportId}")
    public AjaxResult getOptimizationSuggestions(@PathVariable("reportId") Long reportId)
    {
        String suggestions = costReportService.getOptimizationSuggestions(reportId);
        return success(suggestions);
    }
    
    /**
     * 导出邮费报告
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:export')")
    @Log(title = "导出邮费报告", businessType = BusinessType.EXPORT)
    @GetMapping("/export-report/{reportId}")
    public AjaxResult exportReport(@PathVariable("reportId") Long reportId)
    {
        String result = costReportService.exportReport(reportId);
        return success(result);
    }
    
    /**
     * 测试指定日期范围内是否有数据
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:query')")
    @GetMapping("/test-data")
    public AjaxResult testDataAvailability(@RequestParam Date startDate, @RequestParam Date endDate)
    {
        String result = costReportService.testDataAvailability(startDate, endDate, getUserId());
        return success(result);
    }

    /**
     * 查看数据库表结构
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:query')")
    @GetMapping("/check-structure")
    public AjaxResult checkTableStructure()
    {
        String result = costReportService.checkTableStructure();
        return success(result);
    }

    /**
     * 获取所有成本记录（用于测试）
     */
    @PreAuthorize("@ss.hasPermi('merchant:costreport:query')")
    @GetMapping("/all-records")
    public AjaxResult getAllCostRecords()
    {
        List<Map<String, Object>> records = costReportService.getAllCostRecords();
        return success(records);
    }
} 