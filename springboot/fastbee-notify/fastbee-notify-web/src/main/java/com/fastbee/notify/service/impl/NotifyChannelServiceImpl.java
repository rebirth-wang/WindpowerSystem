package com.fastbee.notify.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysDictData;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.notify.NotifyConfigVO;
import com.fastbee.common.extend.enums.NotifyChannelProviderEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.notify.convert.NotifyChannelConvert;
import com.fastbee.notify.domain.NotifyChannel;
import com.fastbee.notify.domain.NotifyTemplate;
import com.fastbee.notify.mapper.NotifyChannelMapper;
import com.fastbee.notify.mapper.NotifyTemplateMapper;
import com.fastbee.notify.service.INotifyChannelService;
import com.fastbee.notify.vo.ChannelProviderVO;
import com.fastbee.notify.vo.NotifyChannelVO;
import com.fastbee.system.service.ISysDictDataService;

/**
 * 通知渠道Service业务层处理
 *
 * @author kerwincui
 * @date 2023-12-01
 */
@Service
public class NotifyChannelServiceImpl extends ServiceImpl<NotifyChannelMapper,NotifyChannel> implements INotifyChannelService
{
    @Resource
    private NotifyChannelMapper notifyChannelMapper;
    @Resource
    private ISysDictDataService sysDictDataService;
    @Resource
    private NotifyTemplateMapper notifyTemplateMapper;
    @Resource
    private RedisCache redisCache;

    /**
     * 查询通知渠道
     *
     * @param notifyChannel 通知渠道
     * @return 通知渠道
     */
    @Override
    @DataScope
    public NotifyChannel selectNotifyChannelById(NotifyChannel notifyChannel)
    {
        LambdaQueryWrapper<NotifyChannel> queryWrapper = this.buildQueryWrapper(notifyChannel);
        return notifyChannelMapper.selectOne(queryWrapper);
    }

    /**
     * 查询通知渠道分页列表
     *
     * @param notifyChannel 通知渠道
     * @return 通知渠道
     */
    @Override
    @DataScope()
    public Page<NotifyChannelVO> pageNotifyChannelVO(NotifyChannel notifyChannel) {
        LambdaQueryWrapper<NotifyChannel> lqw = buildQueryWrapper(notifyChannel);
        lqw.orderByDesc(NotifyChannel::getCreateTime);
        Page<NotifyChannel> notifyChannelPage = baseMapper.selectPage(new Page<>(notifyChannel.getPageNum(), notifyChannel.getPageSize()), lqw);
        return NotifyChannelConvert.INSTANCE.convertNotifyChannelVOPage(notifyChannelPage);
    }

    private LambdaQueryWrapper<NotifyChannel> buildQueryWrapper(NotifyChannel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<NotifyChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, NotifyChannel::getId, query.getId());
        lqw.like(StringUtils.isNotBlank(query.getName()), NotifyChannel::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getChannelType()), NotifyChannel::getChannelType, query.getChannelType());
        lqw.eq(StringUtils.isNotBlank(query.getProvider()), NotifyChannel::getProvider, query.getProvider());
        lqw.eq(StringUtils.isNotBlank(query.getConfigContent()), NotifyChannel::getConfigContent, query.getConfigContent());
        lqw.eq(query.getTenantId() != null, NotifyChannel::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), NotifyChannel::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(NotifyChannel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }

    /**
     * 新增通知渠道
     *
     * @param notifyChannel 通知渠道
     * @return 结果
     */
    @Override
    public int insertNotifyChannel(NotifyChannel notifyChannel)
    {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        notifyChannel.setTenantId(user.getDept().getDeptUserId());
        notifyChannel.setTenantName(user.getDept().getDeptName());
        notifyChannel.setCreateBy(user.getUserName());
        return notifyChannelMapper.insert(notifyChannel);
    }

    /**
     * 修改通知渠道
     *
     * @param notifyChannel 通知渠道
     * @return 结果
     */
    @Override
    public int updateNotifyChannel(NotifyChannel notifyChannel) {
        notifyChannel.setUpdateTime(DateUtils.getNowDate());
        LambdaQueryWrapper<NotifyTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotifyTemplate::getChannelId, notifyChannel.getId());
        List<NotifyTemplate> notifyTemplateList = notifyTemplateMapper.selectList(queryWrapper);
        for (NotifyTemplate notifyTemplate : notifyTemplateList) {
            SmsFactory.unregister(notifyTemplate.getId().toString());
        }
        notifyChannel.setUpdateBy(getUsername());
        clearCacheById(notifyChannel.getId());
        return notifyChannelMapper.updateById(notifyChannel);
    }

    /**
     * 批量删除通知渠道
     *
     * @param ids 需要删除的通知渠道主键
     * @return 结果
     */
    @Override
    public int deleteNotifyChannelByIds(Long[] ids)
    {
        int result = notifyChannelMapper.deleteBatchIds(Arrays.asList(ids));
        if (result > 0) {
            notifyTemplateMapper.deleteBatchIds(Arrays.asList(ids));
            for (Long id : ids) {
                clearCacheById(id);
            }
        }
        return result;
    }

    /**
     * 删除通知渠道信息
     *
     * @param id 通知渠道主键
     * @return 结果
     */
    @Override
    public int deleteNotifyChannelById(Long id)
    {
        int result = notifyChannelMapper.deleteById(id);
        if (result > 0) {
            notifyTemplateMapper.deleteBatchIds(Arrays.asList(id));
            clearCacheById(id);
        }
        return result;
    }

    @Override
    public List<ChannelProviderVO> listChannel() {
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("notify_channel_type");
        sysDictData.setStatus(0);
        List<SysDictData> parentDataList = sysDictDataService.selectDictDataList(sysDictData);
        if (CollectionUtils.isEmpty(parentDataList)) {
            return new ArrayList<>();
        }
        List<String> dictValueList = parentDataList.stream().map(SysDictData::getDictValue).collect(Collectors.toList());
        List<String> dictTypeList = new ArrayList<>();
        for (String s : dictValueList) {
            dictTypeList.add("notify_channel_" + s + "_provider");
        }
        List<SysDictData> childerDataList = sysDictDataService.selectDictDataListByDictTypes(dictTypeList);
        Map<String, List<SysDictData>> map = childerDataList.stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        List<ChannelProviderVO> result = new ArrayList<>();
        for (SysDictData dictData : parentDataList) {
            ChannelProviderVO channelProviderVO = new ChannelProviderVO();
            channelProviderVO.setChannelType(dictData.getDictValue());
            channelProviderVO.setChannelName(dictData.getDictLabel());
            String key = "notify_channel_" + dictData.getDictValue() + "_provider";
            if (!map.containsKey(key)) {
                result.add(channelProviderVO);
                continue;
            }
            List<SysDictData> dataList = map.get(key);
            List<ChannelProviderVO.Provider> providerList = new ArrayList<>();
            for (SysDictData data : dataList) {
                ChannelProviderVO.Provider provider = new ChannelProviderVO.Provider();
                provider.setProvider(data.getDictValue());
                provider.setProviderName(data.getDictLabel());
                provider.setCategory(dictData.getDictValue());
                providerList.add(provider);
            }
            channelProviderVO.setProviderList(providerList);
            result.add(channelProviderVO);
        }
        return result;
    }

    @Override
    public List<NotifyConfigVO> getConfigContent(String channelType, String provider) {
        return NotifyChannelProviderEnum.getConfigContent(Objects.requireNonNull(NotifyChannelProviderEnum.getByChannelTypeAndProvider(channelType, provider)));
    }

    private static final String CACHE_KEY_CHANNEL = "notify:channel:";
    private static final Integer CACHE_TTL_MINUTES = 10;

    @Override
    public NotifyChannel getByIdWithCache(Long id) {
        if (id == null) {
            return null;
        }
        String cacheKey = CACHE_KEY_CHANNEL + id;
        Object cached = redisCache.getCacheObject(cacheKey);
        if (cached != null) {
            return JSONObject.parseObject(cached.toString(), NotifyChannel.class);
        }
        NotifyChannel channel = notifyChannelMapper.selectById(id);
        if (channel != null) {
            redisCache.setCacheObject(cacheKey, JSONObject.toJSONString(channel), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return channel;
    }

    @Override
    public void clearCacheById(Long id) {
        if (id != null) {
            redisCache.deleteObject(CACHE_KEY_CHANNEL + id);
        }
    }
}
