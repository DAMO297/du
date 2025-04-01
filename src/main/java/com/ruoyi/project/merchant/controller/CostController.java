package com.ruoyi.project.merchant.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.project.merchant.service.CostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/merchant/cost")
public class CostController extends BaseController {
    
    private final CostService costService;

    public CostController(CostService costService) {
        this.costService = costService;
    }

    /**
     * 识别运单图片
     */
    @PostMapping("/recognize")
    public AjaxResult recognize(MultipartFile file) {
        try {
            Map<String, String> result = costService.recognizeImage(file);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("识别运单图片失败", e);
            return AjaxResult.error("识别失败：" + e.getMessage());
        }
    }
} 