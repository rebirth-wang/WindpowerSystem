package com.fastbee.isup.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "FDLibBaseCfgList")
@XmlAccessorType(XmlAccessType.FIELD)
public class FDLibBaseCfgList {
    @XmlElement(name = "FDLibBaseCfg")
    private List<FDLibBaseCfg> FDLibBaseCfg;
}
