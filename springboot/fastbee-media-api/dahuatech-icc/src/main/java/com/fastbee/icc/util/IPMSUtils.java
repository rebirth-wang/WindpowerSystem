package com.fastbee.icc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class IPMSUtils {

    public static String decrypt(String userAgent, String encryptStr) {
        try {
            encryptStr = URLDecoder.decode(encryptStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        encryptStr = encryptStr.replace(" ", "+");
        System.out.println("接收到加密信息：" +  encryptStr);
        userAgent = userAgent.substring(userAgent.lastIndexOf("_") + 1);
        String[] nums = userAgent.split("[\\D]+");
        int ciphertnum;
        String lastNum = nums[nums.length - 1];
        if (lastNum.length() > 1) {
            System.out.println(lastNum.length());
            lastNum = lastNum.substring(lastNum.length() - 1, lastNum.length());
        }
        ciphertnum = Integer.parseInt(lastNum);
        String key = userAgent.substring(ciphertnum, ciphertnum + 16);
        System.out.println("接收到加密信息秘钥：" +  key);
        String decryptStr = null;
        try {
            decryptStr = AESUtil.aesDecrypt(encryptStr, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("接收到加密信息解密后信息：" +  decryptStr);
        return decryptStr;
    }

    public static void main(String[] args) {
        String userAgent = "null_/v1/vehicleEntry/addVehicleEntryRecord__ac14d01945c5e6a6a4041c25d6e822faa11211d319b9cd2ac3c";
        String encryptStr = "oWCQKSsq8VXTD2sBF+WFcjz8h7iXv07wRcYdnoA01yEy3YDlf6XlfYGHVwJlR9LHNGr/UmqqApB29rESC2e7qxtr6kFnxKn3etIbFJUCBT9sRDT0IW4+h3ONblVsK737wl8GQ4/Tkbjx2yDEflo0CHeW0wAuBgP+zfEsMUyyCYuUmeefT/Opur+blpD6vz2z1bzYZnNrqhb1yYfSx2F965/8amdvbovPtc4DGronC10vTWNd6ew6FW8/EPSdVTMrrCK7NO+wKhSElAE5wK9A/Fxb69IMJSOsR0FVwVQERKDY8ocQ0BRB5N64urj1JFFPasRyx4WflIkpI3w2KEf72IKR0AHSlXYkHIJUFcduT+WFzAvmXy8AqV0cdYlEFxdYvILgywtBXHuKri1UzErNsSYgZ9J/4COvRX+EvyL4BdXJ62LjGRuOoul//Jqis5DKx5nz+S9Uq9aaOlkQRbIAI3Zi9KSKnCVe374hq7ZovnENSmzRJmh6S+o1o0GxXlDPIr49Ybf9ineMEfwbQ28q/o1ddifwH9dJUKTtsHqxyNE4i8YFjenEc4+P8iF5zM9qRLFdFG0qXoGMIyiY0CIUZ0ss0InQFRFY5/9wcHLE8lBhh6w0ExZ9s1gFJfL9G0vnCvTRkhbiC+MwQj8s8+xAHWQCvpkRAldCa4jgD8xdklI+FsWHWHgOmqnBpVMU6Z04vNEd5nsHLDolk+GfS1d/W+fUiJ0hQ9ANZSnR8rq6/8SMHD56dd25xFcT+dawIbQ/XKixaS30JlGGI9rqoSkkQSBeuEG/yFdWSU5Rj4aM4p3F4G1cfg5a6RL1w2DJiXYk0K9xkgIDs35Lb3hfEzb7bjWgNJ/0CtdTvgis45izozyv8Tu/Jo94u77yPrjQ8LOa+j6jUOuxXtY2DtI98aUxbiUD+1gIVa+Al4N+xaS5+KlufAADc6bfdZOHgjESoZtCrt8kZPxMv7bgk2pONN7XWgrt+QSQ3/VrkjUfcz3f8eYbhQJBMVTlufjsGcFEfcQ7p+fmDt2/gUPts6Jj9en9G6zKBm0wekHwuW/hE8lPq1y9lpsREWnMAiY6z5XvEkbl8OggAw1OhOiQQ0LnqvhX7X11+miPcxkofBktwPVV4zr/PeY+19MrLOWO8lp4Ydi/ecqeA9v+lntgaRBVOdC38bqw72+LQEaeiqCmJIno+3ygHQ2HdhVg0yS0RMCbScuEpwA6dYc1debdkxfV6Sp0dmuK0ODwt4PyzESd/SMuWkEC/BlTkaZWf8zLoQOTBLRb";
        decrypt(userAgent,encryptStr);
    }
}
