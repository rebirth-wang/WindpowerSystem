//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.core.text.Convert;
import com.fastbee.common.utils.DateUtils;

public class ReflectUtils {
    private static final String cw = "set";
    private static final String cx = "get";
    private static final String cy = "$$";
    private static Logger av = LoggerFactory.getLogger(ReflectUtils.class);

    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object var2 = obj;

        for(String var6 : StringUtils.split(propertyName, ".")) {
            String var7 = "get" + StringUtils.capitalize(var6);
            var2 = invokeMethod(var2, var7, new Class[0], new Object[0]);
        }

        return (E)var2;
    }

    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object var3 = obj;
        String[] var4 = StringUtils.split(propertyName, ".");

        for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var5 < var4.length - 1) {
                String var6 = "get" + StringUtils.capitalize(var4[var5]);
                var3 = invokeMethod(var3, var6, new Class[0], new Object[0]);
            } else {
                String var7 = "set" + StringUtils.capitalize(var4[var5]);
                invokeMethodByName(var3, var7, new Object[]{value});
            }
        }

    }

    public static <E> E getFieldValue(Object obj, String fieldName) {
        Field var2 = getAccessibleField(obj, fieldName);
        if (var2 == null) {
            av.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return null;
        } else {
            Object var3 = null;

            try {
                var3 = var2.get(obj);
            } catch (IllegalAccessException var5) {
                av.error("不可能抛出的异常{}", var5.getMessage());
            }

            return (E)var3;
        }
    }

    public static <E> void setFieldValue(Object obj, String fieldName, E value) {
        Field var3 = getAccessibleField(obj, fieldName);
        if (var3 == null) {
            av.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
        } else {
            try {
                var3.set(obj, value);
            } catch (IllegalAccessException var5) {
                av.error("不可能抛出的异常: {}", var5.getMessage());
            }

        }
    }

    public static <E> E invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        if (obj != null && methodName != null) {
            Method var4 = getAccessibleMethod(obj, methodName, parameterTypes);
            if (var4 == null) {
                av.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
                return null;
            } else {
                try {
                    return (E)var4.invoke(obj, args);
                } catch (Exception var7) {
                    String var6 = "method: " + var4 + ", obj: " + obj + ", args: " + args + "";
                    throw convertReflectionExceptionToUnchecked(var6, var7);
                }
            }
        } else {
            return null;
        }
    }

    public static <E> E invokeMethodByName(Object obj, String methodName, Object[] args) {
        Method var3 = getAccessibleMethodByName(obj, methodName, args.length);
        if (var3 == null) {
            av.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        } else {
            try {
                Class[] var4 = var3.getParameterTypes();

                for(int var7 = 0; var7 < var4.length; ++var7) {
                    if (args[var7] != null && !args[var7].getClass().equals(var4[var7])) {
                        if (var4[var7] == String.class) {
                            args[var7] = Convert.toStr(args[var7]);
                            if (StringUtils.endsWith((String)args[var7], ".0")) {
                                args[var7] = StringUtils.substringBefore((String)args[var7], ".0");
                            }
                        } else if (var4[var7] == Integer.class) {
                            args[var7] = Convert.toInt(args[var7]);
                        } else if (var4[var7] == Long.class) {
                            args[var7] = Convert.toLong(args[var7]);
                        } else if (var4[var7] == Double.class) {
                            args[var7] = Convert.toDouble(args[var7]);
                        } else if (var4[var7] == Float.class) {
                            args[var7] = Convert.toFloat(args[var7]);
                        } else if (var4[var7] == Date.class) {
                            if (args[var7] instanceof String) {
                                args[var7] = DateUtils.parseDate(args[var7]);
                            } else {
                                args[var7] = DateUtil.getJavaDate((Double)args[var7]);
                            }
                        } else if (var4[var7] == Boolean.TYPE || var4[var7] == Boolean.class) {
                            args[var7] = Convert.toBool(args[var7]);
                        }
                    }
                }

                return (E)var3.invoke(obj, args);
            } catch (Exception var6) {
                String var5 = "method: " + var3 + ", obj: " + obj + ", args: " + args + "";
                throw convertReflectionExceptionToUnchecked(var5, var6);
            }
        }
    }

    public static Field getAccessibleField(Object obj, String fieldName) {
        if (obj == null) {
            return null;
        } else {
            Validate.notBlank(fieldName, "fieldName can't be blank", new Object[0]);

            for(Class var2 = obj.getClass(); var2 != Object.class; var2 = var2.getSuperclass()) {
                try {
                    Field var3 = var2.getDeclaredField(fieldName);
                    makeAccessible(var3);
                    return var3;
                }catch (Exception e){
                    return null;
                }
            }

            return null;
        }
    }

    public static Method getAccessibleMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        if (obj == null) {
            return null;
        } else {
            Validate.notBlank(methodName, "methodName can't be blank", new Object[0]);

            for(Class var3 = obj.getClass(); var3 != Object.class; var3 = var3.getSuperclass()) {
                try {
                    Method var4 = var3.getDeclaredMethod(methodName, parameterTypes);
                    makeAccessible(var4);
                    return var4;
                }catch (Exception e){
                    return null;
                }
            }

            return null;
        }
    }

    public static Method getAccessibleMethodByName(Object obj, String methodName, int argsNum) {
        if (obj == null) {
            return null;
        } else {
            Validate.notBlank(methodName, "methodName can't be blank", new Object[0]);

            for(Class var3 = obj.getClass(); var3 != Object.class; var3 = var3.getSuperclass()) {
                Method[] var4 = var3.getDeclaredMethods();

                for(Method var8 : var4) {
                    if (var8.getName().equals(methodName) && var8.getParameterTypes().length == argsNum) {
                        makeAccessible(var8);
                        return var8;
                    }
                }
            }

            return null;
        }
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }

    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }

    }

    public static <T> Class<T> getClassGenricType(Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    public static Class getClassGenricType(Class clazz, int index) {
        Type var2 = clazz.getGenericSuperclass();
        if (!(var2 instanceof ParameterizedType)) {
            av.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        } else {
            Type[] var3 = ((ParameterizedType)var2).getActualTypeArguments();
            if (index < var3.length && index >= 0) {
                if (!(var3[index] instanceof Class)) {
                    av.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
                    return Object.class;
                } else {
                    return (Class)var3[index];
                }
            } else {
                av.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + var3.length);
                return Object.class;
            }
        }
    }

    public static Class<?> getUserClass(Object instance) {
        if (instance == null) {
            throw new RuntimeException("Instance must not be null");
        } else {
            Class var1 = instance.getClass();
            if (var1 != null && var1.getName().contains("$$")) {
                Class var2 = var1.getSuperclass();
                if (var2 != null && !Object.class.equals(var2)) {
                    return var2;
                }
            }

            return var1;
        }
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e) {
        if (!(e instanceof IllegalAccessException) && !(e instanceof IllegalArgumentException) && !(e instanceof NoSuchMethodException)) {
            return e instanceof InvocationTargetException ? new RuntimeException(msg, ((InvocationTargetException)e).getTargetException()) : new RuntimeException(msg, e);
        } else {
            return new IllegalArgumentException(msg, e);
        }
    }
}
