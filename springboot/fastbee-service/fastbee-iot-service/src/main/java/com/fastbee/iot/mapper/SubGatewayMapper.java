package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.model.gateWay.GateSubDeviceParamsVO;
import com.fastbee.iot.model.gateWay.GateSubDeviceVO;
import com.fastbee.iot.model.gateWay.SubDeviceListVO;
import com.fastbee.iot.model.vo.SubGatewayVO;

/**
 * 网关与子设备关联Mapper接口
 *
 * @author gsb
 * @date 2024-05-27
 */
public interface SubGatewayMapper extends BaseMapperX<SubGateway>
{

    /**
     * 查询网关与子设备关联列表
     *
     * @param gateway 网关与子设备关联
     * @return 网关与子设备关联集合
     */
    public Page<SubDeviceListVO> selectGatewayList(Page<SubDeviceListVO> page, @Param("gateway") SubGatewayVO gateway);

    /**
     * 获取可选的网关子设备列表
     * @return
     */
    Page<GateSubDeviceVO> getIsSelectGateSubDevice(Page<GateSubDeviceVO> page, @Param("subDeviceVO")GateSubDeviceVO subDeviceVO);

    /**
     * @description: 查询子设备地址
     * @param: subDeviceId 子设备id
     * @return: java.lang.Integer
     */
    String selectSlaveIdBySubDeviceId(Long subDeviceId);

    /**
     * @date 2025-04-27 15:24
     * @param parentClientId
     * @param addressList
     * @return java.util.List<com.fastbee.iot.model.gateWay.GateSubDeviceParamsVO>
     */
    List<GateSubDeviceParamsVO> selectSubModbusParamsVO(@Param("parentClientId") String parentClientId, @Param("addressList") List<String> addressList);
}
