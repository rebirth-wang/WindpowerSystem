package com.fastbee.coap.handler;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResourceHandler implements ResourceHandler {
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getInterface() {
        return null;
    }

    @Override
    public String getResourceType() {
        return null;
    }


    public Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = pair.substring(0, idx);
                    String value = pair.substring(idx + 1);
                    queryParams.put(key, value);
                }
            }
        }
        return queryParams;
    }
}
