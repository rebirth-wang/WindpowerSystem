//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.multipart.MultipartFile;

public class EncodeUtils {
    private static final Logger ay = LoggerFactory.getLogger(EncodeUtils.class);
    private static final String az = "UTF-8";
    private static final char[] aA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static Pattern aB = Pattern.compile("<\\s*(script|link|style|iframe)([\\s\\S]+?)<\\/\\s*\\1\\s*>", 2);
    private static Pattern aC = Pattern.compile("\\s*on[a-z]+\\s*=\\s*(\"[^\"]+\"|'[^']+'|[^\\s]+)\\s*(?=>)", 2);
    private static Pattern aD = Pattern.compile("\\s*(href|src)\\s*=\\s*(\"\\s*(javascript|vbscript):[^\"]+\"|'\\s*(javascript|vbscript):[^']+'|(javascript|vbscript):[^\\s]+)\\s*(?=>)", 2);
    private static Pattern aE = Pattern.compile("epression\\((.|\\n)*\\);?", 2);
    private static Pattern aF = Pattern.compile("(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)", 2);

    public static String encodeHex(byte[] input) {
        return new String(Hex.encode(input));
    }

    public static byte[] decodeHex(String input) {
        try {
            return Hex.decode(input);
        } catch (Exception var2) {
            throw ExceptionUtils.unchecked(var2);
        }
    }

    public static String encodeBase64(byte[] input) {
        return new String(Base64.encodeBase64(input));
    }

    public static String encodeBase64(String input) {
        try {
            return new String(Base64.encodeBase64(input.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input.getBytes());
    }

    public static String decodeBase64String(String input) {
        try {
            return new String(Base64.decodeBase64(input.getBytes()), "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static String encodeBase62(byte[] input) {
        char[] var1 = new char[input.length];

        for(int var2 = 0; var2 < input.length; ++var2) {
            var1[var2] = aA[(input[var2] & 255) % aA.length];
        }

        return new String(var1);
    }

    public static String encodeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    public static String decodeHtml(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    public static String encodeXml(String xml) {
        return StringEscapeUtils.escapeXml(xml);
    }

    public static String decodeXml(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    public static String encodeUrl(String part) {
        return encodeUrl(part, "UTF-8");
    }

    public static String encodeUrl(String part, String encoding) {
        if (part == null) {
            return null;
        } else {
            try {
                return URLEncoder.encode(part, encoding);
            } catch (UnsupportedEncodingException var3) {
                throw ExceptionUtils.unchecked(var3);
            }
        }
    }

    public static String decodeUrl(String part) {
        return decodeUrl(part, "UTF-8");
    }

    public static String decodeUrl(String part, String encoding) {
        try {
            return URLDecoder.decode(part, encoding);
        } catch (UnsupportedEncodingException var3) {
            throw ExceptionUtils.unchecked(var3);
        }
    }

    public static String decodeUrl2(String part) {
        return decodeUrl(decodeUrl(part));
    }

    public static String xssFilter(String text) {
        if (text == null) {
            return null;
        } else {
            String var1 = StringUtils.trim(text);
            String var2 = aB.matcher(var1).replaceAll("");
            var2 = aC.matcher(var2).replaceAll("");
            var2 = aD.matcher(var2).replaceAll("");
            var2 = aE.matcher(var2).replaceAll("");
            if (!StringUtils.startsWithIgnoreCase(var2, "<!--HTML-->") && !StringUtils.startsWithIgnoreCase(var2, "<?xml ") && !StringUtils.contains(var2, "id=\"FormHtml\"") && (!StringUtils.startsWith(var2, "{") || !StringUtils.endsWith(var2, "}")) && (!StringUtils.startsWith(var2, "[") || !StringUtils.endsWith(var2, "]"))) {
                var2 = var2.replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
            }

            if (ay.isInfoEnabled() && !var2.equals(var1)) {
                ay.info("xssFilter: {} to {}", text, var2);
            }

            return var2;
        }
    }

    public static String sqlFilter(String text) {
        if (text != null) {
            String var1 = aF.matcher(text).replaceAll("");
            if (ay.isWarnEnabled() && !var1.equals(text)) {
                ay.warn("sqlFilter: {} to {}", text, var1);
                return "";
            } else {
                return var1;
            }
        } else {
            return null;
        }
    }

    public static MultipartFile base64toMultipartFile(String base64) {
        String[] var1 = base64.split(",");
        String var2;
        String var3;
        if (var1.length > 1) {
            var2 = var1[0];
            var3 = var1[1];
        } else {
            var2 = "data:image/png;base64";
            var3 = var1[0];
        }

        return new Base64ToMultipartFile(var3, var2);
    }
}
