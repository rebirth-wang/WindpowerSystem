package com.fastbee.media.model;

import com.fastbee.media.enums.FrontEndControlType;

public interface IFrontEndControlCode {

    FrontEndControlType getType();
    String encode();
}
