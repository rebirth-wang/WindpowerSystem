package com.fastbee.scada.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.scada.domain.ScadaDeviceShare;
import com.fastbee.scada.vo.ScadaDeviceShareVO;

/**
 * 【请填写功能名称】Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@Mapper
public interface ScadaDeviceShareConvert
{

    ScadaDeviceShareConvert INSTANCE = Mappers.getMapper(ScadaDeviceShareConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scadaDeviceShare
     * @return 【请填写功能名称】集合
     */
    ScadaDeviceShareVO convertScadaDeviceShareVO(ScadaDeviceShare scadaDeviceShare);

    /**
     * VO类转换为实体类集合
     *
     * @param scadaDeviceShareVO
     * @return 【请填写功能名称】集合
     */
    ScadaDeviceShare convertScadaDeviceShare(ScadaDeviceShareVO scadaDeviceShareVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param scadaDeviceShareList
     * @return 【请填写功能名称】集合
     */
    List<ScadaDeviceShareVO> convertScadaDeviceShareVOList(List<ScadaDeviceShare> scadaDeviceShareList);

    /**
     * VO类转换为实体类
     *
     * @param scadaDeviceShareVOList
     * @return 【请填写功能名称】集合
     */
    List<ScadaDeviceShare> convertScadaDeviceShareList(List<ScadaDeviceShareVO> scadaDeviceShareVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param scadaDeviceSharePage
     * @return 【请填写功能名称】分页
     */
    Page<ScadaDeviceShareVO> convertScadaDeviceShareVOPage(Page<ScadaDeviceShare> scadaDeviceSharePage);

    /**
     * VO类转换为实体类
     *
     * @param scadaDeviceShareVOPage
     * @return 【请填写功能名称】分页
     */
    Page<ScadaDeviceShare> convertScadaDeviceSharePage(Page<ScadaDeviceShareVO> scadaDeviceShareVOPage);
}
