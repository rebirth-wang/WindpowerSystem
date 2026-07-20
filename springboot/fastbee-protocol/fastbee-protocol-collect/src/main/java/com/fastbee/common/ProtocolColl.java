package com.fastbee.common;

import lombok.Data;

import com.fastbee.protocol.base.protocol.IProtocol;

/**
 * @author bill
 */
@Data
public class ProtocolColl {

    private IProtocol protocol;

    private Long productId;

    private String protocolCode;
}
