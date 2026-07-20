//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Md5Utils {
    private static final String aH = "MD5";
    private static final String aI = "UTF-8";

    public static String md5(String input) {
        return md5((String)input, 1);
    }

    public static String md5(String input, int iterations) {
        try {
            return EncodeUtils.encodeHex(DigestUtils.digest(input.getBytes("UTF-8"), "MD5", (byte[])null, iterations));
        } catch (UnsupportedEncodingException var3) {
            return "";
        }
    }

    public static byte[] md5(byte[] input) {
        return md5((byte[])(input), 1);
    }

    public static byte[] md5(byte[] input, int iterations) {
        return DigestUtils.digest(input, "MD5", (byte[])null, iterations);
    }

    public static byte[] md5(InputStream input) throws IOException {
        return DigestUtils.digest(input, "MD5");
    }

    public static boolean isMd5(String str) {
        int var1 = 0;

        for(int var2 = 0; var2 < str.length(); ++var2) {
            switch (str.charAt(var2)) {
                case '/':
                    if (var2 + 10 < str.length()) {
                        char var3 = str.charAt(var2 + 1);
                        char var4 = str.charAt(var2 + 8);
                        if ('/' == var4 && ('s' == var3 || 'S' == var3)) {
                            return true;
                        }
                    }
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                default:
                    var1 = 0;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    ++var1;
                    if (32 <= var1) {
                        return true;
                    }
            }
        }

        return false;
    }
}
