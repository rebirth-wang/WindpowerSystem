package com.fastbee.protocol.service;

import java.util.List;

import com.fastbee.common.extend.core.domin.mq.message.ProtocolDto;
import com.fastbee.protocol.base.protocol.IProtocol;

/**
 * @author gsb
 * @date 2022/10/10 17:07
 */
public interface IProtocolManagerService {

    /**
     *获取所有的协议，包含脚本解析协议和系统内部定义协议
     */
    public List<ProtocolDto> getAllProtocols();

    /**
     * 根据协议编码获取系统内部协议
      * @param protocolCode 协议编码
     * @return 协议
     */
   IProtocol getProtocolByProtocolCode(String protocolCode);

}
