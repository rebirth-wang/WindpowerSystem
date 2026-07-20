package com.fastbee.common.extend.utils;

import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class SM3Util {

    static {
        // 注册BouncyCastle提供者
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 使用BouncyCastle的SM3Digest进行加密
     */
    public static String hashWithSM3Digest(String data) {
        try {
            byte[] dataBytes = data.getBytes("UTF-8");
            SM3Digest digest = new SM3Digest();
            digest.update(dataBytes, 0, dataBytes.length);
            byte[] hash = new byte[digest.getDigestSize()];
            digest.doFinal(hash, 0);
            return Hex.toHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SM3加密失败", e);
        }
    }

    /**
     * 使用MessageDigest进行SM3加密
     */
    public static String hashWithMessageDigest(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SM3", "BC");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return Hex.toHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SM3加密失败", e);
        }
    }

    /**
     * 对文件进行SM3哈希计算
     */
    public static String hashFile(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SM3", "BC");
            java.io.FileInputStream fis = new java.io.FileInputStream(filePath);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, length);
            }
            fis.close();
            byte[] hash = digest.digest();
            return Hex.toHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("文件SM3哈希计算失败", e);
        }
    }
}
