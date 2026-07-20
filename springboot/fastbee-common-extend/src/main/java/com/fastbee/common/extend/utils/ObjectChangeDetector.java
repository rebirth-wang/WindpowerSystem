package com.fastbee.common.extend.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 对象属性变更检测工具类
 * @author zzy
 * @date 2025-08-21 15:36
 */
public class ObjectChangeDetector {
    
    // 日期格式化模式
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 自定义字段显示名称注解
     */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    public @interface DisplayName {
        String value();
    }
    
    /**
     * 忽略字段注解
     */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    public @interface IgnoreChange {
    }
    
    /**
     * 日期格式化注解
     */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    public @interface DateFormat {
        String value() default "yyyy-MM-dd HH:mm:ss";
    }
    
    /**
     * 枚举字段注解 - 用于标注存储枚举值的字段
     */
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    public @interface EnumField {
        Class<? extends Enum<?>> enumClass();
        String valueMethod() default "getCode";
        String descriptionMethod() default "getDescription";
    }
    
    /**
     * 检测两个对象之间的属性变更
     * @param oldObj 旧对象
     * @param newObj 新对象
     * @param <T> 对象类型
     * @return 变更描述字符串
     */
    public static <T> String detectChanges(T oldObj, T newObj) {
        return detectChanges(oldObj, newObj, new String[0]);
    }
    
    /**
     * 检测两个对象之间的属性变更（排除指定字段）
     * @param oldObj 旧对象
     * @param newObj 新对象
     * @param excludeFields 要排除的字段名
     * @param <T> 对象类型
     * @return 变更描述字符串
     */
    public static <T> String detectChanges(T oldObj, T newObj, String... excludeFields) {
        if (oldObj == null && newObj == null) {
            return null;
        } else if (oldObj == null) {
            return "新增";
        } else if (newObj == null) {
            return null;
        } else if (!oldObj.getClass().equals(newObj.getClass())) {
            return null;
        }
        
        Set<String> excludedFieldSet = new HashSet<>(Arrays.asList(excludeFields));
        List<String> changes = new ArrayList<>();
        Class<?> clazz = oldObj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            // 检查是否被忽略
            if (field.isAnnotationPresent(IgnoreChange.class)) {
                continue;
            }
            
            // 检查是否在排除列表中
            if (excludedFieldSet.contains(field.getName())) {
                continue;
            }
            
            try {
                field.setAccessible(true);
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);
                
                if (isChanged(oldValue, newValue)) {
                    String fieldName = getFieldDisplayName(field);
                    String change = String.format("%s：由 %s 变更为 %s", 
                            fieldName, 
                            formatValue(field, oldValue), 
                            formatValue(field, newValue));
                    changes.add(change);
                }
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }
        
        return String.join("，", changes);
    }
    
    /**
     * 获取字段的显示名称（支持注解处理）
     */
    private static String getFieldDisplayName(Field field) {
        // 如果字段有@DisplayName注解，使用注解的值作为显示名称
        if (field.isAnnotationPresent(DisplayName.class)) {
            DisplayName displayName = field.getAnnotation(DisplayName.class);
            return displayName.value();
        }
        
        // 默认返回字段名
        return field.getName();
    }
    
    /**
     * 判断值是否发生变化
     */
    private static boolean isChanged(Object oldValue, Object newValue) {
        if (oldValue == null && newValue == null) {
            return false;
        }
        
        if (oldValue == null || newValue == null) {
            return true;
        }
        
        // 处理数组类型的比较
        if (oldValue.getClass().isArray() && newValue.getClass().isArray()) {
            return !arraysEqual(oldValue, newValue);
        }
        
        // 处理集合类型的比较
        if (oldValue instanceof Collection && newValue instanceof Collection) {
            return !collectionsEqual((Collection<?>) oldValue, (Collection<?>) newValue);
        }
        
        // 处理Map类型的比较
        if (oldValue instanceof Map && newValue instanceof Map) {
            return !mapsEqual((Map<?, ?>) oldValue, (Map<?, ?>) newValue);
        }
        
        // 处理日期类型的比较
        if (oldValue instanceof Date && newValue instanceof Date) {
            return !((Date) oldValue).equals((Date) newValue);
        }
        
        return !oldValue.equals(newValue);
    }
    
    /**
     * 比较两个数组是否相等
     */
    private static boolean arraysEqual(Object array1, Object array2) {
        if (array1 instanceof Object[] && array2 instanceof Object[]) {
            return Arrays.equals((Object[]) array1, (Object[]) array2);
        } else if (array1 instanceof int[] && array2 instanceof int[]) {
            return Arrays.equals((int[]) array1, (int[]) array2);
        } else if (array1 instanceof long[] && array2 instanceof long[]) {
            return Arrays.equals((long[]) array1, (long[]) array2);
        } else if (array1 instanceof double[] && array2 instanceof double[]) {
            return Arrays.equals((double[]) array1, (double[]) array2);
        } else if (array1 instanceof float[] && array2 instanceof float[]) {
            return Arrays.equals((float[]) array1, (float[]) array2);
        } else if (array1 instanceof boolean[] && array2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) array1, (boolean[]) array2);
        } else if (array1 instanceof byte[] && array2 instanceof byte[]) {
            return Arrays.equals((byte[]) array1, (byte[]) array2);
        } else if (array1 instanceof char[] && array2 instanceof char[]) {
            return Arrays.equals((char[]) array1, (char[]) array2);
        } else if (array1 instanceof short[] && array2 instanceof short[]) {
            return Arrays.equals((short[]) array1, (short[]) array2);
        }
        
        return false;
    }
    
    /**
     * 比较两个集合是否相等
     */
    private static boolean collectionsEqual(Collection<?> col1, Collection<?> col2) {
        if (col1.size() != col2.size()) {
            return false;
        }
        
        // 使用迭代器比较每个元素
        Iterator<?> it1 = col1.iterator();
        Iterator<?> it2 = col2.iterator();
        
        while (it1.hasNext() && it2.hasNext()) {
            Object o1 = it1.next();
            Object o2 = it2.next();
            
            if (!objectsEqual(o1, o2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 比较两个Map是否相等
     */
    private static boolean mapsEqual(Map<?, ?> map1, Map<?, ?> map2) {
        if (map1.size() != map2.size()) {
            return false;
        }
        
        for (Map.Entry<?, ?> entry : map1.entrySet()) {
            Object key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);
            
            if (!objectsEqual(value1, value2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 通用对象比较方法
     */
    private static boolean objectsEqual(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        
        if (obj1 == null || obj2 == null) {
            return false;
        }
        
        return obj1.equals(obj2);
    }
    
    /**
     * 格式化值显示
     */
    private static String formatValue(Field field, Object value) {
        if (value == null) {
            return "空";
        }
        
        // 处理枚举字段值的格式化
        if (field.isAnnotationPresent(EnumField.class)) {
            return formatEnumFieldValue(field, value);
        }
        
        // 处理枚举类型的格式化
        if (value instanceof Enum) {
            return formatEnum(field, (Enum<?>) value);
        }
        
        // 处理日期类型的格式化
        if (value instanceof Date) {
            return formatDate(field, (Date) value);
        }
        
        // 处理数组类型的格式化
        if (value.getClass().isArray()) {
            return formatArray(value);
        }
        
        // 处理集合类型的格式化
        if (value instanceof Collection) {
            return formatCollection((Collection<?>) value);
        }
        
        // 处理Map类型的格式化
        if (value instanceof Map) {
            return formatMap((Map<?, ?>) value);
        }
        
        // 对于字符串，添加引号
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        
        return value.toString();
    }
    
    /**
     * 格式化枚举字段值显示
     */
    private static String formatEnumFieldValue(Field field, Object fieldValue) {
        EnumField enumField = field.getAnnotation(EnumField.class);
        Class<? extends Enum<?>> enumClass = enumField.enumClass();
        String valueMethod = enumField.valueMethod();
        String descriptionMethod = enumField.descriptionMethod();
        
        try {
            // 获取枚举的所有值
            Method valuesMethod = enumClass.getMethod("values");
            Enum<?>[] enumValues = (Enum<?>[]) valuesMethod.invoke(null);
            
            // 查找匹配的枚举值
            for (Enum<?> enumValue : enumValues) {
                // 获取枚举的值方法（如getCode）
                Method valueGetter = enumValue.getClass().getMethod(valueMethod);
                Object enumFieldValue = valueGetter.invoke(enumValue);
                
                // 比较字段值和枚举值
                if (objectsEqual(enumFieldValue, fieldValue)) {
                    // 获取枚举的描述方法（如getDescription）
                    Method descriptionGetter = enumValue.getClass().getMethod(descriptionMethod);
                    Object description = descriptionGetter.invoke(enumValue);
                    return "\"" + description.toString() + "\"";
                }
            }
            
            // 如果没有找到匹配的枚举值，返回原始值
            return "\"" + fieldValue.toString() + "\"";
        } catch (Exception e) {
            // 如果处理失败，返回原始值
            return "\"" + fieldValue.toString() + "\"";
        }
    }
    
    /**
     * 格式化枚举显示
     */
    private static String formatEnum(Field field, Enum<?> enumValue) {
        try {
            // 尝试调用枚举的描述方法
            Method method = enumValue.getClass().getMethod("getDescription");
            Object description = method.invoke(enumValue);
            return "\"" + description.toString() + "\"";
        } catch (Exception e) {
            // 如果获取描述失败，返回枚举名称
            return "\"" + enumValue.name() + "\"";
        }
    }
    
    /**
     * 格式化日期显示
     */
    private static String formatDate(Field field, Date date) {
        String pattern = DATE_FORMAT_PATTERN;
        
        // 如果字段有@DateFormat注解，使用注解的格式
        if (field.isAnnotationPresent(DateFormat.class)) {
            DateFormat dateFormat = field.getAnnotation(DateFormat.class);
            pattern = dateFormat.value();
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return "\"" + sdf.format(date) + "\"";
    }
    
    /**
     * 格式化数组显示
     */
    private static String formatArray(Object array) {
        if (array instanceof Object[]) {
            return Arrays.toString((Object[]) array);
        } else if (array instanceof int[]) {
            return Arrays.toString((int[]) array);
        } else if (array instanceof long[]) {
            return Arrays.toString((long[]) array);
        } else if (array instanceof double[]) {
            return Arrays.toString((double[]) array);
        } else if (array instanceof float[]) {
            return Arrays.toString((float[]) array);
        } else if (array instanceof boolean[]) {
            return Arrays.toString((boolean[]) array);
        } else if (array instanceof byte[]) {
            return Arrays.toString((byte[]) array);
        } else if (array instanceof char[]) {
            return Arrays.toString((char[]) array);
        } else if (array instanceof short[]) {
            return Arrays.toString((short[]) array);
        }
        
        return array.toString();
    }
    
    /**
     * 格式化集合显示
     */
    private static String formatCollection(Collection<?> collection) {
        if (collection.isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (Object item : collection) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(formatSimpleValue(item));
        }
        sb.append("]");
        
        return sb.toString();
    }
    
    /**
     * 格式化Map显示
     */
    private static String formatMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(formatSimpleValue(entry.getKey()))
              .append("=")
              .append(formatSimpleValue(entry.getValue()));
        }
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 简化值格式化（用于集合和Map内部）
     */
    private static String formatSimpleValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        
        if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            return "\"" + sdf.format((Date) value) + "\"";
        }
        
        if (value instanceof Enum) {
            return "\"" + ((Enum<?>) value).name() + "\"";
        }
        
        return value.toString();
    }
}