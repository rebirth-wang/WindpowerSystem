package com.fastbee.isup.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Params {

    @XmlElement(name = "DeviceStatusXML")
    private DeviceStatusXML deviceStatusXML;
}
