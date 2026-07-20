package com.fastbee.sip.model;

import javax.sip.address.SipURI;
import javax.sip.header.ContentTypeHeader;

import lombok.Data;

@Data
public class ResponseAckExtraParam {
    private String content;
    private ContentTypeHeader contentTypeHeader;
    private SipURI sipURI;
    private int expires = -1;
}
