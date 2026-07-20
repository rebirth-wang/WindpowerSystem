//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.AntPathMatcher;

import com.fastbee.common.core.text.StrFormatter;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final String aJ = "";
    private static final char aK = '_';
    public static final String SEPARATOR2 = ",";
    public static final String SLASH = "/";

    public static <T> T nvl(T value, T defaultValue) {
        return (T)(value != null ? value : defaultValue);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || objects.length == 0;
    }

    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(String str) {
        return isNull(str) || "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return "";
        } else {
            if (start < 0) {
                start += str.length();
            }

            if (start < 0) {
                start = 0;
            }

            return start > str.length() ? "" : str.substring(start);
        }
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return "";
        } else {
            if (end < 0) {
                end += str.length();
            }

            if (start < 0) {
                start += str.length();
            }

            if (end > str.length()) {
                end = str.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }

                return str.substring(start, end);
            }
        }
    }

    public static String format(String template, Object... params) {
        return !isEmpty(params) && !isEmpty(template) ? StrFormatter.format(template, params) : template;
    }

    public static boolean ishttp(String link) {
        return startsWithAny(link, new CharSequence[]{"http://", "https://"});
    }

    public static final Set<String> str2Set(String str, String sep) {
        return new HashSet(str2List(str, sep, true, false));
    }

    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        ArrayList var4 = new ArrayList();
        if (isEmpty(str)) {
            return var4;
        } else if (filterBlank && isBlank(str)) {
            return var4;
        } else {
            String[] var5 = str.split(sep);

            for(String var9 : var5) {
                if (!filterBlank || !isBlank(var9)) {
                    if (trim) {
                        var9 = var9.trim();
                    }

                    var4.add(var9);
                }
            }

            return var4;
        }
    }

    public static boolean containsAny(Collection<String> collection, String... array) {
        if (!isEmpty(collection) && !isEmpty(array)) {
            for(String var5 : array) {
                if (collection.contains(var5)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences) {
        if (!isEmpty(cs) && !isEmpty(searchCharSequences)) {
            for(CharSequence var5 : searchCharSequences) {
                if (containsIgnoreCase(cs, var5)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder var1 = new StringBuilder();
            boolean var2 = true;
            boolean var3 = true;
            boolean var4 = true;

            for(int var5 = 0; var5 < str.length(); ++var5) {
                char var6 = str.charAt(var5);
                if (var5 > 0) {
                    var2 = Character.isUpperCase(str.charAt(var5 - 1));
                } else {
                    var2 = false;
                }

                var3 = Character.isUpperCase(var6);
                if (var5 < str.length() - 1) {
                    var4 = Character.isUpperCase(str.charAt(var5 + 1));
                }

                if (var2 && var3 && !var4) {
                    var1.append('_');
                } else if (var5 != 0 && !var2 && var3) {
                    var1.append('_');
                }

                var1.append(Character.toLowerCase(var6));
            }

            return var1.toString();
        }
    }

    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for(String var5 : strs) {
                if (str.equalsIgnoreCase(trim(var5))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String convertToCamelCase(String name) {
        StringBuilder var1 = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            if (!name.contains("_")) {
                return name.substring(0, 1).toUpperCase() + name.substring(1);
            } else {
                String[] var2 = name.split("_");

                for(String var6 : var2) {
                    if (!var6.isEmpty()) {
                        var1.append(var6.substring(0, 1).toUpperCase());
                        var1.append(var6.substring(1).toLowerCase());
                    }
                }

                return var1.toString();
            }
        } else {
            return "";
        }
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        } else {
            s = s.toLowerCase();
            StringBuilder var1 = new StringBuilder(s.length());
            boolean var2 = false;

            for(int var3 = 0; var3 < s.length(); ++var3) {
                char var4 = s.charAt(var3);
                if (var4 == '_') {
                    var2 = true;
                } else if (var2) {
                    var1.append(Character.toUpperCase(var4));
                    var2 = false;
                } else {
                    var1.append(var4);
                }
            }

            return var1.toString();
        }
    }

    public static boolean matches(String str, List<String> strs) {
        if (!isEmpty(str) && !isEmpty(strs)) {
            for(String var3 : strs) {
                if (isMatch(var3, str)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher var2 = new AntPathMatcher();
        return var2.match(pattern, url);
    }

    public static <T> T cast(Object obj) {
        return (T)obj;
    }

    public static final String padl(Number num, int size) {
        return padl(num.toString(), size, '0');
    }

    public static final String padl(String s, int size, char c) {
        StringBuilder var3 = new StringBuilder(size);
        if (s != null) {
            int var4 = s.length();
            if (s.length() > size) {
                return s.substring(var4 - size, var4);
            }

            for(int var5 = size - var4; var5 > 0; --var5) {
                var3.append(c);
            }

            var3.append(s);
        } else {
            for(int var6 = size; var6 > 0; --var6) {
                var3.append(c);
            }
        }

        return var3.toString();
    }

    public static String upperCase(String str) {
        char[] var1 = str.toLowerCase().toCharArray();
        if (var1[0] >= 'a' && var1[0] <= 'z') {
            var1[0] = (char)(var1[0] - 32);
        }

        return new String(var1);
    }

    public static String toString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof ByteBuf) {
            return ByteBufUtil.hexDump((ByteBuf)value);
        } else if (!value.getClass().isArray()) {
            return value.toString();
        } else {
            StringBuilder var1 = new StringBuilder(32);
            toString(value, var1);
            return var1.toString();
        }
    }

    public static StringBuilder toString(Object value, StringBuilder builder) {
        if (value == null) {
            return builder;
        } else {
            builder.append('[');
            int var2 = builder.length();
            if (value instanceof long[]) {
                long[] var3 = (long[])value;

                for(long var7 : var3) {
                    builder.append(var7).append(',');
                }
            } else if (value instanceof int[]) {
                int[] var9 = (int[])value;

                for(int var50 : var9) {
                    builder.append(var50).append(',');
                }
            } else if (value instanceof short[]) {
                short[] var10 = (short[])value;

                for(short var51 : var10) {
                    builder.append(var51).append(',');
                }
            } else if (value instanceof byte[]) {
                byte[] var11 = (byte[])value;

                for(byte var52 : var11) {
                    builder.append(var52).append(',');
                }
            } else if (value instanceof char[]) {
                char[] var12 = (char[])value;

                for(char var53 : var12) {
                    builder.append(var53).append(',');
                }
            } else if (value instanceof double[]) {
                double[] var13 = (double[])value;

                for(double var54 : var13) {
                    builder.append(var54).append(',');
                }
            } else if (value instanceof float[]) {
                float[] var14 = (float[])value;

                for(float var55 : var14) {
                    builder.append(var55).append(',');
                }
            } else if (value instanceof boolean[]) {
                boolean[] var15 = (boolean[])value;

                for(boolean var56 : var15) {
                    builder.append(var56).append(',');
                }
            } else if (value instanceof String[]) {
                String[] var16 = (String[])value;

                for(String var57 : var16) {
                    builder.append(var57).append(',');
                }
            } else if (a(value)) {
                Object[] var17 = (Object[]) value;

                for(Object var58 : var17) {
                    toString(var58, builder).append(',');
                }
            } else if (value instanceof Object[]) {
                Object[] var18 = (Object[]) value;

                for(Object var59 : var18) {
                    builder.append(var59).append(',');
                }
            }

            int var19 = builder.length();
            if (var19 <= var2) {
                builder.append(']');
            } else {
                builder.setCharAt(var19 - 1, ']');
            }

            return builder;
        }
    }

    private static boolean a(Object var0) {
        Class var1 = var0.getClass().getComponentType();
        return var1 == null ? false : var1.isArray();
    }

    public static String leftPad(String str, int size, char ch) {
        int var3 = str.length();
        int var4 = size - var3;
        if (var4 <= 0) {
            return str;
        } else {
            char[] var5 = new char[size];
            str.getChars(0, var3, var5, var4);

            while(var4 > 0) {
                --var4;
                var5[var4] = ch;
            }

            return new String(var5);
        }
    }

    public static Integer matcherNum(String str) {
        Pattern var1 = Pattern.compile("\\d+");
        Matcher var2 = var1.matcher(str);
        return var2.find() ? Integer.parseInt(var2.group()) : 0;
    }

    public static List<String> getVariables(String variable, String str) {
        ArrayList var2 = new ArrayList();
        Pattern var3 = null;
        switch (variable) {
            case "${}":
                var3 = Pattern.compile("\\$\\{([^}]+)}");
                break;
            case "{{}}":
                var3 = Pattern.compile("\\{\\{([^}]+)}}");
                break;
            case "{}":
                var3 = Pattern.compile("\\{([^}]+)}");
                break;
            case "#{}":
                var3 = Pattern.compile("#\\{([^}]+)}");
        }

        assert var3 != null;

        Matcher var4 = var3.matcher(str);

        while(var4.find()) {
            var2.add(var4.group(1));
        }

        return var2;
    }

    public static List<String> getWeChatMiniVariables(String content) {
        ArrayList var1 = new ArrayList();
        Pattern var2 = Pattern.compile("\\{\\{([^}]+)}}");
        Matcher var3 = var2.matcher(content);

        while(var3.find()) {
            var1.add(var3.group(1).replace(".DATA", ""));
        }

        return var1;
    }

    public static String parse(String openToken, String closeToken, String text, Object... args) {
        if (args != null && args.length > 0) {
            int var4 = 0;
            if (text != null && !text.isEmpty()) {
                char[] var5 = text.toCharArray();
                int var6 = 0;
                int var7 = text.indexOf(openToken, var6);
                if (var7 == -1) {
                    return text;
                } else {
                    StringBuilder var8 = new StringBuilder();

                    for(StringBuilder var9 = null; var7 > -1; var7 = text.indexOf(openToken, var6)) {
                        if (var7 > 0 && var5[var7 - 1] == '\\') {
                            var8.append(var5, var6, var7 - var6 - 1).append(openToken);
                            var6 = var7 + openToken.length();
                        } else {
                            if (var9 == null) {
                                var9 = new StringBuilder();
                            } else {
                                var9.setLength(0);
                            }

                            var8.append(var5, var6, var7 - var6);
                            var6 = var7 + openToken.length();

                            int var10;
                            for(var10 = text.indexOf(closeToken, var6); var10 > -1; var10 = text.indexOf(closeToken, var6)) {
                                if (var10 <= var6 || var5[var10 - 1] != '\\') {
                                    var9.append(var5, var6, var10 - var6);
                                    int var10000 = var10 + closeToken.length();
                                    break;
                                }

                                var9.append(var5, var6, var10 - var6 - 1).append(closeToken);
                                var6 = var10 + closeToken.length();
                            }

                            if (var10 == -1) {
                                var8.append(var5, var7, var5.length - var7);
                                var6 = var5.length;
                            } else {
                                String var11 = var4 <= args.length - 1 ? (args[var4] == null ? "" : args[var4].toString()) : var9.toString();
                                var8.append(var11);
                                var6 = var10 + closeToken.length();
                                ++var4;
                            }
                        }
                    }

                    if (var6 < var5.length) {
                        var8.append(var5, var6, var5.length - var6);
                    }

                    return var8.toString();
                }
            } else {
                return "";
            }
        } else {
            return text;
        }
    }

    public static String reverse(String str) {
        return (new StringBuilder(str)).reverse().toString();
    }

    public static String parseVariable(String text, Object... args) {
        return parse("${", "}", text, args);
    }

    public static String strReplaceVariable(String openIndex, String closeIndex, String content, LinkedHashMap<String, String> map) {
        if (!isEmpty(content) && !MapUtils.isEmpty(map)) {
            StringBuilder var4 = new StringBuilder(content);

            for(Map.Entry var6 : map.entrySet()) {
                var4 = new StringBuilder(var4.toString().replace(openIndex + (String)var6.getKey() + closeIndex, (CharSequence)var6.getValue()));
            }

            return var4.toString();
        } else {
            return content;
        }
    }

    public static List<String> splitEvenly(String str, int size) {
        ArrayList var2 = new ArrayList();
        int var3 = str.length();
        if (size <= var3 && size > 0) {
            for(int var4 = 0; var4 < var3; var4 += size) {
                var2.add(str.substring(var4, Math.min(var3, var4 + size)));
            }

            return var2;
        } else {
            throw new IllegalArgumentException("Size is too large or too small.");
        }
    }

    public static String underlineToHump(String param) {
        StringBuilder var1 = new StringBuilder();
        String[] var2 = param.split("_");

        for(String var6 : var2) {
            var1.append(var6.substring(0, 1).toUpperCase()).append(var6.substring(1));
        }

        return var1.toString();
    }

    public static String toGet(String param) {
        String var1 = underlineToHump(param);
        return "get" + var1 + "()";
    }

    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        return (List<T>)(CollUtil.isEmpty(collection) ? CollUtil.newArrayList(new Object[0]) : (List)collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
