package com.fastbee.onvif.bean;


public class OnvifStream {
    private String uri;
    private boolean invalidAfterConnect;
    private boolean invalidAfterReboot;
    private String timeout;

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isInvalidAfterConnect() {
        return this.invalidAfterConnect;
    }

    public void setInvalidAfterConnect(boolean invalidAfterConnect) {
        this.invalidAfterConnect = invalidAfterConnect;
    }

    public boolean isInvalidAfterReboot() {
        return this.invalidAfterReboot;
    }

    public void setInvalidAfterReboot(boolean invalidAfterReboot) {
        this.invalidAfterReboot = invalidAfterReboot;
    }

    public String getTimeout() {
        return this.timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
