//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.ip;

import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.Forest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.config.ApiConfig;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.spring.SpringUtils;

public class AddressUtils {
    private static final Logger bY = LoggerFactory.getLogger(AddressUtils.class);
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";
    public static final String API_IPPLUS360_URL = "https://api.ipplus360.com/ip/geo/v1/city/";
    public static final String API_GETIP_URL = "https://www.ipplus360.com/getIP";
    public static final String UNKNOWN = "XX XX";
    public static final ApiConfig apiConfig = (ApiConfig)SpringUtils.getBean(ApiConfig.class);

    public static String getRealAddressByIP(String ip) {
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        } else {
            if (RuoYiConfig.isAddressEnabled()) {
                try {
                    if (StringUtils.isEmpty(apiConfig.getIpplus360Key())) {
                        bY.warn("api.ipplus360Key值未配置，使用免费接口!");
                        return getBywWhois(ip);
                    }

                    return getByIpplus360(ip);
                } catch (Exception var2) {
                    bY.error("获取地理位置异常 {}", ip);
                }
            }

            return "XX XX";
        }
    }

    public static String getBywWhois(String ip) {
        JSONObject var1 = getResBywWhois(ip);
        if (var1.containsKey("pro")) {
            String var2 = var1.getString("pro");
            String var3 = var1.getString("city");
            return String.format("%s %s", var2, var3);
        } else {
            bY.error("获取地理位置异常 {}", ip);
            return "XX XX";
        }
    }

    public static String getByIpplus360(String ip) {
        JSONObject var1 = getResByIpplus360(ip);
        if (Objects.equals(var1.getString("code"), "Success")) {
            JSONObject var2 = var1.getJSONObject("data");
            String var3 = var2.getString("prov");
            String var4 = var2.getString("city");
            return String.format("%s %s", var3, var4);
        } else {
            bY.error("获取地理位置异常 {}", ip);
            return "XX XX";
        }
    }

    public static JSONObject getResBywWhois(String ip) {
        try {
            String var1 = (String)Forest.get("http://whois.pconline.com.cn/ipJson.jsp").addQuery("ip", ip).addQuery("json", true).execute(String.class);
            if (StringUtils.isEmpty(var1)) {
                bY.error("获取地理位置异常 {}", ip);
                return new JSONObject();
            } else {
                return JSON.parseObject(var1);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
            bY.error("getResBywWhois 获取地理位置异常 {}", var2.getMessage());
            return new JSONObject();
        }
    }

    public static JSONObject getResByIpplus360(String ip) {
        if (StringUtils.isEmpty(apiConfig.getIpplus360Key())) {
            bY.error("请在配置文件替换api.ipplus360Key值!");
            return new JSONObject();
        } else {
            try {
                String var1 = (String)Forest.get("https://api.ipplus360.com/ip/geo/v1/city/").addQuery("ip", ip).addQuery("key", apiConfig.getIpplus360Key()).addQuery("coordsys", "WGS84").execute(String.class);
                if (StringUtils.isEmpty(var1)) {
                    bY.error("获取地理位置异常 {}", ip);
                    return new JSONObject();
                } else {
                    return JSON.parseObject(var1);
                }
            } catch (Exception var2) {
                var2.printStackTrace();
                bY.error("getResBywWhois 获取地理位置异常 {}", var2.getMessage());
                return new JSONObject();
            }
        }
    }

    public static String getIP() {
        try {
            String var0 = (String)Forest.get("https://www.ipplus360.com/getIP").execute(String.class);
            if (StringUtils.isEmpty(var0)) {
                bY.error("获取公网iP异常!!");
                return "error";
            }

            JSONObject var1 = JSON.parseObject(var0);
            if (Objects.equals(var1.getInteger("code"), 200)) {
                return var1.getString("data");
            }

            bY.error("获取公网iP异常:{}!", var0);
        } catch (Exception var2) {
            var2.printStackTrace();
            bY.error("getResBywWhois 获取地理位置异常 {}", var2.getMessage());
        }

        return "error";
    }

    public static void main(String[] args) {
        String var1 = getIP();
        bY.error("获取地理位置 {}", getByIpplus360(var1));
    }
}
