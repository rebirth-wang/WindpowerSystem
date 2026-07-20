//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HTMLFilter {
    private static final int bj = 34;
    private static final Pattern bk = Pattern.compile("<!--(.*?)-->", 32);
    private static final Pattern bl = Pattern.compile("^!--(.*)--$", 34);
    private static final Pattern bm = Pattern.compile("<(.*?)>", 32);
    private static final Pattern bn = Pattern.compile("^/([a-z0-9]+)", 34);
    private static final Pattern bo = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", 34);
    private static final Pattern bp = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", 34);
    private static final Pattern bq = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", 34);
    private static final Pattern br = Pattern.compile("^([^:]+):", 34);
    private static final Pattern bs = Pattern.compile("&#(\\d+);?");
    private static final Pattern bt = Pattern.compile("&#x([0-9a-f]+);?");
    private static final Pattern bu = Pattern.compile("%([0-9a-f]{2});?");
    private static final Pattern bv = Pattern.compile("&([^&;]*)(?=(;|&|$))");
    private static final Pattern bw = Pattern.compile("(>|^)([^<]+?)(<|$)", 32);
    private static final Pattern bx = Pattern.compile("^>");
    private static final Pattern by = Pattern.compile("<([^>]*?)(?=<|$)");
    private static final Pattern bz = Pattern.compile("(^|>)([^<]*?)(?=>)");
    private static final Pattern bA = Pattern.compile("<([^>]*?)(?=<|$)");
    private static final Pattern bB = Pattern.compile("(^|>)([^<]*?)(?=>)");
    private static final Pattern bC = Pattern.compile("&");
    private static final Pattern bD = Pattern.compile("\"");
    private static final Pattern bE = Pattern.compile("<");
    private static final Pattern bF = Pattern.compile(">");
    private static final Pattern bG = Pattern.compile("<>");
    private static final ConcurrentMap<String, Pattern> bH = new ConcurrentHashMap();
    private static final ConcurrentMap<String, Pattern> bI = new ConcurrentHashMap();
    private final Map<String, List<String>> bJ;
    private final Map<String, Integer> bK = new HashMap();
    private final String[] bL;
    private final String[] bM;
    private final String[] bN;
    private final String[] bO;
    private final String[] bP;
    private final String[] bQ;
    private final String[] bR;
    private final boolean bS;
    private final boolean bT;
    private final boolean bU;

    public HTMLFilter() {
        this.bJ = new HashMap();
        ArrayList var1 = new ArrayList();
        var1.add("href");
        var1.add("target");
        this.bJ.put("a", var1);
        ArrayList var2 = new ArrayList();
        var2.add("src");
        var2.add("width");
        var2.add("height");
        var2.add("alt");
        this.bJ.put("img", var2);
        ArrayList var3 = new ArrayList();
        this.bJ.put("b", var3);
        this.bJ.put("strong", var3);
        this.bJ.put("i", var3);
        this.bJ.put("em", var3);
        this.bL = new String[]{"img"};
        this.bM = new String[]{"a", "b", "strong", "i", "em"};
        this.bN = new String[0];
        this.bP = new String[]{"http", "mailto", "https"};
        this.bO = new String[]{"src", "href"};
        this.bQ = new String[]{"a", "b", "strong", "i", "em"};
        this.bR = new String[]{"amp", "gt", "lt", "quot"};
        this.bS = true;
        this.bT = true;
        this.bU = false;
    }

    public HTMLFilter(Map<String, Object> conf) {
        assert conf.containsKey("vAllowed") : "configuration requires vAllowed";

        assert conf.containsKey("vSelfClosingTags") : "configuration requires vSelfClosingTags";

        assert conf.containsKey("vNeedClosingTags") : "configuration requires vNeedClosingTags";

        assert conf.containsKey("vDisallowed") : "configuration requires vDisallowed";

        assert conf.containsKey("vAllowedProtocols") : "configuration requires vAllowedProtocols";

        assert conf.containsKey("vProtocolAtts") : "configuration requires vProtocolAtts";

        assert conf.containsKey("vRemoveBlanks") : "configuration requires vRemoveBlanks";

        assert conf.containsKey("vAllowedEntities") : "configuration requires vAllowedEntities";

        this.bJ = Collections.unmodifiableMap((HashMap)conf.get("vAllowed"));
        this.bL = (String[])conf.get("vSelfClosingTags");
        this.bM = (String[])conf.get("vNeedClosingTags");
        this.bN = (String[])conf.get("vDisallowed");
        this.bP = (String[])conf.get("vAllowedProtocols");
        this.bO = (String[])conf.get("vProtocolAtts");
        this.bQ = (String[])conf.get("vRemoveBlanks");
        this.bR = (String[])conf.get("vAllowedEntities");
        this.bS = conf.containsKey("stripComment") ? (Boolean)conf.get("stripComment") : true;
        this.bT = conf.containsKey("encodeQuotes") ? (Boolean)conf.get("encodeQuotes") : true;
        this.bU = conf.containsKey("alwaysMakeTags") ? (Boolean)conf.get("alwaysMakeTags") : true;
    }

    private void e() {
        this.bK.clear();
    }

    public static String chr(int decimal) {
        return String.valueOf((char)decimal);
    }

    public static String htmlSpecialChars(String s) {
        String var1 = a(bC, "&amp;", s);
        var1 = a(bD, "&quot;", var1);
        var1 = a(bE, "&lt;", var1);
        var1 = a(bF, "&gt;", var1);
        return var1;
    }

    public String filter(String input) {
        this.e();
        String var2 = this.e(input);
        var2 = this.f(var2);
        var2 = this.g(var2);
        var2 = this.h(var2);
        return var2;
    }

    public boolean isAlwaysMakeTags() {
        return this.bU;
    }

    public boolean isStripComments() {
        return this.bS;
    }

    private String e(String var1) {
        Matcher var2 = bk.matcher(var1);
        StringBuffer var3 = new StringBuffer();
        if (var2.find()) {
            String var4 = var2.group(1);
            var2.appendReplacement(var3, Matcher.quoteReplacement("<!--" + htmlSpecialChars(var4) + "-->"));
        }

        var2.appendTail(var3);
        return var3.toString();
    }

    private String f(String var1) {
        if (this.bU) {
            var1 = a(bx, "", var1);
            var1 = a(by, "<$1>", var1);
            var1 = a(bz, "$1<$2", var1);
        } else {
            var1 = a(bA, "&lt;$1", var1);
            var1 = a(bB, "$1$2&gt;<", var1);
            var1 = a(bG, "", var1);
        }

        return var1;
    }

    private String g(String var1) {
        Matcher var2 = bm.matcher(var1);
        StringBuffer var3 = new StringBuffer();

        while(var2.find()) {
            String var4 = var2.group(1);
            var4 = this.i(var4);
            var2.appendReplacement(var3, Matcher.quoteReplacement(var4));
        }

        var2.appendTail(var3);
        StringBuilder var10 = new StringBuilder(var3.toString());

        for(String var6 : this.bK.keySet()) {
            for(int var7 = 0; var7 < (Integer)this.bK.get(var6); ++var7) {
                var10.append("</").append(var6).append(">");
            }
        }

        var1 = var10.toString();
        return var1;
    }

    private String h(String var1) {
        String var2 = var1;

        for(String var6 : this.bQ) {
            if (!bH.containsKey(var6)) {
                bH.putIfAbsent(var6, Pattern.compile("<" + var6 + "(\\s[^>]*)?></" + var6 + ">"));
            }

            var2 = a((Pattern)bH.get(var6), "", var2);
            if (!bI.containsKey(var6)) {
                bI.putIfAbsent(var6, Pattern.compile("<" + var6 + "(\\s[^>]*)?/>"));
            }

            var2 = a((Pattern)bI.get(var6), "", var2);
        }

        return var2;
    }

    private static String a(Pattern var0, String var1, String var2) {
        Matcher var3 = var0.matcher(var2);
        return var3.replaceAll(var1);
    }

    private String i(String var1) {
        Matcher var2 = bn.matcher(var1);
        if (var2.find()) {
            String var3 = var2.group(1).toLowerCase();
            if (this.o(var3) && !a(var3, this.bL) && this.bK.containsKey(var3)) {
                this.bK.put(var3, (Integer)this.bK.get(var3) - 1);
                return "</" + var3 + ">";
            }
        }

        var2 = bo.matcher(var1);
        if (!var2.find()) {
            var2 = bl.matcher(var1);
            return !this.bS && var2.find() ? "<" + var2.group() + ">" : "";
        } else {
            String var16 = var2.group(1).toLowerCase();
            String var4 = var2.group(2);
            String var5 = var2.group(3);
            if (!this.o(var16)) {
                return "";
            } else {
                StringBuilder var6 = new StringBuilder();
                Matcher var7 = bp.matcher(var4);
                Matcher var8 = bq.matcher(var4);
                ArrayList var9 = new ArrayList();
                ArrayList var10 = new ArrayList();

                while(var7.find()) {
                    var9.add(var7.group(1));
                    var10.add(var7.group(3));
                }

                while(var8.find()) {
                    var9.add(var8.group(1));
                    var10.add(var8.group(3));
                }

                for(int var13 = 0; var13 < var9.size(); ++var13) {
                    String var11 = ((String)var9.get(var13)).toLowerCase();
                    String var12 = (String)var10.get(var13);
                    if (this.b(var16, var11)) {
                        if (a(var11, this.bO)) {
                            var12 = this.j(var12);
                        }

                        var6.append(' ').append(var11).append("=\\\"").append(var12).append("\\\"");
                    }
                }

                if (a(var16, this.bL)) {
                    var5 = " /";
                }

                if (a(var16, this.bM)) {
                    var5 = "";
                }

                if (var5 != null && var5.length() >= 1) {
                    var5 = " /";
                } else if (this.bK.containsKey(var16)) {
                    this.bK.put(var16, (Integer)this.bK.get(var16) + 1);
                } else {
                    this.bK.put(var16, 1);
                }

                return "<" + var16 + var6 + var5 + ">";
            }
        }
    }

    private String j(String var1) {
        var1 = this.k(var1);
        Matcher var2 = br.matcher(var1);
        if (var2.find()) {
            String var3 = var2.group(1);
            if (!a(var3, this.bP)) {
                var1 = "#" + var1.substring(var3.length() + 1);
                if (var1.startsWith("#//")) {
                    var1 = "#" + var1.substring(3);
                }
            }
        }

        return var1;
    }

    private String k(String var1) {
        StringBuffer var2 = new StringBuffer();
        Matcher var3 = bs.matcher(var1);

        while(var3.find()) {
            String var4 = var3.group(1);
            int var5 = Integer.decode(var4);
            var3.appendReplacement(var2, Matcher.quoteReplacement(chr(var5)));
        }

        var3.appendTail(var2);
        var1 = var2.toString();
        var2 = new StringBuffer();
        var3 = bt.matcher(var1);

        while(var3.find()) {
            String var14 = var3.group(1);
            int var16 = Integer.valueOf(var14, 16);
            var3.appendReplacement(var2, Matcher.quoteReplacement(chr(var16)));
        }

        var3.appendTail(var2);
        var1 = var2.toString();
        var2 = new StringBuffer();
        var3 = bu.matcher(var1);

        while(var3.find()) {
            String var15 = var3.group(1);
            int var17 = Integer.valueOf(var15, 16);
            var3.appendReplacement(var2, Matcher.quoteReplacement(chr(var17)));
        }

        var3.appendTail(var2);
        var1 = var2.toString();
        var1 = this.l(var1);
        return var1;
    }

    private String l(String var1) {
        StringBuffer var2 = new StringBuffer();
        Matcher var3 = bv.matcher(var1);

        while(var3.find()) {
            String var4 = var3.group(1);
            String var5 = var3.group(2);
            var3.appendReplacement(var2, Matcher.quoteReplacement(this.a(var4, var5)));
        }

        var3.appendTail(var2);
        return this.m(var2.toString());
    }

    private String m(String var1) {
        if (!this.bT) {
            return var1;
        } else {
            StringBuffer var2 = new StringBuffer();
            Matcher var3 = bw.matcher(var1);

            while(var3.find()) {
                String var4 = var3.group(1);
                String var5 = var3.group(2);
                String var6 = var3.group(3);
                var3.appendReplacement(var2, Matcher.quoteReplacement(var4 + var5 + var6));
            }

            var3.appendTail(var2);
            return var2.toString();
        }
    }

    private String a(String var1, String var2) {
        return ";".equals(var2) && this.n(var1) ? '&' + var1 : "&amp;" + var1;
    }

    private boolean n(String var1) {
        return a(var1, this.bR);
    }

    private static boolean a(String var0, String[] var1) {
        for(String var5 : var1) {
            if (var5 != null && var5.equals(var0)) {
                return true;
            }
        }

        return false;
    }

    private boolean o(String var1) {
        return (this.bJ.isEmpty() || this.bJ.containsKey(var1)) && !a(var1, this.bN);
    }

    private boolean b(String var1, String var2) {
        return this.o(var1) && (this.bJ.isEmpty() || ((List)this.bJ.get(var1)).contains(var2));
    }
}
