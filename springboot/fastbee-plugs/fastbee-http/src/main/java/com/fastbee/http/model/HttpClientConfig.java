package com.fastbee.http.model;

import java.util.Map;

import com.dtflys.forest.http.ForestRequestType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpClientConfig {
    private String backend;
    private String url;
    private ForestRequestType method;
    private Map<String, Object> headers;
    private Map<String, Object> querys;
    private String body;
}
