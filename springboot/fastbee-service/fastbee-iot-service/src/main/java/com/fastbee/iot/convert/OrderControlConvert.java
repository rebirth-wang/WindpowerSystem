package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.OrderControl;
import com.fastbee.iot.model.vo.OrderControlVO;

/**
 * 指令权限控制Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface OrderControlConvert
{

    OrderControlConvert INSTANCE = Mappers.getMapper(OrderControlConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param orderControl
     * @return 指令权限控制集合
     */
    OrderControlVO convertOrderControlVO(OrderControl orderControl);

    /**
     * VO类转换为实体类集合
     *
     * @param orderControlVO
     * @return 指令权限控制集合
     */
    OrderControl convertOrderControl(OrderControlVO orderControlVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param orderControlList
     * @return 指令权限控制集合
     */
    List<OrderControlVO> convertOrderControlVOList(List<OrderControl> orderControlList);

    /**
     * VO类转换为实体类
     *
     * @param orderControlVOList
     * @return 指令权限控制集合
     */
    List<OrderControl> convertOrderControlList(List<OrderControlVO> orderControlVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param orderControlPage
     * @return 指令权限控制分页
     */
    Page<OrderControlVO> convertOrderControlVOPage(Page<OrderControl> orderControlPage);

    /**
     * VO类转换为实体类
     *
     * @param orderControlVOPage
     * @return 指令权限控制分页
     */
    Page<OrderControl> convertOrderControlPage(Page<OrderControlVO> orderControlVOPage);
}
