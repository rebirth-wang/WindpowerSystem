package com.fastbee.common.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fastbee.common.exception.ServiceException;

/**
 * @author lei lei
 * @date 2025-09-10 9:35
 * @description:
 */

public class CaculateVariableAndNumberUtils {
    private static final String ar = "+-,*/,(),%";
    private static final Map<String, Integer> as = new HashMap<String, Integer>() {
        {
            this.put("*", 1);
            this.put("/", 1);
            this.put("%", 1);
            this.put("+", 2);
            this.put("-", 2);
            this.put("(", 3);
            this.put(")", 3);
        }
    };

    public CaculateVariableAndNumberUtils() {
    }

    public static BigDecimal execute(String exeStr, Map<String, String> replaceMap) {
        List<String> var2 = suffixHandle(exeStr);
        System.out.println("计算结果： " + var2);
        ArrayList var3 = new ArrayList();

        for(String var5 : var2) {
            String var6 = (String)replaceMap.get(var5);
            if (StringUtils.isNotEmpty(var6)) {
                var3.add(var6);
            } else {
                var3.add(var5);
            }
        }

        return caculateAnalyse(var3);
    }

    public static BigDecimal caculateAnalyse(List<String> suffixList) {
        BigDecimal var1 = BigDecimal.ZERO;
        BigDecimal var2 = BigDecimal.ZERO;
        Stack var3 = new Stack();
        if (suffixList.size() > 1) {
            for(int var4 = 0; var4 < suffixList.size(); ++var4) {
                String var5 = (String)suffixList.get(var4);
                if ("+-,*/,(),%".contains(var5)) {
                    var2 = (BigDecimal)var3.pop();
                    BigDecimal var6 = (BigDecimal)var3.pop();
                    var1 = caculate(var6, var2, var5.toCharArray()[0]);
                    var3.push(var1);
                } else {
                    if (!isNumber((String)suffixList.get(var4))) {
                        throw new RuntimeException("公式异常！");
                    }

                    var3.push(new BigDecimal((String)suffixList.get(var4)));
                }
            }
        } else if (suffixList.size() == 1) {
            String var8 = (String)suffixList.get(0);
            if (!isNumber(var8)) {
                throw new RuntimeException("公式异常！");
            }

            var1 = BigDecimal.valueOf(Double.parseDouble(var8));
        }

        return var1;
    }

    public static BigDecimal caculate(BigDecimal a, BigDecimal b, char symbol) {
        switch (symbol) {
            case '%':
            case '/':
                int var3 = a(a, b);
                return a.divide(b, var3, 4);
            case '&':
            case '\'':
            case '(':
            case ')':
            case ',':
            case '.':
            default:
                throw new RuntimeException("操作符号异常！");
            case '*':
                return a.multiply(b);
            case '+':
                return a.add(b).stripTrailingZeros();
            case '-':
                return a.subtract(b).stripTrailingZeros();
        }
    }

    private static int a(BigDecimal var0, BigDecimal var1) {
        String var2 = var0.toString();
        String var3 = var1.toString();
        int var4 = 0;
        int var5 = 0;
        if (var2.contains(".")) {
            var4 = var2.split("\\.")[1].length();
        }

        if (var3.contains(".")) {
            var5 = var3.split("\\.")[1].length();
        }

        return var4 == 0 && var5 == 0 ? 2 : Math.max(var4, var5);
    }

    public static List<String> suffixHandle(String exeStr) {
        StringBuilder var1 = new StringBuilder();
        Stack var2 = new Stack();
        char[] var3 = exeStr.toCharArray();
        ArrayList var4 = new ArrayList();

        for(char var8 : var3) {
            if ("+-,*/,(),%".indexOf(var8) <= -1) {
                var1.append(var8);
            } else {
                if (var1.length() > 0) {
                    String var9 = var1.toString();
                    if (!isVariableAndNumber(var9)) {
                        throw new RuntimeException(var1.append("  格式不对").toString());
                    }

                    var4.add(var9);
                    var1.delete(0, var1.length());
                }

                if (var2.isEmpty()) {
                    var2.push(var8);
                } else if (var8 == '(') {
                    var2.push(var8);
                } else if (var8 == ')') {
                    boolean var12 = false;

                    while(!var2.isEmpty()) {
                        char var13 = (Character)var2.peek();
                        if (var13 == '(' && !var12) {
                            var2.pop();
                            var12 = true;
                        } else {
                            if (var12) {
                                break;
                            }

                            var4.add(String.valueOf(var2.pop()));
                        }
                    }
                } else {
                    for(int var11 = var2.size(); var11 > 0; --var11) {
                        char var10 = (Character)var2.peek();
                        if (compare(var10, var8) > 0) {
                            var4.add(String.valueOf(var2.pop()));
                        }
                    }

                    var2.push(var8);
                }
            }
        }

        if (var1.length() > 0) {
            var4.add(var1.toString());
        }

        while(!var2.isEmpty()) {
            var4.add(String.valueOf(var2.pop()));
        }

        return var4;
    }

    public static int compare(char a, char b) {
        String var2 = String.valueOf(a);
        String var3 = String.valueOf(b);
        Integer var4 = (Integer)as.get(var2);
        Integer var5 = (Integer)as.get(var3);
        if (null != var4 && null != var5) {
            return var4 <= var5 ? 1 : -1;
        } else {
            return 0;
        }
    }

    public static boolean isNumber(String str) {
        Pattern var1 = Pattern.compile("[-+]?\\d+(?:\\.\\d+)?");
        Matcher var2 = var1.matcher(str);
        return var2.matches();
    }

    public static boolean isVariable(String str) {
        Pattern var1 = Pattern.compile("^[A-Z]+$");
        Matcher var2 = var1.matcher(str);
        return var2.matches();
    }

    public static boolean isVariableAndNumber(String str) {
        Pattern var1 = Pattern.compile("[A-Z]|-?\\d+(\\.\\d+)?");
        Matcher var2 = var1.matcher(str);
        return var2.matches();
    }

    public static String caculateReplace(String str, Map<String, String> map) {
        for(Map.Entry var3 : map.entrySet()) {
            str = str.replaceAll((String)var3.getKey(), var3.getValue() == null ? "1" : (String)var3.getValue());
        }

        return str;
    }

    public static String toFloat(byte[] bytes) throws IOException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(bytes);
        DataInputStream var2 = new DataInputStream(var1);

        String var4;
        try {
            float var3 = var2.readFloat();
            var4 = String.format("%.6f", var3);
        } catch (Exception var8) {
            throw new ServiceException("modbus16转浮点数错误");
        } finally {
            var2.close();
            var1.close();
        }

        return var4;
    }

    public static String toUnSign16(long value) {
        long var2 = value & 65535L;
        return var2 + "";
    }

    public static String toSign32_CDAB(long value) {
        byte[] var2 = intToBytes2((int)value);
        return bytesToInt2(var2) + "";
    }

    public static String toUnSign32_ABCD(long value) {
        return Integer.toUnsignedString((int)value);
    }

    public static String toUnSign32_CDAB(long value) {
        byte[] var2 = intToBytes2((int)value);
        int var3 = bytesToInt2(var2);
        return Integer.toUnsignedString(var3);
    }

    public static float toFloat32_ABCD(byte[] bytes) {
        int var1 = bytes[0] << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
        return Float.intBitsToFloat(var1);
    }

    public static Float toFloat32_CDAB(byte[] bytes) {
        int var1 = (bytes[2] & 255) << 24 | (bytes[3] & 255) << 16 | (bytes[0] & 255) << 8 | bytes[1] & 255;
        return Float.intBitsToFloat(var1);
    }

    public static int bytesToInt2(byte[] src) {
        return (src[2] & 255) << 24 | (src[3] & 255) << 16 | (src[0] & 255) << 8 | src[1] & 255;
    }

    public static byte[] intToBytes2(int value) {
        byte[] var1 = new byte[]{(byte)(value >> 24 & 255), (byte)(value >> 16 & 255), (byte)(value >> 8 & 255), (byte)(value & 255)};
        return var1;
    }

    public static String subHexValue(String hexString) {
        String var1 = hexString.substring(4, 6);
        int var2 = Integer.parseInt(var1);
        return hexString.substring(6, 6 + var2 * 2);
    }

    public static void main(String[] args) throws IOException {
        String var1 = "A/B*C";
        String var2 = "E-((A+B)-(C+D))%10";
        String var3 = "A-B-C*(D-E)+10*5";
        String var4 = "A-B-C*(D+E)-(A+B)+(2+3)";
        String var5 = "A-(A-(B-C)*(D+E))%10+B";
        String var6 = "A-(B+C)*D+10";
        String var7 = "1+2*3-2+2*(1-2+3*4+5-6/2+(2-1)+3*4-2)%10";
        boolean var8 = isNumber("-10");
        System.out.println(var8);
        HashMap var9 = new HashMap();
        var9.put("A", "1");
        var9.put("B", "2");
        var9.put("C", "3");
        var9.put("D", "4");
        var9.put("E", "10");
        BigDecimal var10 = execute(var7, var9);
        System.out.println(var10);
    }
}
