//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.file;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.utils.StringUtils;

public class ImageUtils {
    private static final Logger aX = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] getImage(String imagePath) {
        InputStream var1 = getFile(imagePath);

        Object var3;
        try {
            byte[] var2 = IOUtils.toByteArray(var1);
            return var2;
        } catch (Exception var7) {
            aX.error("图片加载异常 {}", var7);
            var3 = null;
        } finally {
            IOUtils.closeQuietly(var1);
        }

        return (byte[])var3;
    }

    public static InputStream getFile(String imagePath) {
        try {
            byte[] var1 = readFile(imagePath);
            var1 = Arrays.copyOf(var1, var1.length);
            return new ByteArrayInputStream(var1);
        } catch (Exception var2) {
            aX.error("获取图片异常 {}", var2);
            return null;
        }
    }

    public static byte[] readFile(String url) {
        Object var1 = null;

        Object var3;
        try {
            if (url.startsWith("http")) {
                URL var2 = new URL(url);
                URLConnection var11 = var2.openConnection();
                var11.setConnectTimeout(30000);
                var11.setReadTimeout(60000);
                var11.setDoInput(true);
                var1 = var11.getInputStream();
            } else {
                String var9 = RuoYiConfig.getProfile();
                String var12 = var9 + StringUtils.substringAfter(url, "/profile");
                var1 = new FileInputStream(var12);
            }

            byte[] var10 = IOUtils.toByteArray((InputStream)var1);
            return var10;
        } catch (Exception var7) {
            aX.error("获取文件路径异常 {}", var7);
            var3 = null;
        } finally {
            IOUtils.closeQuietly((Closeable)var1);
        }

        return (byte[])var3;
    }
}
