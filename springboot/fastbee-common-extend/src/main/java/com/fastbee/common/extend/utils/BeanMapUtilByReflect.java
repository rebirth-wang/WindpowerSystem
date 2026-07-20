package com.fastbee.common.extend.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

public class BeanMapUtilByReflect {

    /**
     * 对象转Map
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    public static Map beanToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

    /**
     * bean转item对象
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    public static List<ThingsModelSimpleItem> beanToItem(Object object) throws IllegalAccessException {
        List<ThingsModelSimpleItem> result = new ArrayList<>();

        // 获取当前类和所有父类的字段
        Class<?> currentClass = object.getClass();
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                // 跳过静态字段（可选）
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                item.setId(field.getName());

                Object value = field.get(object);
                item.setValue(value != null ? value.toString() : "null");
                item.setTs(new Date());
                result.add(item);
            }
            // 获取父类继续处理
            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

    /**
     * map转对象
     * @param map
     * @param beanClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T mapToBean(Map map, Class<T> beanClass) throws Exception {
        T object = beanClass.newInstance();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                field.set(object, map.get(field.getName()));
            }
        }
        return object;
    }
}
