//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fastbee.common.annotation.Anonymous;

@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware {
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");
    private ApplicationContext applicationContext;
    private List<String> urls = new ArrayList();
    public String ASTERISK = "*";

    public void afterPropertiesSet() {
        String[] beanNames = this.applicationContext.getBeanNamesForType(RequestMappingHandlerMapping.class);
        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping)this.applicationContext.getBean(beanNames[0], RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        map.keySet().forEach((info) -> {
            HandlerMethod handlerMethod = (HandlerMethod)map.get(info);
            Anonymous method = (Anonymous)AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
            Optional.ofNullable(method).ifPresent((anonymous) -> info.getDirectPaths().forEach((url) -> this.urls.add(RegExUtils.replaceAll(url, PATTERN, this.ASTERISK))));
            Anonymous controller = (Anonymous)AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);
            Optional.ofNullable(controller).ifPresent((anonymous) -> info.getDirectPaths().forEach((url) -> this.urls.add(RegExUtils.replaceAll(url, PATTERN, this.ASTERISK))));
        });
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    public List<String> getUrls() {
        return this.urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
