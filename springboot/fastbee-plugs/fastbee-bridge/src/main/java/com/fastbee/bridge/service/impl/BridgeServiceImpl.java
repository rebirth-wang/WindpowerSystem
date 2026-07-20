package com.fastbee.bridge.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.bridge.convert.BridgeConvert;
import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.domain.vo.BridgeVO;
import com.fastbee.bridge.enums.BridgeType;
import com.fastbee.bridge.mapper.BridgeMapper;
import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.model.MqttClient;
import com.fastbee.bridge.model.MqttClientConfig;
import com.fastbee.bridge.ruleEngine.MqttClientFactory;
import com.fastbee.bridge.service.*;
import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.mqttclient.PubMqttClient;

/**
 * 数据桥接Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@Slf4j
@Service
public class BridgeServiceImpl extends ServiceImpl<BridgeMapper, Bridge> implements IBridgeService {

    @Resource
    private IHttpClientService httpClientService;

    @Resource
    private IMqttClientService mqttClientService;

    @Resource
    private IDatabaseClientServiceService dataSourceService;

    @Resource
    private PubMqttClient mqttClient;

    @PostConstruct
    public void initBridge() {
        CompletableFuture.runAsync(() -> {
            while (true) {
                if (!mqttClient.isConnected()) {
                    log.info("内部客户端未启动，等待15s。。。。。。");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    log.info("初始化mqtt客户端");
                    Bridge query = new Bridge();
                    query.setEnable("1");
                    LambdaQueryWrapper<Bridge> lqw = buildQueryWrapper(query);
                    List<Bridge> list = baseMapper.selectList(lqw);
                    for (Bridge bridge : list) {
                        if (Objects.equals(bridge.getType(), BridgeType.mqtt.getValue())) {
                            MqttClientConfig config = (MqttClientConfig) mqttClientService.buildConfig(bridge);
                            if (config != null) {
                                mqttClientService.instance(config);
                            }
                        }
                    }
                    break;
                }
            }
        });
    }

    /**
     * 查询数据桥接
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param bridge 主键
     * @return 数据桥接
     */
    @Override
    @Cacheable(cacheNames = "Bridge", key = "#id")
    @DataScope()
    public Bridge queryByIdWithCache(Bridge bridge) {
        LambdaQueryWrapper<Bridge> wrapper = this.buildQueryWrapper(bridge);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Bridge selectBridgeByName(String bridgeName) {
        LambdaQueryWrapper<Bridge> lqw = Wrappers.lambdaQuery();
        lqw.eq(Bridge::getName, bridgeName);
        return baseMapper.selectOne(lqw);
    }

    /**
     * 查询数据桥接分页列表
     *
     * @param bridge 数据桥接
     * @return 数据桥接
     */
    @Override
    @DataScope()
    public Page<BridgeVO> pageBridgeVO(Bridge bridge) {
        LambdaQueryWrapper<Bridge> lqw = buildQueryWrapper(bridge);
        lqw.orderByDesc(Bridge::getCreateTime);
        Page<Bridge> bridgePage = baseMapper.selectPage(new Page<>(bridge.getPageNum(), bridge.getPageSize()), lqw);
        return BridgeConvert.INSTANCE.convertBridgeVOPage(bridgePage);
    }

    private LambdaQueryWrapper<Bridge> buildQueryWrapper(Bridge query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Bridge> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != query.getId(), Bridge::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getConfigJson()), Bridge::getConfigJson, query.getConfigJson());
        lqw.like(StringUtils.isNotBlank(query.getName()), Bridge::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getEnable()), Bridge::getEnable, query.getEnable());
        lqw.eq(query.getStatus() != null, Bridge::getStatus, query.getStatus());
        lqw.eq(query.getType() != null, Bridge::getType, query.getType());
        lqw.eq(query.getDirection() != null, Bridge::getDirection, query.getDirection());
        lqw.eq(StringUtils.isNotBlank(query.getRoute()), Bridge::getRoute, query.getRoute());
        lqw.eq(query.getTenantId() != null, Bridge::getTenantId, query.getTenantId());
        lqw.eq(StringUtils.isNotBlank(query.getTenantName()), Bridge::getTenantName, query.getTenantName());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Bridge::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增数据桥接
     *
     * @param add 数据桥接
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(Bridge add) {
        SysUser sysUser = getLoginUser().getUser();
        // 归属为机构管理员
        add.setTenantId(sysUser.getDept().getDeptUserId());
        add.setTenantName(sysUser.getDept().getDeptName());
        add.setCreateBy(sysUser.getUserName());
        validEntityBeforeSave(add);
        if (Objects.equals(add.getType(), BridgeType.mqtt.getValue())) {
            MqttClientConfig config = (MqttClientConfig) mqttClientService.buildConfig(add);
            if (config != null) {
                mqttClientService.instance(config);
            }
        }
        return this.save(add);
    }

    /**
     * 修改数据桥接
     *
     * @param update 数据桥接
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "Bridge", key = "#update.id")
    public Boolean updateWithCache(Bridge update) {
        validEntityBeforeSave(update);
        if (Objects.equals(update.getEnable(), "0")) {
            update.setStatus(0);
        }
        if (Objects.equals(update.getType(), BridgeType.mqtt.getValue())) {
            if (update.getConfigJson() != null && update.getDirection() == 1) {
                MqttClient config = JSON.parseObject(update.getConfigJson(), MqttClient.class);
                update.setStatus(1);
                MqttClientFactory.addSubscribe(config.getHostUrl() + config.getClientId(), update.getRoute());
            }
            Config config =  mqttClientService.buildConfig(update);
            Object obj = mqttClientService.instance(config);
            if (Objects.equals(update.getEnable(), "0")) {
                mqttClientService.disconnect(obj);
            } else {
                mqttClientService.connect(obj);
            }
        }
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Bridge entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除数据桥接信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "Bridge", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public int connect(Bridge bridge) {
        AtomicInteger result = new AtomicInteger();
        // 判断 enable 字段是否为 0
        if ("0".equals(bridge.getEnable())) {
            result.set(-1);
            log.warn("当前配置未启用，请先启用");
            return result.get();
        }
        IBridge bridgeService = null;
        // 根据桥接类型获取对应的桥接服务
        if (Objects.equals(bridge.getType(), BridgeType.http.getValue())) {
            bridgeService = httpClientService;
        } else if (Objects.equals(bridge.getType(), BridgeType.mqtt.getValue())) {
            bridgeService = mqttClientService;
        } else if (Objects.equals(bridge.getType(), BridgeType.database.getValue())) {
            bridgeService = dataSourceService;
        }
        int ret = 0;
        if (bridgeService != null) {
            Config config =  bridgeService.buildConfig(bridge);
            Object obj = bridgeService.instance(config);
            ret = bridgeService.connect(obj);
            bridge.setStatus(ret);
            result.set(ret);
            this.updateWithCache(bridge);
        }
        return ret;
    }
}
