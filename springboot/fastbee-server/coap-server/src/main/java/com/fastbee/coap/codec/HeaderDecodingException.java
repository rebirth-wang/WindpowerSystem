package com.fastbee.coap.codec;

import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderDecodingException extends Exception{

    private int messageID;
    private InetSocketAddress remoteSocket;

    public HeaderDecodingException(int messageID, InetSocketAddress remoteSocket, String message) {
        super(message);
        this.messageID = messageID;
        this.remoteSocket = remoteSocket;
    }

}
