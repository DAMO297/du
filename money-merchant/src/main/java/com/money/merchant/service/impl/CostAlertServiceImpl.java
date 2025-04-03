package com.money.merchant.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.money.merchant.mapper.CostAlertMapper;
import com.money.merchant.mapper.CostMapper;
import com.money.merchant.domain.CostAlert;
import com.money.merchant.domain.Cost;
import com.money.merchant.service.ICostAlertService;
import com.money.common.utils.DateUtils;
import com.money.common.utils.SecurityUtils;
import com.money.common.annotation.DataScope;
import com.money.common.utils.StringUtils;

/**
 * 邮费异常提醒Service业务层处理
 * 
 * @author dm
 * @date 2025-04-03
 */
@Service
public class CostAlertServiceImpl implements ICostAlertService 
{
    @Autowired
    private CostAlertMapper costAlertMapper;
    
    @Autowired
    private CostMapper costMapper;

    /**
     * 查询邮费异常提醒
     * 
     * @param id 邮费异常提醒主键
     * @return 邮费异常提醒
     */
    @Override
    public CostAlert selectCostAlertById(Long id)
    {
        return costAlertMapper.selectCostAlertById(id);
    }

    /**
     * 查询邮费异常提醒列表
     * 
     * @param costAlert 邮费异常提醒
     * @return 邮费异常提醒
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CostAlert> selectCostAlertList(CostAlert costAlert)
    {
        return costAlertMapper.selectCostAlertList(costAlert);
    }

    /**
     * 新增邮费异常提醒
     * 
     * @param costAlert 邮费异常提醒
     * @return 结果
     */
    @Override
    public int insertCostAlert(CostAlert costAlert)
    {
        costAlert.setCreateTime(DateUtils.getNowDate());
        costAlert.setUserId(SecurityUtils.getUserId());
        
        // 默认启用状态
        if (costAlert.getStatus() == null) {
            costAlert.setStatus(1);
        }
        
        return costAlertMapper.insertCostAlert(costAlert);
    }

    /**
     * 修改邮费异常提醒
     * 
     * @param costAlert 邮费异常提醒
     * @return 结果
     */
    @Override
    public int updateCostAlert(CostAlert costAlert)
    {
        costAlert.setUpdateTime(DateUtils.getNowDate());
        return costAlertMapper.updateCostAlert(costAlert);
    }

    /**
     * 批量删除邮费异常提醒
     * 
     * @param ids 需要删除的邮费异常提醒主键
     * @return 结果
     */
    @Override
    public int deleteCostAlertByIds(Long[] ids)
    {
        return costAlertMapper.deleteCostAlertByIds(ids);
    }

    /**
     * 删除邮费异常提醒信息
     * 
     * @param id 邮费异常提醒主键
     * @return 结果
     */
    @Override
    public int deleteCostAlertById(Long id)
    {
        return costAlertMapper.deleteCostAlertById(id);
    }
    
    /**
     * 检查邮费记录是否触发异常提醒
     * 
     * @param cost 邮费记录
     * @return 触发的提醒列表
     */
    @Override
    public List<CostAlert> checkCostAlerts(Cost cost)
    {
        List<CostAlert> triggeredAlerts = new ArrayList<>();
        
        // 获取用户已启用的单笔金额提醒配置
        CostAlert query = new CostAlert();
        query.setUserId(cost.getUserId());
        query.setDeptId(cost.getDeptId());
        query.setStatus(1); // 启用状态
        query.setThresholdType(1); // 单笔金额类型
        List<CostAlert> activeAlerts = costAlertMapper.selectActiveAlerts(cost.getUserId(), cost.getDeptId());
        
        // 检查是否超过阈值
        for (CostAlert alert : activeAlerts) {
            if (alert.getThresholdType() == 1 && // 单笔金额
                cost.getCostAmount() != null && 
                alert.getThresholdAmount() != null &&
                cost.getCostAmount().compareTo(alert.getThresholdAmount()) > 0) {
                
                // 更新触发时间
                alert.setLastTriggeredTime(DateUtils.getNowDate());
                costAlertMapper.updateAlertTriggeredTime(alert.getId(), alert.getLastTriggeredTime());
                
                // 发送提醒
                sendAlert(alert, cost);
                
                triggeredAlerts.add(alert);
            }
        }
        
        return triggeredAlerts;
    }
    
    /**
     * 发送邮费异常提醒
     * 
     * @param costAlert 异常提醒配置
     * @param cost 触发提醒的邮费记录
     * @return 结果
     */
    @Override
    public boolean sendAlert(CostAlert costAlert, Cost cost)
    {
        // 构建提醒消息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String alertTime = sdf.format(DateUtils.getNowDate());
        String trackingNo = cost.getFullTrackingNumber() != null ? cost.getFullTrackingNumber() : "未知";
        String logisticsCompany = cost.getLogisticsCompany() != null ? cost.getLogisticsCompany() : "未知";
        
        StringBuilder message = new StringBuilder();
        message.append("邮费异常提醒 [")
               .append(alertTime)
               .append("]\n运单号: ")
               .append(trackingNo)
               .append("\n物流公司: ")
               .append(logisticsCompany)
               .append("\n邮费金额: ")
               .append(cost.getCostAmount())
               .append("\n阈值金额: ")
               .append(costAlert.getThresholdAmount())
               .append("\n\n该笔邮费超出了您设置的阈值，请注意检查。");
               
        // 根据提醒方式发送不同类型的通知
        boolean result = false;
        switch (costAlert.getAlertMethod()) {
            case 1: // 系统消息
                // 这里需要调用系统消息发送接口
                // TODO: 实现系统消息发送逻辑
                result = true;
                break;
                
            case 2: // 邮件
                // 这里需要调用邮件发送接口
                // TODO: 实现邮件发送逻辑
                result = true;
                break;
                
            case 3: // 短信
                // 这里需要调用短信发送接口
                // TODO: 实现短信发送逻辑
                result = true;
                break;
        }
        
        return result;
    }
    
    /**
     * 检查日累计/周累计/月累计邮费是否超过阈值
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 触发的提醒列表
     */
    @Override
    public List<CostAlert> checkAggregatedCostAlerts(Long userId, Long deptId)
    {
        List<CostAlert> triggeredAlerts = new ArrayList<>();
        
        // 获取用户已启用的累计金额提醒配置
        List<CostAlert> activeAlerts = costAlertMapper.selectActiveAlerts(userId, deptId);
        
        for (CostAlert alert : activeAlerts) {
            if (alert.getThresholdType() >= 2 && alert.getThresholdType() <= 4) { // 2:日累计, 3:周累计, 4:月累计
                // 获取对应时间段的开始时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                
                // 根据累计类型设置开始时间
                switch (alert.getThresholdType()) {
                    case 2: // 日累计
                        // 当天开始时间，已设置
                        break;
                        
                    case 3: // 周累计
                        // 设置为本周的第一天
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        break;
                        
                    case 4: // 月累计
                        // 设置为本月的第一天
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        break;
                }
                
                // 查询该时间段内的邮费总额
                Cost query = new Cost();
                query.setUserId(userId);
                query.setDeptId(deptId);
                // 这里需要扩展Cost查询，增加时间范围查询功能
                // 模拟查询结果
                BigDecimal totalCost = new BigDecimal("100.00"); // 替换为实际查询结果
                
                // 检查是否超过阈值
                if (alert.getThresholdAmount() != null && totalCost.compareTo(alert.getThresholdAmount()) > 0) {
                    // 更新触发时间
                    alert.setLastTriggeredTime(DateUtils.getNowDate());
                    costAlertMapper.updateAlertTriggeredTime(alert.getId(), alert.getLastTriggeredTime());
                    
                    // 创建模拟Cost对象用于发送提醒
                    Cost dummyCost = new Cost();
                    dummyCost.setUserId(userId);
                    dummyCost.setDeptId(deptId);
                    dummyCost.setCostAmount(totalCost);
                    
                    // 发送提醒
                    sendAggregatedAlert(alert, dummyCost, totalCost, alert.getThresholdType());
                    
                    triggeredAlerts.add(alert);
                }
            }
        }
        
        return triggeredAlerts;
    }
    
    /**
     * 发送累计邮费异常提醒
     * 
     * @param costAlert 异常提醒配置
     * @param cost 模拟的邮费记录
     * @param totalCost 累计金额
     * @param type 累计类型
     * @return 结果
     */
    private boolean sendAggregatedAlert(CostAlert costAlert, Cost cost, BigDecimal totalCost, int type)
    {
        // 构建提醒消息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String alertTime = sdf.format(DateUtils.getNowDate());
        
        String periodType;
        switch (type) {
            case 2:
                periodType = "日";
                break;
            case 3:
                periodType = "周";
                break;
            case 4:
                periodType = "月";
                break;
            default:
                periodType = "";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("邮费异常提醒 [")
               .append(alertTime)
               .append("]\n累计类型: ")
               .append(periodType)
               .append("累计\n")
               .append("累计邮费金额: ")
               .append(totalCost)
               .append("\n阈值金额: ")
               .append(costAlert.getThresholdAmount())
               .append("\n\n")
               .append(periodType)
               .append("累计邮费超出了您设置的阈值，请注意控制邮费支出。");
               
        // 发送通知
        boolean result = false;
        switch (costAlert.getAlertMethod()) {
            case 1: // 系统消息
                // TODO: 实现系统消息发送逻辑
                result = true;
                break;
                
            case 2: // 邮件
                // TODO: 实现邮件发送逻辑
                result = true;
                break;
                
            case 3: // 短信
                // TODO: 实现短信发送逻辑
                result = true;
                break;
        }
        
        return result;
    }
    
    /**
     * 生成邮费节约建议
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return 节约建议
     */
    @Override
    public String generateCostSavingSuggestions(Long userId, Long deptId)
    {
        // 获取用户近期邮费记录
        Cost query = new Cost();
        query.setUserId(userId);
        query.setDeptId(deptId);
        // 查询近期邮费记录
        // List<Cost> recentCosts = costMapper.selectRecentCosts(userId, deptId, 90); // 假设查询近90天记录
        
        // 分析邮费数据并生成建议
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("邮费节约建议：\n\n");
        
        // 1. 物流公司选择建议
        suggestions.append("1. 物流公司选择建议\n");
        suggestions.append("   - 建议优先使用合作物流公司，可获得更优惠的价格\n");
        suggestions.append("   - 对于轻小件，建议使用邮政小包、韵达快递等经济型快递\n");
        suggestions.append("   - 同城快递可考虑使用同城配送服务，通常价格更低\n\n");
        
        // 2. 包装优化建议
        suggestions.append("2. 包装优化建议\n");
        suggestions.append("   - 使用轻量化包装材料，减少包裹重量\n");
        suggestions.append("   - 合理设计包装尺寸，避免体积重计费带来额外成本\n");
        suggestions.append("   - 考虑使用物流公司提供的标准包装，可能获得更优惠价格\n\n");
        
        // 3. 批量发货建议
        suggestions.append("3. 批量发货建议\n");
        suggestions.append("   - 同一地区的多个包裹合并发货，可降低单件运费\n");
        suggestions.append("   - 与物流公司协商批量折扣，提高发货量以获取更低单价\n\n");
        
        // 4. 区域优化建议
        suggestions.append("4. 区域优化建议\n");
        suggestions.append("   - 分析您的高成本地区：偏远地区配送成本较高\n");
        suggestions.append("   - 考虑对偏远地区采用不同的配送策略或运费策略\n\n");
        
        // 5. 时间优化建议
        suggestions.append("5. 时间优化建议\n");
        suggestions.append("   - 避开物流高峰期（如节假日前后）发货，减少加急费用\n");
        suggestions.append("   - 提前规划发货时间，避免使用加急服务\n");
        
        return suggestions.toString();
    }
} 