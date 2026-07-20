package com.fastbee.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Protocol;

/**
 * 协议管理Mapper
 * @author gsb
 * @date 2022/10/19 15:46
 */
@Repository
public interface ProtocolMapper extends BaseMapperX<Protocol> {

    /**
     * 获取所有唯一协议
     * @param protocol
     * @return
     */
   public List<Protocol> selectByUnion(Protocol protocol);

    /**
     * 获取所有可用协议
     * @param status
     * @param delFlag
     * @return
     */
   public List<Protocol> selectAll(@Param("status") Integer status, @Param("delFlag") Integer delFlag);
}
