//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.fastbee.common.utils.StringUtils;

public class Convert {
    public static String toStr(Object value, String defaultValue) {
        if (null == value) {
            return defaultValue;
        } else {
            return value instanceof String ? (String)value : value.toString();
        }
    }

    public static String toStr(Object value) {
        return toStr(value, (String)null);
    }

    public static Character toChar(Object value, Character defaultValue) {
        if (null == value) {
            return defaultValue;
        } else if (value instanceof Character) {
            return (Character)value;
        } else {
            String var2 = toStr(value, (String)null);
            return StringUtils.isEmpty(var2) ? defaultValue : var2.charAt(0);
        }
    }

    public static Character toChar(Object value) {
        return toChar(value, (Character)null);
    }

    public static Byte toByte(Object value, Byte defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Byte) {
            return (Byte)value;
        } else if (value instanceof Number) {
            return ((Number)value).byteValue();
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return Byte.parseByte(var2);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Byte toByte(Object value) {
        return toByte(value, (Byte)null);
    }

    public static Short toShort(Object value, Short defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Short) {
            return (Short)value;
        } else if (value instanceof Number) {
            return ((Number)value).shortValue();
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return Short.parseShort(var2.trim());
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Short toShort(Object value) {
        return toShort(value, (Short)null);
    }

    public static Number toNumber(Object value, Number defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number) {
            return (Number)value;
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return NumberFormat.getInstance().parse(var2);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Number toNumber(Object value) {
        return toNumber(value, (Number)null);
    }

    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Integer) {
            return (Integer)value;
        } else if (value instanceof Number) {
            return ((Number)value).intValue();
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return Integer.parseInt(var2.trim());
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Integer toInt(Object value) {
        return toInt(value, (Integer)null);
    }

    public static Integer[] toIntArray(String str) {
        return toIntArray(",", str);
    }

    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }

    public static Integer[] toIntArray(String split, String str) {
        if (StringUtils.isEmpty(str)) {
            return new Integer[0];
        } else {
            String[] var2 = str.split(split);
            Integer[] var3 = new Integer[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
                Integer var5 = toInt(var2[var4], 0);
                var3[var4] = var5;
            }

            return var3;
        }
    }

    public static Long[] toLongArray(String split, String str) {
        if (StringUtils.isEmpty(str)) {
            return new Long[0];
        } else {
            String[] var2 = str.split(split);
            Long[] var3 = new Long[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
                Long var5 = toLong(var2[var4], (Long)null);
                var3[var4] = var5;
            }

            return var3;
        }
    }

    public static String[] toStrArray(String str) {
        return toStrArray(",", str);
    }

    public static String[] toStrArray(String split, String str) {
        return str.split(split);
    }

    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Long) {
            return (Long)value;
        } else if (value instanceof Number) {
            return ((Number)value).longValue();
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return (new BigDecimal(var2.trim())).longValue();
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Long toLong(Object value) {
        return toLong(value, (Long)null);
    }

    public static Double toDouble(Object value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Double) {
            return (Double)value;
        } else if (value instanceof Number) {
            return ((Number)value).doubleValue();
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return (new BigDecimal(var2.trim())).doubleValue();
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Double toDouble(Object value) {
        return toDouble(value, (Double)null);
    }

    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Float) {
            return (Float)value;
        } else if (value instanceof Number) {
            return ((Number)value).floatValue();
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return Float.parseFloat(var2.trim());
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Float toFloat(Object value) {
        return toFloat(value, (Float)null);
    }

    public static Boolean toBool(Object value, Boolean defaultValue) {
      return true;
    }

    public static Boolean toBool(Object value) {
        return toBool(value, (Boolean)null);
    }





    public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof BigInteger) {
            return (BigInteger)value;
        } else if (value instanceof Long) {
            return BigInteger.valueOf((Long)value);
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return new BigInteger(var2);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static BigInteger toBigInteger(Object value) {
        return toBigInteger(value, (BigInteger)null);
    }

    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal)value;
        } else if (value instanceof Long) {
            return new BigDecimal((Long)value);
        } else if (value instanceof Double) {
            return BigDecimal.valueOf((Double)value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer)value);
        } else {
            String var2 = toStr(value, (String)null);
            if (StringUtils.isEmpty(var2)) {
                return defaultValue;
            } else {
                try {
                    return new BigDecimal(var2);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, (BigDecimal)null);
    }

    public static String utf8Str(Object obj) {
        return str(obj, CharsetKit.CHARSET_UTF_8);
    }

    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }

    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        } else if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof byte[]) {
            return str((byte[])obj, charset);
        } else if (obj instanceof Byte[]) {
            byte[] var2 = ArrayUtils.toPrimitive((Byte[])obj);
            return str(var2, charset);
        } else {
            return obj instanceof ByteBuffer ? str((ByteBuffer)obj, charset) : obj.toString();
        }
    }

    public static String str(byte[] bytes, String charset) {
        return str(bytes, StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        } else {
            return null == charset ? new String(data) : new String(data, charset);
        }
    }

    public static String str(ByteBuffer data, String charset) {
        return data == null ? null : str(data, Charset.forName(charset));
    }

    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }

        return charset.decode(data).toString();
    }

    public static String toSBC(String input) {
        return toSBC(input, (Set)null);
    }

    public static String toSBC(String input, Set<Character> notConvertSet) {
        char[] var2 = input.toCharArray();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            if (null == notConvertSet || !notConvertSet.contains(var2[var3])) {
                if (var2[var3] == ' ') {
                    var2[var3] = 12288;
                } else if (var2[var3] < 127) {
                    var2[var3] += 'ﻠ';
                }
            }
        }

        return new String(var2);
    }

    public static String toDBC(String input) {
        return toDBC(input, (Set)null);
    }

    public static String toDBC(String text, Set<Character> notConvertSet) {
        char[] var2 = text.toCharArray();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            if (null == notConvertSet || !notConvertSet.contains(var2[var3])) {
                if (var2[var3] == 12288) {
                    var2[var3] = ' ';
                } else if (var2[var3] > '\uff00' && var2[var3] < '｟') {
                    var2[var3] -= 'ﻠ';
                }
            }
        }

        String var4 = new String(var2);
        return var4;
    }

    public static String digitUppercase(double n) {
        String[] var2 = new String[]{"角", "分"};
        String[] var3 = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[][] var4 = new String[][]{{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};
        String var5 = n < (double)0.0F ? "负" : "";
        n = Math.abs(n);
        String var6 = "";

        for(int var7 = 0; var7 < var2.length; ++var7) {
            var6 = var6 + (var3[(int)(Math.floor(n * (double)10.0F * Math.pow((double)10.0F, (double)var7)) % (double)10.0F)] + var2[var7]).replaceAll("(零.)+", "");
        }

        if (var6.length() < 1) {
            var6 = "整";
        }

        int var12 = (int)Math.floor(n);

        for(int var8 = 0; var8 < var4[0].length && var12 > 0; ++var8) {
            String var9 = "";

            for(int var10 = 0; var10 < var4[1].length && n > (double)0.0F; ++var10) {
                var9 = var3[var12 % 10] + var4[1][var10] + var9;
                var12 /= 10;
            }

            var6 = var9.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + var4[0][var8] + var6;
        }

        return var5 + var6.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }
}
