package com.fastbee.media.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SsrcUtil {
    private static String ssrcPrefix;
    private static List<String> isUsed;

    private static List<String> notUsed;

    private static void init() {
        isUsed = new ArrayList<String>();
        notUsed = new ArrayList<String>();
        for (int i = 1; i < 10000; i++) {
            if (i < 10) {
                notUsed.add("000" + i);
            } else if (i < 100) {
                notUsed.add("00" + i);
            } else if (i < 1000) {
                notUsed.add("0" + i);
            } else {
                notUsed.add(String.valueOf(i));
            }
        }
    }
    /**
     * 获取视频预览的SSRC值,第一位固定为0
     *
     */
    public static String getPlaySsrc(String domain) {
        return "0" + getSsrcPrefix(domain) + getSN();
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     *
     */
    public static String getPlayBackSsrc(String domain) {
        return "1" + getSsrcPrefix(domain) + getSN();
    }

    /**
     * 释放ssrc，主要用完的ssrc一定要释放，否则会耗尽
     *
     */
    public static void releaseSsrc(String ssrc) {
        if (isUsed == null || notUsed == null) {
            init();
        }
        String sn = ssrc.substring(6);
        isUsed.remove(sn);
        notUsed.add(sn);
    }

    private static String getSsrcPrefix(String domain) {
        if (ssrcPrefix == null) {
            ssrcPrefix = domain.substring(3, 8);
            init();
        }
        return ssrcPrefix;
    }

    /**
     * 获取后四位数SN,随机数
     *
     */
    private static String getSN() {
        String sn = null;
        int index = 0;
        if (notUsed.size() == 0) {
            throw new RuntimeException("ssrc已经用完");
        } else if (notUsed.size() == 1) {
            sn = notUsed.get(0);
        } else {
            index = new Random().nextInt(notUsed.size() - 1);
            sn = notUsed.get(index);
        }
        notUsed.remove(index);
        isUsed.add(sn);
        return sn;
    }

}
