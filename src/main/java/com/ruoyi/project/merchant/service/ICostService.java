package com.ruoyi.project.merchant.service;

import com.ruoyi.project.merchant.domain.Cost;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface ICostService {
    // ... existing code ...

    /**
     * 识别运单图片
     * 
     * @param file 图片文件
     * @return 识别结果
     */
    Map<String, String> recognizeImage(MultipartFile file) throws Exception;
} 