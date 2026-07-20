package com.fastbee.isup.model.xml;

import javax.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "MaskInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaskInfo {
    @XmlElement(name = "PID")
    private String pid;

    @XmlElement(name = "picURL")
    private String picURL;
}
