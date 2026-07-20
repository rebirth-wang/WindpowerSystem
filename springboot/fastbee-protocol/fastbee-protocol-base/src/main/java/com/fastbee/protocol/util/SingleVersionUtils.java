package com.fastbee.protocol.util;

import java.lang.reflect.Field;
import java.util.*;

import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.model.ModelRegistry;
import com.fastbee.protocol.base.model.RuntimeSchema;
import com.fastbee.protocol.base.struc.BaseStructure;

/**
 * 单版本加载
 * @author bill
 */
public abstract class SingleVersionUtils {
    private static final Map<String, RuntimeSchema> CACHE = new WeakHashMap<>();

    public static <T> RuntimeSchema<T> getActiveModel(Class<T> typeClass) {
        return getActiveModel(CACHE, typeClass);
    }

    public static <T> RuntimeSchema<T> getActiveModel(Map<String, RuntimeSchema> root, Class<T> typeClass) {
        RuntimeSchema<T> schema = root.get(typeClass.getName());
        //不支持循环引用
        if (schema != null) return schema;

        List<Field> fs = findFields(typeClass);
        if (fs.isEmpty()) return null;

        List<BaseStructure> fieldList = findFields(root, fs);
        BaseStructure[] fields = fieldList.toArray(new BaseStructure[fieldList.size()]);
        Arrays.sort(fields);

        schema = new RuntimeSchema(typeClass, 0, fields);
        root.put(typeClass.getName(), schema);
        return schema;
    }

    private static List<Field> findFields(Class typeClass) {
        Field[] fields = typeClass.getDeclaredFields();
        List<Field> result = new ArrayList<>(fields.length);

        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                result.add(f);
            }
        }
        return result;
    }

    private static List<BaseStructure> findFields(Map<String, RuntimeSchema> root, List<Field> fs) {
        int size = fs.size();
        List<BaseStructure> fields = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            Field f = fs.get(i);
            Column column = f.getDeclaredAnnotation(Column.class);
            if (column != null) {
                f.setAccessible(true);
                fillField(root, fields, column, f, i);
            }
        }
        return fields;
    }

    private static void fillField(Map<String, RuntimeSchema> root, List<BaseStructure> fields, Column column, Field f, int position) {
        BaseStructure BaseStructure = ModelRegistry.get(column, f);
        if (BaseStructure != null) {
            fields.add(BaseStructure.init(column, f, position));
        } else {
            RuntimeSchema schema = getActiveModel(root, ClassUtils.getGenericType(f));
            BaseStructure = ModelRegistry.get(column, f, schema);
            fields.add(BaseStructure.init(column, f, position));
        }
    }
}
