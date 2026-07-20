package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.OrderControl;
import com.fastbee.iot.model.vo.OrderControlVO;

/**
 * 指令权限控制Service接口
 *
 * @author kerwincui
 * @date 2024-07-01
 */
public interface IOrderControlService extends IService<OrderControl>
{
    /**
     * 查询指令权限控制
     *
     * @param orderControl 指令权限
     * @return 指令权限控制
     */
    public OrderControl selectOrderControlById(OrderControl orderControl);

    /**
     * 查询指令权限控制列表
     *
     * @param orderControl 指令权限控制
     * @return 指令权限控制集合
     */
    public List<OrderControlVO> selectOrderControlList(OrderControl orderControl);

    /**
     * 查询指令权限控制列表
     *
     * @param orderControlVO 指令权限控制
     * @return 指令权限控制分页集合
     */
    Page<OrderControlVO> pageOrderControlVO(OrderControlVO orderControlVO);

    /**
     * 新增指令权限控制
     *
     * @param orderControl 指令权限控制
     * @return 结果
     */
    public int insertOrderControl(OrderControl orderControl);

    /**
     * 修改指令权限控制
     *
     * @param orderControl 指令权限控制
     * @return 结果
     */
    public int updateOrderControl(OrderControl orderControl);

    /**
     * 批量删除指令权限控制
     *
     * @param ids 需要删除的指令权限控制主键集合
     * @return 结果
     */
    public int deleteOrderControlByIds(Long[] ids);

    /**
     * 删除指令权限控制信息
     *
     * @param id 指令权限控制主键
     * @return 结果
     */
    public int deleteOrderControlById(Long id);

    /**
     * 根据用户id获取
     * @param userId
     * @return
     */
     List<OrderControl> selectByUserId(Long userId,Long deviceId);

    /**
     * 判定是否有操作权限
     * @param deviceId
     * @param modelId
     * @return
     */
    AjaxResult judgeThingsModel(Long deviceId, Long modelId);

    /**
     * 指令权限控制次数减一
     *
     * @param id ID
     */
    void reduceOrderControlCount(Long id);
}
