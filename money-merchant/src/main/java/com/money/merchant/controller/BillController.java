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
import com.money.merchant.domain.Bill;
import com.money.merchant.service.IBillService;
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;

/**
 * 账单Controller
 * 
 * @author dm
 * @date 2024-10-27
 */
@RestController
@RequestMapping("/merchant/bill")
public class BillController extends BaseController
{
    @Autowired
    private IBillService billService;

    /**
     * 查询账单列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:bill:list')")
    @GetMapping("/list")
    public TableDataInfo list(Bill bill)
    {
        startPage();
        List<Bill> list = billService.selectBillList(bill);
        return getDataTable(list);
    }

    /**
     * 导出账单列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:bill:export')")
    @Log(title = "账单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Bill bill)
    {
        List<Bill> list = billService.selectBillList(bill);
        ExcelUtil<Bill> util = new ExcelUtil<Bill>(Bill.class);
        util.exportExcel(response, list, "账单数据");
    }

    /**
     * 获取账单详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:bill:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(billService.selectBillById(id));
    }

    /**
     * 新增账单
     */
    @PreAuthorize("@ss.hasPermi('merchant:bill:add')")
    @Log(title = "账单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Bill bill)
    {
        return toAjax(billService.insertBill(bill));
    }

    /**
     * 修改账单
     */
    @PreAuthorize("@ss.hasPermi('merchant:bill:edit')")
    @Log(title = "账单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Bill bill)
    {
        return toAjax(billService.updateBill(bill));
    }

    /**
     * 删除账单
     */
    @PreAuthorize("@ss.hasPermi('merchant:bill:remove')")
    @Log(title = "账单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(billService.deleteBillByIds(ids));
    }
}
