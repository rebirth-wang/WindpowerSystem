//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

import java.util.Arrays;

import com.fastbee.common.core.text.IntArrayValuable;

public enum CommonStatusEnum implements IntArrayValuable {
    ENABLE(0, "开启"),
    DISABLE(1, "关闭");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CommonStatusEnum::getStatus).toArray();
    private final Integer status;
    private final String name;

    public int[] array() {
        return ARRAYS;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    private CommonStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }
}
