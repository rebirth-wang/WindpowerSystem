package com.fastbee.isup.model.xml;

import java.util.List;

import javax.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "InputProxyChannelStatusList")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputProxyChannelStatusList {

    @XmlAttribute
    private String version;

    @XmlElement(name = "InputProxyChannelStatus")
    private List<InputProxyChannelStatus> channels;

}
