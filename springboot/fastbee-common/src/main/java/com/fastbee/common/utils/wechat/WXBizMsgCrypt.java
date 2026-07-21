package com.fastbee.common.utils.wechat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author lei lei
 * @date 2025-09-09 16:23
 * @description:
 */

public class WXBizMsgCrypt {
    static Charset dl = Charset.forName("utf-8");
    Base64 dn = new Base64();
    byte[] dO;
    String dp;
    String dq;

    public WXBizMsgCrypt(String token, String encodingAesKey, String receiveid) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(-40004);
        } else {
            this.dp = token;
            this.dq = receiveid;
            this.dO = Base64.decodeBase64(encodingAesKey + "=");
        }
    }

    byte[] d(int var1) {
        byte[] var2 = new byte[]{(byte)(var1 >> 24 & 255), (byte)(var1 >> 16 & 255), (byte)(var1 >> 8 & 255), (byte)(var1 & 255)};
        return var2;
    }

    int h(byte[] var1) {
        int var2 = 0;

        for(int var3 = 0; var3 < 4; ++var3) {
            var2 <<= 8;
            var2 |= var1[var3] & 255;
        }

        return var2;
    }

    String j() {
        String var1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random var2 = new Random();
        StringBuffer var3 = new StringBuffer();

        for(int var4 = 0; var4 < 16; ++var4) {
            int var5 = var2.nextInt(var1.length());
            var3.append(var1.charAt(var5));
        }

        return var3.toString();
    }

    String encrypt(String randomStr, String text) throws AesException {
        a var3 = new a();
        byte[] var4 = randomStr.getBytes(dl);
        byte[] var5 = text.getBytes(dl);
        byte[] var6 = this.d(var5.length);
        byte[] var7 = this.dq.getBytes(dl);
        var3.f(var4);
        var3.f(var6);
        var3.f(var5);
        var3.f(var7);
        byte[] var8 = b.b(var3.size());
        var3.f(var8);
        byte[] var9 = var3.i();

        try {
            Cipher var10 = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec var11 = new SecretKeySpec(this.dO, "AES");
            IvParameterSpec var12 = new IvParameterSpec(this.dO, 0, 16);
            var10.init(1, var11, var12);
            byte[] var13 = var10.doFinal(var9);
            String var14 = this.dn.encodeToString(var13);
            return var14;
        } catch (Exception var15) {
            var15.printStackTrace();
            throw new AesException(-40006);
        }
    }

    String s(String var1) throws AesException {
        byte[] var2;
        try {
            Cipher var3 = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec var4 = new SecretKeySpec(this.dO, "AES");
            IvParameterSpec var5 = new IvParameterSpec(Arrays.copyOfRange(this.dO, 0, 16));
            var3.init(2, var4, var5);
            byte[] var6 = Base64.decodeBase64(var1);
            var2 = var3.doFinal(var6);
        } catch (Exception var9) {
            var9.printStackTrace();
            throw new AesException(-40007);
        }

        String var10;
        String var11;
        try {
            byte[] var12 = b.g(var2);
            byte[] var13 = Arrays.copyOfRange(var12, 16, 20);
            int var7 = this.h(var13);
            var10 = new String(Arrays.copyOfRange(var12, 20, 20 + var7), dl);
            var11 = new String(Arrays.copyOfRange(var12, 20 + var7, var12.length), dl);
        } catch (Exception var8) {
            var8.printStackTrace();
            throw new AesException(-40008);
        }

        if (!var11.equals(this.dq)) {
            throw new AesException(-40005);
        } else {
            return var10;
        }
    }

    public String EncryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        String var4 = this.encrypt(this.j(), replyMsg);
        if (timeStamp == "") {
            timeStamp = Long.toString(System.currentTimeMillis());
        }

        String var5 = SHA1.getSHA1(this.dp, timeStamp, nonce, var4);
        String var6 = c.a(var4, var5, timeStamp, nonce);
        return var6;
    }

    public String DecryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {
        Object[] var5 = c.t(postData);
        String var6 = SHA1.getSHA1(this.dp, timeStamp, nonce, var5[1].toString());
        if (!var6.equals(msgSignature)) {
            throw new AesException(-40001);
        } else {
            String var7 = this.s(var5[1].toString());
            return var7;
        }
    }

    public String VerifyURL(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
        String var5 = SHA1.getSHA1(this.dp, timeStamp, nonce, echoStr);
        if (!var5.equals(msgSignature)) {
            throw new AesException(-40001);
        } else {
            String var6 = this.s(echoStr);
            return var6;
        }
    }
}
