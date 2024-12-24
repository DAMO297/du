package com.money.merchant.service;

import java.util.Map;

public interface ExpressQueryService {
    /**
     * 查询物流信息
     * @param trackingNumber 快递单号
     * @return 物流信息
     */
    Map<String, Object> queryExpress(String trackingNumber);
} 