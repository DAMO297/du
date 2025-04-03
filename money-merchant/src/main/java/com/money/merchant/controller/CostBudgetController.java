package com.money.merchant.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.money.merchant.domain.CostBudget;
import com.money.merchant.service.ICostBudgetService;
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;

/**
 * 邮费预算Controller
 * 
 * @author dm
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/merchant/costbudget")
public class CostBudgetController extends BaseController
{
    @Autowired
    private ICostBudgetService costBudgetService;

    /**
     * 查询邮费预算列表
     */
    @GetMapping("/list")
    public TableDataInfo list(CostBudget costBudget)
    {
        // 确保有用户ID
        if (costBudget.getUserId() == null) {
            // 设置默认用户ID
            costBudget.setUserId(1L);
        }
        
        startPage();
        List<CostBudget> list = costBudgetService.selectCostBudgetList(costBudget);
        return getDataTable(list);
    }

    /**
     * 导出邮费预算列表
     */
    @Log(title = "邮费预算", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CostBudget costBudget)
    {
        List<CostBudget> list = costBudgetService.selectCostBudgetList(costBudget);
        ExcelUtil<CostBudget> util = new ExcelUtil<CostBudget>(CostBudget.class);
        util.exportExcel(response, list, "邮费预算数据");
    }

    /**
     * 获取邮费预算详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        CostBudget costBudget = costBudgetService.selectCostBudgetById(id);
        if (costBudget == null) {
            return error("未找到该预算信息");
        }
        return success(costBudget);
    }

    /**
     * 新增邮费预算
     */
    @Log(title = "邮费预算", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CostBudget costBudget)
    {
        return toAjax(costBudgetService.insertCostBudget(costBudget));
    }

    /**
     * 修改邮费预算
     */
    @Log(title = "邮费预算", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CostBudget costBudget)
    {
        // 检查ID是否存在
        if (costBudget.getId() == null) {
            return error("预算ID不能为空");
        }
        
        // 确保有用户ID
        if (costBudget.getUserId() == null) {
            // 设置默认用户ID
            costBudget.setUserId(1L);
        }
        
        return toAjax(costBudgetService.updateCostBudget(costBudget));
    }

    /**
     * 删除邮费预算
     */
    @Log(title = "邮费预算", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(costBudgetService.deleteCostBudgetByIds(ids));
    }
    
    /**
     * 创建周期性预算
     */
    @Log(title = "创建周期性预算", businessType = BusinessType.INSERT)
    @PostMapping("/periodic")
    public AjaxResult createPeriodicBudget(@RequestBody CostBudget costBudget)
    {
        return toAjax(costBudgetService.createPeriodicBudget(costBudget));
    }
    
    /**
     * 查询预算使用情况
     */
    @GetMapping("/usage")
    public AjaxResult checkBudgetUsage()
    {
        // 使用默认用户和部门ID
        Long userId = 1L;
        Long deptId = 100L;
        return success(costBudgetService.checkBudgetUsage(userId, deptId));
    }
} 