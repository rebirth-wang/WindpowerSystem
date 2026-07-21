//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum ServerType {
    MQTT(1, "MQTT", "MQTT-BROKER"),
    COAP(2, "COAP", "COAP-SERVER"),
    TCP(3, "TCP", "TCP-SERVER"),
    UDP(4, "UDP", "UDP-SERVER"),
    WEBSOCKET(5, "WEBSOCKET", "WEBSOCKET-SERVER"),
    GB28181(6, "GB28181", "SIP-SERVER"),
    HTTP(6, "HTTP", "HTTP-SERVER"),
    OTHER(999, "WEBSOCKET", "MQTT-BROKER");

    private int type;
    private String code;
    private String des;

    public static ServerType explain(String code) {
        for(ServerType var4 : values()) {
            if (var4.code.equals(code)) {
                return var4;
            }
        }

        return MQTT;
    }

    public static ServerType explainByType(int type) {
        for(ServerType var4 : values()) {
            if (var4.type == type) {
                return var4;
            }
        }

        return MQTT;
    }

    public int getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }

    public String getDes() {
        return this.des;
    }

    private ServerType(int type, String code, String des) {
        this.type = type;
        this.code = code;
        this.des = des;
    }
}
