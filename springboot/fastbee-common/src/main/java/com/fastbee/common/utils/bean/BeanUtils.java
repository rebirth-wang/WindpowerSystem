//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeanUtils extends org.springframework.beans.BeanUtils {
    private static final int aS = 3;
    private static final Pattern aT = Pattern.compile("get(\\p{javaUpperCase}\\w*)");
    private static final Pattern aU = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    public static void copyBeanProp(Object dest, Object src) {
        try {
            copyProperties(src, dest);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static List<Method> getSetterMethods(Object obj) {
        ArrayList var1 = new ArrayList();
        Method[] var2 = obj.getClass().getMethods();

        for(Method var6 : var2) {
            Matcher var7 = aU.matcher(var6.getName());
            if (var7.matches() && var6.getParameterTypes().length == 1) {
                var1.add(var6);
            }
        }

        return var1;
    }

    public static List<Method> getGetterMethods(Object obj) {
        ArrayList var1 = new ArrayList();
        Method[] var2 = obj.getClass().getMethods();

        for(Method var6 : var2) {
            Matcher var7 = aT.matcher(var6.getName());
            if (var7.matches() && var6.getParameterTypes().length == 0) {
                var1.add(var6);
            }
        }

        return var1;
    }

    public static boolean isMethodPropEquals(String m1, String m2) {
        return m1.substring(3).equals(m2.substring(3));
    }
}
