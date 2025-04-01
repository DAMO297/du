package com.ruoyi.project.merchant.service.impl;

import com.ruoyi.project.merchant.service.CostService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CostServiceImpl implements CostService {
    
    private static final Logger log = LoggerFactory.getLogger(CostServiceImpl.class);
    private final Tesseract tesseract;

    public CostServiceImpl(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    @Override
    public Map<String, String> recognizeImage(MultipartFile file) throws Exception {
        Map<String, String> result = new HashMap<>();
        
        try {
            // 读取图片并预处理
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage processedImage = preprocess(originalImage);
            
            // 进行OCR识别
            String text = tesseract.doOCR(processedImage);
            log.info("OCR识别结果: {}", text);

            // 解析识别结果
            // 1. 运单号匹配（JD开头的运单号）
            Pattern trackingPattern = Pattern.compile("JDX\\d{11}");
            Matcher trackingMatcher = trackingPattern.matcher(text);
            if (trackingMatcher.find()) {
                result.put("trackingNumber", trackingMatcher.group());
            }

            // 2. 重量匹配
            Pattern weightPattern = Pattern.compile("实际重量[：:]?\\s*(\\d+\\.?\\d*)");
            Matcher weightMatcher = weightPattern.matcher(text);
            if (weightMatcher.find()) {
                result.put("weight", weightMatcher.group(1));
            }

            // 3. 费用匹配
            Pattern costPattern = Pattern.compile("[¥￥]\\s*(\\d+\\.?\\d*)");
            Matcher costMatcher = costPattern.matcher(text);
            if (costMatcher.find()) {
                result.put("costAmount", costMatcher.group(1));
            }

            return result;
        } catch (TesseractException e) {
            log.error("OCR识别失败", e);
            throw new Exception("OCR识别失败: " + e.getMessage());
        }
    }

    /**
     * 图像预处理
     * @param originalImage 原始图像
     * @return 处理后的图像
     */
    private BufferedImage preprocess(BufferedImage originalImage) {
        // 1. 转换为灰度图
        BufferedImage grayImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(),
            BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        // 2. 二值化处理
        BufferedImage binaryImage = new BufferedImage(
            grayImage.getWidth(),
            grayImage.getHeight(),
            BufferedImage.TYPE_BYTE_BINARY);

        int threshold = calculateThreshold(grayImage);
        for (int x = 0; x < grayImage.getWidth(); x++) {
            for (int y = 0; y < grayImage.getHeight(); y++) {
                int rgb = grayImage.getRGB(x, y);
                int gray = (rgb >> 16) & 0xff;
                if (gray > threshold) {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return binaryImage;
    }

    /**
     * 计算二值化阈值（OTSU算法）
     */
    private int calculateThreshold(BufferedImage grayImage) {
        int[] histogram = new int[256];
        
        // 计算直方图
        for (int x = 0; x < grayImage.getWidth(); x++) {
            for (int y = 0; y < grayImage.getHeight(); y++) {
                int gray = (grayImage.getRGB(x, y) >> 16) & 0xff;
                histogram[gray]++;
            }
        }

        // 图像总像素数
        int total = grayImage.getWidth() * grayImage.getHeight();
        
        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        float sumB = 0;
        int wB = 0;
        int wF;
        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;
            
            wF = total - wB;
            if (wF == 0) break;

            sumB += i * histogram[i];
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }
} 