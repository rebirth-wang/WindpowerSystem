package com.fastbee.isup.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceInputPortDescriptor {

    @XmlElement(name = "adminProtocol")
    private String adminProtocol;

    @XmlElement(name = "addressingFormatType")
    private String addressingFormatType;

    @XmlElement(name = "ipAddress")
    private String ipAddress;

    @XmlElement(name = "managePortNo")
    private int managePortNo;

    @XmlElement(name = "srcInputPort")
    private int srcInputPort;

    @XmlElement(name = "userName")
    private String userName;

    @XmlElement(name = "password")
    private String password;

    @XmlElement(name = "firmwareVersion")
    private String firmwareVersion;

    @XmlElement(name = "getSubStream")
    private boolean getSubStream;
}
