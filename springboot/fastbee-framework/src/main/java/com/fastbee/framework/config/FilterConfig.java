//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastbee.common.filter.RepeatableFilter;
import com.fastbee.common.filter.XssFilter;
import com.fastbee.common.utils.StringUtils;

@Configuration
public class FilterConfig {
    @Value("${xss.excludes}")
    private String excludes;
    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @Bean
    @ConditionalOnProperty(
            value = {"xss.enabled"},
            havingValue = "true"
    )
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[0]);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns(StringUtils.split(this.urlPatterns, ","));
        registration.setName("xssFilter");
        registration.setOrder(Integer.MIN_VALUE);
        Map<String, String> initParameters = new HashMap();
        initParameters.put("excludes", this.excludes);
        registration.setInitParameters(initParameters);
        return registration;
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setName("repeatableFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }


}
