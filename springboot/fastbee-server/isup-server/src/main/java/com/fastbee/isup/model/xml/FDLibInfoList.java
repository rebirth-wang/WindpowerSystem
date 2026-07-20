package com.fastbee.isup.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "FDLibInfoList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FDLibInfoList {
    @XmlElement(name = "FDLibInfo")
    private List<FDLibInfo> FDLibInfo;
}
