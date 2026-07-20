package com.fastbee.oss.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.oss.convert.OssConfigConvert;
import com.fastbee.oss.domain.OssConfig;
import com.fastbee.oss.entity.OssConstant;
import com.fastbee.oss.mapper.OssConfigMapper;
import com.fastbee.oss.service.IOssConfigService;
import com.fastbee.oss.vo.OssConfigVO;

/**
 * 对象存储配置Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
@Service
public class OssConfigServiceImpl extends ServiceImpl<OssConfigMapper,OssConfig> implements IOssConfigService
{
    @Resource
    private OssConfigMapper ossConfigMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    public void init() {
        List<OssConfig> list = ossConfigMapper.selectList();
        // 加载OSS初始化配置
        for (OssConfig config : list) {
            String configKey = config.getConfigKey();
            if (0 == config.getStatus()) {
                redisCache.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey);
            }
            redisCache.setCacheObject(OssConstant.OSS_CONFIG_KEY+configKey, config);
        }
    }

    /**
     * 查询对象存储配置
     *
     * @param id 对象存储配置主键
     * @return 对象存储配置
     */
    @Cacheable(cacheNames = "ossConfig", key = "#root.methodName + '_' + #id", unless = "#result == null")
    @Override
    public OssConfig selectOssConfigById(Integer id)
    {
        return ossConfigMapper.selectById(id);
    }

    /**
     * 查询对象存储配置列表
     *
     * @param ossConfig 对象存储配置
     * @return 对象存储配置
     */
    @Override
    public List<OssConfigVO> selectOssConfigList(OssConfig ossConfig)
    {
        SysUser user = getLoginUser().getUser();
        ossConfig.setTenantId(user.getDept().getDeptUserId());
        LambdaQueryWrapper<OssConfig> lqw = buildQueryWrapper(ossConfig);
        List<OssConfig> ossConfigList = baseMapper.selectList(lqw);
        return OssConfigConvert.INSTANCE.convertOssConfigVOList(ossConfigList);
    }

    /**
     * 查询对象存储配置分页列表
     *
     * @param ossConfig 对象存储配置
     * @return 对象存储配置
     */
    @Override
    public Page<OssConfigVO> pageOssConfigVO(OssConfig ossConfig) {
        SysUser user = getLoginUser().getUser();
        ossConfig.setTenantId(user.getDept().getDeptUserId());
        LambdaQueryWrapper<OssConfig> lqw = buildQueryWrapper(ossConfig);
        Page<OssConfig> ossConfigPage = baseMapper.selectPage(new Page<>(ossConfig.getPageNum(), ossConfig.getPageSize()), lqw);
        return OssConfigConvert.INSTANCE.convertOssConfigVOPage(ossConfigPage);
    }

    private LambdaQueryWrapper<OssConfig> buildQueryWrapper(OssConfig query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<OssConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTenantId() != null, OssConfig::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), OssConfig::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getConfigKey()), OssConfig::getConfigKey, query.getConfigKey());
        lqw.eq(StringUtils.isNotBlank(query.getAccessKey()), OssConfig::getAccessKey, query.getAccessKey());
        lqw.eq(StringUtils.isNotBlank(query.getSecretKey()), OssConfig::getSecretKey, query.getSecretKey());
        lqw.like(StringUtils.isNotBlank(query.getBucketName()), OssConfig::getBucketName, query.getBucketName());
        lqw.eq(StringUtils.isNotBlank(query.getPrefix()), OssConfig::getPrefix, query.getPrefix());
        lqw.eq(StringUtils.isNotBlank(query.getEndpoint()), OssConfig::getEndpoint, query.getEndpoint());
        lqw.eq(StringUtils.isNotBlank(query.getDomainAlias()), OssConfig::getDomainAlias, query.getDomainAlias());
        lqw.eq(StringUtils.isNotBlank(query.getIsHttps()), OssConfig::getIsHttps, query.getIsHttps());
        lqw.eq(StringUtils.isNotBlank(query.getRegion()), OssConfig::getRegion, query.getRegion());
        lqw.eq(StringUtils.isNotBlank(query.getAccessPolicy()), OssConfig::getAccessPolicy, query.getAccessPolicy());
        lqw.eq(query.getStatus() != null, OssConfig::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getExt1()), OssConfig::getExt1, query.getExt1());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(OssConfig::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增对象存储配置
     *
     * @param ossConfig 对象存储配置
     * @return 结果
     */
    @Override
    public int insertOssConfig(OssConfig ossConfig)
    {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        ossConfig.setTenantId(user.getDept().getDeptUserId());
        ossConfig.setTenantName(user.getDept().getDeptName());
        ossConfig.setCreateBy(user.getCreateBy());
        ossConfig.setCreateTime(DateUtils.getNowDate());
        redisCache.setCacheObject(OssConstant.OSS_CONFIG_KEY+ossConfig.getConfigKey(), ossConfig);
        return ossConfigMapper.insert(ossConfig);
    }

    /**
     * 修改对象存储配置
     *
     * @param ossConfig 对象存储配置
     * @return 结果
     */

    @CacheEvict(cacheNames = "ossConfig", key = "'selectOssConfigById_' + #ossConfig.getId()")
    @Override
    public int updateOssConfig(OssConfig ossConfig)
    {
        ossConfig.setUpdateBy(getUsername());
        ossConfig.setUpdateTime(DateUtils.getNowDate());
        redisCache.setCacheObject(OssConstant.OSS_CONFIG_KEY+ossConfig.getConfigKey(), ossConfig);
        return ossConfigMapper.updateById(ossConfig);
    }

    /**
     * 批量删除对象存储配置
     *
     * @param ids 需要删除的对象存储配置主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "ossConfig", allEntries = true)
    @Override
    public int deleteOssConfigByIds(Integer[] ids)
    {
        return ossConfigMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除对象存储配置信息
     *
     * @param id 对象存储配置主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "ossConfig", key = "'selectOssConfigById_' + #id")
    @Override
    public int deleteOssConfigById(Integer id)
    {
        return ossConfigMapper.deleteById(id);
    }

    @Override
    public int updateOssConfigStatus(OssConfig ossConfig) {
        //重置其他配置状态
        ossConfigMapper.resetConfigStatus();
        int row = ossConfigMapper.updateById(ossConfig);
        if (row > 0) {
            redisCache.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, ossConfig.getConfigKey());
        }
        return row;
    }
}
