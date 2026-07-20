package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Protocol;
import com.fastbee.iot.model.vo.ProtocolVO;

/**
 * 协议Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface ProtocolConvert
{
    /** 代码生成区域 可直接覆盖**/
    ProtocolConvert INSTANCE = Mappers.getMapper(ProtocolConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param protocol
     * @return 协议集合
     */
    ProtocolVO convertProtocolVO(Protocol protocol);

    /**
     * VO类转换为实体类集合
     *
     * @param protocolVO
     * @return 协议集合
     */
    Protocol convertProtocol(ProtocolVO protocolVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param protocolList
     * @return 协议集合
     */
    List<ProtocolVO> convertProtocolVOList(List<Protocol> protocolList);

    /**
     * VO类转换为实体类
     *
     * @param protocolVOList
     * @return 协议集合
     */
    List<Protocol> convertProtocolList(List<ProtocolVO> protocolVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param protocolPage
     * @return 协议分页
     */
    Page<ProtocolVO> convertProtocolVOPage(Page<Protocol> protocolPage);

    /**
     * VO类转换为实体类
     *
     * @param protocolVOPage
     * @return 协议分页
     */
    Page<Protocol> convertProtocolPage(Page<ProtocolVO> protocolVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
