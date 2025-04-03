package com.money.merchant.controller;

import java.util.List;
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
    public AjaxResult generateCustomReport(@RequestParam Date startDate, @RequestParam Date endDate)
    {
        CostReport report = costReportService.generateCustomReport(startDate, endDate, getUserId(), getDeptId());
        return success(report);
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
} 