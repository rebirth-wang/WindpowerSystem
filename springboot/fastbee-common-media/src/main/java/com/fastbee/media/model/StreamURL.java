package com.fastbee.media.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class StreamURL implements Serializable,Cloneable {

    private String protocol;

    private String host;

    private Long port = -1L;

    private String file;

    private String url;

    public StreamURL() {
    }
    public StreamURL(String protocol, String host, Long port, String file) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.file = file;
    }

    @Override
    public String toString() {
        if (protocol != null && host != null && port != -1 ) {
            return String.format("%s://%s:%s/%s", protocol, host, port, file);
        }else {
            return null;
        }
    }
    @Override
    public StreamURL clone() throws CloneNotSupportedException {
        return (StreamURL) super.clone();
    }
}
