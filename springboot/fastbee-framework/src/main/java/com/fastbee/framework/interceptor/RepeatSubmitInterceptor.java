//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.interceptor;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fastbee.common.annotation.RepeatSubmit;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.ServletUtils;

@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = (RepeatSubmit)method.getAnnotation(RepeatSubmit.class);
            if (annotation != null && this.isRepeatSubmit(request, annotation)) {
                AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public abstract boolean isRepeatSubmit(HttpServletRequest var1, RepeatSubmit var2);
}
