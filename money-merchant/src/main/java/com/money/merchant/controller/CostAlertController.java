package com.money.merchant.controller;

import java.util.List;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RestController;
import com.money.common.annotation.Log;
import com.money.common.core.controller.BaseController;
import com.money.common.core.domain.AjaxResult;
import com.money.common.enums.BusinessType;
import com.money.merchant.domain.CostAlert;
import com.money.merchant.service.ICostAlertService;
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;
import com.money.common.utils.SecurityUtils;

/**
 * 邮费异常提醒Controller
 * 
 * @author dm
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/merchant/costalert")
public class CostAlertController extends BaseController
{
    @Autowired
    private ICostAlertService costAlertService;

    /**
     * 查询邮费异常提醒列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:list')")
    @GetMapping("/list")
    public TableDataInfo list(CostAlert costAlert)
    {
        try {
            startPage();
            
            // 设置默认值
            if (costAlert.getUserId() == null) {
                try {
                    costAlert.setUserId(SecurityUtils.getUserId());
                } catch (Exception e) {
                    costAlert.setUserId(1L);
                }
            }
            
            List<CostAlert> list = costAlertService.selectCostAlertList(costAlert);
            return getDataTable(list);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常时返回空列表
            return getDataTable(new ArrayList<>());
        }
    }

    /**
     * 导出邮费异常提醒列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:export')")
    @Log(title = "邮费异常提醒", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CostAlert costAlert)
    {
        List<CostAlert> list = costAlertService.selectCostAlertList(costAlert);
        ExcelUtil<CostAlert> util = new ExcelUtil<CostAlert>(CostAlert.class);
        util.exportExcel(response, list, "邮费异常提醒数据");
    }

    /**
     * 获取邮费异常提醒详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(costAlertService.selectCostAlertById(id));
    }

    /**
     * 新增邮费异常提醒
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:add')")
    @Log(title = "邮费异常提醒", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CostAlert costAlert)
    {
        return toAjax(costAlertService.insertCostAlert(costAlert));
    }

    /**
     * 修改邮费异常提醒
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:edit')")
    @Log(title = "邮费异常提醒", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CostAlert costAlert)
    {
        return toAjax(costAlertService.updateCostAlert(costAlert));
    }

    /**
     * 删除邮费异常提醒
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:remove')")
    @Log(title = "邮费异常提醒", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(costAlertService.deleteCostAlertByIds(ids));
    }
    
    /**
     * 获取邮费节约建议
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:query')")
    @GetMapping("/saving-suggestions")
    public AjaxResult getCostSavingSuggestions()
    {
        String suggestions = costAlertService.generateCostSavingSuggestions(getUserId(), getDeptId());
        return success(suggestions);
    }
    
    /**
     * 检查累计邮费异常
     */
    @PreAuthorize("@ss.hasPermi('merchant:costalert:query')")
    @GetMapping("/check-aggregated")
    public AjaxResult checkAggregatedCostAlerts()
    {
        List<CostAlert> alerts = costAlertService.checkAggregatedCostAlerts(getUserId(), getDeptId());
        return success(alerts);
    }
} 