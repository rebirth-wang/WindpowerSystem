package com.fastbee.ezviz.client;

import java.util.Map;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fastbee.ezviz.config.EzvizConfig;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.ezviz.model.EzvizBaseResponse;

/**
 * 萤石云开放平台 HTTP 客户端
 * <p>基于 RestTemplate 封装，统一处理 accessToken 注入、错误码解析</p>
 *
 * @author fastbee
 */
@Slf4j
public class EzvizHttpClient {

    private final EzvizConfig config;
    private final RestTemplate restTemplate;
    private final EzvizTokenHolder tokenHolder;

    public EzvizHttpClient(EzvizConfig config, RestTemplate restTemplate, EzvizTokenHolder tokenHolder) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.tokenHolder = tokenHolder;
    }

    /**
     * 以 application/x-www-form-urlencoded 方式发送 POST 请求（用于获取 Token）
     *
     * @param path        接口路径，例如 /api/lapp/token/get
     * @param formParams  表单参数
     * @param clazz       响应类型
     */
    public <T extends EzvizBaseResponse> T postForm(String path, Map<String, String> formParams, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        if (formParams != null) {
            formParams.forEach(body::add);
        }
        String url = config.getApiBase() + path;
        log.debug("[萤石云] POST FORM url={}, params={}", url, formParams);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            String json = response.getBody();
            log.debug("[萤石云] 响应: {}", json);
            T result = JSON.parseObject(json, clazz);
            checkResponse(result, path);
            return result;
        } catch (EzvizApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[萤石云] 请求失败 path={}", path, e);
            throw new EzvizApiException("HTTP请求失败: " + e.getMessage());
        }
    }

    /**
     * 携带 accessToken 发送 POST 表单请求
     *
     * @param path       接口路径
     * @param formParams 业务参数（不含 accessToken，将自动注入）
     * @param clazz      响应类型
     */
    public <T extends EzvizBaseResponse> T postWithToken(String path, Map<String, String> formParams, Class<T> clazz) {
        String accessToken = tokenHolder.getAccessToken();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("accessToken", accessToken);
        if (formParams != null) {
            formParams.forEach(form::add);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = config.getApiBase() + path;
        log.debug("[萤石云] POST WITH TOKEN url={}", url);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(form, headers), String.class);
            String json = response.getBody();
            log.debug("[萤石云] 响应: {}", json);
            T result = JSON.parseObject(json, clazz);
            checkResponse(result, path);
            return result;
        } catch (EzvizApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[萤石云] 请求失败 path={}", path, e);
            throw new EzvizApiException("HTTP请求失败: " + e.getMessage());
        }
    }

    /**
     * 检查响应状态码，非200时抛出业务异常
     */
    private void checkResponse(EzvizBaseResponse response, String path) {
        if (response == null) {
            throw new EzvizApiException("萤石云接口响应为空，path=" + path);
        }
        if (!response.isSuccess()) {
            String errMsg = String.format("[萤石云] 业务失败 path=%s code=%s msg=%s", path, response.getCode(), response.getMsg());
            log.warn(errMsg);
            throw new EzvizApiException(errMsg, response.getCode());
        }
    }

}
