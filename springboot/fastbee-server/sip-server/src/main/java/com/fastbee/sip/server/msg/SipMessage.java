package com.fastbee.sip.server.msg;

public interface SipMessage {
    String getDeviceId();

    String getSn();

    default int totalPart() {
        return 1;
    }

    default int numberOfPart() {
        return 1;
    }
}
