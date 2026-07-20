package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.model.vo.SubGatewayVO;

/**
 * 网关与子设备关联Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface SubGatewayConvert
{

    SubGatewayConvert INSTANCE = Mappers.getMapper(SubGatewayConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param subGateway
     * @return 网关与子设备关联集合
     */
    SubGatewayVO convertSubGatewayVO(SubGateway subGateway);

    /**
     * VO类转换为实体类集合
     *
     * @param subGatewayVO
     * @return 网关与子设备关联集合
     */
    SubGateway convertSubGateway(SubGatewayVO subGatewayVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param subGatewayList
     * @return 网关与子设备关联集合
     */
    List<SubGatewayVO> convertSubGatewayVOList(List<SubGateway> subGatewayList);

    /**
     * VO类转换为实体类
     *
     * @param subGatewayVOList
     * @return 网关与子设备关联集合
     */
    List<SubGateway> convertSubGatewayList(List<SubGatewayVO> subGatewayVOList);
}
