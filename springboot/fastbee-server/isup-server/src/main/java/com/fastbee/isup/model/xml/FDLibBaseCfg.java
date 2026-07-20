package com.fastbee.isup.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class FDLibBaseCfg {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "FDID")
    private String FDID;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "thresholdValue")
    private Integer thresholdValue;

    @XmlElement(name = "customInfo")
    private String customInfo;

    @XmlElement(name = "customFaceLibID")
    private String customFaceLibID;

}
