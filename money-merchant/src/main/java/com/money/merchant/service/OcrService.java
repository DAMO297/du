package com.money.merchant.service;

public interface OcrService {
    /**
     * 识别图片中的文字
     * @param base64Image Base64编码的图片数据
     * @return 识别出的文字
     */
    String recognizeImage(String base64Image) throws Exception;
} 