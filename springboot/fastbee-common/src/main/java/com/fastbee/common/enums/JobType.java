//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

import java.util.Objects;

public enum JobType {
    Device(1),
    DeviceAlert(2),
    Scene(3),
    RuleEngine(4);

    private final Integer value;

    public static JobType fromValue(Integer value) {
        for(JobType var4 : values()) {
            if (Objects.equals(var4.getValue(), value)) {
                return var4;
            }
        }

        return null;
    }

    public static String getName(Integer value) {
        for(JobType var4 : values()) {
            if (Objects.equals(var4.getValue(), value)) {
                return var4.name();
            }
        }

        return null;
    }

    public Integer getValue() {
        return this.value;
    }

    private JobType(Integer value) {
        this.value = value;
    }
}
