//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.interceptor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fastbee.common.annotation.RepeatSubmit;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.filter.RepeatedlyRequestWrapper;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.http.HttpHelper;
import com.fastbee.framework.interceptor.RepeatSubmitInterceptor;

@Component
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor {
    public final String REPEAT_PARAMS = "repeatParams";
    public final String REPEAT_TIME = "repeatTime";
    @Value("${token.header}")
    private String header;
    @Autowired
    private RedisCache redisCache;

    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper repeatedlyRequest) {
            nowParams = HttpHelper.getBodyString(repeatedlyRequest);
        }

        if (StringUtils.isEmpty(nowParams)) {
            nowParams = JSON.toJSONString(request.getParameterMap());
        }

        Map<String, Object> nowDataMap = new HashMap();
        nowDataMap.put("repeatParams", nowParams);
        nowDataMap.put("repeatTime", System.currentTimeMillis());
        String url = request.getRequestURI();
        String submitKey = StringUtils.trimToEmpty(request.getHeader(this.header));
        String cacheRepeatKey = "repeat_submit:" + url + submitKey;
        Object sessionObj = this.redisCache.getCacheObject(cacheRepeatKey);
        if (sessionObj != null) {
            Map<String, Object> sessionMap = (Map)sessionObj;
            if (sessionMap.containsKey(url)) {
                Map<String, Object> preDataMap = (Map)sessionMap.get(url);
                if (this.compareParams(nowDataMap, preDataMap) && this.compareTime(nowDataMap, preDataMap, annotation.interval())) {
                    return true;
                }
            }
        }

        Map<String, Object> cacheMap = new HashMap();
        cacheMap.put(url, nowDataMap);
        this.redisCache.setCacheObject(cacheRepeatKey, cacheMap, annotation.interval(), TimeUnit.MILLISECONDS);
        return false;
    }

    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String)nowMap.get("repeatParams");
        String preParams = (String)preMap.get("repeatParams");
        return nowParams.equals(preParams);
    }

    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = (Long)nowMap.get("repeatTime");
        long time2 = (Long)preMap.get("repeatTime");
        return time1 - time2 < (long)interval;
    }
}
