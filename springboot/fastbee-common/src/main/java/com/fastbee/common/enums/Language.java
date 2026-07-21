//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum Language {
    ZH_CN("zh-CN"),
    EN("en-US"),
    DEFAULT("default");

    private String value;

    private Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static String matches(String language) {
        return language.equals("zh") ? ZH_CN.value : EN.value;
    }
}
