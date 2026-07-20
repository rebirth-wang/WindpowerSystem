package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.SipRelation;
import com.fastbee.iot.mapper.SipRelationMapper;
import com.fastbee.iot.model.vo.SipRelationVO;
import com.fastbee.iot.service.ISipRelationService;

/**
 * 监控设备关联Service业务层处理
 *
 * @author kerwincui
 * @date 2024-06-06
 */
@Service
public class SipRelationServiceImpl extends ServiceImpl<SipRelationMapper,SipRelation> implements ISipRelationService
{
    @Resource
    private SipRelationMapper sipRelationMapper;

    /**
     * 查询监控设备关联
     *
     * @param id 监控设备关联主键
     * @return 监控设备关联
     */
    @Override
    public SipRelation selectSipRelationById(Long id)
    {
        return sipRelationMapper.selectById(id);
    }

    @Override
    public List<SipRelationVO> selectSipRelationByDeviceId(Long deviceId) {
        SipRelation sipRelation = new SipRelation();
        sipRelation.setReDeviceId(deviceId);
        return sipRelationMapper.selectSipRelationList(sipRelation);
    }

    /**
     * 查询监控设备关联列表
     *
     * @param sipRelation 监控设备关联
     * @return 监控设备关联
     */
    @Override
    public Page<SipRelationVO> selectSipRelationList(SipRelation sipRelation)
    {
        return sipRelationMapper.selectSipRelationList(new Page<>(sipRelation.getPageNum(), sipRelation.getPageSize()),sipRelation);
    }

    /**
     * 根据channelId获取关联关系
     * @param channelId
     * @return
     */
    @Override
     public SipRelation selectByChannelId(String channelId){
        LambdaQueryWrapper<SipRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SipRelation::getChannelId,channelId);
        return sipRelationMapper.selectOne(queryWrapper);
     }

    /**
     * 新增或者更新监控设备关联
     * @param sipRelation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addOrUpdateSipRelation(SipRelation sipRelation){
        String channelId = sipRelation.getChannelId();
        assert !Objects.isNull(channelId) : "channelId is null";
        LambdaQueryWrapper<SipRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SipRelation::getChannelId,channelId);
        SipRelation selectObj = sipRelationMapper.selectOne(queryWrapper);
        if (Objects.isNull(selectObj)){
            //新增
            sipRelation.setCreateTime(DateUtils.getNowDate());
            sipRelation.setCreateBy(SecurityUtils.getUsername());
            return this.insertSipRelation(sipRelation);
        }else {
            sipRelation.setUpdateTime(DateUtils.getNowDate());
            sipRelation.setUpdateBy(SecurityUtils.getUsername());
            return sipRelationMapper.updateByChannelId(sipRelation);
        }
    }

    /**
     * 新增监控设备关联
     *
     * @param sipRelation 监控设备关联
     * @return 结果
     */
    @Override
    public int insertSipRelation(SipRelation sipRelation)
    {
        sipRelation.setCreateTime(DateUtils.getNowDate());
        return sipRelationMapper.insert(sipRelation);
    }

    /**
     * 修改监控设备关联
     *
     * @param sipRelation 监控设备关联
     * @return 结果
     */
    @Override
    public int updateSipRelation(SipRelation sipRelation)
    {
        sipRelation.setUpdateTime(DateUtils.getNowDate());
        return sipRelationMapper.updateById(sipRelation);
    }


    /**
     * 批量删除监控设备关联
     *
     * @param ids 需要删除的监控设备关联主键
     * @return 结果
     */
    @Override
    public int deleteSipRelationByIds(Long[] ids)
    {
        return sipRelationMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除监控设备关联信息
     *
     * @param id 监控设备关联主键
     * @return 结果
     */
    @Override
    public int deleteSipRelationById(Long id)
    {
        return sipRelationMapper.deleteById(id);
    }

    private LambdaQueryWrapper<SipRelation> buildQueryWrapper(SipRelation query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SipRelation> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getChannelId()), SipRelation::getChannelId, query.getChannelId());
        lqw.eq(query.getReDeviceId() != null, SipRelation::getReDeviceId, query.getReDeviceId());
        lqw.eq(query.getReSceneModelId() != null, SipRelation::getReSceneModelId, query.getReSceneModelId());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SipRelation::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SipRelation entity){
        //TODO 做一些数据校验,如唯一约束
    }

}
