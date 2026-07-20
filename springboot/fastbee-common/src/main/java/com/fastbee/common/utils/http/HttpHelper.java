//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            InputStream inputStream = request.getInputStream();

            try {
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line = "";

                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (Throwable var17) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                    }
                }

                throw var17;
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException var18) {
            LOGGER.warn("getBodyString出现问题！");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(ExceptionUtils.getMessage(e));
                }
            }

        }

        return sb.toString();
    }
}
