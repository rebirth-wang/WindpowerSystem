package com.fastbee.media.model;

import lombok.Getter;
import lombok.Setter;

import com.fastbee.media.enums.FrontEndControlType;

public class FrontEndControlCodeForAuxiliary implements IFrontEndControlCode {

    private final FrontEndControlType type =  FrontEndControlType.AUXILIARY;

    @Override
    public FrontEndControlType getType() {
        return type;
    }

    /**
     * 辅助开关控制指令： 1为开， 2为关
     */
    @Getter
    @Setter
    private Integer code;

    /**
     * 辅助开关编号
     */
    @Getter
    @Setter
    private Integer auxiliaryId;

    @Override
    public String encode() {
        return "";
    }
}
