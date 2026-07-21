//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.text;

import com.fastbee.common.utils.StringUtils;

public class StrFormatter {
    public static final String EMPTY_JSON = "{}";
    public static final char C_BACKSLASH = '\\';
    public static final char C_DELIM_START = '{';
    public static final char C_DELIM_END = '}';

    public static String format(String strPattern, Object... argArray) {
        if (!StringUtils.isEmpty(strPattern) && !StringUtils.isEmpty(argArray)) {
            int var2 = strPattern.length();
            StringBuilder var3 = new StringBuilder(var2 + 50);
            int var4 = 0;

            for(int var6 = 0; var6 < argArray.length; ++var6) {
                int var5 = strPattern.indexOf("{}", var4);
                if (var5 == -1) {
                    if (var4 == 0) {
                        return strPattern;
                    }

                    var3.append(strPattern, var4, var2);
                    return var3.toString();
                }

                if (var5 > 0 && strPattern.charAt(var5 - 1) == '\\') {
                    if (var5 > 1 && strPattern.charAt(var5 - 2) == '\\') {
                        var3.append(strPattern, var4, var5 - 1);
                        var3.append(Convert.utf8Str(argArray[var6]));
                        var4 = var5 + 2;
                    } else {
                        --var6;
                        var3.append(strPattern, var4, var5 - 1);
                        var3.append('{');
                        var4 = var5 + 1;
                    }
                } else {
                    var3.append(strPattern, var4, var5);
                    var3.append(Convert.utf8Str(argArray[var6]));
                    var4 = var5 + 2;
                }
            }

            var3.append(strPattern, var4, strPattern.length());
            return var3.toString();
        } else {
            return strPattern;
        }
    }
}
