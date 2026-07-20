package com.fastbee.scada.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.scada.domain.ScadaDeviceBind;
import com.fastbee.scada.vo.ScadaBindDeviceSimVO;
import com.fastbee.scada.vo.ScadaDeviceBindVO;
import org.apache.ibatis.annotations.Param;

/**
 * 组态设备关联Mapper接口
 *
 * @author kerwincui
 * @date 2023-11-13
 */
public interface ScadaDeviceBindMapper extends BaseMapperX<ScadaDeviceBind>
{

    /**
     * 查询组态设备关联列表
     *
     * @param scadaDeviceBind 组态设备关联
     * @return 组态设备关联集合
     */
    public List<ScadaDeviceBind> selectScadaDeviceBindList(ScadaDeviceBind scadaDeviceBind);

    List<ScadaDeviceBind> listByGuidAndSerialNumber(@Param("scadaGuid") String scadaGuid, @Param("serialNumberList") List<String> serialNumberList);

    /**
     * 查询组态绑定设备
     * @param guid 组态guid
     * @return java.util.List<com.fastbee.scada.vo.ScadaBindDeviceSimVO>
     */
    List<ScadaBindDeviceSimVO> listDeviceSimByGuid(String guid);

    /**
     * 查询组态绑定设备信息
     * @param scadaDeviceBindVO 传参类
     * @return java.util.List<com.fastbee.scada.domain.ScadaDeviceBind>
     */
    Page<ScadaDeviceBindVO> selectScadaDeviceBindInfoList(Page<ScadaDeviceBindVO> page, @Param("scadaDeviceBindVO") ScadaDeviceBindVO scadaDeviceBindVO);

    /**
     * 查询场景组态绑定设备信息
     * @param scadaDeviceBindVO 传参类
     * @return java.util.List<com.fastbee.scada.domain.ScadaDeviceBind>
     */
    Page<ScadaDeviceBindVO> selectScadaSceneModelDeviceList(Page<ScadaDeviceBindVO> page, @Param("scadaDeviceBindVO") ScadaDeviceBindVO scadaDeviceBindVO);

}
