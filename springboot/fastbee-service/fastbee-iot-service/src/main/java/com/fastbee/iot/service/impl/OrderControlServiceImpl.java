package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.OrderControlConvert;
import com.fastbee.iot.domain.OrderControl;
import com.fastbee.iot.mapper.OrderControlMapper;
import com.fastbee.iot.model.vo.OrderControlVO;
import com.fastbee.iot.model.vo.ThingsModelVO;
import com.fastbee.iot.service.IOrderControlService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.system.mapper.SysDeptMapper;

/**
 * 指令权限控制Service业务层处理
 *
 * @author kerwincui
 * @date 2024-07-01
 */
@Service
public class OrderControlServiceImpl extends ServiceImpl<OrderControlMapper,OrderControl> implements IOrderControlService
{
    @Resource
    private OrderControlMapper orderControlMapper;
    @Resource
    private IThingsModelService thingsModelService;
    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 查询指令权限控制
     *
     * @param orderControl 指令权限控制
     * @return 指令权限控制
     */
    @Override
    @DataScope
    public OrderControl selectOrderControlById(OrderControl orderControl)
    {
        LambdaQueryWrapper<OrderControl> queryWrapper = this.buildQueryWrapper(orderControl);
        return orderControlMapper.selectOne(queryWrapper);
    }

    /**
     * 查询指令权限控制列表
     *
     * @param orderControl 指令权限控制
     * @return 指令权限控制
     */
    @Override
    public List<OrderControlVO> selectOrderControlList(OrderControl orderControl)
    {
        orderControl.setTenantId(SecurityUtils.getDeptId());
        LambdaQueryWrapper<OrderControl> wrapper = this.buildQueryWrapper(orderControl);
        List<OrderControl> orderControls = orderControlMapper.selectList(wrapper);
        List<OrderControlVO> orderControlVOS = OrderControlConvert.INSTANCE.convertOrderControlVOList(orderControls);
        for (OrderControlVO control : orderControlVOS) {
            String[] split = control.getSelectOrder().split(",");
            List<Long> ids = Arrays.stream(split)
                    .map(s -> {
                        try {
                            return Long.parseLong(s.trim());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            ThingsModelVO thingsModelVO = new ThingsModelVO();
            thingsModelVO.setModelIdList(ids);
            List<ThingsModelVO> thingsModels = thingsModelService.selectThingsModelList(thingsModelVO);
            String names = thingsModels.stream().map(ThingsModelVO::getModelName).collect(Collectors.joining(","));
            control.setModelNames(names);
            //判断是否在执行时间内的控制
            if (Objects.nonNull(control.getStartTime()) && Objects.nonNull(control.getEndTime())) {
                Date now = DateUtils.getNowDate();
                Date startTime = control.getStartTime();
                Date endTime = control.getEndTime();
                if (now.after(startTime) && now.before(endTime) && control.getCount() > 0) {
                    control.setStatus(1);
                }else {
                    control.setStatus(0);
                }
            }
        }
        return orderControlVOS;
    }

    /**
     * 查询指令权限控制分页列表
     *
     * @param orderControlVO 指令权限控制
     * @return 指令权限控制
     */
    @Override
    @DataScope(deptAlias = "c", userAlias = "c")
    public Page<OrderControlVO> pageOrderControlVO(OrderControlVO orderControlVO) {
        Page<OrderControlVO> orderControlVOPage = orderControlMapper.selectOrderControlVoPage(new Page<>(orderControlVO.getPageNum(), orderControlVO.getPageSize()), orderControlVO);
        if (0 == orderControlVOPage.getTotal()) {
            return new Page<>();
        }
        List<OrderControlVO> voList = orderControlVOPage.getRecords();
        List<Long> tenantIdList = voList.stream().map(OrderControlVO::getTenantId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDept::getDeptUserId, tenantIdList);
        List<SysDept> sysDeptList = sysDeptMapper.selectList(queryWrapper);
        Map<Long, SysDept> deptMap = sysDeptList.stream().collect(Collectors.toMap(SysDept::getDeptUserId, Function.identity()));
        for (OrderControlVO control : voList) {
            String[] split = control.getSelectOrder().split(",");
            List<Long> ids = Arrays.stream(split)
                    .map(s -> {
                        try {
                            return Long.parseLong(s.trim());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            ThingsModelVO thingsModelVO = new ThingsModelVO();
            thingsModelVO.setModelIdList(ids);
            List<ThingsModelVO> thingsModels = thingsModelService.selectThingsModelList(thingsModelVO);
            String names = thingsModels.stream().map(ThingsModelVO::getModelName).collect(Collectors.joining(","));
            control.setModelNames(names);
            //判断是否在执行时间内的控制
            if (Objects.nonNull(control.getStartTime()) && Objects.nonNull(control.getEndTime())) {
                Date now = DateUtils.getNowDate();
                Date startTime = control.getStartTime();
                Date endTime = control.getEndTime();
                if (now.after(startTime) && now.before(endTime) && control.getCount() > 0) {
                    control.setStatus(1);
                }else {
                    control.setStatus(0);
                }
            }
            SysDept sysDept = deptMap.get(control.getTenantId());
            if (Objects.nonNull(sysDept)) {
                control.setTenantName(sysDept.getDeptName());
            }
        }
        return orderControlVOPage;
    }

    private LambdaQueryWrapper<OrderControl> buildQueryWrapper(OrderControl query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<OrderControl> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, OrderControl::getId, query.getId());
        lqw.eq(query.getTenantId() != null, OrderControl::getTenantId, query.getTenantId());
        lqw.eq(StringUtils.isNotBlank(query.getSelectOrder()), OrderControl::getSelectOrder, query.getSelectOrder());
        lqw.eq(query.getStatus() != null, OrderControl::getStatus, query.getStatus());
        lqw.eq(query.getUserId() != null, OrderControl::getUserId, query.getUserId());
        lqw.eq(query.getDeviceId() != null, OrderControl::getDeviceId, query.getDeviceId());
        lqw.eq(query.getCount() != null, OrderControl::getCount, query.getCount());
        lqw.eq(query.getStartTime() != null, OrderControl::getStartTime, query.getStartTime());
        lqw.eq(query.getEndTime() != null, OrderControl::getEndTime, query.getEndTime());
        lqw.eq(StringUtils.isNotBlank(query.getFilePath()), OrderControl::getFilePath, query.getFilePath());
        lqw.eq(StringUtils.isNotBlank(query.getImgUrl()), OrderControl::getImgUrl, query.getImgUrl());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(OrderControl::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增指令权限控制
     *
     * @param orderControl 指令权限控制
     * @return 结果
     */
    @Override
    public int insertOrderControl(OrderControl orderControl)
    {
        LoginUser user = getLoginUser();
        orderControl.setTenantId(user.getUser().getDept().getDeptUserId());
        orderControl.setCreateBy(user.getUsername());
        orderControl.setCreateTime(DateUtils.getNowDate());
        orderControl.setStatus(1);
        return orderControlMapper.insert(orderControl);
    }

    /**
     * 修改指令权限控制
     *
     * @param orderControl 指令权限控制
     * @return 结果
     */
    @Override
    public int updateOrderControl(OrderControl orderControl)
    {
        orderControl.setUpdateTime(DateUtils.getNowDate());
        orderControl.setUpdateBy(getUsername());
        return orderControlMapper.updateById(orderControl);
    }

    /**
     * 批量删除指令权限控制
     *
     * @param ids 需要删除的指令权限控制主键
     * @return 结果
     */
    @Override
    public int deleteOrderControlByIds(Long[] ids)
    {
        return orderControlMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除指令权限控制信息
     *
     * @param id 指令权限控制主键
     * @return 结果
     */
    @Override
    public int deleteOrderControlById(Long id)
    {
        return orderControlMapper.deleteById(id);
    }

    /**
     * 根据用户id获取
     * @param userId
     * @return
     */
    @Override
    public List<OrderControl> selectByUserId(Long userId,Long deviceId){
        LambdaQueryWrapper<OrderControl> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderControl::getUserId, userId);
        queryWrapper.eq(OrderControl::getDeviceId, deviceId);
        List<OrderControl> list = orderControlMapper.selectList(queryWrapper);
        for (OrderControl control : list) {
            //判断是否在执行时间内的控制
            Date now = DateUtils.getNowDate();
            Date startTime = control.getStartTime();
            Date endTime = control.getEndTime();
            if (now.after(startTime) && now.before(endTime) && control.getCount() > 0){
                control.setStatus(1);
            }else {
                control.setStatus(0);
            }
        }
        return list;
    }
    /**
     * 判定是否有操作权限
     * @param deviceId
     * @param modelId
     * @return
     */
    @Override
    public AjaxResult judgeThingsModel(Long deviceId, Long modelId) {
        Long userId = SecurityUtils.getUserId();
        LoginUser loginUser = getLoginUser();
        //这里排除管理员和终端用户和演示账号
        if (!Objects.isNull(loginUser.getDeptId()) && !userId.equals(loginUser.getUser().getDept().getDeptUserId()) && !"fastbee".equals(loginUser.getUsername())) {
            LambdaQueryWrapper<OrderControl> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderControl::getUserId, userId);
            queryWrapper.eq(OrderControl::getDeviceId, deviceId);
            List<OrderControl> list = orderControlMapper.selectList(queryWrapper);
            for (OrderControl control : list) {
                //判断是否在执行时间内的控制
                Date now = DateUtils.getNowDate();
                Date startTime = control.getStartTime();
                Date endTime = control.getEndTime();
                if (now.after(startTime) && now.before(endTime) && control.getCount() > 0) {
                    String selectOrder = control.getSelectOrder();
                    String[] ids = selectOrder.split(",");
                    if (Arrays.asList(ids).contains(modelId + "")) {
                        return AjaxResult.success();
                    }
                }
            }
            return AjaxResult.error(MessageUtils.message("device.order.control.no.permission"));
        }
        return AjaxResult.success();
    }

    @Override
    public void reduceOrderControlCount(Long id) {
        baseMapper.reduceOrderControlCount(id);
    }
}
