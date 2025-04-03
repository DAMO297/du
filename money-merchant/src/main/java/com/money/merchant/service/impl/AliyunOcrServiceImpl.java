package com.money.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.money.merchant.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AliyunOcrServiceImpl implements OcrService {
    private static final Logger log = LoggerFactory.getLogger(AliyunOcrServiceImpl.class);
    private static final String HOST = "https://ocr.market.alicloudapi.com";
    private static final String PATH = "/ocr/recognize";
    private static final String APP_CODE = "dbfb1ed4feb74d418111ba2690e500db";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String recognizeImage(String base64Image) throws Exception {
        log.info("开始图片识别请求");
        log.debug("Base64图片长度：{}", base64Image.length());

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "APPCODE " + APP_CODE);
        headers.set("Accept", "application/json");
        log.info("请求头信息：Content-Type={}, Authorization=APPCODE {}, Accept={}", 
                headers.getContentType(), APP_CODE, headers.get("Accept"));

        // 准备请求体
        Map<String, Object> body = new HashMap<>();
        body.put("image", base64Image);
        body.put("type", "general");
        body.put("config", new HashMap<String, Object>() {{
            put("min_size", 16);
            put("output_prob", true);
        }});
        
        // 打印请求体信息（不包含图片数据）
        Map<String, Object> logBody = new HashMap<>(body);
        logBody.put("image", "[BASE64_IMAGE_DATA]");
        log.info("请求体信息：{}", JSON.toJSONString(logBody));

        // 发送请求
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String url = HOST + PATH;
        log.info("开始发送OCR请求到阿里云，URL: {}", url);
        
        try {
            String response = restTemplate.postForObject(url, request, String.class);
            log.info("收到OCR响应：{}", response);

            if (response == null) {
                throw new Exception("OCR服务返回空响应");
            }

            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(response);
            if (jsonResponse == null) {
                throw new Exception("OCR响应解析失败");
            }

            Integer code = jsonResponse.getInteger("code");
            if (code == null || code != 200) {
                String msg = jsonResponse.getString("msg");
                throw new Exception("OCR识别失败：" + (msg != null ? msg : "未知错误"));
            }

            JSONObject data = jsonResponse.getJSONObject("data");
            if (data == null) {
                throw new Exception("OCR响应数据为空");
            }

            String result = data.getString("text");
            if (result == null) {
                throw new Exception("OCR识别结果为空");
            }

            log.info("图片识别完成，结果长度：{}", result.length());
            return result;
        } catch (RestClientException e) {
            log.error("OCR服务请求失败: {}", e.getMessage());
            log.error("请求详情 - URL: {}, Headers: {}, Body: {}", 
                    url, headers, JSON.toJSONString(logBody));
            throw new Exception("OCR服务请求失败: " + e.getMessage());
        }
    }
} 