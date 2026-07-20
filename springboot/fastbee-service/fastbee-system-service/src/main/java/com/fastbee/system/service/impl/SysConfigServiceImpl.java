package com.fastbee.system.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.CacheConstants;
import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.text.Convert;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.convert.SysConfigConvert;
import com.fastbee.system.domain.SysConfig;
import com.fastbee.system.domain.vo.SysConfigVO;
import com.fastbee.system.mapper.SysConfigMapper;
import com.fastbee.system.service.ISysConfigService;

/**
 * 参数配置 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper,SysConfig> implements ISysConfigService
{
    @Resource
    private SysConfigMapper configMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DS("master")
    public SysConfig selectConfigById(Long configId)
    {
        return configMapper.selectById(configId);
//        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectOne(buildQueryWrapper(config));
        if (StringUtils.isNotNull(retConfig))
        {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public SysConfig selectSysConfigByKey(String configKey)
    {
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        return configMapper.selectOne(buildQueryWrapper(config));
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        LambdaQueryWrapper<SysConfig> wrapper = buildQueryWrapper(config);
        return configMapper.selectList(wrapper);
    }

    /**
     * 查询参数配置分页列表
     *
     * @param sysConfig 参数配置
     * @return 参数配置
     */
    @Override
    public Page<SysConfigVO> pageSysConfigVO(SysConfig sysConfig) {
        LambdaQueryWrapper<SysConfig> lqw = buildQueryWrapper(sysConfig);
        Page<SysConfig> sysConfigPage = baseMapper.selectPage(new Page<>(sysConfig.getPageNum(), sysConfig.getPageSize()), lqw);
        return SysConfigConvert.INSTANCE.convertSysConfigVOPage(sysConfigPage);
    }

    private LambdaQueryWrapper<SysConfig> buildQueryWrapper(SysConfig query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getConfigId() != null, SysConfig::getConfigId, query.getConfigId());
        lqw.like(StringUtils.isNotBlank(query.getConfigName()), SysConfig::getConfigName, query.getConfigName());
        lqw.like(StringUtils.isNotBlank(query.getConfigKey()), SysConfig::getConfigKey, query.getConfigKey());
        lqw.eq(StringUtils.isNotBlank(query.getConfigValue()), SysConfig::getConfigValue, query.getConfigValue());
        lqw.eq(StringUtils.isNotBlank(query.getConfigType()), SysConfig::getConfigType, query.getConfigType());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysConfig::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysConfig::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysConfig::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysConfig::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysConfig::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysConfig::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        // 判断是否需要加密

        if (Objects.equals(config.getIsEncryption(), 1)) {
            // 对 configValue 进行 Base64 编码
            String encodedValue = Base64.getEncoder().encodeToString(config.getConfigValue().getBytes(StandardCharsets.UTF_8));
            config.setConfigValue(encodedValue);
        }
        int row = configMapper.insert(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        // 3. 核心：先解析原始值为明文（无论原始是否加密）
        String originalDbValue = temp.getConfigValue(); // 数据库中存储的原始值（编码/明文）
        Integer originalIsEncryption = temp.getIsEncryption(); // 原始加密状态
        Integer newIsEncryption = config.getIsEncryption(); // 新加密状态
        String inputValue = config.getConfigValue(); // 前端传入的值（可能是：新明文/原编码值/空）

        boolean shouldEncryptOriginal = Objects.equals(originalIsEncryption, 1);
        boolean shouldEncryptNew = Objects.equals(newIsEncryption, 1);

        // 3.1 第一步：解析数据库原始值为明文（基础明文）
        String basePlainValue = originalDbValue;
        if (shouldEncryptOriginal) {
            try {
                basePlainValue = new String(Base64.getDecoder().decode(originalDbValue), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                // 非Base64值，直接用原始值作为明文
                basePlainValue = originalDbValue;
            }
        }

        // 3.2 第二步：判断「用户是否真的修改了值内容」（核心修复点）
        String finalPlainValue = basePlainValue;
        if (StringUtils.isNotBlank(inputValue)) {
            // 情况1：前端传入了值，需要判断是否是「新值」而非「原编码值」
            boolean isValueChanged = false;

            // 对比逻辑：
            // - 如果原始是加密状态：前端传入的inputValue 不等于 数据库中的编码值 → 说明是新明文
            // - 如果原始是未加密状态：前端传入的inputValue 不等于 数据库中的明文 → 说明是新明文
            if (shouldEncryptOriginal) {
                isValueChanged = !StringUtils.equals(inputValue, originalDbValue);
            } else {
                isValueChanged = !StringUtils.equals(inputValue, originalDbValue);
            }

            // 只有值真的变更了，才用传入的inputValue（明文）覆盖基础明文
            if (isValueChanged) {
                finalPlainValue = inputValue;
            }
            // 若值未变更（只是切换加密状态），则保留basePlainValue（原始明文）
        }

        // 3.3 第三步：根据新加密状态处理最终存储值
        if (shouldEncryptNew) {
            // 新状态加密：对最终明文做一次编码
            String finalEncodedValue = Base64.getEncoder().encodeToString(finalPlainValue.getBytes(StandardCharsets.UTF_8));
            config.setConfigValue(finalEncodedValue);
        } else {
            // 新状态不加密：直接用最终明文
            config.setConfigValue(finalPlainValue);
        }

        int row = configMapper.updateById(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public int updateConfigByKey(SysConfig config)
    {
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(config.getConfigKey()), SysConfig::getConfigKey, config.getConfigKey());
        int row = configMapper.update(config,lqw);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteById(configId);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = this.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysConfig::getConfigKey, config.getConfigKey());
        SysConfig info = configMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
