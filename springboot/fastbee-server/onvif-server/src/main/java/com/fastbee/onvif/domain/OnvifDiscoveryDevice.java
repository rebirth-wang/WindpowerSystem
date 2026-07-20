package com.fastbee.onvif.domain;

import lombok.Data;

/**
 * Device discovered by WS-Discovery Probe.
 */
@Data
public class OnvifDiscoveryDevice {

    private String ip;

    private Integer port;

    private String xaddr;

    private String types;

    private String scopes;
}
