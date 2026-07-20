//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum ThingsModelType {
    PROP(1, "PROPERTY", "属性", "properties"),
    SERVICE(2, "FUNCTION", "服务", "functions"),
    EVENT(3, "EVENT", "事件", "events");

    int code;
    String type;
    String name;
    String list;

    public static ThingsModelType getType(int code) {
        for(ThingsModelType var4 : values()) {
            if (var4.code == code) {
                return var4;
            }
        }

        return PROP;
    }

    public static ThingsModelType getType(String type) {
        for(ThingsModelType var4 : values()) {
            if (var4.type.equals(type)) {
                return var4;
            }
        }

        return PROP;
    }

    public static String getName(int code) {
        for(ThingsModelType var4 : values()) {
            if (var4.code == code) {
                return var4.list;
            }
        }

        return PROP.list;
    }

    public int getCode() {
        return this.code;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getList() {
        return this.list;
    }

    private ThingsModelType(int code, String type, String name, String list) {
        this.code = code;
        this.type = type;
        this.name = name;
        this.list = list;
    }
}
