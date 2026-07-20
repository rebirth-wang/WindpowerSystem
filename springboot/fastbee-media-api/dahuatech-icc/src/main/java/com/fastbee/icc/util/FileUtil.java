package com.fastbee.icc.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-15 14:36
 * @Description: 文件工具类
 */
public class FileUtil {

    /**
     * 图片转换成base64字符串（不带图片头data:image/jpg;base64,）
     * @param imagePath 图片文件存放地址
     * @return
     * @throws IOException
     */
    public static String convertImageToBase64(String imagePath) throws IOException {

        // 读取图片文件到字节数组
        Path path = new File(imagePath).toPath();
        byte[] imageBytes = Files.readAllBytes(path);

        // 将字节数组编码为Base64字符串
        String base64ImageString = Base64.getEncoder().encodeToString(imageBytes);

        return base64ImageString;
    }

    /**
     * 图片转换成base64字符串（带图片头data:image/jpg;base64,）
     * @param imagePath
     * @return
     * @throws IOException
     */
    public static String convertImageToBase64WithHeader(String imagePath) throws IOException {
        String imageSuffix = imagePath.substring(imagePath.lastIndexOf(".")+1);
        return "data:image/"+imageSuffix+";base64,"+convertImageToBase64(imagePath);
    }
}
