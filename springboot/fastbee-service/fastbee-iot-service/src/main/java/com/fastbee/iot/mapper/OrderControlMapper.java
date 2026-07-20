package com.fastbee.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.OrderControl;
import com.fastbee.iot.model.vo.OrderControlVO;

/**
 * 指令权限控制Mapper接口
 *
 * @author kerwincui
 * @date 2024-07-01
 */
public interface OrderControlMapper extends BaseMapperX<OrderControl>
{

    /**
     * 查询指令权限控制列表
     *
     * @param orderControlVO 指令权限控制
     * @return 指令权限控制集合
     */
    public Page<OrderControlVO> selectOrderControlVoPage(Page<OrderControlVO> page, @Param("orderControlVO") OrderControlVO orderControlVO);

    /**
     * 指令权限控制次数减一
     *
     * @param id ID
     */
    void reduceOrderControlCount(Long id);
}
