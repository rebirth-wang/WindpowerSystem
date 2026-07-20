package com.fastbee.media.service.impl;

import java.util.*;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.SipRelation;
import com.fastbee.iot.service.IProductService;
import com.fastbee.iot.service.impl.SipRelationServiceImpl;
import com.fastbee.media.constant.MediaConstant;
import com.fastbee.media.convert.CommonChannelConvert;
import com.fastbee.media.domain.BindingCommonChannel;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.vo.CommonChannelVO;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.SessionType;
import com.fastbee.media.mapper.CommonChannelMapper;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.ICommonChannelService;

/**
 * 监控视频通道信息Service业务层处理
 *
 * @author fastbee
 * @date 2026-01-30
 */
@Service
public class CommonChannelServiceImpl extends ServiceImpl<CommonChannelMapper,CommonChannel> implements ICommonChannelService {
    @Resource
    private VideoSessionManager streamSession;

    @Resource
    private SipRelationServiceImpl sipRelationService;

    @Resource
    private IProductService productService;

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询监控视频通道信息
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 监控视频通道信息
     */
    @Override
    @Cacheable(cacheNames = "CommonChannel", key = "#id")
    public CommonChannel queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询监控视频通道信息
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 监控视频通道信息
     */
    @Override
    @Cacheable(cacheNames = "CommonChannel", key = "#id")
    public CommonChannel selectCommonChannelById(Long id){
        return this.getById(id);
    }

    @Override
    public CommonChannel selectCommonChannelByDeviceChannelId(String deviceId, String channelId){
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getDeviceId, deviceId);
        lqw.eq(CommonChannel::getChannelId, channelId);
        return this.getOne(lqw);
    }

    @Override
    public CommonChannel selectCommonChannelByChannelId(String channelId){
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommonChannel::getChannelId, channelId);
        return this.getOne(lqw);
    }

    /**
     * 查询监控视频通道信息分页列表
     *
     * @param commonChannel 监控视频通道信息
     * @return 监控视频通道信息
     */
    @Override
    public Page<CommonChannelVO> pageCommonChannelVO(CommonChannel commonChannel) {
        LambdaQueryWrapper<CommonChannel> lqw = buildQueryWrapper(commonChannel);
        Page<CommonChannel> commonChannelPage = baseMapper.selectPage(new Page<>(commonChannel.getPageNum(), commonChannel.getPageSize()), lqw);
        Page<CommonChannelVO> commonChannelVOPage = CommonChannelConvert.INSTANCE.convertCommonChannelVOPage(commonChannelPage);
        List<CommonChannelVO> sipDeviceChannelVOS = commonChannelVOPage.getRecords();
        if (sipDeviceChannelVOS != null && !sipDeviceChannelVOS.isEmpty()) {
            for (CommonChannelVO channel : sipDeviceChannelVOS) {
                // 新增关联数据查询逻辑
                SipRelation relation = sipRelationService.selectByChannelId(channel.getChannelId());
                if (relation != null) {
                    channel.setReDeviceId(relation.getReDeviceId());
                    channel.setReSceneModelId(relation.getReSceneModelId());
                }
                // 推流状态
                updateChannelStatus(channel);
            }
        }
        return commonChannelVOPage;
    }

    /**
     * 查询监控视频通道信息列表
     *
     * @param commonChannel 监控视频通道信息
     * @return 监控视频通道信息
     */
    @Override
    public List<CommonChannelVO> listCommonChannelVO(CommonChannel commonChannel) {
        LambdaQueryWrapper<CommonChannel> lqw = buildQueryWrapper(commonChannel);
        List<CommonChannel> commonChannelList = baseMapper.selectList(lqw);
        return CommonChannelConvert.INSTANCE.convertCommonChannelVOList(commonChannelList);
    }

    private LambdaQueryWrapper<CommonChannel> buildQueryWrapper(CommonChannel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<CommonChannel> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, CommonChannel::getId, query.getId());
                    lqw.eq(query.getTenantId() != null, CommonChannel::getTenantId, query.getTenantId());
                    lqw.eq(query.getProductId() != null, CommonChannel::getProductId, query.getProductId());
                    lqw.eq(StringUtils.isNotBlank(query.getDeviceId()), CommonChannel::getDeviceId, query.getDeviceId());
                    lqw.eq(StringUtils.isNotBlank(query.getChannelId()), CommonChannel::getChannelId, query.getChannelId());
                    lqw.like(StringUtils.isNotBlank(query.getChannelName()), CommonChannel::getChannelName, query.getChannelName());
                    lqw.eq(query.getRegisterTime() != null, CommonChannel::getRegisterTime, query.getRegisterTime());
                    lqw.eq(StringUtils.isNotBlank(query.getDeviceType()), CommonChannel::getDeviceType, query.getDeviceType());
                    lqw.eq(StringUtils.isNotBlank(query.getChannelType()), CommonChannel::getChannelType, query.getChannelType());
                    lqw.eq(query.getDataType() != null, CommonChannel::getDataType, query.getDataType());
                    lqw.eq(StringUtils.isNotBlank(query.getCityCode()), CommonChannel::getCityCode, query.getCityCode());
                    lqw.eq(StringUtils.isNotBlank(query.getCivilCode()), CommonChannel::getCivilCode, query.getCivilCode());
                    lqw.eq(StringUtils.isNotBlank(query.getManufacture()), CommonChannel::getManufacture, query.getManufacture());
                    lqw.eq(StringUtils.isNotBlank(query.getModel()), CommonChannel::getModel, query.getModel());
                    lqw.eq(StringUtils.isNotBlank(query.getOwner()), CommonChannel::getOwner, query.getOwner());
                    lqw.eq(StringUtils.isNotBlank(query.getPassword()), CommonChannel::getPassword, query.getPassword());
                    lqw.eq(StringUtils.isNotBlank(query.getParentId()), CommonChannel::getParentId, query.getParentId());
                    lqw.eq(StringUtils.isNotBlank(query.getIpAddress()), CommonChannel::getIpAddress, query.getIpAddress());
                    lqw.eq(query.getPort() != null, CommonChannel::getPort, query.getPort());
                    lqw.eq(query.getPtzType() != null, CommonChannel::getPtzType, query.getPtzType());
                    lqw.eq(StringUtils.isNotBlank(query.getPtzTypeText()), CommonChannel::getPtzTypeText, query.getPtzTypeText());
                    lqw.eq(query.getStatus() != null, CommonChannel::getStatus, query.getStatus());
                    lqw.eq(query.getLongitude() != null, CommonChannel::getLongitude, query.getLongitude());
                    lqw.eq(query.getLatitude() != null, CommonChannel::getLatitude, query.getLatitude());
                    lqw.eq(StringUtils.isNotBlank(query.getStreamId()), CommonChannel::getStreamId, query.getStreamId());
                    lqw.eq(StringUtils.isNotBlank(query.getPlayUrl()), CommonChannel::getPlayUrl, query.getPlayUrl());
                    lqw.eq(StringUtils.isNotBlank(query.getProxyUrl()), CommonChannel::getProxyUrl, query.getProxyUrl());
                    lqw.eq(query.getHasAudio() != null, CommonChannel::getHasAudio, query.getHasAudio());
                    lqw.eq(query.getHasBroadcast() != null, CommonChannel::getHasBroadcast, query.getHasBroadcast());
                    lqw.eq(StringUtils.isNotBlank(query.getCtrlConfig()), CommonChannel::getCtrlConfig, query.getCtrlConfig());
                    lqw.eq(StringUtils.isNotBlank(query.getExtendInfo()), CommonChannel::getExtendInfo, query.getExtendInfo());
                    lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), CommonChannel::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), CommonChannel::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, CommonChannel::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), CommonChannel::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, CommonChannel::getUpdateTime, query.getUpdateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), CommonChannel::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(CommonChannel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增监控视频通道信息
     *
     * @param add 监控视频通道信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(CommonChannel add) {
        validEntityBeforeSave(add);
        setDataTypeByProductTransport(add);
        add.setCreateTime(DateUtils.getNowDate());
        return this.save(add);
    }

    /**
     * 修改监控视频通道信息
     *
     * @param update 监控视频通道信息
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "CommonChannel", key = "#update.id")
    public Boolean updateWithCache(CommonChannel update) {
        validEntityBeforeSave(update);
        setDataTypeByProductTransport(update);
        update.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(update);
    }

    @Override
    @CacheEvict(cacheNames = "CommonChannel", allEntries = true)
    public Boolean upsertWithCache(CommonChannel commonChannel) {
        CommonChannel oldChannel = getOldChannel(commonChannel);
        if (oldChannel == null) {
            return insertWithCache(commonChannel);
        }
        commonChannel.setId(oldChannel.getId());
        return updateWithCache(commonChannel);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CommonChannel entity){
        // 做一些数据校验,如唯一约束
    }

    private void setDataTypeByProductTransport(CommonChannel channel) {
        Long productId = getProductId(channel);
        if (productId == null) {
            return;
        }
        Product product = productService.selectProductByProductId(productId);
        if (product == null) {
            return;
        }
        Integer dataType = ChannelStreamType.getDataTypeByTransport(product.getTransport());
        if (dataType != null) {
            channel.setDataType(dataType);
        }
    }

    private Long getProductId(CommonChannel channel) {
        if (channel == null) {
            return null;
        }
        if (channel.getProductId() != null) {
            return channel.getProductId();
        }
        if (channel.getId() == null) {
            return null;
        }
        CommonChannel oldChannel = this.getById(channel.getId());
        return oldChannel == null ? null : oldChannel.getProductId();
    }

    private CommonChannel getOldChannel(CommonChannel channel) {
        if (channel == null) {
            return null;
        }
        if (channel.getId() != null) {
            return this.getById(channel.getId());
        }
        if (StringUtils.isBlank(channel.getDeviceId()) || StringUtils.isBlank(channel.getChannelId())) {
            return null;
        }
        return selectCommonChannelByDeviceChannelId(channel.getDeviceId(), channel.getChannelId());
    }

    /**
     * 校验并批量删除监控视频通道信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "CommonChannel", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /**
     * 根据channelId获取绑定的设备或场景
     *
     * @param channelId
     * @return
     */
    @Override
    public BindingCommonChannel getBindingChannel(String channelId) {
        return baseMapper.getBindingChannel(channelId);
    }

    @Override
    public List<CommonChannelVO> listRelDeviceOrScene(String serialNumber, Long sceneModelId) {
        List<CommonChannel> sipDeviceChannelList = new ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(serialNumber)) {
            sipDeviceChannelList = baseMapper.selectDeviceRelSipDeviceChannelList(serialNumber);
        }
        if (null != sceneModelId) {
            sipDeviceChannelList = baseMapper.selectSceneRelSipDeviceChannelList(sceneModelId);
        }
        List<CommonChannelVO> sipDeviceChannelVOS = CommonChannelConvert.INSTANCE.convertCommonChannelVOList(sipDeviceChannelList);
        for (CommonChannelVO sipDeviceChannel : sipDeviceChannelVOS) {
//            Stream play = playService.play(String.valueOf(sipDeviceChannel.getDeviceSipId()), sipDeviceChannel.getChannelSipId(), false);
//            if (null != play) {
//                sipDeviceChannel.setPlayUrl(play.getPlayurl());
//            }
        }
        return sipDeviceChannelVOS;
    }

    private void updateChannelStatus(CommonChannelVO channel) {
        String playsid = String.format("%s_%s_%s", MediaConstant.PREFIX.PREFIX_GB_PLAY, channel.getDeviceId(), channel.getChannelId());
        String playrsid = String.format("%s_%s_%s", MediaConstant.PREFIX.PREFIX_GB_PLAY_RECORD, channel.getDeviceId(), channel.getChannelId());
        VideoSessionInfo pinfo = streamSession.getSessionInfo(channel.getDeviceId(),
                channel.getChannelId(), playsid, SessionType.PLAY.name());
        if (pinfo != null) {
            channel.setStreamPush(pinfo.getPushing() ? 1 : 0);
        }
        VideoSessionInfo prinfo = streamSession.getSessionInfo(channel.getDeviceId(),
                channel.getChannelId(), playrsid, SessionType.PLAYRECORD.name());
        if (prinfo != null) {
            channel.setStreamRecord(prinfo.getRecording() ? 1 : 0);
        }
    }

    /** 自定义代码区域 END**/
}
