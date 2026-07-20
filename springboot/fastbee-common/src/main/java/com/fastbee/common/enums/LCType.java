//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum LCType {
    AGENT(1L),
    ENTERPRISE(2L),
    PERSON(3L),
    TRIAL(4L);

    private Long type;

    private LCType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return this.type;
    }
}
