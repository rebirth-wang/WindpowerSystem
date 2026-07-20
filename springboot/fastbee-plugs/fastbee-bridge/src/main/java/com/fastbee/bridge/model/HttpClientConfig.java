package com.fastbee.bridge.model;

import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.dtflys.forest.http.ForestRequestType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

@Data
@NoArgsConstructor
public class HttpClientConfig implements Config {
    private String backend;
    private String url;
    private ForestRequestType method;
    private Map<String, Object> headers;
    private Map<String, Object> params;
    private String body;

    @Override
    public void load(String configJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HttpClient config = JSON.parseObject(configJson, HttpClient.class);
            if (config.getRequestHeaders() != null) {
                Map<String, Object> headers = mapper.readValue(config.getRequestHeaders(), Map.class);
                if(this.headers == null) {
                    this.headers = headers;
                } else {
                    this.headers.putAll(headers);
                }
            }
            if (config.getRequestQuerys() != null) {
                Map<String, Object> params = mapper.readValue(config.getRequestQuerys(), Map.class);
                if(this.params == null) {
                    this.params = params;
                } else {
                    this.params.putAll(params);
                }
            }
            this.body = config.getRequestBody();
            this.url = config.getHostUrl();
            this.method = ForestRequestType.findType(config.getMethod());
            this.backend = "okhttp3";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload(NodeComponent node, Map<String, Object> dataMap) {
        StringSubstitutor substitutor = new StringSubstitutor(dataMap);
        // ${}占位符替换
        headers.forEach((k, v) -> {
            headers.put(k, substitutor.replace(v));
        });

        params.forEach((k, v) -> {
            params.put(k, substitutor.replace(v));
        });

        body = substitutor.replace(body);
        // ${}占位符替换

        // 请求参数 请求头 请求体的替换
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(dataMap.containsKey("headers")) {
                Map<String, Object> headers = mapper.readValue((String) dataMap.get("headers"), Map.class);
                if(this.headers == null) {
                    this.headers = headers;
                } else {
                    this.headers.putAll(headers);
                }
            }
            if(dataMap.containsKey("params")) {
                Map<String, Object> params = mapper.readValue((String) dataMap.get("params"), Map.class);
                if(this.params == null) {
                    this.params = params;
                } else {
                    this.params.putAll(params);
                }
            }
            if(dataMap.containsKey("body")) {
                this.body = (String) dataMap.get("body");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
