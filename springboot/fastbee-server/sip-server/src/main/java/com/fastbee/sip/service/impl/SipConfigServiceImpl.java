package com.fastbee.sip.service.impl;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.mapper.SipConfigMapper;
import com.fastbee.sip.service.ISipConfigService;

/**
 * sip系统配置Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Service
public class SipConfigServiceImpl extends ServiceImpl<SipConfigMapper, SipConfig> implements ISipConfigService {
    @Resource
    private SipConfigMapper sipConfigMapper;

    @Resource
    private IDeviceService deviceService;

    @Resource
    private SysSipConfig sysSipConfig;

    @Resource
    private RedisCache redisCache;

    @Override
    public void updateDefaultSipConfig(SipConfig sipConfig) {
        SysSipConfig defConfig = new SysSipConfig();
        defConfig.setEnabled(sipConfig.getEnabled() == 1);
        defConfig.setIp(sipConfig.getIp());
        defConfig.setPort(sipConfig.getPort());
        defConfig.setDomain(sipConfig.getDomainAlias());
        defConfig.setId(sipConfig.getServerSipid());
        defConfig.setPassword(sipConfig.getPassword());
        redisCache.setCacheObject(FastBeeConstant.REDIS.DEFAULT_SIP_CONFIG, sipConfig);
    }

    @Override
    public SipConfig GetDefaultSipConfig() {
        Object temp = redisCache.getCacheObject(FastBeeConstant.REDIS.DEFAULT_SIP_CONFIG);
        SipConfig sipConfig = new SipConfig();
        if (temp == null) {
            sipConfig.setEnabled(sysSipConfig.isEnabled() ? 1 : 0);
            sipConfig.setIp(sysSipConfig.getIp());
            sipConfig.setPort(sysSipConfig.getPort());
            sipConfig.setDomainAlias(sysSipConfig.getDomain());
            sipConfig.setServerSipid(sysSipConfig.getId());
            sipConfig.setPassword(sysSipConfig.getPassword());
            redisCache.setCacheObject(FastBeeConstant.REDIS.DEFAULT_SIP_CONFIG, sipConfig);
        } else if (temp instanceof SipConfig) {
            SipConfig temp2 = (SipConfig) temp;
            sipConfig.setEnabled(temp2.getEnabled());
            sipConfig.setIp(temp2.getIp());
            sipConfig.setPort(temp2.getPort());
            sipConfig.setDomainAlias(temp2.getDomainAlias());
            sipConfig.setServerSipid(temp2.getServerSipid());
            sipConfig.setPassword(temp2.getPassword());
            updateDefaultSipConfig(sipConfig);
        } else if (temp instanceof SysSipConfig) {
            SysSipConfig temp2 = (SysSipConfig) temp;
            sipConfig.setEnabled(temp2.isEnabled() ? 1 : 0);
            sipConfig.setIp(temp2.getIp());
            sipConfig.setPort(temp2.getPort());
            sipConfig.setDomainAlias(temp2.getDomain());
            sipConfig.setServerSipid(temp2.getId());
            sipConfig.setPassword(temp2.getPassword());
        }
        return sipConfig;
    }

    /**
     * 查询产品下第一条sip系统配置
     *
     * @return sip系统配置
     */
    @Cacheable(cacheNames = "sipConfig", key = "#root.methodName +'_' + #productId", unless = "#result == null")
    @Override
    public SipConfig selectSipConfigByProductId(Long productId) {
        SipConfig sipConfig1 = new SipConfig();
        sipConfig1.setProductId(productId);
        SipConfig sipConfig = sipConfigMapper.selectOne(buildQueryWrapper(sipConfig1));
        if (sipConfig == null) {
            sipConfig = GetDefaultSipConfig();
            sipConfig.setProductId(productId);
            sipConfig.setCreateTime(DateUtils.getNowDate());
            sipConfigMapper.insert(sipConfig);
        }
        return sipConfig;
    }


    @Override
    public SipConfig selectSipConfigBydeviceSipId(String deviceSipId) {
        Device device = deviceService.selectDeviceBySerialNumber(deviceSipId);
        if (device != null) {
            return this.selectSipConfigByProductId(device.getProductId());
        } else {
            return this.GetDefaultSipConfig();
        }
    }

    /**
     * 新增sip系统配置
     *
     * @param sipConfig sip系统配置
     * @return 结果
     */
    @Override
    public int insertSipConfig(SipConfig sipConfig) {
        sipConfig.setCreateTime(DateUtils.getNowDate());
        sipConfig.setCreateBy(getUserName());
        if (sipConfig.getIsdefault() != null && sipConfig.getIsdefault() == 1) {
            sipConfigMapper.resetDefaultSipConfig();
            updateDefaultSipConfig(sipConfig);
        }
        return sipConfigMapper.insert(sipConfig);
    }

    /**
     * 修改sip系统配置
     *
     * @param sipConfig sip系统配置
     * @return 结果
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "sipConfig", key = "'selectSipConfigByProductId_' + #sipConfig.productId"),
    })
    @Override
    public int updateSipConfig(SipConfig sipConfig) {
        sipConfig.setUpdateTime(DateUtils.getNowDate());
        sipConfig.setUpdateBy(getUserName());
        if (sipConfig.getIsdefault() != null && sipConfig.getIsdefault() == 1) {
            sipConfigMapper.resetDefaultSipConfig();
            updateDefaultSipConfig(sipConfig);
        }
        return sipConfigMapper.updateById(sipConfig);
    }

    @Override
    public void syncSipConfig(SysSipConfig sipConfig) {
        List<SipConfig> list = sipConfigMapper.selectSipConfigList(new SipConfig());
        for (SipConfig config : list) {
            config.setIp(sipConfig.getIp());
            config.setPort(sipConfig.getPort());
            sipConfigMapper.updateById(config);
        }
        GetDefaultSipConfig();
    }

    /**
     * 批量删除sip系统配置
     *
     * @param ids 需要删除的sip系统配置主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "sipConfig", allEntries = true)
    @Override
    public int deleteSipConfigByIds(Long[] ids) {
        return sipConfigMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @CacheEvict(cacheNames = "sipConfig", allEntries = true)
    @Override
    public int deleteSipConfigByProductIds(Long[] productIds) {
        SipConfig sipConfig = new SipConfig();
        int delete = 0;
        for (Long productId : productIds) {
            sipConfig.setProductId(productId);
            delete += sipConfigMapper.delete(buildQueryWrapper(sipConfig));
        }
        return delete;
    }

    private LambdaQueryWrapper<SipConfig> buildQueryWrapper(SipConfig query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SipConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SipConfig::getId, query.getId());
        lqw.eq(query.getProductId() != null, SipConfig::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), SipConfig::getProductName, query.getProductName());
        lqw.eq(query.getEnabled() != null, SipConfig::getEnabled, query.getEnabled());
        lqw.eq(query.getIsdefault() != null, SipConfig::getIsdefault, query.getIsdefault());
        lqw.eq(query.getSeniorSdp() != null, SipConfig::getSeniorSdp, query.getSeniorSdp());
        lqw.eq(StringUtils.isNotBlank(query.getDomainAlias()), SipConfig::getDomainAlias, query.getDomainAlias());
        lqw.eq(StringUtils.isNotBlank(query.getServerSipid()), SipConfig::getServerSipid, query.getServerSipid());
        lqw.eq(StringUtils.isNotBlank(query.getPassword()), SipConfig::getPassword, query.getPassword());
        lqw.eq(StringUtils.isNotBlank(query.getIp()), SipConfig::getIp, query.getIp());
        lqw.eq(query.getPort() != null, SipConfig::getPort, query.getPort());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), SipConfig::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SipConfig::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SipConfig::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SipConfig::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SipConfig::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SipConfig::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SipConfig::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
