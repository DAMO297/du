package com.ruoyi.project.merchant.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface CostService {
    /**
     * 识别运单图片
     * 
     * @param file 图片文件
     * @return 识别结果，包含运单号、重量、金额等信息
     * @throws Exception 识别过程中的异常
     */
    Map<String, String> recognizeImage(MultipartFile file) throws Exception;
} 