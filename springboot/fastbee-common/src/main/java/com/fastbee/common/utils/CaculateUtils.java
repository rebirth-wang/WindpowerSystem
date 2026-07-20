//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.buffer.ByteBufUtil;

import com.fastbee.common.enums.ModbusDataType;
import com.fastbee.common.exception.ServiceException;

public class CaculateUtils {
    private static final String ao = "+-,*/,(),%";

    public static BigDecimal execute(String exeStr, Map<String, String> replaceMap) {
        exeStr = caculateReplace(exeStr, replaceMap);
        exeStr = exeStr.replaceAll("\\s*", "");
        List var2 = suffixHandle(exeStr);
        return caculateAnalyse(var2);
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
                if (!b.toString().contains(".0")) {
                    b = new BigDecimal(b + ".0");
                }

                int var3 = b.toString().split("\\.")[1].length();
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
            case '/':
                return a.divide(b);
        }
    }

    public static List<String> suffixHandle(String exeStr) {
        StringBuilder var1 = new StringBuilder();
        Stack var2 = new Stack();
        char[] var3 = exeStr.toCharArray();
        ArrayList var4 = new ArrayList();

        for(char var8 : var3) {
            if ("+-,*/,(),%".indexOf(var8) > -1) {
                if (var1.length() > 0) {
                    String var9 = var1.toString();
                    if (!isNumber(var9)) {
                        throw new RuntimeException(var1.append("  格式不对").toString());
                    }

                    var4.add(var9);
                    var1.delete(0, var1.length());
                }

                if (var2.size() > 0) {
                    if (var8 == '(') {
                        var2.push(var8);
                    } else if (var8 == ')') {
                        while(var2.size() > 0) {
                            char var11 = (Character)var2.peek();
                            if (var11 == '(') {
                                var2.pop();
                            } else {
                                var4.add(String.valueOf(var2.pop()));
                            }
                        }
                    } else {
                        char var10 = (Character)var2.peek();
                        if (compare(var8, var10) > 0) {
                            var2.push(var8);
                        } else if (compare(var8, var10) <= 0) {
                            if (var10 != '(') {
                                var4.add(String.valueOf(var2.pop()));
                            }

                            var2.push(var8);
                        }
                    }
                } else {
                    var2.push(var8);
                }
            } else {
                var1.append(var8);
            }
        }

        if (var1.length() > 0) {
            var4.add(var1.toString());
        }

        while(var2.size() > 0) {
            var4.add(String.valueOf(var2.pop()));
        }

        return var4;
    }

    public static int compare(char a, char b) {
        if ("+-,*/,(),%".indexOf(a) - "+-,*/,(),%".indexOf(b) > 1) {
            return 1;
        } else {
            return "+-,*/,(),%".indexOf(a) - "+-,*/,(),%".indexOf(b) < -1 ? -1 : 0;
        }
    }

    public static boolean isNumber(String str) {
        Pattern var1 = Pattern.compile("[0-9]+\\.{0,1}[0-9]*");
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

    public static String handleToUnSign16(String value, String dataType) {
        long var2 = Long.parseLong(value);
        if (dataType.equals(ModbusDataType.U_SHORT.getType())) {
            return toUnSign16(var2);
        } else {
            return dataType.equals(ModbusDataType.HEX.getType()) ? Integer.toHexString((int)var2) + "" : value;
        }
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

    public static String parseValue(String dataType, String hexString) {
        String var2 = "";
        Long var3 = Long.parseLong(hexString, 16);
        byte[] var4 = ByteBufUtil.decodeHexDump(hexString);
        if (StringUtils.isNotEmpty(dataType)) {
            ModbusDataType var5 = ModbusDataType.convert(dataType);
            switch (var5) {
                case U_SHORT:
                    var2 = toUnSign16(var3);
                    break;
                case SHORT:
                case LONG_ABCD:
                    var2 = var3 + "";
                    break;
                case LONG_CDAB:
                    var2 = toSign32_CDAB(var3);
                    break;
                case U_LONG_ABCD:
                    var2 = toUnSign32_ABCD(var3);
                    break;
                case U_LONG_CDAB:
                    var2 = toUnSign32_CDAB(var3);
                    break;
                case FLOAT_ABCD:
                    var2 = toFloat32_ABCD(var4) + "";
                    break;
                case FLOAT_CDAB:
                    var2 = toFloat32_CDAB(var4) + "";
            }
        }

        return var2;
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
        HashMap var1 = new HashMap();
        var1.put("A", "1.5");
        var1.put("B", "2.5");
        var1.put("C", "3.5");
        var1.put("D", "4.5");
        var1.put("E", "10");
        BigDecimal var2 = execute("A + B * (C - D) % E", var1);
        System.out.println(var2);
        HashMap var3 = new HashMap();
        var3.put("%s", "10");
        String var4 = caculateReplace("%s*2", var3);
        System.out.println(var4);
        System.out.println(execute("%s%3.00", var3));
        String var5 = toUnSign16(-1L);
        System.out.println("转16位无符号：" + var5);
        String var6 = toSign32_CDAB(40100L);
        System.out.println("转32位有符号-CDAB序" + var6);
        String var7 = toUnSign32_ABCD(-10L);
        System.out.println("转32位无符号-ABCD序：" + var7);
        String var8 = toUnSign32_CDAB(123456789L);
        System.out.println("转32位无符号-CDAB序：" + var8);
        String var9 = "3fea3d71";
        byte[] var10 = ByteBufUtil.decodeHexDump(var9);
        float var11 = toFloat32_ABCD(var10);
        System.out.println("转32位浮点型-ABCD序：" + var11);
        String var12 = "800041EE";
        long var13 = Long.parseLong(var12, 16);
        System.out.println(var13);
        byte[] var15 = ByteBufUtil.decodeHexDump(var12);
        float var16 = toFloat32_CDAB(var15);
        System.out.println("转32位浮点型-CDAB序：" + var16);
        short var17 = -32627;
        int var18 = var17 & '\uffff';
        System.out.println(var18);
        long var19 = Long.parseLong("00501F40", 16);
        System.out.println(var19);
        int var21 = -6553510;
        byte[] var22 = intToBytes2(var21);
        int var23 = bytesToInt2(var22);
        System.out.println(var23);
    }
}
