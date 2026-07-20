
package com.fastbee.coap.handler;

import java.net.InetSocketAddress;

import com.google.common.util.concurrent.SettableFuture;

import com.fastbee.coap.model.CoapRequest;
import com.fastbee.coap.model.CoapResponse;

public interface RequestConsumer {
    public void processCoapRequest(SettableFuture<CoapResponse> responseFuture, CoapRequest coapRequest,
                                            InetSocketAddress remoteSocket) throws Exception;
}
