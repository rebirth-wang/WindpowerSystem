//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.file;

import java.io.IOException;
import java.util.HashMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtils {
    public static BitMatrix createCode(String content) throws IOException {
        short var1 = 200;
        short var2 = 200;
        HashMap var3 = new HashMap();
        var3.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        var3.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        var3.put(EncodeHintType.MARGIN, 0);
        BitMatrix var4 = null;

        try {
            var4 = (new MultiFormatWriter()).encode(content, BarcodeFormat.QR_CODE, var1, var2, var3);
        } catch (WriterException var6) {
            var6.printStackTrace();
        }

        return var4;
    }

    private static BitMatrix a(BitMatrix var0) {
        int[] var1 = var0.getEnclosingRectangle();
        int var2 = var1[2] + 1;
        int var3 = var1[3] + 1;
        BitMatrix var4 = new BitMatrix(var2, var3);
        var4.clear();

        for(int var5 = 0; var5 < var2; ++var5) {
            for(int var6 = 0; var6 < var3; ++var6) {
                if (var0.get(var5 + var1[0], var6 + var1[1])) {
                    var4.set(var5, var6);
                }
            }
        }

        return var4;
    }
}
