//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.spring;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.utils.StringUtils;

@Component
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static ConfigurableListableBeanFactory cK;
    private static ApplicationContext cL;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        cK = beanFactory;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cL = applicationContext;
    }

    public static <T> T getBean(String name) throws BeansException {
        return (T)cK.getBean(name);
    }

    public static <T> T getBean(Class<T> clz) throws BeansException {
        Object var1 = cK.getBean(clz);
        return (T)var1;
    }

    public static boolean containsBean(String name) {
        return cK.containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return cK.isSingleton(name);
    }

    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return cK.getType(name);
    }

    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return cK.getAliases(name);
    }

    public static <T> T getAopProxy(T invoker) {
        return (T)AopContext.currentProxy();
    }

    public static String[] getActiveProfiles() {
        return cL.getEnvironment().getActiveProfiles();
    }

    public static String getActiveProfile() {
        String[] var0 = getActiveProfiles();
        return StringUtils.isNotEmpty(var0) ? var0[0] : null;
    }

    public static String getRequiredProperty(String key) {
        return cL.getEnvironment().getRequiredProperty(key);
    }

    public static <T> Map<String, T> getBeanWithAnnotation(Class<? extends Annotation> annotation) {
        HashMap var1 = new HashMap();
        Map var2 = cL.getBeansWithAnnotation(annotation);
        if (CollectionUtils.isEmpty(var2)) {
            return var1;
        } else {
            var2.forEach((var1x, var2x) -> var1.put(var1x, var2x));
            return var1;
        }
    }
}
