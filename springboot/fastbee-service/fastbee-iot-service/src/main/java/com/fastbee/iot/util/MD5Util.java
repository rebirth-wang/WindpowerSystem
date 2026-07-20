package com.fastbee.iot.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String encryptToMD5(String password) {
        if (password == null) {
            return null;
        }

        try {
            // 1. 获取MD5摘要算法的 MessageDigest 实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2. 将输入的密码转换为字节数组并更新摘要
            byte[] digest = md.digest(password.getBytes());

            // 3. 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 将每个字节转换为两位十六进制（补零）
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // 实际开发中应使用日志记录
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    // 测试示例
    public static void main(String[] args) {
        String password = "yourPassword123";
        String encrypted = encryptToMD5(password);
        System.out.println("原始密码: " + password);
        System.out.println("MD5加密: " + encrypted);
        // 输出示例：e99a18c428cb38d5f260853678922e03
    }
}
