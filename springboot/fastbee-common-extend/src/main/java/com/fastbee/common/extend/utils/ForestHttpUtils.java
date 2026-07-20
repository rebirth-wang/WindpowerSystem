package com.fastbee.common.extend.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.function.Consumer;

import com.alibaba.fastjson2.JSON;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP请求工具类 - 基于Forest实现
 * 支持GET/POST/PUT/DELETE请求，支持JSON和表单格式传参
 *
 * @author mszhou
 */
public class ForestHttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(ForestHttpUtils.class);

    public static final String ENCODING = "UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    // ============================== 同步请求方法 ==============================

    /**
     * POST请求 - JSON格式（默认）
     */
    public static String postJson(String url, Map<String, String> headers, Object body) throws Exception {
        try {
            String bodyStr = (body instanceof String) ? (String) body : JSON.toJSONString(body);

            ForestRequest<?> request = Forest.post(url)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON + ";charset=" + ENCODING);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (bodyStr != null && !bodyStr.trim().isEmpty()) {
                request.addBody(bodyStr);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("POST JSON请求失败: {}", url, e);
            throw new Exception("POST JSON请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * POST请求 - 表单格式
     */
    public static String postForm(String url, Map<String, String> headers, Map<String, String> formData) throws Exception {
        try {
            String bodyStr = mapToFormData(formData);

            ForestRequest<?> request = Forest.post(url)
                    .addHeader("Content-Type", CONTENT_TYPE_FORM + ";charset=" + ENCODING);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (bodyStr != null && !bodyStr.trim().isEmpty()) {
                request.addBody(bodyStr);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("POST Form请求失败: {}", url, e);
            throw new Exception("POST Form请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * POST请求 - 自动判断格式
     */
    public static String post(String url, Map<String, String> headers, Object body) throws Exception {
        if (body instanceof Map) {
            return postForm(url, headers, (Map<String, String>) body);
        } else {
            return postJson(url, headers, body);
        }
    }

    /**
     * GET请求
     */
    public static String get(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        try {
            ForestRequest<?> request = Forest.get(url);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (params != null) {
                request.addQuery(params);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("GET请求失败: {}", url, e);
            throw new Exception("GET请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * GET请求 - 不对参数编码
     */
    public static String getWithoutEncode(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        try {
            String fullUrl = buildUrlWithParams(url, params, false);

            ForestRequest<?> request = Forest.get(fullUrl);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("GET请求失败: {}", url, e);
            throw new Exception("GET请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * PUT请求 - JSON格式
     */
    public static String putJson(String url, Map<String, String> headers, Object body) throws Exception {
        try {
            String bodyStr = (body instanceof String) ? (String) body : JSON.toJSONString(body);

            ForestRequest<?> request = Forest.put(url)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON + ";charset=" + ENCODING);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (bodyStr != null && !bodyStr.trim().isEmpty()) {
                request.addBody(bodyStr);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("PUT JSON请求失败: {}", url, e);
            throw new Exception("PUT JSON请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * PUT请求 - 表单格式
     */
    public static String putForm(String url, Map<String, String> headers, Map<String, String> formData) throws Exception {
        try {
            String bodyStr = mapToFormData(formData);

            ForestRequest<?> request = Forest.put(url)
                    .addHeader("Content-Type", CONTENT_TYPE_FORM + ";charset=" + ENCODING);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (bodyStr != null && !bodyStr.trim().isEmpty()) {
                request.addBody(bodyStr);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("PUT Form请求失败: {}", url, e);
            throw new Exception("PUT Form请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * DELETE请求
     */
    public static String delete(String url, Map<String, String> headers) throws Exception {
        try {
            ForestRequest<?> request = Forest.delete(url);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (response.getStatusCode() >= 400) {
                throw new HttpException(response.getStatusCode(),
                        "HTTP请求失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getContent();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("DELETE请求失败: {}", url, e);
            throw new Exception("DELETE请求失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下载文件
     */
    public static String downloadFile(String url, Map<String, String> headers, File outputFile) throws Exception {
        FileOutputStream fos = null;
        InputStream is = null;

        try {
            ForestRequest<?> request = Forest.get(url);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (!response.isSuccess()) {
                throw new HttpException(response.getStatusCode(),
                        "下载文件失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            is = response.getInputStream();
            fos = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            return "success";
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("下载文件失败: {}", url, e);
            throw new Exception("下载文件失败: " + e.getMessage(), e);
        } finally {
            closeQuietly(fos);
            closeQuietly(is);
        }
    }

    /**
     * 下载文件为字节数组
     */
    public static byte[] downloadFileAsBytes(String url, Map<String, String> headers) throws Exception {
        InputStream is = null;
        ByteArrayOutputStream baos = null;

        try {
            ForestRequest<?> request = Forest.get(url);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            ForestResponse response = request.execute(ForestResponse.class);

            if (!response.isSuccess()) {
                throw new HttpException(response.getStatusCode(),
                        "下载文件失败: " + response.getStatusCode() + " - " + response.getContent());
            }

            return response.getByteArray();
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("下载文件失败: {}", url, e);
            throw new Exception("下载文件失败: " + e.getMessage(), e);
        }
    }

    // ============================== 异步请求方法 ==============================

    /**
     * 异步POST请求 - JSON格式
     */
    public static void postJsonAsync(String url, Map<String, String> headers, Object body,
                                     Consumer<String> onSuccess, Consumer<Throwable> onError) {
        try {
            String bodyStr = (body instanceof String) ? (String) body : JSON.toJSONString(body);

            ForestRequest<?> request = Forest.post(url)
                    .addHeader("Content-Type", CONTENT_TYPE_JSON + ";charset=" + ENCODING);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (bodyStr != null && !bodyStr.trim().isEmpty()) {
                request.addBody(bodyStr);
            }

            request.async()
                    .onSuccess((data, req, res) -> {
                        if (onSuccess != null) {
                            onSuccess.accept(res.getContent());
                        }
                    })
                    .onError((ex, req, res) -> {
                        logger.error("异步POST JSON请求失败: {}", url, ex);
                        if (onError != null) {
                            onError.accept(ex);
                        }
                    })
                    .execute();
        } catch (Exception e) {
            logger.error("异步POST JSON请求失败: {}", url, e);
            if (onError != null) {
                onError.accept(e);
            }
        }
    }

    /**
     * 异步POST请求 - 表单格式
     */
    public static void postFormAsync(String url, Map<String, String> headers, Map<String, String> formData,
                                     Consumer<String> onSuccess, Consumer<Throwable> onError) {
        try {
            String bodyStr = mapToFormData(formData);

            ForestRequest<?> request = Forest.post(url)
                    .addHeader("Content-Type", CONTENT_TYPE_FORM + ";charset=" + ENCODING);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (bodyStr != null && !bodyStr.trim().isEmpty()) {
                request.addBody(bodyStr);
            }

            request.async()
                    .onSuccess((data, req, res) -> {
                        if (onSuccess != null) {
                            onSuccess.accept(res.getContent());
                        }
                    })
                    .onError((ex, req, res) -> {
                        logger.error("异步POST Form请求失败: {}", url, ex);
                        if (onError != null) {
                            onError.accept(ex);
                        }
                    })
                    .execute();
        } catch (Exception e) {
            logger.error("异步POST Form请求失败: {}", url, e);
            if (onError != null) {
                onError.accept(e);
            }
        }
    }

    /**
     * 异步GET请求
     */
    public static void getAsync(String url, Map<String, String> headers, Map<String, String> params,
                                Consumer<String> onSuccess, Consumer<Throwable> onError) {
        try {
            ForestRequest<?> request = Forest.get(url);

            if (headers != null) {
                headers.forEach(request::addHeader);
            }

            if (params != null) {
                request.addQuery(params);
            }

            request.async()
                    .onSuccess((data, req, res) -> {
                        if (onSuccess != null) {
                            onSuccess.accept(res.getContent());
                        }
                    })
                    .onError((ex, req, res) -> {
                        logger.error("异步GET请求失败: {}", url, ex);
                        if (onError != null) {
                            onError.accept(ex);
                        }
                    })
                    .execute();
        } catch (Exception e) {
            logger.error("异步GET请求失败: {}", url, e);
            if (onError != null) {
                onError.accept(e);
            }
        }
    }

    // ============================== 辅助方法 ==============================

    /**
     * 将Map转换为表单数据
     */
    public static String mapToFormData(Map<String, String> params) {
        return mapToFormData(params, true);
    }

    public static String mapToFormData(Map<String, String> params, boolean urlEncode) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }

            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue() : "";

            if (urlEncode) {
                try {
                    key = URLEncoder.encode(key, ENCODING);
                    value = URLEncoder.encode(value, ENCODING);
                } catch (UnsupportedEncodingException e) {
                    // 使用原始值
                }
            }

            sb.append(key).append("=").append(value);
        }

        return sb.toString();
    }

    /**
     * 构建带参数的URL
     */
    private static String buildUrlWithParams(String url, Map<String, String> params, boolean encode) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        String queryString = mapToFormData(params, encode);
        if (url.contains("?")) {
            return url + "&" + queryString;
        } else {
            return url + "?" + queryString;
        }
    }

    /**
     * 安静关闭流
     */
    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.warn("关闭资源时发生异常", e);
            }
        }
    }

    // ============================== 异常类 ==============================

    /**
     * 自定义HTTP异常
     */
    public static class HttpException extends Exception {
        private final int statusCode;

        public HttpException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
