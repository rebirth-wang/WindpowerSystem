package com.fastbee.common.utils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.excel.support.cglib.beans.BeanMap;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanGenerator;

/**
 * @author lei lei
 * @date 2025-09-10 10:11
 * @description:
 */

public class DynamicBeanUtils {
    private static final Logger aw = LoggerFactory.getLogger(DynamicBeanUtils.class);

    public DynamicBeanUtils() {
    }

    public static Object getTarget(Object dest, Map<String, Object> addProperties) {
        PropertyUtilsBean var2 = new PropertyUtilsBean();
        PropertyDescriptor[] var3 = var2.getPropertyDescriptors(dest);
        HashMap<String,Class<?>> var4 = Maps.newHashMap();

        for(PropertyDescriptor var8 : var3) {
            if (!"class".equalsIgnoreCase(var8.getName())) {
                var4.put(var8.getName(), var8.getPropertyType());
            }
        }

        addProperties.forEach((var1, var2x) -> {
            Class var10000 = (Class)var4.put(var1, var2x.getClass());
        });
        a var10 = new a(dest.getClass(), var4);

        for(Map.Entry<String ,Class<?>> var12 : var4.entrySet()) {
            try {
                if (!addProperties.containsKey(var12.getKey())) {
                    var10.a((String)var12.getKey(), var2.getNestedProperty(dest, (String)var12.getKey()));
                } else {
                    var10.a((String)var12.getKey(), addProperties.get(var12.getKey()));
                }
            } catch (Exception var9) {
                aw.error(var9.getMessage(), var9);
            }
        }

        return var10.b();
    }

    private static class a {
        private Object ax;
        private BeanMap ay;

        public a(Class<?> var1, Map<String, Class<?>> var2) {
            this.ax = this.a(var1, var2);
            this.ay = BeanMap.create(this.ax);
        }

        public void a(String var1, Object var2) {
            this.ay.put(var1, var2);
        }

        public Object b(String var1) {
            return this.ay.get(var1);
        }

        public Object b() {
            return this.ax;
        }

        private Object a(Class<?> var1, Map<String, Class<?>> var2) {
            BeanGenerator var3 = new BeanGenerator();
            if (null != var1) {
                var3.setSuperclass(var1);
            }

            BeanGenerator.addProperties(var3, var2);
            return var3.create();
        }
    }
}
