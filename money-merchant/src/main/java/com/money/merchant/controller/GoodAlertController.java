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
import com.money.common.utils.poi.ExcelUtil;
import com.money.common.core.page.TableDataInfo;
import com.money.merchant.domain.GoodAlertConfig;
import com.money.merchant.domain.GoodAlertRecord;
import com.money.merchant.service.IGoodAlertService;

/**
 * 商品预警Controller
 */
@RestController
@RequestMapping("/merchant/alert")
public class GoodAlertController extends BaseController {
    @Autowired
    private IGoodAlertService goodAlertService;

    /**
     * 查询预警配置列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:list')")
    @GetMapping("/config/list")
    public TableDataInfo list(GoodAlertConfig config) {
        startPage();
        List<GoodAlertConfig> list = goodAlertService.selectGoodAlertConfigList(config);
        return getDataTable(list);
    }

    /**
     * 导出预警配置列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:export')")
    @Log(title = "预警配置", businessType = BusinessType.EXPORT)
    @PostMapping("/config/export")
    public void export(HttpServletResponse response, GoodAlertConfig config) {
        List<GoodAlertConfig> list = goodAlertService.selectGoodAlertConfigList(config);
        ExcelUtil<GoodAlertConfig> util = new ExcelUtil<GoodAlertConfig>(GoodAlertConfig.class);
        util.exportExcel(response, list, "预警配置数据");
    }

    /**
     * 获取预警配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:query')")
    @GetMapping(value = "/config/{id}")
    public AjaxResult getConfigInfo(@PathVariable("id") Long id) {
        return success(goodAlertService.selectGoodAlertConfigById(id));
    }

    /**
     * 新增预警配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:add')")
    @Log(title = "预警配置", businessType = BusinessType.INSERT)
    @PostMapping("/config")
    public AjaxResult addConfig(@RequestBody GoodAlertConfig config) {
        return toAjax(goodAlertService.insertGoodAlertConfig(config));
    }

    /**
     * 修改预警配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:edit')")
    @Log(title = "预警配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult editConfig(@RequestBody GoodAlertConfig config) {
        return toAjax(goodAlertService.updateGoodAlertConfig(config));
    }

    /**
     * 删除预警配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:remove')")
    @Log(title = "预警配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/config/{ids}")
    public AjaxResult removeConfig(@PathVariable Long[] ids) {
        return toAjax(goodAlertService.deleteGoodAlertConfigByIds(ids));
    }

    /**
     * 查询预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:list')")
    @GetMapping("/record/list")
    public TableDataInfo list(GoodAlertRecord record) {
        startPage();
        List<GoodAlertRecord> list = goodAlertService.selectGoodAlertRecordList(record);
        return getDataTable(list);
    }

    /**
     * 导出预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:export')")
    @Log(title = "预警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/record/export")
    public void export(HttpServletResponse response, GoodAlertRecord record) {
        List<GoodAlertRecord> list = goodAlertService.selectGoodAlertRecordList(record);
        ExcelUtil<GoodAlertRecord> util = new ExcelUtil<GoodAlertRecord>(GoodAlertRecord.class);
        util.exportExcel(response, list, "预警记录数据");
    }

    /**
     * 获取预警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:query')")
    @GetMapping(value = "/record/{id}")
    public AjaxResult getRecordInfo(@PathVariable("id") Long id) {
        return success(goodAlertService.selectGoodAlertRecordById(id));
    }

    /**
     * 处理预警记录
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:edit')")
    @Log(title = "预警记录", businessType = BusinessType.UPDATE)
    @PutMapping("/record/handle/{id}")
    public AjaxResult handleAlert(@PathVariable("id") Long id, @RequestBody GoodAlertRecord record) {
        return toAjax(goodAlertService.handleAlert(id, record.getHandler(), record.getHandleRemark()));
    }

    /**
     * 删除预警记录
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:remove')")
    @Log(title = "预警记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/record/{ids}")
    public AjaxResult removeRecord(@PathVariable Long[] ids) {
        return toAjax(goodAlertService.deleteGoodAlertRecordByIds(ids));
    }

    /**
     * 手动触发预警检查
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:check')")
    @Log(title = "预警检查", businessType = BusinessType.OTHER)
    @PostMapping("/check")
    public AjaxResult checkAlerts() {
        goodAlertService.checkPriceAlert();
        goodAlertService.checkProfitAlert();
        goodAlertService.checkSalesAlert();
        return success("预警检查完成");
    }

    /**
     * 批量创建所有商品的默认预警配置
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:config:add')")
    @Log(title = "预警配置", businessType = BusinessType.INSERT)
    @PostMapping("/config/createDefault")
    public AjaxResult createDefaultConfigs() {
        int count = goodAlertService.createDefaultAlertConfigForAllGoods();
        return success("成功创建" + count + "个预警配置");
    }

    /**
     * 手动触发未动销商品检查
     */
    @PreAuthorize("@ss.hasPermi('merchant:alert:check')")
    @Log(title = "商品预警", businessType = BusinessType.OTHER)
    @PostMapping("/check/sales")
    public AjaxResult checkSalesAlert() {
        goodAlertService.checkSalesAlert();
        return success();
    }
} 