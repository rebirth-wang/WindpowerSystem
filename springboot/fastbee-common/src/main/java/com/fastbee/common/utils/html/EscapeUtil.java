//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.html;

import com.fastbee.common.utils.StringUtils;

public class EscapeUtil {
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
    private static final char[][] bi = new char[64][];

    public static String escape(String text) {
        return d(text);
    }

    public static String unescape(String content) {
        return decode(content);
    }

    public static String clean(String content) {
        return (new HTMLFilter()).filter(content);
    }

    private static String d(String var0) {
        if (StringUtils.isEmpty(var0)) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder(var0.length() * 6);

            for(int var3 = 0; var3 < var0.length(); ++var3) {
                char var2 = var0.charAt(var3);
                if (var2 < 256) {
                    var1.append("%");
                    if (var2 < 16) {
                        var1.append("0");
                    }

                    var1.append(Integer.toString(var2, 16));
                } else {
                    var1.append("%u");
                    if (var2 <= 4095) {
                        var1.append("0");
                    }

                    var1.append(Integer.toString(var2, 16));
                }
            }

            return var1.toString();
        }
    }

    public static String decode(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        } else {
            StringBuilder var1 = new StringBuilder(content.length());
            int var2 = 0;
            int var3 = 0;

            while(var2 < content.length()) {
                var3 = content.indexOf("%", var2);
                if (var3 == var2) {
                    if (content.charAt(var3 + 1) == 'u') {
                        char var4 = (char)Integer.parseInt(content.substring(var3 + 2, var3 + 6), 16);
                        var1.append(var4);
                        var2 = var3 + 6;
                    } else {
                        char var6 = (char)Integer.parseInt(content.substring(var3 + 1, var3 + 3), 16);
                        var1.append(var6);
                        var2 = var3 + 3;
                    }
                } else if (var3 == -1) {
                    var1.append(content.substring(var2));
                    var2 = content.length();
                } else {
                    var1.append(content.substring(var2, var3));
                    var2 = var3;
                }
            }

            return var1.toString();
        }
    }

    public static void main(String[] args) {
        String var1 = "<script>alert(1);</script>";
        String var2 = escape(var1);
        System.out.println("clean: " + clean(var1));
        System.out.println("escape: " + var2);
        System.out.println("unescape: " + unescape(var2));
    }

    static {
        for(int var0 = 0; var0 < 64; ++var0) {
            bi[var0] = new char[]{(char)var0};
        }

        bi[39] = "&#039;".toCharArray();
        bi[34] = "&#34;".toCharArray();
        bi[38] = "&#38;".toCharArray();
        bi[60] = "&#60;".toCharArray();
        bi[62] = "&#62;".toCharArray();
    }
}
