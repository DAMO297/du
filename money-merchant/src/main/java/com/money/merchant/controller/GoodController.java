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
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 获取库存警报信息
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:query')")
    @GetMapping("/inventory-alert")
    public AjaxResult getInventoryAlert(@RequestParam(value = "threshold", defaultValue = "5") Integer threshold)
    {
        // 获取库存低于阈值的商品
        Good queryParam = new Good();
        List<Good> goods = goodService.selectGoodList(queryParam);
        List<Good> lowStockGoods = goods.stream()
                .filter(good -> good.getStock() != null && good.getStock() <= threshold)
                .collect(java.util.stream.Collectors.toList());
        
        return success()
                .put("lowStockGoods", lowStockGoods)
                .put("threshold", threshold)
                .put("totalCount", lowStockGoods.size());
    }
    
    /**
     * 获取热门商品分析
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:query')")
    @GetMapping("/popular-goods")
    public AjaxResult getPopularGoods(@RequestParam(value = "limit", defaultValue = "10") Integer limit)
    {
        // 获取价格较高的前N个商品作为热门商品
        // 实际场景可能需要基于销量等其他因素
        Good queryParam = new Good();
        List<Good> allGoods = goodService.selectGoodList(queryParam);
        
        List<Good> popularGoods = allGoods.stream()
                .filter(good -> good.getSalePrice() != null)
                .sorted((g1, g2) -> g2.getSalePrice().compareTo(g1.getSalePrice()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        
        return success()
                .put("popularGoods", popularGoods)
                .put("totalCount", popularGoods.size());
    }
    
    /**
     * 计算库存周转率
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:query')")
    @GetMapping("/inventory-turnover")
    public AjaxResult getInventoryTurnover(@RequestParam(value = "days", defaultValue = "30") Integer days)
    {
        // 简化实现：假设周转率 = 销售额 / 平均库存价值
        // 这里使用模拟数据，实际场景应从销售记录计算
        Good queryParam = new Good();
        List<Good> goods = goodService.selectGoodList(queryParam);
        
        java.math.BigDecimal totalInventoryValue = goods.stream()
                .filter(good -> good.getSalePrice() != null && good.getStock() != null)
                .map(good -> good.getSalePrice().multiply(new java.math.BigDecimal(good.getStock())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        // 模拟一个销售额
        java.math.BigDecimal mockSales = totalInventoryValue.multiply(new java.math.BigDecimal("0.3"));
        
        // 计算周转率
        java.math.BigDecimal turnoverRate = java.math.BigDecimal.ZERO;
        if (totalInventoryValue.compareTo(java.math.BigDecimal.ZERO) > 0) {
            turnoverRate = mockSales.divide(totalInventoryValue, 2, java.math.RoundingMode.HALF_UP);
        }
        
        return success()
                .put("totalInventoryValue", totalInventoryValue)
                .put("estimatedSales", mockSales)
                .put("turnoverRate", turnoverRate)
                .put("period", days + "天");
    }
    
    /**
     * 批量更新商品标签
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:edit')")
    @Log(title = "商品标签", businessType = BusinessType.UPDATE)
    @PutMapping("/update-tags")
    public AjaxResult updateTags(@RequestBody java.util.Map<String, Object> params)
    {
        // 从请求参数中获取商品ID数组和标签值
        @SuppressWarnings("unchecked")
        List<Long> goodIds = (List<Long>) params.get("goodIds");
        String tag = (String) params.get("tag");
        
        if (goodIds == null || goodIds.isEmpty() || tag == null) {
            return error("参数错误：必须提供商品ID列表和标签值");
        }
        
        int successCount = 0;
        for (Long id : goodIds) {
            Good good = new Good();
            good.setId(id);
            good.setRemark(tag); // 临时使用remark字段存储标签
            if (goodService.updateGood(good) > 0) {
                successCount++;
            }
        }
        
        return success("成功更新" + successCount + "个商品的标签");
    }
    
    /**
     * 获取商品利润分析
     */
    @PreAuthorize("@ss.hasPermi('merchant:good:query')")
    @GetMapping("/profit-analysis")
    public AjaxResult getProfitAnalysis()
    {
        Good queryParam = new Good();
        List<Good> goods = goodService.selectGoodList(queryParam);
        
        // 计算总利润和平均利润率
        java.math.BigDecimal totalProfit = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalCost = java.math.BigDecimal.ZERO;
        
        List<java.util.Map<String, Object>> profitDetails = new java.util.ArrayList<>();
        
        for (Good good : goods) {
            if (good.getSalePrice() != null && good.getCost() != null && good.getStock() != null && good.getStock() > 0) {
                java.math.BigDecimal revenue = good.getSalePrice().multiply(new java.math.BigDecimal(good.getStock()));
                java.math.BigDecimal cost = good.getCost().multiply(new java.math.BigDecimal(good.getStock()));
                java.math.BigDecimal profit = revenue.subtract(cost);
                java.math.BigDecimal profitRate = java.math.BigDecimal.ZERO;
                
                if (revenue.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    profitRate = profit.divide(revenue, 4, java.math.RoundingMode.HALF_UP)
                            .multiply(new java.math.BigDecimal("100"));
                }
                
                totalProfit = totalProfit.add(profit);
                totalRevenue = totalRevenue.add(revenue);
                totalCost = totalCost.add(cost);
                
                java.util.Map<String, Object> detail = new java.util.HashMap<>();
                detail.put("id", good.getId());
                detail.put("name", good.getProductName());
                detail.put("revenue", revenue);
                detail.put("cost", cost);
                detail.put("profit", profit);
                detail.put("profitRate", profitRate);
                
                profitDetails.add(detail);
            }
        }
        
        java.math.BigDecimal overallProfitRate = java.math.BigDecimal.ZERO;
        if (totalRevenue.compareTo(java.math.BigDecimal.ZERO) > 0) {
            overallProfitRate = totalProfit.divide(totalRevenue, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new java.math.BigDecimal("100"));
        }
        
        return success()
                .put("totalProfit", totalProfit)
                .put("totalRevenue", totalRevenue)
                .put("totalCost", totalCost)
                .put("overallProfitRate", overallProfitRate)
                .put("profitDetails", profitDetails);
    }
}
