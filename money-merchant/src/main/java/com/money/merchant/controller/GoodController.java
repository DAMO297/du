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
import com.money.merchant.domain.Good;
import com.money.merchant.service.IGoodService;
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;

/**
 * 仓库Controller
 *
 * @author dm
 * @date 2024-10-27
 */
@RestController
@RequestMapping("/merchant/good")
public class GoodController extends BaseController
{
    @Autowired
    private IGoodService goodService;

    /**
     * 查询仓库列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:list')")
    @GetMapping("/list")
    public TableDataInfo list(Good good)
    {
        startPage();
        List<Good> list = goodService.selectGoodList(good);
        return getDataTable(list);
    }

    /**
     * 导出仓库列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:export')")
    @Log(title = "仓库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Good good)
    {
        List<Good> list = goodService.selectGoodList(good);
        ExcelUtil<Good> util = new ExcelUtil<Good>(Good.class);
        util.exportExcel(response, list, "仓库数据");
    }

    /**
     * 获取仓库详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(goodService.selectGoodById(id));
    }

    /**
     * 新增仓库
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:add')")
    @Log(title = "仓库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Good good)
    {
        good.setDeptId(getDeptId());
        good.setUserId(getUserId());
        return toAjax(goodService.insertGood(good));
    }

    /**
     * 修改仓库
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:edit')")
    @Log(title = "仓库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Good good)
    {
        return toAjax(goodService.updateGood(good));
    }

    /**
     * 删除仓库
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:remove')")
    @Log(title = "仓库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(goodService.deleteGoodByIds(ids));
    }
}
