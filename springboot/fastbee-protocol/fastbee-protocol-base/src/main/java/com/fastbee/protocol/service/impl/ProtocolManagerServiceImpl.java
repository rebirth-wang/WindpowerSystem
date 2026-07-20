package com.fastbee.protocol.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.annotation.SysProtocol;
import com.fastbee.common.extend.core.domin.mq.message.ProtocolDto;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.domain.Protocol;
import com.fastbee.iot.service.IProtocolService;
import com.fastbee.protocol.base.protocol.IProtocol;
import com.fastbee.protocol.service.IProtocolManagerService;

/**
 * 设备内部协议管理类
 * @author bill
 */
@Slf4j
@Service
public class ProtocolManagerServiceImpl<M> implements IProtocolManagerService {

    @Autowired
    private IProtocolService protocolService;
    private final Map<String, IProtocol> protocolMap = new HashMap<>();

    /**
     *获取所有的协议，包含脚本解析协议和系统内部定义协议
     */
    @Override
    public List<ProtocolDto> getAllProtocols(){
        List<ProtocolDto> result = new ArrayList<>();
        //获取@SysProtocol注解的bean
        Map<String, Object> annotations = SpringUtils.getBeanWithAnnotation(SysProtocol.class);
        //获取外部配置协议
        Protocol protocol = new Protocol();
        protocol.setProtocolStatus(1);
        List<Protocol> protocolList = protocolService.selectByCondition(protocol);
        annotations.forEach((key,value)->{
            SysProtocol annotation = value.getClass().getAnnotation(SysProtocol.class);
            ProtocolDto protocolDto = new ProtocolDto();
            protocolDto.setCode(annotation.protocolCode());
            protocolDto.setName(annotation.name());
            protocolDto.setDescription(annotation.description());
            /*系统内部协议*/
            protocolDto.setProtocolType(0);
            result.add(protocolDto);
            boolean match = protocolList.stream().anyMatch(po -> po.getProtocolCode().equals(annotation.protocolCode()));
            if (!match){
                Protocol newPo = new Protocol();
                newPo.setProtocolCode(annotation.protocolCode());
                newPo.setProtocolName(annotation.name());
                newPo.setJarSign(annotation.description());
                newPo.setProtocolStatus(1);
                newPo.setDelFlag(0);
                newPo.setCreateTime(DateUtils.getNowDate());
                protocolService.insertProtocol(newPo);
            }
        });
        /**外部协议*/
        for (Protocol item : protocolList) {
            ProtocolDto protocolDto = new ProtocolDto();
            protocolDto.setCode(item.getProtocolCode());
            protocolDto.setName(item.getProtocolName());
            protocolDto.setProtocolUrl(item.getProtocolFileUrl());
            protocolDto.setProtocolType(item.getProtocolType());
            result.add(protocolDto);
        }
        return result;
    }

    /**
     * 根据协议编码获取系统内部协议
     * @param protocolCode 协议编码
     * @return 协议
     */
    @Override
    public IProtocol getProtocolByProtocolCode(String protocolCode) {
        if (!CollectionUtils.isEmpty(this.protocolMap)){
            return protocolMap.get(protocolCode);
        }
        Map<String, IProtocol> annotations = SpringUtils.getBeanWithAnnotation(SysProtocol.class);
        IProtocol protocol = null;
        for (IProtocol item : annotations.values()) {
            SysProtocol annotation = item.getClass().getAnnotation(SysProtocol.class);
            protocolMap.put(annotation.protocolCode(), item);
            if (annotation.protocolCode().equals(protocolCode)){
                protocol = item;
            }
        }
        return protocol;
    }

}
