package com.money.merchant.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.merchant.config.ExpressConfig;
import com.money.merchant.service.ExpressQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AliyunExpressServiceImpl implements ExpressQueryService {

    private static final Logger log = LoggerFactory.getLogger(AliyunExpressServiceImpl.class);

    @Autowired
    private ExpressConfig expressConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> queryExpress(String trackingNumber) {
        String host = "https://wuliu.market.alicloudapi.com";// 【1】请求地址
        String path = "/kdi";  // 【2】后缀
        String appcode = "dbfb1ed4feb74d418111ba2690e500db"; // 【3】开通服务后 买家中心-查看AppCode
        String no = trackingNumber;// 【4】请求参数，详见文档描述
        String type = ""; //  【4】请求参数，不知道可不填 95%能自动识别
        String urlSend = host + path + "?no=" + no + "&type=" + type;  // 【5】拼接请求链接
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始查询物流信息，单号：{}", no);
            log.info("请求URL: {}", urlSend);
            log.info("AppCode: {}", appcode);
            
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE (中间是英文空格)
            
            int httpCode = httpURLCon.getResponseCode();
            log.info("API响应状态码: {}", httpCode);
            
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                log.info("正常请求计费(其他均不计费)");
                log.info("获取返回的json: {}", json);
                result = objectMapper.readValue(json, Map.class);
                result.put("success", true);
            } else {
                result.put("success", false);
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                
                String errorMessage;
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    errorMessage = "AppCode错误";
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    errorMessage = "请求的 Method、Path 或者环境错误";
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    errorMessage = "参数错误";
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    errorMessage = "服务未被授权（或URL和Path不正确）";
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    errorMessage = "套餐包次数用完";
                } else if (httpCode == 403 && error.equals("Api Market Subscription quota exhausted")) {
                    errorMessage = "套餐包次数用完，请续购套餐";
                } else {
                    errorMessage = "参数名错误 或 其他错误: " + error;
                }
                
                result.put("message", errorMessage);
            }
        } catch (MalformedURLException e) {
            log.error("URL格式错误", e);
            result.put("success", false);
            result.put("message", "URL格式错误");
        } catch (UnknownHostException e) {
            log.error("URL地址错误", e);
            result.put("success", false);
            result.put("message", "URL地址错误");
        } catch (Exception e) {
            log.error("查询物流信息失败", e);
            result.put("success", false);
            result.put("message", "查询物流信息失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 读取返回结果
     */
    private String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
} 