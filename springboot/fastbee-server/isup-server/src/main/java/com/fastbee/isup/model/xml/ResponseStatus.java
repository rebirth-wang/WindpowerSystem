package com.fastbee.isup.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "ResponseStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseStatus {
    @XmlElement(name = "requestURL")
    private String requestURL;

    @XmlElement(name = "statusCode")
    private int statusCode;

    @XmlElement(name = "statusString")
    private String statusString;

    @XmlElement(name = "subStatusCode")
    private String subStatusCode;

    @XmlElement(name = "description")
    private String description;
}
