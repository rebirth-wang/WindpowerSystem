//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jakarta.servlet.http.HttpServletRequest;

import com.fastbee.common.utils.StringUtils;

public class IpUtils {
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        } else {
            String var1 = request.getHeader("x-forwarded-for");
            if (var1 == null || var1.length() == 0 || "unknown".equalsIgnoreCase(var1)) {
                var1 = request.getHeader("Proxy-Client-IP");
            }

            if (var1 == null || var1.length() == 0 || "unknown".equalsIgnoreCase(var1)) {
                var1 = request.getHeader("X-Forwarded-For");
            }

            if (var1 == null || var1.length() == 0 || "unknown".equalsIgnoreCase(var1)) {
                var1 = request.getHeader("WL-Proxy-Client-IP");
            }

            if (var1 == null || var1.length() == 0 || "unknown".equalsIgnoreCase(var1)) {
                var1 = request.getHeader("X-Real-IP");
            }

            if (var1 == null || var1.length() == 0 || "unknown".equalsIgnoreCase(var1)) {
                var1 = request.getRemoteAddr();
            }

            return "0:0:0:0:0:0:0:1".equals(var1) ? "127.0.0.1" : getMultistageReverseProxyIp(var1);
        }
    }

    public static boolean internalIp(String ip) {
        byte[] var1 = textToNumericFormatV4(ip);
        return c(var1) || "127.0.0.1".equals(ip);
    }

    private static boolean c(byte[] var0) {
        if (!StringUtils.isNull(var0) && var0.length >= 2) {
            byte var1 = var0[0];
            byte var2 = var0[1];
            boolean var3 = true;
            boolean var4 = true;
            boolean var5 = true;
            boolean var6 = true;
            boolean var7 = true;
            boolean var8 = true;
            switch (var1) {
                case -84:
                    if (var2 >= 16 && var2 <= 31) {
                        return true;
                    }
                case -64:
                    switch (var2) {
                        case -88:
                            return true;
                    }
                default:
                    return false;
                case 10:
                    return true;
            }
        } else {
            return true;
        }
    }

    public static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return null;
        } else {
            byte[] var1 = new byte[4];
            String[] var2 = text.split("\\.", -1);

            try {
                switch (var2.length) {
                    case 1:
                        long var11 = Long.parseLong(var2[0]);
                        if (var11 < 0L || var11 > 4294967295L) {
                            return null;
                        }

                        var1[0] = (byte)((int)(var11 >> 24 & 255L));
                        var1[1] = (byte)((int)((var11 & 16777215L) >> 16 & 255L));
                        var1[2] = (byte)((int)((var11 & 65535L) >> 8 & 255L));
                        var1[3] = (byte)((int)(var11 & 255L));
                        break;
                    case 2:
                        long var9 = (long)Integer.parseInt(var2[0]);
                        if (var9 < 0L || var9 > 255L) {
                            return null;
                        }

                        var1[0] = (byte)((int)(var9 & 255L));
                        var9 = (long)Integer.parseInt(var2[1]);
                        if (var9 < 0L || var9 > 16777215L) {
                            return null;
                        }

                        var1[1] = (byte)((int)(var9 >> 16 & 255L));
                        var1[2] = (byte)((int)((var9 & 65535L) >> 8 & 255L));
                        var1[3] = (byte)((int)(var9 & 255L));
                        break;
                    case 3:
                        for(int var12 = 0; var12 < 2; ++var12) {
                            long var7 = (long)Integer.parseInt(var2[var12]);
                            if (var7 < 0L || var7 > 255L) {
                                return null;
                            }

                            var1[var12] = (byte)((int)(var7 & 255L));
                        }

                        long var8 = (long)Integer.parseInt(var2[2]);
                        if (var8 < 0L || var8 > 65535L) {
                            return null;
                        }

                        var1[2] = (byte)((int)(var8 >> 8 & 255L));
                        var1[3] = (byte)((int)(var8 & 255L));
                        break;
                    case 4:
                        for(int var5 = 0; var5 < 4; ++var5) {
                            long var3 = (long)Integer.parseInt(var2[var5]);
                            if (var3 < 0L || var3 > 255L) {
                                return null;
                            }

                            var1[var5] = (byte)((int)(var3 & 255L));
                        }
                        break;
                    default:
                        return null;
                }

                return var1;
            } catch (NumberFormatException var6) {
                return null;
            }
        }
    }

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var1) {
            return "127.0.0.1";
        }
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var1) {
            return "未知";
        }
    }

    public static String getMultistageReverseProxyIp(String ip) {
        if (ip != null && ip.indexOf(",") > 0) {
            String[] var1 = ip.trim().split(",");

            for(String var5 : var1) {
                if (!isUnknown(var5)) {
                    ip = var5;
                    break;
                }
            }
        }

        return ip;
    }

    public static boolean isUnknown(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }
}
