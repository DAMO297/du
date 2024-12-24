package com.money.merchant.controller;

import java.util.List;
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
import com.money.merchant.domain.Cost;
import com.money.merchant.service.ICostService;
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;

/**
 * 邮费Controller
 * 
 * @author dm
 * @date 2024-10-27
 */
@RestController
@RequestMapping("/merchant/cost")
public class CostController extends BaseController
{
    @Autowired
    private ICostService costService;

    /**
     * 查询邮费列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:cost:list')")
    @GetMapping("/list")
    public TableDataInfo list(Cost cost)
    {
        startPage();
        List<Cost> list = costService.selectCostList(cost);
        return getDataTable(list);
    }

    /**
     * 导出邮费列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:cost:export')")
    @Log(title = "邮费", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Cost cost)
    {
        List<Cost> list = costService.selectCostList(cost);
        ExcelUtil<Cost> util = new ExcelUtil<Cost>(Cost.class);
        util.exportExcel(response, list, "邮费数据");
    }

    /**
     * 获取邮费详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:cost:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(costService.selectCostById(id));
    }

    /**
     * 新增邮费
     */
    @PreAuthorize("@ss.hasPermi('merchant:cost:add')")
    @Log(title = "邮费", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Cost cost)
    {
        cost.setDeptId(getDeptId());
        cost.setUserId(getUserId());
        return toAjax(costService.insertCost(cost));
    }

    /**
     * 修改邮费
     */
    @PreAuthorize("@ss.hasPermi('merchant:cost:edit')")
    @Log(title = "邮费", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Cost cost)
    {
        return toAjax(costService.updateCost(cost));
    }

    /**
     * 删除邮费
     */
    @PreAuthorize("@ss.hasPermi('merchant:cost:remove')")
    @Log(title = "邮费", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(costService.deleteCostByIds(ids));
    }
}
