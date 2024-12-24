package com.money.merchant.controller;

import com.money.merchant.service.ExpressQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/merchant/express")
public class ExpressController {

    @Autowired
    private ExpressQueryService expressQueryService;

    @GetMapping("/query")
    public Map<String, Object> queryExpress(@RequestParam String trackingNumber) {
        return expressQueryService.queryExpress(trackingNumber);
    }
} 