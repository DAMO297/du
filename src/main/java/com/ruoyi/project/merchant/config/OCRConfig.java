package com.ruoyi.project.merchant.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@Configuration
public class OCRConfig {

    @Bean
    public Tesseract tesseract() throws IOException {
        Tesseract tesseract = new Tesseract();
        // 设置训练数据路径为resources目录下的tessdata文件夹
        String tessdataPath = new ClassPathResource("tessdata").getFile().getAbsolutePath();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage("chi_sim");
        // 设置识别白名单
        tesseract.setTessVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz:：.¥￥");
        // 设置页面分割模式为单行文本
        tesseract.setPageSegMode(7);
        return tesseract;
    }
} 