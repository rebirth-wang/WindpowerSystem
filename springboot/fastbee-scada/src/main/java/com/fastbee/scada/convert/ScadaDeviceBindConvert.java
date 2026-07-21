package com.fastbee.scada.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.scada.domain.ScadaDeviceBind;
import com.fastbee.scada.vo.ScadaDeviceBindVO;

/**
 * 组态设备关联Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaDeviceBindConvert
{

    ScadaDeviceBindConvert INSTANCE = Mappers.getMapper(ScadaDeviceBindConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scadaDeviceBind
     * @return 组态设备关联集合
     */
    ScadaDeviceBindVO convertScadaDeviceBindVO(ScadaDeviceBind scadaDeviceBind);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaDeviceBindVO
     * @return 组态设备关联集合
     */
    ScadaDeviceBind convertScadaDeviceBind(ScadaDeviceBindVO scadaDeviceBindVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaDeviceBindList
     * @return 组态设备关联集合
     */
    List<ScadaDeviceBindVO> convertScadaDeviceBindVOList(List<ScadaDeviceBind> scadaDeviceBindList);

    /**
     * VO类转换为实体类
     *
     * @param scadaDeviceBindVOList
     * @return 组态设备关联集合
     */
    List<ScadaDeviceBind> convertScadaDeviceBindList(List<ScadaDeviceBindVO> scadaDeviceBindVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaDeviceBindPage
     * @return 组态设备关联分页
     */
    Page<ScadaDeviceBindVO> convertScadaDeviceBindVOPage(Page<ScadaDeviceBind> scadaDeviceBindPage);

    /**
     * VO类转换为实体类
     *
     * @param scadaDeviceBindVOPage
     * @return 组态设备关联分页
     */
    Page<ScadaDeviceBind> convertScadaDeviceBindPage(Page<ScadaDeviceBindVO> scadaDeviceBindVOPage);
}
