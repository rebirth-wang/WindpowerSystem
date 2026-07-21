package com.fastbee.common.utils.wechat;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author lei lei
 * @date 2025-09-09 16:23
 * @description:
 */

class c {
    c() {
    }

    public static Object[] t(String var0) throws AesException {
        Object[] var1 = new Object[3];

        try {
            DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
            Object var3 = null;
            String var11 = "http://apache.org/xml/features/disallow-doctype-decl";
            var2.setFeature(var11, true);
            var11 = "http://xml.org/sax/features/external-general-entities";
            var2.setFeature(var11, false);
            var11 = "http://xml.org/sax/features/external-parameter-entities";
            var2.setFeature(var11, false);
            var11 = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            var2.setFeature(var11, false);
            var2.setXIncludeAware(false);
            var2.setExpandEntityReferences(false);
            DocumentBuilder var4 = var2.newDocumentBuilder();
            StringReader var5 = new StringReader(var0);
            InputSource var6 = new InputSource(var5);
            Document var7 = var4.parse(var6);
            Element var8 = var7.getDocumentElement();
            NodeList var9 = var8.getElementsByTagName("Encrypt");
            var1[0] = 0;
            var1[1] = var9.item(0).getTextContent();
            return var1;
        } catch (Exception var10) {
            var10.printStackTrace();
            throw new AesException(-40002);
        }
    }

    public static String a(String var0, String var1, String var2, String var3) {
        String var4 = "<xml>\n<Encrypt><![CDATA[%1$s]]></Encrypt>\n<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n<TimeStamp>%3$s</TimeStamp>\n<Nonce><![CDATA[%4$s]]></Nonce>\n</xml>";
        return String.format(var4, var0, var1, var2, var3);
    }
}
