package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.constant.Constants;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.constant.ProductAuthConstant;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.enums.DeviceDistributeTypeEnum;
import com.fastbee.common.enums.DeviceRecordTypeEnum;
import com.fastbee.common.enums.StatusEnum;
import com.fastbee.common.enums.ThingsModelType;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.config.HttpAuthConfig;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelValuesInput;
import com.fastbee.common.extend.enums.ModbusParamsPollTypeEnum;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.mybatis.LambdaQueryWrapperX;
import com.fastbee.common.utils.CaculateVariableAndNumberUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.http.HttpUtils;
import com.fastbee.common.utils.ip.AddressUtils;
import com.fastbee.common.utils.ip.IpUtils;
import com.fastbee.common.utils.json.JsonUtils;
import com.fastbee.framework.mybatis.helper.DataBaseHelper;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.convert.DeviceConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.*;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModelItem.EnumItem;
import com.fastbee.iot.model.ThingsModels.ThingsModelShadow;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO;
import com.fastbee.iot.model.gateWay.ProductSubGatewayVO;
import com.fastbee.iot.model.gateWay.SubDeviceListVO;
import com.fastbee.iot.model.vo.*;
import com.fastbee.iot.service.*;
import com.fastbee.iot.tsdb.model.TdLogDto;
import com.fastbee.iot.tsdb.service.ILogService;
import com.fastbee.system.mapper.SysDeptMapper;
import com.fastbee.system.service.ISysDeptService;
import com.fastbee.system.service.ISysUserService;

/**
 * 设备Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
    @Value("${server.broker.port}")
    private Long brokerPort;
    @Autowired
    private ITSLCache itslCache;
    @Autowired
    private IToolService toolService;
    @Resource
    private ProductMapper productMapper;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ILogService logService;
    @Autowired
    private IAlertLogService alertLogService;
    @Autowired
    private RedisCache redisCache;
    @Resource
    @Lazy
    private IDeviceCache deviceCache;
    @Resource
    private IOrderControlService orderControlService;
    @Resource
    private IFunctionLogService functionLogService;

    @Resource
    private FunctionLogMapper functionLogMapper;
    @Resource
    private IProductAuthorizeService productAuthorizeService;
    @Resource
    private IDeviceShareService deviceShareService;
    @Resource
    private ISceneService sceneService;
    @Resource
    private ISipRelationService sipRelationService;
    @Resource
    private IThingsModelService thingsModelService;
    @Resource
    private SceneDeviceMapper sceneDeviceMapper;
    @Resource
    private ITSLValueCache itslValueCache;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private ISysDeptService sysDeptService;
    @Resource
    private DeviceRecordMapper deviceRecordMapper;
    @Resource
    private SubGatewayMapper subGatewayMapper;
    @Resource
    private ISubGatewayService subGatewayService;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceUserMapper deviceUserMapper;
    @Resource
    private ProductModbusJobMapper productModbusJobMapper;
    @Resource
    private IModbusJobService modbusJobService;
    @Resource
    private IProductSubGatewayService productSubGatewayService;
    @Resource
    private ModbusParamsMapper modbusParamsMapper;
    @Resource
    private HttpAuthConfig httpAuthConfig;

    @Value("${server.tcp.port}")
    private Long tcpPort;

    @Value("${server.coap.port}")
    private Long coapPort;

    @Value("${server.http.port}")
    private Long httpPort;

    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;

    @Resource
    private AlertLogMapper alertLogMapper;

    @Resource
    private IDeviceUpdateService deviceUpdateService;

    @Resource
    private ICardService cardService;

    @Resource
    private IDeviceExtParamValueService deviceExtParamValueService;


    // select cache

    /**
     * 查询设备
     *
     * @param deviceId 设备id
     * @return 设备
     */
//    @Cacheable(cacheNames = "Device", key = "#root.methodName + ':' + #serialNumber", unless = "#result == null")
    @Override
    public DeviceVO selectDeviceByDeviceId(Long deviceId) {
        SysUser user = getLoginUser().getUser();
        DeviceVO deviceVO;
        if (null == user.getDeptId()) {
            this.checkDeviceDataScope(deviceId);
            Device device = deviceMapper.selectById(deviceId);
            deviceVO = DeviceConvert.INSTANCE.convertDeviceVO(device);
        } else {
            Device param = new Device();
            param.setDeviceId(deviceId);
            deviceVO = deviceMapper.selectDeviceByDeviceId(param);
        }
        if (Objects.isNull(deviceVO)) {
            return null;
        }
        List<ValueItem> list = itslValueCache.getCacheDeviceStatus(deviceVO.getProductId(), deviceVO.getSerialNumber());
        if (list != null && list.size() > 0) {
            // redis中获取设备状态（物模型值）
            deviceVO.setThingsModelValue(JSONObject.toJSONString(list));
        }
        //查询关联的监控设备
        SipRelation sipRelation = new SipRelation();
        sipRelation.setReDeviceId(deviceVO.getDeviceId());
        List<SipRelationVO> sipRelationVOList = sipRelationService.selectSipRelationList(sipRelation).getRecords();
        deviceVO.setSipRelationVOList(sipRelationVOList);
        // 没图片用产品图片
        if (StringUtils.isEmpty(deviceVO.getImgUrl())) {
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getProductId, deviceVO.getProductId());
            queryWrapper.select(Product::getImgUrl);
            Product product = productMapper.selectOne(queryWrapper);
            deviceVO.setImgUrl(Objects.nonNull(product) ? product.getImgUrl() : null);
        }
        if (DeviceType.SUB_GATEWAY.getCode() == deviceVO.getDeviceType()) {
            deviceVO.setCanConfigPoll(false);
            LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubGateway::getSubClientId, deviceVO.getSerialNumber());
            SubGateway subGateway = subGatewayMapper.selectOne(queryWrapper);
            if (Objects.nonNull(subGateway)) {
                deviceVO.setParentProductId(subGateway.getParentProductId());
                deviceVO.setParentSerialNumber(subGateway.getParentClientId());
            }
        } else {
            if (FastBeeConstant.PROTOCOL.ModbusRtu.equals(deviceVO.getProtocolCode())
                    || FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(deviceVO.getProtocolCode())
                    || FastBeeConstant.PROTOCOL.ModbusTcp.equals(deviceVO.getProtocolCode())) {
                LambdaQueryWrapper<ModbusParams> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ModbusParams::getProductId, deviceVO.getProductId());
                ModbusParams modbusParams = modbusParamsMapper.selectOne(queryWrapper);
                if (null != modbusParams && DeviceType.SUB_GATEWAY.getCode() != deviceVO.getDeviceType()) {
                    deviceVO.setCanConfigPoll(ModbusParamsPollTypeEnum.CLOUD_POLL.getType().equals(modbusParams.getPollType()));
                } else {
                    deviceVO.setCanConfigPoll(false);
                }
            }
        }
        if (DeviceType.GATEWAY.getCode() == deviceVO.getDeviceType()) {
            deviceVO.setParentProductId(deviceVO.getProductId());
            deviceVO.setParentSerialNumber(deviceVO.getSerialNumber());
        }
        // 查询设备sim卡
        deviceVO.setIccid(cardService.selectIccIdByDeviceId(deviceId));
        DeviceExtParamValue deviceExtParamValue = new DeviceExtParamValue();
        deviceExtParamValue.setDeviceId(deviceId);
        List<ProductExtParamVO> extParamVOList = deviceExtParamValueService.pageDeviceExtParamValueVO(deviceExtParamValue).getRecords();
        deviceVO.setExtParamList(extParamVOList);
        return deviceVO;
    }

    @Override
    public void checkDeviceDataScope(Long deviceId) {
        Long userId = getUserId();
        DeviceUserVO deviceUserVO = deviceUserMapper.selectDeviceUserByDeviceId(deviceId);
        if (Objects.nonNull(deviceUserVO) && deviceUserVO.getUserId().equals(userId)) {
            return;
        }
        DeviceShare deviceShare = deviceShareService.selectDeviceShareByDeviceIdAndUserId(deviceId, userId);
        if (Objects.nonNull(deviceShare)) {
            return;
        }
        throw new ServiceException(MessageUtils.message("no.operate.permission"));
    }

    /**
     * 根据设备编号查询设备
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    @Cacheable(cacheNames = "Device", key = "#root.methodName + ':' + #serialNumber", unless = "#result == null")
    @Override
    public Device selectDeviceBySerialNumber(String serialNumber) {
        return deviceMapper.selectDeviceBySerialNumber(serialNumber);
    }

    /**
     * 根据设备编号查询简洁设备
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    @Cacheable(cacheNames = "Device", key = "#root.methodName + ':' + #serialNumber", unless = "#result == null")
    @Override
    public Device selectShortDeviceBySerialNumber(String serialNumber) {
        Device device = deviceMapper.selectShortDeviceBySerialNumber(serialNumber);
        if (!Objects.isNull(device)) {
            // redis中获取设备状态（物模型值）
            List<ValueItem> list = itslValueCache.getCacheDeviceStatus(device.getProductId(), device.getSerialNumber());
            if (list != null && list.size() > 0) {
                device.setThingsModelValue(JSONObject.toJSONString(list));
            }
        }
        return device;
    }

    /**
     * 查询设备状态以及传输协议
     *
     * @param serialNumber
     * @return
     */
    @Cacheable(cacheNames = "Device", key = "#root.methodName + ':' + #serialNumber", unless = "#result == null")
    @Override
    public DeviceStatusVO selectDeviceStatusAndTransportStatus(String serialNumber) {
        return deviceMapper.selectDeviceStatusAndTransportStatus(serialNumber);
    }

    /**
     * 根据设备编号查询协议编号
     *
     * @param serialNumber 设备编号
     * @return 协议编号
     */
    @Cacheable(cacheNames = "Device", key = "#root.methodName + ':' + #serialNumber", unless = "#result == null")
    @Override
    public ProductCode getProtocolBySerialNumber(String serialNumber) {
        return deviceMapper.getProtocolBySerialNumber(serialNumber);
    }

    /**
     * 删除设备
     *
     * @param deviceId 需要删除的设备主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "Device", allEntries = true)
    @Override
    public int deleteDeviceByDeviceId(Long deviceId) {
        //查询设备
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            log.warn("deleteDeviceByDeviceId: 设备不存在, deviceId={}", deviceId);
            return -1;
        }
        SysUser user = getLoginUser().getUser();
        Long sceneUserId;
        if (null == user.getDeptId()) {
            sceneUserId = user.getUserId();
        } else {
            sceneUserId = user.getDept() != null ? user.getDept().getDeptUserId() : user.getUserId();
        }
        // 2.3版本更改 设备管理者和设备拥有者为true，普通用户如果不是设备所有者，只能删除设备用户和用户自己的设备关联分组信息
        boolean isGeneralUser;
        if (isAdmin(user.getUserId())) {
            isGeneralUser = false;
        } else {
            // 终端用户和非设备所属机构用户不允许删除设备
            if (null == user.getDeptId()) {
                isGeneralUser = true;
            } else if (user.getDept() == null || !Objects.equals(device.getTenantId(), user.getDept().getDeptUserId())) {
                isGeneralUser = true;
            } else {
                isGeneralUser = false;
            }
        }
        // 设备下不能有场景联动
        List<SceneDeviceBindVO> sceneDeviceBindVOList = sceneDeviceMapper.listSceneDeviceBind(device.getSerialNumber());
        if (null != sceneDeviceBindVOList.stream().filter(s -> null != s.getUserId() && sceneUserId.equals(s.getUserId())).findAny().orElse(null)) {
            return 1;
        }
        LambdaQueryWrapperX<SceneModelDevice> wrapperX = new LambdaQueryWrapperX<>();
        wrapperX.eq(SceneModelDevice::getCusDeviceId, device.getDeviceId());
        List<SceneModelDevice> modelDeviceList = sceneModelDeviceMapper.selectList(wrapperX);
        if (!isGeneralUser && org.apache.commons.collections4.CollectionUtils.isNotEmpty(modelDeviceList)) {
            return 2;
        }

        // 查绑定用户被分享用户配置的场景，需要把场景删掉
        DeviceUserVO deviceUserVO = deviceUserMapper.selectDeviceUserByDeviceId(deviceId);
        List<Long> userIdSceneList = new ArrayList<>();
        if (null != deviceUserVO) {
            userIdSceneList.add(deviceUserVO.getUserId());
        }
        Long[] shareSceneIds = null;
        List<DeviceShare> deviceShares = deviceShareService.selectDeviceShareByDeviceId(deviceId);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deviceShares)) {
            List<Long> userIdList = deviceShares.stream().map(DeviceShare::getUserId).collect(Collectors.toList());
            userIdSceneList.addAll(userIdList);
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIdSceneList)) {
            shareSceneIds = sceneDeviceMapper.listSceneIdByDeviceIdAndUserId(device.getSerialNumber(), userIdSceneList);
        }
        if (isGeneralUser) {
            if (null != deviceUserVO && deviceUserVO.getUserId().equals(user.getUserId())) {
                if (null != shareSceneIds && shareSceneIds.length > 0) {
                    sceneService.deleteSceneBySceneIds(shareSceneIds);
                }
                // 删除用户的设备用户信息。
                LambdaQueryWrapper<DeviceUser> deviceUserWrapper = new LambdaQueryWrapper<>();
                deviceUserWrapper.eq(DeviceUser::getDeviceId, deviceId);
                deviceUserWrapper.eq(DeviceUser::getUserId, user.getUserId());
                deviceUserMapper.delete(deviceUserWrapper);
                deviceShareService.deleteDeviceShareByDeviceId(deviceId);
            } else {
                DeviceShare deviceShare = new DeviceShare();
                deviceShare.setDeviceId(deviceId).setUserId(user.getUserId());
                deviceShareService.deleteDeviceShareByDeviceIdAndUserId(deviceShare);
            }
            // 删除用户分组中的设备 普通用户，且不是设备所有者。
            deviceMapper.deleteDeviceGroupByDeviceId(new UserIdDeviceIdModel(user.getUserId(), deviceId));
        } else {
            if (null != shareSceneIds && shareSceneIds.length > 0) {
                sceneService.deleteSceneBySceneIds(shareSceneIds);
            }
            // 删除设备分组。  租户、管理员和设备所有者
            deviceMapper.deleteDeviceGroupByDeviceId(new UserIdDeviceIdModel(null, deviceId));
            // 删除设备用户。
            LambdaQueryWrapper<DeviceUser> deviceUserWrapper = new LambdaQueryWrapper<>();
            deviceUserWrapper.eq(DeviceUser::getDeviceId, deviceId);
            deviceUserMapper.delete(deviceUserWrapper);
            deviceShareService.deleteDeviceShareByDeviceId(deviceId);
            // 删除定时任务 TODO - emq兼容
            // deviceJobService.deleteJobByDeviceIds(new Long[]{deviceId});
            // 批量删除设备监测日志
            logService.deleteDeviceLogByDeviceNumber(device.getSerialNumber());
            // 批量删除设备功能日志
            functionLogService.deleteFunctionLogByDeviceNumber(device.getSerialNumber());
            // 批量删除设备告警记录
            alertLogService.deleteAlertLogBySerialNumber(device.getSerialNumber());
            // redis中删除设备物模型（状态）
            String key = RedisKeyBuilder.buildTSLVCacheKey(device.getProductId(), device.getSerialNumber());
            redisCache.deleteObject(key);
            // 删除设备
            deviceMapper.deleteById(deviceId);
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
            // 删除tcp协议设备缓存
            String tcpKey1 = RedisKeyBuilder.buildModbusTcpCacheKey(device.getSerialNumber());
            String tcpKey2 = RedisKeyBuilder.buildModbusTcpRuntimeCacheKey(device.getSerialNumber());
            redisCache.deleteStrObject(tcpKey1);
            redisCache.deleteStrHash(tcpKey2);
            // 删除轮询任务
            ModbusJobVO modbusJobVO = new ModbusJobVO();
            modbusJobVO.setDeviceId(deviceId);
            List<ModbusJobVO> modbusJobVOList = modbusJobService.selectModbusJobList(modbusJobVO);
            for (ModbusJobVO jobVO : modbusJobVOList) {
                try {
                    modbusJobService.deleteModbusJobByTaskId(jobVO);
                } catch (SchedulerException e) {
                    log.error("删除设备时删除设备轮询任务出错，{}，{}", e, jobVO);
                }
            }
            // 删除网关绑定子设备关系
            subGatewayService.deleteByParentClientId(device.getSerialNumber());
            LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubGateway::getSubClientId, device.getSerialNumber());
            subGatewayMapper.delete(queryWrapper);
            // 删除modbus轮询指令
            String keyModbus = RedisKeyBuilder.buildModbusRuntimeCacheKey(device.getSerialNumber());
            redisCache.deleteObject(keyModbus);
            // 删除设备回收记录
            LambdaQueryWrapper<DeviceRecord> deviceRecordWrapper = new LambdaQueryWrapper<>();
            deviceRecordWrapper.eq(DeviceRecord::getDeviceId, deviceId);
            deviceRecordMapper.delete(deviceRecordWrapper);
        }
        return 0;
    }


    /**
     * TODO-2.3 查询设备属性、功能、事件
     *
     * @return 设备
     */
    @Override
    public DeviceStatistic selectDeviceStatistic() {
        LoginUser loginUser = getLoginUser();
        DeviceStatistic statistic = new DeviceStatistic();
        Device device = new Device();
        if (loginUser.getUser().getDept() == null) {
            log.warn("selectDeviceStatistic: loginUser dept is null, userId={}", loginUser.getUserId());
            return statistic;
        }
        device.setTenantId(loginUser.getUser().getDept().getDeptUserId());
        DeviceStatistic thingsCount = logService.selectCategoryLogCount(device);
        if (thingsCount == null) {
            return statistic;
        }
        // 组合属性、功能、事件和监测数据
        statistic.setPropertyCount(thingsCount.getPropertyCount());
        statistic.setEventCount(thingsCount.getEventCount());
        statistic.setMonitorCount(thingsCount.getMonitorCount());
        return statistic;
    }

    /**
     * 获取设备、产品和告警数量
     *
     * @return 设备
     */
    @Override
    public DeviceStatistic selectDeviceProductAlertCount() {
        LoginUser loginUser = getLoginUser();
        Long deptId = loginUser.getDeptId();
        // 获取设备、产品和告警数量
        DeviceStatistic statistic = deviceMapper.selectDeviceProductAlertCount(deptId);
        if (statistic == null) {
            statistic = new DeviceStatistic();
        }
        return statistic;
    }

    /**
     * @return 设备
     */
    @Override
    public Long selectFunctionLogCount() {
        LoginUser loginUser = getLoginUser();
        Device device = new Device();
        if (loginUser.getUser().getDept() == null) {
            log.warn("selectFunctionLogCount: loginUser dept is null, userId={}", loginUser.getUserId());
            return 0L;
        }
        device.setTenantId(loginUser.getUser().getDept().getDeptUserId());

        //事件日志都存于mysql的function_log表，需要单独查询
        return functionLogMapper.selectFunctionLogCount(device);
    }


    /**
     * 根据设备编号查询设备认证信息
     *
     * @param model 设备编号和产品ID
     * @return 设备
     */
    @Override
    public ProductAuthenticateModel selectProductAuthenticate(AuthenticateInputModel model) {
        return deviceMapper.selectProductAuthenticate(model);
    }


    /**
     * 更新设备的物模型
     *
     * @param input    设备ID和物模型值
     * @param type     1=属性 2=功能 3=事件
     * @param isShadow 是否影子值
     * @return 设备
     */
    @Override
    @DSTransactional
    public List<ThingsModelSimpleItem> reportDeviceThingsModelValue(ThingsModelValuesInput input, Integer type, boolean isShadow) {
        String key = RedisKeyBuilder.buildTSLVCacheKey(input.getProductId(), input.getDeviceNumber());
        Map<String, String> maps = new HashMap<String, String>();
        List<ThingsModelSimpleItem> list = new ArrayList<>();

        //属性存储集合
        List<DeviceLog> deviceLogList = new ArrayList<>();
        //指令存储集合
        List<FunctionLog> functionLogList = new ArrayList<>();
        for (ThingsModelSimpleItem item : input.getThingsModelValueRemarkItem()) {
            String identity = item.getId();
            String serialNumber = input.getDeviceNumber();
            ThingsModelValueItem thingModels = thingsModelService.getSingleThingModels(input.getProductId(), identity);
            if (Objects.isNull(thingModels)) {
                log.warn("=>查询物模型为空[{}],标识符:[{}]", input.getProductId(), identity);
                continue;
            }
            String id = item.getId();
            String value = item.getValue();
            item.setName(thingModels.getName());

            /* ★★★★★★★★★★★★★★★★★★★★★★  数据计算 -开始 ★★★★★★★★★★★★★★★★★★★★★★*/
            //有计算公式的，经过计算公式
            if (StringUtils.isNotEmpty(thingModels.getFormula())) {
                Map<String, String> params = new HashMap<>(2);
//                params.put("%s", value);
                String formula = thingModels.getFormula().replace("%s", "A");
                params.put("A", value);
                value = String.valueOf(CaculateVariableAndNumberUtils.execute(formula, params));
                item.setValue(value);
            }
            /* ★★★★★★★★★★★★★★★★★★★★★★  数据计算 -结束  ★★★★★★★★★★★★★★★★★★★★★★*/

            /* ★★★★★★★★★★★★★★★★★★★★★★  处理数据 - 开始 ★★★★★★★★★★★★★★★★★★★★★★*/
            ValueItem valueItem = new ValueItem();
            valueItem.setId(identity);
            if (isShadow) {
                // 获取设备实时值
                ValueItem cacheIdentifier = itslValueCache.getCacheIdentifier(input.getProductId(), input.getDeviceNumber(), thingModels.getId());
                if (Objects.isNull(cacheIdentifier)) {
                    log.warn("=>查询物模型值为空[{}],标识符:[{}]", input.getProductId(), identity);
                    continue;
                }
                valueItem.setShadow(value);
                // 影子模式也缓存value值，不然设备上线后处理会报null
                if (null != cacheIdentifier.getValue()) {
                    valueItem.setValue(cacheIdentifier.getValue());
                }
            } else {
                valueItem.setValue(value);
                valueItem.setShadow(value);
                valueItem.setTs(DateUtils.getNowDate());
            }
            maps.put(identity, JSONObject.toJSONString(valueItem));

            /* ★★★★★★★★★★★★★★★★★★★★★★  处理数据 - 结束 ★★★★★★★★★★★★★★★★★★★★★★*/

            /*★★★★★★★★★★★★★★★★★★★★★★  存储数据 - 开始 ★★★★★★★★★★★★★★★★★★★★★★*/
            ThingsModelType modelType = ThingsModelType.getType(thingModels.getType());
            // 查询设备缓存
            DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(input.getDeviceNumber());
            if (Objects.isNull(deviceMetaData) || Objects.isNull(deviceMetaData.getDevice())) {
                log.warn("=>deviceMetaData为空[{}]", input.getDeviceNumber());
                continue;
            }
            switch (modelType) {
                case PROP:
                    if (1 == thingModels.getIsHistory()) {
                        DeviceLog deviceLog = new DeviceLog();
                        deviceLog.setSerialNumber(serialNumber);
                        deviceLog.setLogType(type.byteValue());
                        // 1=影子模式，2=在线模式，3=其他
                        byte mode = (byte) (isShadow ? 1 : 2);
                        deviceLog.setMode(mode);
                        // 设备日志值
                        deviceLog.setLogValue(value);
                        deviceLog.setRemark(item.getRemark());
                        deviceLog.setIdentify(id);
                        deviceLog.setCreateTime(DateUtils.getNowDate());
                        deviceLog.setCreateBy(deviceMetaData.getDevice().getCreateBy());
                        deviceLog.setUserId(deviceMetaData.getDevice().getTenantId());
                        deviceLog.setUserName(deviceMetaData.getDevice().getTenantName());
                        deviceLog.setTenantId(deviceMetaData.getDevice().getTenantId());
                        deviceLog.setTenantName(deviceMetaData.getDevice().getTenantName());
                        deviceLog.setModelName(thingModels.getName());
                        deviceLog.setIsMonitor(thingModels.getIsMonitor().byteValue());
                        deviceLogList.add(deviceLog);
                    }
                    break;
                case SERVICE:
                    if (1 == thingModels.getIsHistory()) {
                        FunctionLog function = new FunctionLog();
                        function.setCreateTime(DateUtils.getNowDate());
                        function.setFunValue(value);
                        function.setSerialNumber(input.getDeviceNumber());
                        function.setIdentify(id);
                        function.setShowValue(value);
                        // 属性获取
                        function.setFunType(2);
                        function.setUserId(deviceMetaData.getDevice().getTenantId());
                        function.setCreateBy(deviceMetaData.getDevice().getCreateBy());
                        function.setModelName(thingModels.getName());
                        functionLogList.add(function);
                    }
                    break;
                case EVENT:
                    DeviceLog event = new DeviceLog();
                    byte logType = 3;
                    byte isMonitor = 0;
                    byte mode = 2;
                    event.setDeviceId(deviceMetaData.getDevice().getDeviceId());
                    event.setDeviceName(deviceMetaData.getDevice().getDeviceName());
                    event.setLogValue(value);
                    event.setSerialNumber(serialNumber);
                    event.setIdentify(id);
                    event.setLogType(logType);
                    event.setIsMonitor(isMonitor);
                    event.setUserId(deviceMetaData.getDevice().getTenantId());
                    event.setUserName(deviceMetaData.getDevice().getTenantName());
                    event.setTenantId(deviceMetaData.getDevice().getTenantId());
                    event.setTenantName(deviceMetaData.getDevice().getTenantName());
                    event.setCreateTime(DateUtils.getNowDate());
                    event.setCreateBy(deviceMetaData.getDevice().getCreateBy());
                    // 1=影子模式，2=在线模式，3=其他
                    event.setMode(mode);
                    event.setModelName(thingModels.getName());
                    deviceLogList.add(event);
                    break;
            }
            list.add(item);
        }
        // 缓存最新一条数据到redis
        redisCache.hashPutAll(key, maps);
        //指令存储,影子模式不存储
        if (!CollectionUtils.isEmpty(functionLogList) && !isShadow) {
            functionLogService.insertBatch(functionLogList);
        }
        if (!CollectionUtils.isEmpty(deviceLogList) && !isShadow) {
            // 给每个日志设置唯一时间戳，并按设备号分组后批量写入
            long baseTs = System.currentTimeMillis();
            Map<String, List<DeviceLog>> deviceLogsBySerial = new LinkedHashMap<>();
            for (int i = 0; i < deviceLogList.size(); i++) {
                DeviceLog deviceLog = deviceLogList.get(i);
                // 每条间隔1毫秒，避免TDengine时间冲突
                deviceLog.setTs(new Date(baseTs + i));
                if (StringUtils.isEmpty(deviceLog.getSerialNumber())) {
                    logService.saveDeviceLog(deviceLog);
                    continue;
                }
                deviceLogsBySerial.computeIfAbsent(deviceLog.getSerialNumber(), key1 -> new ArrayList<>()).add(deviceLog);
            }
            for (Map.Entry<String, List<DeviceLog>> entry : deviceLogsBySerial.entrySet()) {
                logService.saveBatch(new TdLogDto(entry.getKey(), entry.getValue()));
            }

        }
        /* ★★★★★★★★★★★★★★★★★★★★★★  存储数据 - 结束 ★★★★★★★★★★★★★★★★★★★★★★*/
        return list;
    }

    /**
     * 查询设备分页列表
     *
     * @param deviceVO 设备
     * @return 设备
     */
    @Override
    @DataScope()
    public Page<DeviceVO> pageDeviceVO(DeviceVO deviceVO) {
        Device device = DeviceConvert.INSTANCE.convertDevice(deviceVO);
        LambdaQueryWrapper<Device> lqw = buildQueryWrapper(device);
        // 数据范围过滤
        if (ObjectUtil.isNull(deviceVO.getDeptId())) {
            if (ObjectUtil.isNotEmpty(deviceVO.getParams().get(DataScopeAspect.DATA_SCOPE))) {
                lqw.apply((String) deviceVO.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        } else {
            if (Boolean.TRUE.equals(deviceVO.getShowChild())) {
                SysDept sysDept = new SysDept();
                sysDept.setDeptId(deviceVO.getDeptId());
                List<SysDept> sysDeptList = sysDeptService.listDeptAndChild(sysDept);
                List<Long> tenantIdList = sysDeptList.stream().map(SysDept::getDeptUserId).collect(Collectors.toList());
                lqw.in(Device::getTenantId, tenantIdList);
            } else {
                SysDept sysDept = sysDeptMapper.selectDeptById(deviceVO.getDeptId());
                if (sysDept == null) {
                    throw new ServiceException(MessageUtils.message("dept.not.found"));
                }
                lqw.eq(Device::getTenantId, sysDept.getDeptUserId());
            }
        }
        lqw.orderByDesc(Device::getCreateTime);
        Page<Device> devicePage = baseMapper.selectPage(new Page<>(device.getPageNum(), device.getPageSize()), lqw);
        if (0 == devicePage.getTotal()) {
            return new Page<>();
        }
        Page<DeviceVO> deviceVOPage = DeviceConvert.INSTANCE.convertDeviceVOPage(devicePage);
        List<DeviceVO> deviceVOList = deviceVOPage.getRecords();
        List<Long> tenantIdList = deviceVOList.stream().map(DeviceVO::getTenantId).distinct().collect(Collectors.toList());
        List<SysDept> deviceDeptNameList = this.selectDeptNameByDeptUserIdList(tenantIdList);
        Map<Long, SysDept> deptMap = deviceDeptNameList.stream().collect(Collectors.toMap(SysDept::getDeptUserId, Function.identity()));
        for (DeviceVO device1 : deviceVOList) {
            SysDept sysDept = deptMap.get(device1.getTenantId());
            if (Objects.nonNull(sysDept)) {
                device1.setDeptId(sysDept.getDeptId());
                device1.setDeptName(sysDept.getDeptName());
            }
        }
        return deviceVOPage;
    }

    private List<SysDept> selectDeptNameByDeptUserIdList(List<Long> deptUserId) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDept::getDeptUserId, deptUserId);
        queryWrapper.select(SysDept::getDeptName, SysDept::getDeptId, SysDept::getDeptUserId);
        return sysDeptMapper.selectList(queryWrapper);
    }

    /**
     * 查询未分配授权码设备列表
     *
     * @param deviceVO 设备
     * @return 设备
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "d")
    public Page<Device> selectUnAuthDeviceList(DeviceVO deviceVO) {
        return deviceMapper.selectUnAuthDeviceList(new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize()), deviceVO);
    }

    /**
     * TODO-2.3 查询分组可添加设备分页列表（分组用户与设备用户匹配）
     *
     * @param deviceVO 设备
     * @return 设备
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "d")
    public Page<DeviceVO> selectDeviceListByGroup(DeviceVO deviceVO) {
        return deviceMapper.selectDeviceListByGroup(new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize()), deviceVO);
    }

    /**
     * 查询所有设备简短列表
     *
     * @return 设备
     */
    @Override
    @DataScope()
    public List<DeviceAllShortOutput> selectAllDeviceShortList(DeviceVO deviceVO) {
        MPJLambdaWrapper<Device> wrapper = JoinWrappers.lambda(Device.class);
        // select
        wrapper.select(Device::getDeviceId, Device::getDeviceName, Device::getProductName, Device::getTenantName,
                Device::getSerialNumber, Device::getGwDevCode, Device::getFirmwareVersion, Device::getStatus, Device::getRssi, Device::getIsShadow,
                Device::getLocationWay, Device::getActiveTime, Device::getNetworkAddress, Device::getLongitude, Device::getLatitude);
        // where
        wrapper.eq(deviceVO.getProductId() != null, Device::getProductId, deviceVO.getProductId());

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(deviceVO.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            wrapper.apply((String) deviceVO.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        // group by
        wrapper.groupBy(Device::getDeviceId, Device::getDeviceName, Device::getProductName, Device::getTenantName,
                Device::getSerialNumber, Device::getGwDevCode, Device::getFirmwareVersion, Device::getStatus, Device::getRssi, Device::getIsShadow,
                Device::getLocationWay, Device::getActiveTime, Device::getNetworkAddress, Device::getLongitude, Device::getLatitude);
        Page<Device> page = deviceMapper.selectPage(new Page<>(1, 3000), wrapper);
        if (0 == page.getTotal()) {
            return new ArrayList<>();
        }
        List<Device> deviceList = page.getRecords();
        List<DeviceAllShortOutput> deviceAllShortOutputs = DeviceConvert.INSTANCE.convertDeviceShortList(deviceList);
        if (!CollectionUtils.isEmpty(deviceAllShortOutputs)) {
            List<String> serialNumberList = deviceAllShortOutputs.stream().map(DeviceAllShortOutput::getSerialNumber).distinct().collect(Collectors.toList());
            List<List<String>> partition = Lists.partition(serialNumberList, 500);
            Map<String, Boolean> hasAlertMap = new HashMap<>(5);
            for (List<String> serNumList : partition) {
                List<DeviceAllShortOutput> deviceAllShortOutputs1 = alertLogMapper.selectHasAlertMap(serNumList);
                if (CollectionUtils.isEmpty(deviceAllShortOutputs1)) {
                    continue;
                }
                hasAlertMap.putAll(deviceAllShortOutputs1.stream().collect(Collectors.toMap(DeviceAllShortOutput::getSerialNumber, DeviceAllShortOutput::getHasAlert)));
            }
            for (DeviceAllShortOutput deviceAllShortOutput : deviceAllShortOutputs) {
                Boolean b = hasAlertMap.get(deviceAllShortOutput.getSerialNumber());
                deviceAllShortOutput.setHasAlert(!Objects.isNull(b) && b);
            }
        }
        return deviceAllShortOutputs;
    }

    /**
     * 查询设备分页简短列表
     *
     * @param deviceVO 设备
     * @return 设备
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "d")
    public Page<DeviceShortOutput> selectDeviceShortList(DeviceVO deviceVO) {
        LoginUser loginUser = getLoginUser();
        // 数据范围过滤
        if (ObjectUtil.isNotNull(deviceVO.getDeptId())) {
            if (ObjectUtil.isNull(deviceVO.getShowChild())) {
                deviceVO.setShowChild(false);
            }
            Map<String, Object> p = deviceVO.getParams();
            p.put("findInSetStr", DataBaseHelper.findInSet(deviceVO.getDeptId(), "de.ancestors"));
            p.put(DataScopeAspect.DATA_SCOPE, null);
        }
        Page<DeviceShortOutput> deviceShortOutputPage = deviceMapper.selectDeviceShortList(new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize()), deviceVO);

        if (0 == deviceShortOutputPage.getTotal()) {
            return deviceShortOutputPage;
        }
        List<DeviceShortOutput> list = deviceShortOutputPage.getRecords();
        List<String> serialNumberList;
        SysUser user = getLoginUser().getUser();
        if (null != user.getDept()) {
            Long tenantId = user.getDept().getDeptUserId();
            serialNumberList = list.stream().filter(d -> tenantId.equals(d.getTenantId())).map(DeviceShortOutput::getSerialNumber).collect(Collectors.toList());
        } else {
            serialNumberList = list.stream().map(DeviceShortOutput::getSerialNumber).collect(Collectors.toList());
        }
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(serialNumberList)) {
            List<DeviceAlertCount> alist = alertLogService.selectDeviceAlertCount(serialNumberList);
            for (DeviceAlertCount item : alist) {
                list.stream()
                        .filter(it -> Objects.equals(it.getSerialNumber(), item.getSerialNumber()))
                        .forEach(it -> it.setAlertCount(item));
            }
        }
        List<Long> deptUserIdList = list.stream().map(DeviceShortOutput::getTenantId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deptUserIdList)) {
            return deviceShortOutputPage;
        }
        List<SysDept> deviceDeptNameList = this.selectDeptNameByDeptUserIdList(deptUserIdList);
        Map<Long, String> deptNameMap = deviceDeptNameList.stream().collect(Collectors.toMap(SysDept::getDeptUserId, SysDept::getDeptName));
        for (DeviceShortOutput deviceShortOutput : list) {
            if (isAdmin(loginUser.getUserId())) {
                deviceShortOutput.setCanSeeCode(true);
            } else {
                if (deviceShortOutput.getTenantId().equals(loginUser.getUserId())) {
                    deviceShortOutput.setCanSeeCode(true);
                } else {
                    deviceShortOutput.setCanSeeCode(deviceShortOutput.getCreateBy().equals(loginUser.getUsername()));
                }
            }
            deviceShortOutput.setDeptName(deptNameMap.get(deviceShortOutput.getTenantId()));
        }
        return deviceShortOutputPage;
    }


    /**
     * 查询设备
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    // TODO 22--slaveId
    @Override
    public DeviceShortOutput selectDeviceRunningStatusByDeviceId(Long deviceId) {
        SysUser user = getLoginUser().getUser();
        DeviceShortOutput device;
        if (null == user.getDeptId()) {
            this.checkDeviceDataScope(deviceId);
            Device device1 = deviceMapper.selectById(deviceId);
            if (Objects.isNull(device1)) {
                return new  DeviceShortOutput();
            }
            device = DeviceConvert.INSTANCE.convertDeviceShortOutput(device1);
        } else {
            Device param = new Device();
            param.setDeviceId(deviceId);
            device = deviceMapper.selectDeviceRunningStatusByDeviceId(param);
        }
        JSONObject thingsModelObject = JSONObject.parseObject(itslCache.getCacheThingsModelByProductId(device.getProductId()));
        if (thingsModelObject == null) {
            log.warn("selectDeviceRunningStatusByDeviceId: 物模型json为空, productId={}", device.getProductId());
            device.setThingsModels(new ArrayList<>());
            return device;
        }
        JSONArray properties = thingsModelObject.getJSONArray("properties");
        JSONArray functions = thingsModelObject.getJSONArray("functions");
        List<ValueItem> thingsModelValueItems = itslValueCache.getCacheDeviceStatus(device.getProductId(), device.getSerialNumber());
        // 物模型转换赋值
        List<ThingsModelValueItem> thingsList = new ArrayList<>();
        //判断一下properties 和 functions是否为空, 否则报空指针
        if (!CollectionUtils.isEmpty(properties)) {
            thingsList.addAll(convertJsonToThingsList(properties, thingsModelValueItems, 1));
        }
        if (!CollectionUtils.isEmpty(functions)) {
            thingsList.addAll(convertJsonToThingsList(functions, thingsModelValueItems, 2));
        }
        thingsList.sort(Comparator.comparing(ThingsModelValueItem::getOrder));
        device.setThingsModels(thingsList);
        return device;

    }

    /**
     * 物模型基本类型转换赋值
     *
     * @param jsonArray
     * @param thingsModelValues
     * @param type
     * @return
     */
    @Async
    public List<ThingsModelValueItem> convertJsonToThingsList(JSONArray jsonArray, List<ValueItem> thingsModelValues, Integer type) {
        List<ThingsModelValueItem> thingsModelList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            ThingsModelValueItem thingsModel = new ThingsModelValueItem();
            JSONObject thingsJson = jsonArray.getJSONObject(i);
            JSONObject datatypeJson = thingsJson.getJSONObject("datatype");
            thingsModel.setId(thingsJson.getString("id"));
            thingsModel.setName(thingsJson.getString("name"));
            thingsModel.setIsMonitor(thingsJson.getInteger("isMonitor") == null ? 0 : thingsJson.getInteger("isMonitor"));
            thingsModel.setIsReadonly(thingsJson.getInteger("isReadonly") == null ? 0 : thingsJson.getInteger("isReadonly"));
            thingsModel.setIsChart(thingsJson.getInteger("isChart") == null ? 0 : thingsJson.getInteger("isChart"));
            thingsModel.setIsSharePerm(thingsJson.getInteger("isSharePerm") == null ? 0 : thingsJson.getInteger("isSharePerm"));
            thingsModel.setIsHistory(thingsJson.getInteger("isHistory") == null ? 0 : thingsJson.getInteger("isHistory"));
            thingsModel.setIsApp(thingsJson.getInteger("isApp") == null ? 0 : thingsJson.getInteger("isApp"));
            thingsModel.setOrder(thingsJson.getInteger("order") == null ? 0 : thingsJson.getInteger("order"));
            thingsModel.setType(type);
            thingsModel.setModelId(thingsJson.getLong("modelId"));
            // 获取value
            for (ValueItem valueItem : thingsModelValues) {
                if (valueItem.getId().equals(thingsModel.getId())) {
                    thingsModel.setValue(valueItem.getValue());
                    thingsModel.setShadow(valueItem.getShadow());
                    thingsModel.setTs(valueItem.getTs());
                    break;
                }
            }
            // json转DataType(DataType赋值)
            Datatype dataType = convertJsonToDataType(datatypeJson, thingsModelValues, type, thingsModel.getId() + "_");
            thingsModel.setDatatype(dataType);
            if (JsonUtils.isJson(thingsModel.getValue())) {
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(dataType.getEnumList())) {
                    JSONObject jsonObject = JSONObject.parseObject(thingsModel.getValue());
                    for (EnumItem enumItem : dataType.getEnumList()) {
                        ThingsModelValueItem model = new ThingsModelValueItem();
                        BeanUtils.copyProperties(thingsModel, model);
                        String val = jsonObject.getString(enumItem.getValue());
                        model.setValue(val);
                        model.setName(enumItem.getValue());
                        thingsModelList.add(model);
                    }
                }
            } else {
                // 物模型项添加到集合
                thingsModelList.add(thingsModel);
            }
        }
        return thingsModelList;
    }

    /**
     * 物模型DataType转换
     *
     * @param datatypeJson
     * @param thingsModelValues
     * @param type
     * @param parentIdentifier  上级标识符
     * @return
     */
    private Datatype convertJsonToDataType(JSONObject datatypeJson, List<ValueItem> thingsModelValues, Integer type, String parentIdentifier) {
        Datatype dataType = new Datatype();
        //有些物模型数据定义为空的情况兼容
        if (datatypeJson == null) {
            return dataType;
        }
        String realType = datatypeJson.getString("type");
        if (StringUtils.isEmpty(realType)) {
            return dataType;
        }
        dataType.setType(realType);
        dataType.setParentType(datatypeJson.getString("parentType"));
        dataType.setParentIdentifier(datatypeJson.getString("parentIdentifier"));
        dataType.setParentName(datatypeJson.getString("parentName"));
        dataType.setParentIndexName(datatypeJson.getString("parentIndexName"));
        if (dataType.getType().equals("decimal")) {
            dataType.setMax(datatypeJson.getBigDecimal("max"));
            dataType.setMin(datatypeJson.getBigDecimal("min"));
            dataType.setStep(datatypeJson.getBigDecimal("step"));
            dataType.setUnit(datatypeJson.getString("unit"));
        } else if (dataType.getType().equals("integer")) {
            dataType.setMax(datatypeJson.getBigDecimal("max"));
            dataType.setMin(datatypeJson.getBigDecimal("min"));
            dataType.setStep(datatypeJson.getBigDecimal("step"));
            dataType.setUnit(datatypeJson.getString("unit"));
        } else if (dataType.getType().equals("bool")) {
            dataType.setFalseText(datatypeJson.getString("falseText"));
            dataType.setTrueText(datatypeJson.getString("trueText"));
        } else if (dataType.getType().equals("string")) {
            dataType.setMaxLength(datatypeJson.getInteger("maxLength"));
        } else if (dataType.getType().equals("enum")) {
            List<EnumItem> enumItemList = JSON.parseArray(datatypeJson.getString("enumList"), EnumItem.class);
            dataType.setEnumList(enumItemList);
            dataType.setShowWay(datatypeJson.getString("showWay"));
        }
//        } else if (dataType.getType().equals("object")) {
//            JSONArray jsonArray = JSON.parseArray(datatypeJson.getString("params"));
//            // 物模型值过滤（parentId_开头）
//            thingsModelValues = thingsModelValues.stream().filter(x -> x.getId().startsWith(parentIdentifier)).collect(Collectors.toList());
//            List<ThingsModelValueItem> thingsList = convertJsonToThingsList(jsonArray, thingsModelValues, type);
//            // 排序
//            thingsList = thingsList.stream().sorted(Comparator.comparing(ThingsModelValueItem::getOrder).reversed()).collect(Collectors.toList());
//            dataType.setParams(thingsList);
//        } else if (dataType.getType().equals("array")) {
//            dataType.setArrayType(datatypeJson.getString("arrayType"));
//            dataType.setArrayCount(datatypeJson.getInteger("arrayCount"));
//            if ("object".equals(dataType.getArrayType())) {
//                // 对象数组
//                JSONArray jsonArray = datatypeJson.getJSONArray("params");
//                // 物模型值过滤（parentId_开头）
//                thingsModelValues = thingsModelValues.stream().filter(x -> x.getId().startsWith(parentIdentifier)).collect(Collectors.toList());
//                List<ThingsModelValueItem> thingsList = convertJsonToThingsList(jsonArray, thingsModelValues, type);
//                // 排序
//                thingsList = thingsList.stream().sorted(Comparator.comparing(ThingsModelValueItem::getOrder).reversed()).collect(Collectors.toList());
//                // 数组类型物模型里面对象赋值
//                List<ThingsModel>[] arrayParams = new List[dataType.getArrayCount()];
//                for (int i = 0; i < dataType.getArrayCount(); i++) {
//                    List<ThingsModel> thingsModels = new ArrayList<>();
//                    for (int j = 0; j < thingsList.size(); j++) {
//                        ThingsModel thingsModel = new ThingsModel();
//                        BeanUtils.copyProperties(thingsList.get(j), thingsModel);
//                        String shadow = thingsList.get(j).getShadow();
//                        if (StringUtils.isNotEmpty(shadow) && !shadow.equals("")) {
//                            String[] shadows = shadow.split(",");
//                            if (i + 1 > shadows.length) {
//                                // 解决产品取消发布，增加数组长度导致设备影子和值赋值失败
//                                thingsModel.setShadow(" ");
//                            } else {
//                                thingsModel.setShadow(shadows[i]);
//                            }
//                        }
//                        String value = thingsList.get(j).getValue();
//                        if (StringUtils.isNotEmpty(value) && !value.equals("")) {
//                            String[] values = value.split(",");
//                            if (i + 1 > values.length) {
//                                thingsModel.setValue(" ");
//                            } else {
//                                thingsModel.setValue(values[i]);
//                            }
//                        }
//                        thingsModels.add(thingsModel);
//                    }
//                    arrayParams[i] = thingsModels;
//                }
//                dataType.setArrayParams(arrayParams);
//            }
//        }
        return dataType;
    }

    /**
     * 新增设备
     *
     * @param deviceVO 设备
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceVO insertDevice(DeviceVO deviceVO) {
        // 设备编号唯一检查
        Device existDevice = deviceMapper.selectDeviceBySerialNumber(deviceVO.getSerialNumber());
        if (!Objects.isNull(existDevice)) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("device.insert.fail.device.number.already.exist"), deviceVO.getSerialNumber()));
        }
        SysUser sysUser = getLoginUser().getUser();
        //添加设备
        deviceVO.setCreateTime(DateUtils.getNowDate());
        if (sysUser.getDept() != null) {
            deviceVO.setTenantId(sysUser.getDept().getDeptUserId());
            deviceVO.setTenantName(sysUser.getDept().getDeptName());
        } else {
            deviceVO.setTenantId(sysUser.getUserId());
            deviceVO.setTenantName(sysUser.getUserName());
        }
        deviceVO.setCreateBy(sysUser.getUserName());
        deviceVO.setRssi(0);
        // 设置图片
        Product product = productMapper.selectById(deviceVO.getProductId());
        if (product == null) {
            throw new ServiceException(StringUtils.format(MessageUtils.message("product.not.found.by.product.id"), deviceVO.getProductId()));
        }
        deviceVO.setImgUrl(product.getImgUrl());
        // 随机经纬度和地址
        deviceVO.setNetworkIp(sysUser.getLoginIp());
        Device device = DeviceConvert.INSTANCE.convertDevice(deviceVO);
        // 更新用户登录ip设置设备网络地址和经纬度
        setLocation(sysUser.getLoginIp(), device);
        deviceMapper.insert(device);
        deviceVO.setDeviceId(device.getDeviceId());
        // 处理modbus产品
        if (FastBeeConstant.PROTOCOL.ModbusRtu.equals(product.getProtocolCode()) || FastBeeConstant.PROTOCOL.ModbusTcp.equals(product.getProtocolCode())
                || FastBeeConstant.PROTOCOL.ModbusToJsonHP.equals(product.getProtocolCode()) || FastBeeConstant.PROTOCOL.ModbusToJsonZQWL.equals(product.getProtocolCode())
                || FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(product.getProtocolCode()) || FastBeeConstant.PROTOCOL.JsonGateway.equals(product.getProtocolCode())) {
            this.handleModbusConfig(deviceVO, product);
        }
        // redis缓存设备默认状态（物模型值）
        itslValueCache.addCacheDeviceStatus(device.getProductId(), device.getSerialNumber());
        return deviceVO;
    }

    private void handleModbusConfig(DeviceVO deviceVO, Product product) {
        List<List<ProductSubDeviceAddVO.SubDevice>> subDeviceList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(deviceVO.getSubProductList())) {
            subDeviceList = deviceVO.getSubProductList().stream()
                    .map(ProductSubDeviceAddVO.SubProduct::getSubDeviceList)
                    .filter(org.apache.commons.collections4.CollectionUtils::isNotEmpty).collect(Collectors.toList());
        }
        if (DeviceType.SUB_GATEWAY.getCode() != deviceVO.getDeviceType()) {
            LambdaQueryWrapper<ProductModbusJob> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(ProductModbusJob::getProductId, product.getProductId());
            List<ProductModbusJob> productModbusJobList = productModbusJobMapper.selectList(queryWrapper2);
            for (ProductModbusJob productModbusJob : productModbusJobList) {
                ModbusJobVO modbusJobVO = new ModbusJobVO();
                modbusJobVO.setJobName(productModbusJob.getJobName());
                modbusJobVO.setDeviceId(deviceVO.getDeviceId());
                modbusJobVO.setSerialNumber(deviceVO.getSerialNumber());
                modbusJobVO.setCommand(productModbusJob.getCommand());
                modbusJobVO.setStatus(0);
                modbusJobVO.setAddress(productModbusJob.getAddress());
                modbusJobVO.setProductId(deviceVO.getProductId());
                modbusJobVO.setRemark(productModbusJob.getRemark());
                modbusJobVO.setCreateBy(deviceVO.getCreateBy());
                modbusJobVO.setCommandType(productModbusJob.getCommandType());
                modbusJobService.insertModbusJob(modbusJobVO);
                if (FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(product.getProtocolCode())) {
                    redisCache.getCacheModbusTcpId(deviceVO.getSerialNumber());
                }
            }
        }
        if (DeviceType.GATEWAY.getCode() == deviceVO.getDeviceType()) {
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(subDeviceList)) {
                LambdaQueryWrapper<ProductSubGateway> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ProductSubGateway::getGwProductId, product.getProductId());
                // 处理子设备添加和子设备轮询
                ProductSubGateway productSubGateway = new ProductSubGateway();
                productSubGateway.setGwProductId(product.getProductId());
                List<ProductSubGatewayVO> productSubGatewayList = productSubGatewayService.selectProductSubGatewayList(productSubGateway).getRecords();
                if (CollectionUtils.isEmpty(productSubGatewayList)) {
                    return;
                }
                productSubGatewayList.sort(Comparator.comparing(ProductSubGatewayVO::getAddress, Comparator.nullsLast(Comparator.naturalOrder())));
                List<Long> subProductIdList = productSubGatewayList.stream().map(ProductSubGatewayVO::getSubProductId).collect(Collectors.toList());
                LambdaQueryWrapper<ProductModbusJob> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.in(ProductModbusJob::getProductId, subProductIdList);
                // 新增子设备、绑定子设备、子设备生成轮询
                for (ProductSubGatewayVO productSubGatewayVO : productSubGatewayList) {
                    Device addDevice = new Device();
                    addDevice.setDeviceName(productSubGatewayVO.getSubProductName() + "_" + productSubGatewayVO.getAddress());
                    addDevice.setProductId(productSubGatewayVO.getSubProductId());
                    addDevice.setProductName(productSubGatewayVO.getSubProductName());
                    String deviceIp = null;
                    if (FastBeeConstant.PROTOCOL.ModbusTcp.equals(product.getProtocolCode())) {
                        deviceIp = deviceVO.getDeviceIp() + "#" + productSubGatewayVO.getAddress();
                    }
                    addDevice.setDeviceIp(deviceIp);
                    addDevice.setSerialNumber(this.generationDeviceNum(1));
                    addDevice.setTenantId(deviceVO.getTenantId());
                    addDevice.setTenantName(deviceVO.getTenantName());
                    addDevice.setCreateBy(deviceVO.getCreateBy());
                    addDevice.setFirmwareVersion(new BigDecimal(1));
                    addDevice.setRssi(0);
                    addDevice.setIsShadow(0);
                    addDevice.setLocationWay(productSubGatewayVO.getSubLocationWay());
                    addDevice.setImgUrl(product.getImgUrl());
                    addDevice.setCreateTime(new Date());
                    deviceMapper.insert(addDevice);
                    SubGateway subGateway = new SubGateway();
                    subGateway.setParentClientId(deviceVO.getSerialNumber());
                    subGateway.setParentProductId(deviceVO.getProductId());
                    subGateway.setSubClientId(addDevice.getSerialNumber());
                    subGateway.setSubProductId(addDevice.getProductId());
                    subGateway.setAddress(productSubGatewayVO.getAddress());
                    subGateway.setCreateBy(addDevice.getCreateBy());
                    subGateway.setCreateTime(new Date());
                    subGatewayMapper.insert(subGateway);
                }
            } else {
                ProductSubDeviceAddVO productSubDeviceAddVO = new ProductSubDeviceAddVO();
                productSubDeviceAddVO.setParentProductId(deviceVO.getProductId());
                productSubDeviceAddVO.setParentClientId(deviceVO.getSerialNumber());
                productSubDeviceAddVO.setSubProductList(deviceVO.getSubProductList());
                this.handleSubProductDeviceAddBatch(productSubDeviceAddVO);
            }
        }
    }

    /**
     * 终端-设备关联用户
     *
     * @param params
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deviceRelateUser(DeviceRelateUserInput params) {
        Long userId = params.getUserId(); //终端用户id
        // 查询用户信息
        SysUser sysUser = userService.selectUserById(userId);
        for (DeviceNumberAndProductId item : params.getDeviceNumberAndProductIds()) {
            Device existDevice = deviceMapper.selectDeviceBySerialNumber(item.getDeviceNumber());
            //查看设备是否已经分配
            if (!Objects.isNull(existDevice)) {
                if (null != sysUser.getDeptId()) {
                    return AjaxResult.error(StringUtils.format(MessageUtils.message("device.tenant.can.not.bind.exist.device"), item.getDeviceNumber()));
                }
                DeviceUserVO deviceUserVO = deviceUserMapper.selectDeviceUserByDeviceId(existDevice.getDeviceId());
                if (!Objects.isNull(deviceUserVO)) {
                    if (deviceUserVO.getUserId().equals(userId)) {
                        return AjaxResult.error(StringUtils.format(MessageUtils.message("now.user.belong.device.can.not.repeat.share"), item.getDeviceNumber()));
                    } else {
                        return AjaxResult.error(StringUtils.format(MessageUtils.message("device.share.other.user.can.not.share"), item.getDeviceNumber()));
                    }
                }
                // 先删除设备的所有用户
                //deviceUserMapper.deleteDeviceUserByDeviceId(new UserIdDeviceIdModel(null, existDevice.getDeviceId()));
                // 添加新的设备用户
                DeviceUser deviceUser = new DeviceUser();
                deviceUser.setUserId(sysUser.getUserId());
                deviceUser.setPhonenumber(sysUser.getPhonenumber());
                deviceUser.setDeviceId(existDevice.getDeviceId());
                deviceUser.setCreateTime(DateUtils.getNowDate());
                deviceUserMapper.insert(deviceUser);
            } else {
                // 自动添加设备
                int result = insertDeviceAuto(
                        item.getDeviceNumber(),
                        userId,
                        item.getProductId(), 1);
                if (result == 0) {
                    return AjaxResult.error(MessageUtils.message("device.not.exist.add.fail.please.check.product.id.is.correct"));
                }
            }
        }
        return AjaxResult.success(MessageUtils.message("device.add.success"));
    }

    /**
     * 设备认证后自动添加设备
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDeviceAuto(String serialNumber, Long userId, Long productId, Integer status) {
        // 设备编号唯一检查
        int count = deviceMapper.selectDeviceCountBySerialNumber(serialNumber);
        if (count != 0) {
            log.error("设备编号：" + serialNumber + "已经存在了，新增设备失败");
            return 0;
        }
        Product product = productMapper.selectById(productId);
        if (product == null) {
            log.error("自动添加设备时，根据产品ID查找不到对应产品");
            return 0;
        }
        Device device = new Device();
        device.setSerialNumber(serialNumber);
        SysUser user = userService.selectUserById(userId);
        if (null == user || null == user.getDept()) {
            log.error("自动添加设备时，根据用户id查找不到用户或用户不是租户");
            return 0;
        }
        device.setTenantId(user.getDept().getDeptUserId());
        device.setTenantName(user.getDept().getDeptUserName());
        device.setCreateBy(user.getUserName());
        device.setFirmwareVersion(BigDecimal.valueOf(1.0));
        // 设备状态（1-未激活，2-禁用，3-在线，4-离线）
        device.setStatus(status);
        device.setActiveTime(DateUtils.getNowDate());
        device.setIsShadow(0);
        device.setRssi(0);
        // 1-自动定位，2-设备定位，3-自定义位置
        device.setLocationWay(product.getLocationWay());
        device.setCreateTime(DateUtils.getNowDate());
        // 随机位置
        device.setLongitude(BigDecimal.valueOf(116.23 - (Math.random() * 15)));
        device.setLatitude(BigDecimal.valueOf(39.54 - (Math.random() * 15)));
        device.setNetworkAddress("中国");
        device.setNetworkIp("127.0.0.1");

        int random = (int) (Math.random() * (90)) + 10;
        device.setDeviceName(product.getProductName() + random);
        device.setImgUrl(product.getImgUrl());
        device.setProductId(product.getProductId());
        device.setProductName(product.getProductName());
        int result = deviceMapper.insert(device);

        // 缓存设备状态
        itslValueCache.addCacheDeviceStatus(device.getProductId(), device.getSerialNumber());
        return result;
    }

    /**
     * 获取用户操作设备的影子值
     *
     * @param device
     * @return
     */
    @Override
    public ThingsModelShadow getDeviceShadowThingsModel(Device device) {
        // 物模型值
        List<ValueItem> thingsModelValueItems = itslValueCache.getCacheDeviceStatus(device.getProductId(), device.getSerialNumber());
        ThingsModelShadow shadow = new ThingsModelShadow();
        // 查询出设置的影子值
        List<ThingsModelValueItem> shadowList = new ArrayList<>();
        for (ValueItem item : thingsModelValueItems) {
            String shadowVal = item.getShadow();
            String valueVal = item.getValue();
            if (shadowVal == null || valueVal == null) {
                continue;
            }
            if (!shadowVal.equals(valueVal) && !shadowVal.isEmpty()) {
                //处理id
                String id = item.getId();
//                if (item.getShadow().contains(",")) {
//                    String[] shadows = item.getShadow().split(",");
//                    String[] values = item.getValue().split(",");
//                    for (int i = 0; i < shadows.length; i++) {
//                        String value = values[i];
//                        String shaVal = shadows[i];
//                        if (!shaVal.equals(value)) {
//                            id = "array_0" + i + "_" + id;
//                            ThingsModelSimpleItem shaowItem = new ThingsModelSimpleItem(id, shaVal, "");
//                            shadow.getProperties().add(shaowItem);
//                        }
//                    }
//                } else {
//                    ThingsModelSimpleItem shaowItem = new ThingsModelSimpleItem(id, item.getShadow(), "");
//                    shadow.getProperties().add(shaowItem);
//                }
                ThingsModelSimpleItem shaowItem = new ThingsModelSimpleItem(id, item.getShadow(), "");
                shadow.getProperties().add(shaowItem);
                String cacheKey = RedisKeyBuilder.buildTSLVCacheKey(device.getProductId(), device.getSerialNumber());
                item.setShadow(item.getValue());
                redisCache.setCacheMapValue(cacheKey, item.getId(), JSON.toJSONString(item));
            }
        }
        return shadow;
    }


    /**
     * 生成设备唯一编号
     *
     * @return 结果
     */
    @Override
    public String generationDeviceNum(Integer type) {
        // 设备编号：D + userId + 15位随机字母和数字
        SysUser user = getLoginUser().getUser();
        String number;
        //Hex随机字符串
        if (type == 3) {
            number = toolService.generateRandomHex(12);
        } else {
            number = "D" + user.getUserId().toString() + toolService.getStringRandom(10);
        }
        int count = deviceMapper.getDeviceNumCount(number);
        if (count == 0) {
            return number;
        } else {
            generationDeviceNum(type);
        }
        return "";
    }

    /**
     * 上报设备信息
     *
     * @param deviceVO     上报的设备信息
     * @param deviceEntity 查询的设备实体
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reportDevice(DeviceVO deviceVO, Device deviceEntity) {
        // 未采用设备定位则清空定位，定位方式(1=ip自动定位，2=设备定位，3=自定义)
        if (deviceEntity.getLocationWay() != 2) {
            deviceVO.setLatitude(null);
            deviceVO.setLongitude(null);
        }
        int result = 0;
        // 设备端默认可设置UserID=1，需要排除
        if (deviceEntity != null && deviceVO.getUserId() != null && deviceVO.getUserId() != 1) {
            // 通过配网或者扫码关联设备后，设备的用户信息需要变更
            if (deviceEntity.getTenantId().longValue() != deviceVO.getUserId().longValue()) {
                // 先删除设备的所有用户
                LambdaQueryWrapper<DeviceUser> deviceUserWrapper = new LambdaQueryWrapper<>();
                deviceUserWrapper.eq(DeviceUser::getDeviceId, deviceEntity.getDeviceId());
                deviceUserMapper.delete(deviceUserWrapper);
                // 添加新的设备用户
                SysUser sysUser = userService.selectUserById(deviceVO.getTenantId());
                if (sysUser != null) {
                    DeviceUser deviceUser = new DeviceUser();
                    deviceUser.setUserId(sysUser.getUserId());
                    deviceUser.setPhonenumber(sysUser.getPhonenumber());
                    deviceUser.setDeviceId(deviceEntity.getDeviceId());
                    deviceUser.setCreateTime(DateUtils.getNowDate());
                    deviceUserMapper.insert(deviceUser);
                }
                // 更新设备用户信息 多租户版本不需要
//                device.setTenantId(device.getTenantId());
//                device.setTenantName(sysUser.getUserName());
            }
            deviceVO.setUpdateTime(DateUtils.getNowDate());
            if (deviceEntity.getActiveTime() == null || deviceEntity.getActiveTime().equals("")) {
                deviceVO.setActiveTime(DateUtils.getNowDate());
            }

        }
        // 不更新物模型
        deviceVO.setThingsModelValue(null);
        Device device = DeviceConvert.INSTANCE.convertDevice(deviceVO);
        result = deviceUpdateService.updateDeviceBySerialNumber(device);
        // 绑定sim卡
        if (StringUtils.isNotEmpty(deviceVO.getIccid())) {
            try {
                cardService.deviceReportIccId(deviceEntity, deviceVO.getIccid(), deviceVO.getCardPlatformId());
            } catch (Exception e) {
                log.error("设备上报sim卡绑定失败，iccid：{}，error：{}", deviceVO.getIccid(), e.getMessage());
            }
        }
        return result;
    }

    /**
     * 获取设备MQTT连接参数
     *
     * @param deviceId 设备id
     * @return
     */
    @Override
    public DeviceMqttConnectVO getMqttConnectData(Long deviceId) {
        DeviceMqttConnectVO connectVO = new DeviceMqttConnectVO();
        DeviceMqttVO deviceMqttVO = deviceMapper.selectMqttConnectData(deviceId);
        if (deviceMqttVO == null) {
            throw new ServiceException(MessageUtils.message("device.get.mqtt.connection.param.fail"));
        }
        if (FastBeeConstant.TRANSPORT.TCP.equals(deviceMqttVO.getTransport())) {
            connectVO.setPort(tcpPort);
            connectVO.setEnrollPackage("7e80" + deviceMqttVO.getSerialNumber() + "7e");
            return connectVO;
        }
        // 不管认证方式，目前就只返回简单认证方式
        String password;
        if (ProductAuthConstant.AUTHORIZE.equals(deviceMqttVO.getIsAuthorize())) {
            // 查询产品授权码
            List<ProductAuthorize> productAuthorizeList = productAuthorizeService.listByProductId(deviceMqttVO.getProductId());
            if (CollectionUtils.isEmpty(productAuthorizeList)) {
                throw new ServiceException(MessageUtils.message("device.get.authorization.fail.please.config"));
            }
            List<ProductAuthorize> collect = productAuthorizeList.stream()
                    .filter(p -> Objects.equals(p.getDeviceId(), deviceMqttVO.getDeviceId()))
                    .collect(Collectors.toList());
            ProductAuthorize productAuthorize = CollectionUtils.isEmpty(collect) ? productAuthorizeList.get(0) : collect.get(0);
            password = deviceMqttVO.getAuthPassword() + "&" + productAuthorize.getAuthorizeCode();
        } else {
            password = deviceMqttVO.getAuthPassword();
        }
        if (FastBeeConstant.TRANSPORT.COAP.equals(deviceMqttVO.getTransport())) {
            String clientId = ProductAuthConstant.CLIENT_ID_AUTH_TYPE_SIMPLE + "-" + deviceMqttVO.getSerialNumber() + "-" + deviceMqttVO.getProductId() + "-" + deviceMqttVO.getTenantId();
            connectVO.setClientId(clientId).setUsername(deviceMqttVO.getAccount()).setPasswd(password).setPort(coapPort);
            connectVO.setSubscribeTopic("/function/get");
            connectVO.setReportTopic("/property/post");
        } else {
            String clientId = ProductAuthConstant.CLIENT_ID_AUTH_TYPE_SIMPLE + "&" + deviceMqttVO.getSerialNumber() + "&" + deviceMqttVO.getProductId() + "&" + deviceMqttVO.getTenantId();
            // 组装返回结果
            connectVO.setClientId(clientId).setUsername(deviceMqttVO.getAccount()).setPasswd(password).setPort(brokerPort);
            connectVO.setSubscribeTopic("/" + deviceMqttVO.getProductId() + "/" + deviceMqttVO.getSerialNumber() + "/function/get");
            connectVO.setReportTopic("/" + deviceMqttVO.getProductId() + "/" + deviceMqttVO.getSerialNumber() + "/property/post");
        }
        return connectVO;
    }

    public DeviceHttpAuthVO getHttpAuthData(Long deviceId) {
        DeviceHttpAuthVO httpAuthVO = new DeviceHttpAuthVO();
        DeviceMqttVO deviceMqttVO = deviceMapper.selectMqttConnectData(deviceId);
        if (deviceMqttVO == null) {
            throw new ServiceException(MessageUtils.message("device.get.mqtt.connection.param.fail"));
        }
        // 不管认证方式，目前就只返回简单认证方式
        String password;
        if (ProductAuthConstant.AUTHORIZE.equals(deviceMqttVO.getIsAuthorize()) && this.httpAuthConfig.getType().equals("Basic")) {
            // 查询产品授权码
            List<ProductAuthorize> productAuthorizeList = productAuthorizeService.listByProductId(deviceMqttVO.getProductId());
            if (CollectionUtils.isEmpty(productAuthorizeList)) {
                throw new ServiceException(MessageUtils.message("device.get.authorization.fail.please.config"));
            }
            List<ProductAuthorize> collect = productAuthorizeList.stream().filter(p -> p.getProductId().equals(deviceMqttVO.getDeviceId())).collect(Collectors.toList());
            ProductAuthorize productAuthorize = CollectionUtils.isEmpty(collect) ? productAuthorizeList.get(0) : collect.get(0);
            password = deviceMqttVO.getAuthPassword() + "&" + productAuthorize.getAuthorizeCode();
        } else {
            password = deviceMqttVO.getAuthPassword();
        }
        httpAuthVO.setType(this.httpAuthConfig.getType());
        String clientId = ProductAuthConstant.CLIENT_ID_AUTH_TYPE_SIMPLE + "-" + deviceMqttVO.getSerialNumber() + "-" + deviceMqttVO.getProductId() + "-" + deviceMqttVO.getTenantId();
        httpAuthVO.setUsername(deviceMqttVO.getAccount()).setPassword(password);
        httpAuthVO.setClientId(clientId);
        httpAuthVO.setPort(httpPort);
        return httpAuthVO;
    }

    /**
     * 根据IP获取地址
     *
     * @param ip
     * @return
     */
    private void setLocation(String ip, Device device) {
        String address = "未知地址";
        if (StringUtils.isEmpty(ip)) {
            device.setNetworkAddress(address);
            return;
        }
        if (IpUtils.internalIp(ip)) {
            address = "内网IP";
        } else {
            if (StringUtils.isEmpty(AddressUtils.apiConfig.getIpplus360Key())) {
                log.warn("api.ipplus360Key值未配置，使用免费接口!");
                JSONObject obj = AddressUtils.getResBywWhois(ip);
                if (obj != null) {
                    setLatitudeAndLongitude(obj.getString("city"), device);
                }
            } else {
                JSONObject obj = AddressUtils.getResByIpplus360(ip);
                if (obj != null && Objects.equals(obj.getString("code"), "Success")) {
                    JSONObject data = obj.getJSONObject("data");
                    if (data != null) {
                        String region = data.getString("prov");
                        String city = data.getString("city");
                        address = String.format("%s %s", region, city);
                        String latStr = data.getString("lat");
                        String lngStr = data.getString("lng");
                        if (latStr != null) device.setLatitude(new BigDecimal(latStr));
                        if (lngStr != null) device.setLongitude(new BigDecimal(lngStr));
                    }
                }
            }
        }
        device.setNetworkAddress(address);
    }

    /**
     * 设置经纬度
     *
     * @param city
     */
    private void setLatitudeAndLongitude(String city, Device device) {
        String BAIDU_URL = "https://api.map.baidu.com/geocoder";
        String baiduResponse = HttpUtils.sendGet(BAIDU_URL, "address=" + city + "&output=json", Constants.GBK);
        if (!StringUtils.isEmpty(baiduResponse)) {
            JSONObject baiduObject = JSONObject.parseObject(baiduResponse);
            if (baiduObject == null) {
                return;
            }
            JSONObject result = baiduObject.getJSONObject("result");
            if (result == null) {
                return;
            }
            JSONObject location = result.getJSONObject("location");
            if (location == null) {
                return;
            }
            device.setLongitude(location.getBigDecimal("lng"));
            device.setLatitude(location.getBigDecimal("lat"));
        }
    }


    /**
     * 根据产品ID获取产品下所有编号
     *
     * @param productId
     * @return
     */
    @Override
    public String[] getDeviceNumsByProductId(Long productId) {
        return deviceMapper.getDeviceNumsByProductId(productId);
    }


    /**
     * 重置设备状态
     *
     * @return 结果
     */
    @Override
    public int resetDeviceStatus(String deviceNum) {
        // redis中删除设备协议缓存信息
        deviceCache.deleteDeviceProtocolDetailCache(deviceNum);
        return deviceMapper.resetDeviceStatus(deviceNum);
    }

    @Override
    public String importDevice(List<DeviceImportVO> deviceImportVOList, Long productId) {
        LoginUser loginUser = getLoginUser();
        SysUser deptUser = userService.getDeptUserByUserId(loginUser.getUserId());
        Product product = productMapper.selectById(productId);
        if (null == product) {
            return MessageUtils.message("device.import.assignment.fail.product.information.is.empty");
        }
        boolean isCamera = DeviceType.CAMERA.getCode().equals(product.getDeviceType()) || "GB28181".equals(product.getTransport());
        List<String> serialNumberList = deviceImportVOList.stream().map(DeviceImportVO::getSerialNumber).collect(Collectors.toList());
        List<String> oldSerialNumberList = deviceMapper.checkExistBySerialNumbers(serialNumberList);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(oldSerialNumberList)) {
            return StringUtils.format(MessageUtils.message("device.import.assignment.fail.serialNumber.already.exists"), String.join(",", oldSerialNumberList));
        }
        List<Device> deviceList = new ArrayList<>();
        for (DeviceImportVO deviceImportVO : deviceImportVOList) {
            Device device = new Device();
            device.setDeviceName(deviceImportVO.getDeviceName());

            // 对于监控设备，需要验证国标协议设备编号格式
            if (isCamera) {
                // 国标设备编号验证逻辑
                if (!isValidGbDeviceNumber(deviceImportVO.getSerialNumber())) {
                    return StringUtils.format(MessageUtils.message("device.import.serialNumber.not.comply.national.standard.protocol"), deviceImportVO.getSerialNumber());
                }
                device.setSerialNumber(deviceImportVO.getSerialNumber());
            } else {
                // 普通设备编号处理
                if (StringUtils.isEmpty(deviceImportVO.getSerialNumber())) {
                    device.setSerialNumber(this.generationDeviceNum(1));
                } else {
                    device.setSerialNumber(deviceImportVO.getSerialNumber());
                }
            }
            device.setProductId(product.getProductId());
            device.setProductName(product.getProductName());
            device.setLocationWay(product.getLocationWay());
            device.setTenantId(deptUser.getUserId());
            device.setTenantName(deptUser.getUserName());
            device.setCreateBy(loginUser.getUser().getUserName());
            device.setFirmwareVersion(new BigDecimal(1));
            device.setRssi(0);
            device.setIsShadow(0);
            device.setImgUrl(product.getImgUrl());
            device.setCreateTime(new Date());
            deviceList.add(device);
        }
        boolean result = deviceMapper.insertBatch(deviceList);
        // 新增导入记录
        DeviceRecord deviceRecord = new DeviceRecord();
        deviceRecord.setOperateDeptId(loginUser.getDeptId()).setProductId(productId).setType(DeviceRecordTypeEnum.IMPORT.getType())
                .setTotal(deviceList.size()).setTenantId(loginUser.getUser().getDept().getDeptUserId()).setTenantName(loginUser.getUser().getDept().getDeptUserName());
        if (result) {
            deviceRecord.setStatus(StatusEnum.SUCCESS.getStatus()).setSuccessQuantity(deviceList.size()).setFailQuantity(0);
        } else {
            deviceRecord.setStatus(StatusEnum.FAIL.getStatus()).setSuccessQuantity(0).setFailQuantity(deviceList.size());
        }
        deviceRecord.setCreateBy(loginUser.getUsername());
        deviceRecordMapper.insert(deviceRecord);
        return result ? "" : MessageUtils.message("import.fail");
    }

    /**
     * 验证国标设备编号格式
     * 国标设备编号通常为20位数字，格式为：行政区划(6位)+行业代码(2位)+类型代码(2位)+网络标识(3位)+设备序号(7位)
     * @param deviceNumber 设备编号
     * @return 是否符合格式
     */
    private boolean isValidGbDeviceNumber(String deviceNumber) {
        if (StringUtils.isEmpty(deviceNumber)) {
            return false;
        }

        // 国标设备编号应为20位数字
        if (deviceNumber.length() != 20) {
            return false;
        }

        // 检查是否全为数字
        return deviceNumber.matches("\\d{20}");
    }

    @Override
    public AjaxResult importAssignmentDevice(List<DeviceAssignmentVO> deviceAssignmentVOS, Long productId, Long deptId) {
        LoginUser loginUser = getLoginUser();
        Product product = productMapper.selectById(productId);
        if (null == product) {
            return AjaxResult.error(MessageUtils.message("device.import.assignment.fail.product.information.is.empty"));
        }
        boolean isCamera = DeviceType.CAMERA.getCode().equals(product.getDeviceType()) || "GB28181".equals(product.getTransport());
        List<String> serialNumberList = deviceAssignmentVOS.stream().map(DeviceAssignmentVO::getSerialNumber).collect(Collectors.toList());
        List<String> oldSerialNumberList = deviceMapper.checkExistBySerialNumbers(serialNumberList);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(oldSerialNumberList)) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("device.import.assignment.fail.serialNumber.already.exists"), String.join(",", oldSerialNumberList)));
        }
        SysDept sysDept = sysDeptMapper.selectDeptById(deptId);
        if (null == sysDept) {
            return AjaxResult.error(MessageUtils.message("device.import.assignment.fail.dept.not.exists"));
        }
        SysUser sysUser = userService.selectUserById(sysDept.getDeptUserId());
        if (null == sysUser) {
            return AjaxResult.error(MessageUtils.message("device.import.assignment.fail.dept.admin.not.exists"));
        }
        List<Device> deviceList = new ArrayList<>();
        for (DeviceAssignmentVO deviceAssignmentVO : deviceAssignmentVOS) {
            Device device = new Device();
            device.setDeviceName(deviceAssignmentVO.getDeviceName());
            // 对于监控设备，需要验证国标协议设备编号格式
            if (isCamera) {
                // 国标设备编号验证逻辑
                if (!isValidGbDeviceNumber(deviceAssignmentVO.getSerialNumber())) {
                    return AjaxResult.error(StringUtils.format(MessageUtils.message("device.import.serialNumber.not.comply.national.standard.protocol"), deviceAssignmentVO.getSerialNumber()));
                }
                device.setSerialNumber(deviceAssignmentVO.getSerialNumber());
            } else {
                // 普通设备编号处理
                if (StringUtils.isEmpty(deviceAssignmentVO.getSerialNumber())) {
                    device.setSerialNumber(this.generationDeviceNum(1));
                } else {
                    device.setSerialNumber(deviceAssignmentVO.getSerialNumber());
                }
            }
            device.setProductId(product.getProductId());
            device.setProductName(product.getProductName());
            device.setLocationWay(product.getLocationWay());
            device.setCreateBy(sysUser.getUserName());
            device.setTenantId(sysUser.getUserId());
            device.setTenantName(sysUser.getUserName());
            device.setFirmwareVersion(new BigDecimal(1));
            device.setRssi(0);
            device.setIsShadow(0);
            device.setImgUrl(product.getImgUrl());
            device.setCreateTime(new Date());
            deviceList.add(device);
        }
        boolean result = deviceMapper.insertBatch(deviceList);
        // 新增导入记录
        DeviceRecord deviceRecord = new DeviceRecord();
        deviceRecord.setOperateDeptId(loginUser.getDeptId()).setTargetDeptId(deptId).setTargetDeptId(deptId).setProductId(productId).setDistributeType(DeviceDistributeTypeEnum.IMPORT.getType())
                .setType(DeviceRecordTypeEnum.ASSIGNMENT.getType()).setTotal(deviceList.size()).setTenantId(loginUser.getUser().getDept().getDeptUserId()).setTenantName(loginUser.getUser().getDept().getDeptUserName());
        List<DeviceRecord> deviceRecordList = new ArrayList<>();
        if (result) {
            deviceRecord.setStatus(StatusEnum.SUCCESS.getStatus());
            deviceRecord.setSuccessQuantity(deviceList.size());
            deviceRecord.setFailQuantity(0);
        } else {
            deviceRecord.setStatus(StatusEnum.FAIL.getStatus());
            deviceRecord.setSuccessQuantity(0);
            deviceRecord.setFailQuantity(deviceList.size());
        }
        deviceRecord.setCreateBy(loginUser.getUsername());
        int insertRecordStatus = deviceRecordMapper.insert(deviceRecord);
        for (Device device : deviceList) {
            DeviceRecord deviceRecord1 = new DeviceRecord();
            deviceRecord1.setOperateDeptId(loginUser.getDeptId()).setTargetDeptId(deptId).setProductId(productId).setDeviceId(device.getDeviceId())
                    .setType(DeviceRecordTypeEnum.ASSIGNMENT_DETAIL.getType()).setParentId(deviceRecord.getId()).setSerialNumber(device.getSerialNumber())
                    .setDistributeType(DeviceDistributeTypeEnum.IMPORT.getType()).setTenantId(loginUser.getUser().getDept().getDeptUserId()).setTenantName(loginUser.getUser().getDept().getDeptUserName());
            deviceRecord1.setStatus(insertRecordStatus > 0 ? StatusEnum.SUCCESS.getStatus() : StatusEnum.FAIL.getStatus());
            deviceRecord1.setCreateBy(loginUser.getUsername());
            deviceRecordList.add(deviceRecord1);
        }
        deviceRecordMapper.insertBatch(deviceRecordList);
        return result ? AjaxResult.success(MessageUtils.message("import.success")) : AjaxResult.error(MessageUtils.message("import.fail"));
    }

    /**
     * 获取所有已经激活并不是禁用的设备
     *
     * @return
     */
    @Override
    public List<DeviceStatusVO> selectDeviceActive() {
        return deviceMapper.selectDeviceActive();
    }

    @Override
    public List<DeviceStatusVO> selectModbusTcpDevice() {
        return deviceMapper.selectModbusTcpDevice();
    }

    @Override
    public AjaxResult assignment(Long deptId, String deviceIds) {
        SysUser user = getLoginUser().getUser();
        SysDept sysDept = sysDeptMapper.selectDeptById(deptId);
        if (null == sysDept || null == sysDept.getDeptUserId()) {
            return AjaxResult.error(MessageUtils.message("device.assignment.fail.dept.not.exist"));
        }
        Long deptUserId = sysDept.getDeptUserId();
        SysUser sysUser = userService.selectUserById(deptUserId);
        if (null == sysUser) {
            return AjaxResult.error(MessageUtils.message("device.assignment.fail.dept.admin.not.exist"));
        }
        List<Long> deviceIdList = Arrays.stream(deviceIds.split(",")).map(d -> Long.parseLong(d.trim())).distinct().collect(Collectors.toList());
        int result = deviceMapper.updateTenantIdByDeptIds(deptUserId, sysDept.getDeptName(), sysUser.getUserName(), deviceIdList);
        DeviceRecord deviceRecord = new DeviceRecord();
        deviceRecord.setOperateDeptId(user.getDeptId()).setTargetDeptId(deptId).setTargetDeptId(deptId).setDistributeType(DeviceDistributeTypeEnum.SELECT.getType())
                .setType(DeviceRecordTypeEnum.ASSIGNMENT.getType()).setTotal(deviceIdList.size()).setTenantId(user.getDept().getDeptUserId()).setTenantName(user.getDept().getDeptUserName());
        if (result > 0) {
            deviceRecord.setStatus(StatusEnum.SUCCESS.getStatus());
            deviceRecord.setSuccessQuantity(deviceIdList.size());
            deviceRecord.setFailQuantity(0);
        } else {
            deviceRecord.setStatus(StatusEnum.FAIL.getStatus());
            deviceRecord.setSuccessQuantity(0);
            deviceRecord.setFailQuantity(deviceIdList.size());
        }
        deviceRecord.setCreateBy(user.getUserName());
        int insertRecordStatus = deviceRecordMapper.insert(deviceRecord);
        List<DeviceRecord> deviceRecordList = new ArrayList<>();
        List<Device> deviceList = deviceMapper.selectDeviceListByDeviceIds(deviceIdList);
        for (Device device : deviceList) {
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
            DeviceRecord deviceRecord1 = new DeviceRecord();
            deviceRecord1.setOperateDeptId(user.getDeptId()).setTargetDeptId(deptId).setDeviceId(device.getDeviceId())
                    .setProductId(device.getProductId()).setSerialNumber(device.getSerialNumber())
                    .setType(DeviceRecordTypeEnum.ASSIGNMENT_DETAIL.getType()).setParentId(deviceRecord.getId())
                    .setDistributeType(DeviceDistributeTypeEnum.SELECT.getType()).setTenantId(user.getDept().getDeptUserId()).setTenantName(user.getDept().getDeptUserName());
            deviceRecord1.setStatus(insertRecordStatus > 0 ? StatusEnum.SUCCESS.getStatus() : StatusEnum.FAIL.getStatus());
            deviceRecord1.setCreateBy(user.getUserName());
            deviceRecordList.add(deviceRecord1);
            DeviceStatusVO statusVO = deviceMapper.selectDeviceStatusAndTransportStatus(device.getSerialNumber());
            if (statusVO != null && statusVO.getDeviceType() == DeviceType.CAMERA.getCode()) {
                deviceMapper.updateChannelTenantId(sysDept.getDeptUserId(), device.getSerialNumber());
            }
        }
        deviceRecordMapper.insertBatch(deviceRecordList);
        return result > 0 ? AjaxResult.success(MessageUtils.message("device.assignment.success")) : AjaxResult.error(MessageUtils.message("device.assignment.fail"));
    }

    @Override
    public AjaxResult recovery(Map<Long, Long> deviceMap) {
        LoginUser loginUser = getLoginUser();
        Long deptId = loginUser.getDeptId();
        SysDept sysDept = sysDeptMapper.selectDeptById(deptId);
        if (null == sysDept || null == sysDept.getDeptUserId()) {
            return AjaxResult.error(MessageUtils.message("device.recovery.fail.dept.not.exist"));
        }
        Long deptUserId = sysDept.getDeptUserId();
        SysUser sysUser = userService.selectUserById(deptUserId);
        if (null == sysUser) {
            return AjaxResult.error(MessageUtils.message("device.recovery.fail.dept.admin.not.exist"));
        }
        List<Long> deviceIdList = new ArrayList<>(deviceMap.keySet());
        int result = deviceMapper.updateTenantIdByDeptIds(deptUserId, sysDept.getDeptName(), sysUser.getUserName(), deviceIdList);
        List<DeviceRecord> deviceRecordList = new ArrayList<>();
        List<Device> deviceList = deviceMapper.selectDeviceListByDeviceIds(deviceIdList);
        for (Device device : deviceList) {
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(device.getSerialNumber());
            Long recoveryDeptId = deviceMap.get(device.getDeviceId());
            DeviceRecord deviceRecord1 = new DeviceRecord();
            deviceRecord1.setOperateDeptId(deptId).setTargetDeptId(recoveryDeptId).setDeviceId(device.getDeviceId())
                    .setProductId(device.getProductId()).setSerialNumber(device.getSerialNumber())
                    .setType(DeviceRecordTypeEnum.RECOVERY.getType()).setTenantId(loginUser.getUser().getDept().getDeptUserId()).setTenantName(loginUser.getUser().getDept().getDeptUserName());
            deviceRecord1.setStatus(result > 0 ? StatusEnum.SUCCESS.getStatus() : StatusEnum.FAIL.getStatus());
            deviceRecord1.setCreateBy(loginUser.getUsername());
            deviceRecordList.add(deviceRecord1);
            DeviceStatusVO statusVO = deviceMapper.selectDeviceStatusAndTransportStatus(device.getSerialNumber());
            if (statusVO != null && statusVO.getDeviceType() == DeviceType.CAMERA.getCode()) {
                deviceMapper.updateChannelTenantId(sysDept.getDeptUserId(), device.getSerialNumber());
            }
        }
        deviceRecordMapper.insertBatch(deviceRecordList);
        return result > 0 ? AjaxResult.success(MessageUtils.message("device.recovery.success")) : AjaxResult.error(MessageUtils.message("device.recovery.fail"));
    }

    @Override
    public Page<DeviceShortOutput> listTerminalUser(DeviceVO deviceVO) {
        return deviceMapper.listTerminalUser(new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize()), deviceVO);
    }

    @Override
    public List<DeviceVO> listTerminalUserByGroup(DeviceVO deviceVO) {
        return deviceMapper.listTerminalUserByGroup(deviceVO);
    }

    @Override
    public List<ThingsModelDTO> listThingsModel(Long deviceId) {
        Device device = deviceMapper.selectById(deviceId);
        if (null == device) {
            return new ArrayList<>();
        }
        //管理员可以下发，其他看指令控制
        int canSend = getLoginUser().getUserId().equals(getLoginUser().getDeptUserId()) ? 1 : 0;
        List<ThingsModelDTO> list = thingsModelService.getCacheByProductId(device.getProductId(), device.getSerialNumber());
        LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubGateway::getSubClientId, device.getSerialNumber());
        SubGateway subGateway1 = subGatewayMapper.selectOne(queryWrapper);
        for (ThingsModelDTO thingsModelDTO : list) {
            thingsModelDTO.setProductId(device.getProductId());
            thingsModelDTO.setSerialNumber(device.getSerialNumber());
            thingsModelDTO.setIsShadow(1 == device.getIsShadow());
            thingsModelDTO.setCanSend(canSend);
            thingsModelDTO.setDeviceName(device.getDeviceName());
            if (Objects.nonNull(subGateway1)) {
                thingsModelDTO.setAddress(subGateway1.getAddress());
            }
        }
        SubGatewayVO subGateway = new SubGatewayVO();
        subGateway.setParentClientId(device.getSerialNumber());
        List<SubDeviceListVO> subDeviceListVOList = subGatewayService.selectGatewayList(subGateway).getRecords();
        for (SubDeviceListVO subDeviceListVO : subDeviceListVOList) {
            List<ThingsModelDTO> subList = thingsModelService.getCacheByProductId(subDeviceListVO.getSubProductId(), subDeviceListVO.getSubClientId());
            for (ThingsModelDTO thingsModelDTO : subList) {
                thingsModelDTO.setProductId(subDeviceListVO.getSubProductId());
                thingsModelDTO.setSerialNumber(subDeviceListVO.getSubClientId());
                thingsModelDTO.setIsShadow(1 == device.getIsShadow());
                thingsModelDTO.setCanSend(canSend);
                thingsModelDTO.setDeviceName(subDeviceListVO.getSubDeviceName());
                thingsModelDTO.setAddress(subDeviceListVO.getAddress());
            }
            //指令控制
            if (0 == canSend) {
                jurisdictionThingsModel(subList, deviceId);
            }
            list.addAll(subList);
        }
        return list;
    }

    private void jurisdictionThingsModel(List<ThingsModelDTO> list, Long deviceId) {
        Long userId = SecurityUtils.getUserId();
        //查询是否有指令控制
        List<OrderControl> orderControls = orderControlService.selectByUserId(userId, deviceId);
        if (CollectionUtils.isEmpty(orderControls)) return;
        List<Long> modelIdList = new ArrayList<>();
        for (OrderControl control : orderControls) {
            String selectOrder = control.getSelectOrder();
            String[] split = selectOrder.split(",");
            //判断是否生效
            boolean just = just(control);
            if (just) {
                List<Long> ids = Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList());
                modelIdList.addAll(ids);
            }
        }
        if (!CollectionUtils.isEmpty(modelIdList)) {
            for (ThingsModelDTO dto : list) {
                if (modelIdList.contains(dto.getModelId())) {
                    dto.setCanSend(1);
                } else {
                    dto.setCanSend(0);
                }
            }
        }

    }

    private boolean just(OrderControl control) {
        Date startTime = control.getStartTime();
        Date endTime = control.getEndTime();
        Integer count = control.getCount();
        Date now = DateUtils.getNowDate();
        return now.after(startTime) && now.before(endTime) && count > 0;
    }

    @Override
    public void updateByOrder(Long userId, Long deviceId) {
        //处理指令权限问题 ,非管理员角色
        List<OrderControl> controlList = orderControlService.selectByUserId(userId, deviceId);
        controlList = controlList.stream().filter(control -> 1 == control.getStatus()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(controlList)) {
            //更新次数
            OrderControl control = controlList.get(0);
            orderControlService.reduceOrderControlCount(control.getId());
        }
    }

    /**
     * 查询产品下所有设备，返回设备编号
     *
     * @param productId 产品id
     * @return
     */
    @Override
    public List<String> selectSerialNumbersByProductId(Long productId) {
        return deviceMapper.selectSerialNumbersByProductId(productId);
    }

    @Override
    public Page<DeviceVO> pageModbusTcpHost(DeviceVO deviceVO) {
        return deviceMapper.pageModbusTcpHost(new Page<DeviceVO>(deviceVO.getPageNum(), deviceVO.getPageSize()), deviceVO);
    }

    private LambdaQueryWrapper<Device> buildQueryWrapper(Device query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Device> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getDeviceId() != null, Device::getDeviceId, query.getDeviceId());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), Device::getDeviceName, query.getDeviceName());
        lqw.eq(query.getProductId() != null, Device::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), Device::getProductName, query.getProductName());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), Device::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getTenantId() != null, Device::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), Device::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getGwDevCode()), Device::getGwDevCode, query.getGwDevCode());
        lqw.eq(query.getFirmwareVersion() != null, Device::getFirmwareVersion, query.getFirmwareVersion());
        lqw.eq(query.getStatus() != null, Device::getStatus, query.getStatus());
        lqw.eq(query.getRssi() != null, Device::getRssi, query.getRssi());
        lqw.eq(query.getIsShadow() != null, Device::getIsShadow, query.getIsShadow());
        lqw.eq(query.getLocationWay() != null, Device::getLocationWay, query.getLocationWay());
        lqw.eq(StringUtils.isNotBlank(query.getThingsModelValue()), Device::getThingsModelValue, query.getThingsModelValue());
        lqw.eq(StringUtils.isNotBlank(query.getNetworkAddress()), Device::getNetworkAddress, query.getNetworkAddress());
        lqw.eq(StringUtils.isNotBlank(query.getNetworkIp()), Device::getNetworkIp, query.getNetworkIp());
        lqw.eq(query.getLongitude() != null, Device::getLongitude, query.getLongitude());
        lqw.eq(query.getLatitude() != null, Device::getLatitude, query.getLatitude());
        lqw.eq(StringUtils.isNotBlank(query.getSummary()), Device::getSummary, query.getSummary());
        lqw.eq(StringUtils.isNotBlank(query.getImgUrl()), Device::getImgUrl, query.getImgUrl());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), Device::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), Device::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, Device::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), Device::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, Device::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), Device::getRemark, query.getRemark());
        lqw.eq(query.getSlaveId() != null, Device::getSlaveId, query.getSlaveId());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Device::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        if (!Objects.isNull(params.get("beginActiveTime")) &&
                !Objects.isNull(params.get("endActiveTime"))) {
            lqw.between(Device::getActiveTime, params.get("beginActiveTime"), params.get("endActiveTime"));
        }

        return lqw;
    }

    @Override
    public AjaxResult restoreDeviceByDeviceId(Long deviceId) {
        // 根据 serialNumber 查询所有记录
        List<Device> devices = deviceMapper.selectListByDeviceId(deviceId);

        if (CollectionUtils.isEmpty(devices)) {
            return AjaxResult.error(MessageUtils.message("device.not.found.by.serial.number"));
        }

        // 筛选出 del_flag = null 的设备
        List<Device> deletedDevices = devices.stream()
                .filter(device -> device.getDelFlag() == null)
                .collect(Collectors.toList());

        if (devices.size() > 1) {
            return AjaxResult.error(MessageUtils.message("device.duplicate.by.serial.number"));
        }

        if (CollectionUtils.isEmpty(deletedDevices)) {
            return AjaxResult.error(MessageUtils.message("device.not.found.by.serial.number"));
        }

        Device device = deletedDevices.get(0);
        Product product = productMapper.selectById(device.getProductId());
        if (product == null) {
            return AjaxResult.error(MessageUtils.message("product.not.found.by.product.id"));
        }
        device.setDelFlag("0"); // 恢复设备
        int rows = deviceMapper.restoreDevice(device);

        if (rows > 0) {
            return AjaxResult.success(MessageUtils.message("device.restore.success"));
        } else {
            return AjaxResult.error(MessageUtils.message("device.restore.fail"));
        }
    }

    @Override
    public AjaxResult deleteDeviceByIds(Long[] deviceIds) {
        int delete = 0;
        for (Long deviceId : deviceIds) {
            delete += deviceMapper.deleteByDeviceId(deviceId);
        }
        if (delete > 0) {
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    @Override
    @DataScope()
    public Page<Device> pageDeleteDeviceVO(DeviceVO deviceVO) {
        return deviceMapper.selectDelDeviceVO(new Page<>(deviceVO.getPageNum(), deviceVO.getPageSize()), deviceVO);
    }

    @Override
    public AjaxResult userAssignment(Long userId, String deviceIds) {
        SysUser operateUser = getLoginUser().getUser();
        SysUser targetUser = userService.selectUserById(userId);
        if (Objects.isNull(targetUser)) {
            return AjaxResult.error(MessageUtils.message("user.not.exists"));
        }
        List<Long> deviceIdList = Arrays.stream(deviceIds.split(",")).map(d -> Long.parseLong(d.trim())).distinct().collect(Collectors.toList());
        LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Device::getDeviceId, deviceIdList).set(Device::getCreateBy, targetUser.getUserName());
        int result = deviceMapper.update(null, updateWrapper);
        DeviceRecord deviceRecord = new DeviceRecord();
        deviceRecord.setOperateDeptId(operateUser.getDeptId()).setTargetDeptId(targetUser.getDeptId()).setDistributeType(DeviceDistributeTypeEnum.USER.getType())
                .setType(DeviceRecordTypeEnum.ASSIGNMENT.getType()).setTotal(deviceIdList.size()).setTenantId(operateUser.getDept().getDeptUserId()).setTenantName(operateUser.getDept().getDeptUserName());
        deviceRecord.setCreateBy(operateUser.getUserName());
        deviceRecord.setUpdateBy(targetUser.getUserName());
        if (result > 0) {
            deviceRecord.setStatus(StatusEnum.SUCCESS.getStatus());
            deviceRecord.setSuccessQuantity(deviceIdList.size());
            deviceRecord.setFailQuantity(0);
        } else {
            deviceRecord.setStatus(StatusEnum.FAIL.getStatus());
            deviceRecord.setSuccessQuantity(0);
            deviceRecord.setFailQuantity(deviceIdList.size());
        }
        int insertRecordStatus = deviceRecordMapper.insert(deviceRecord);
        List<DeviceRecord> deviceRecordList = new ArrayList<>();
        List<Device> deviceList = deviceMapper.selectDeviceListByDeviceIds(deviceIdList);
        for (Device device : deviceList) {
            DeviceRecord deviceRecord1 = new DeviceRecord();
            deviceRecord1.setOperateDeptId(operateUser.getDeptId()).setTargetDeptId(targetUser.getDeptId()).setDeviceId(device.getDeviceId())
                    .setProductId(device.getProductId()).setSerialNumber(device.getSerialNumber())
                    .setType(DeviceRecordTypeEnum.ASSIGNMENT_DETAIL.getType()).setParentId(deviceRecord.getId())
                    .setDistributeType(DeviceDistributeTypeEnum.USER.getType()).setTenantId(operateUser.getDept().getDeptUserId()).setTenantName(operateUser.getDept().getDeptUserName());
            deviceRecord1.setStatus(insertRecordStatus > 0 ? StatusEnum.SUCCESS.getStatus() : StatusEnum.FAIL.getStatus());
            deviceRecord1.setCreateBy(operateUser.getUserName());
            deviceRecordList.add(deviceRecord1);
        }
        deviceRecordMapper.insertBatch(deviceRecordList);
        return result > 0 ? AjaxResult.success(MessageUtils.message("device.assignment.success")) : AjaxResult.error(MessageUtils.message("device.assignment.fail"));
    }

    @Override
    public Device selectAndCheckBySerialNumber(Device device) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            Device device1 = deviceMapper.selectDeviceBySerialNumber(device.getSerialNumber());
            if (Objects.isNull(device1)) {
                return null;
            }
            this.checkDeviceDataScope(device1.getDeviceId());
            return device1;
        }
        LambdaQueryWrapper<Device> queryWrapper = this.buildQueryWrapper(device);
        if (ObjectUtil.isNotEmpty(device.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            queryWrapper.apply((String) device.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return this.getOne(queryWrapper);
    }

    @Override
    public Device getDeviceByIp(String deviceIp) {
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Device::getDeviceIp, deviceIp);
        return deviceMapper.selectOne(queryWrapper);
    }

    @Override
    public void handleSubProductDeviceAddBatch(ProductSubDeviceAddVO vo) {
        // 新增设备
        Device parentDevice = this.selectDeviceBySerialNumber(vo.getParentClientId());
        List<Long> subProductIdList = vo.getSubProductList().stream().map(ProductSubDeviceAddVO.SubProduct::getSubProductId).collect(Collectors.toList());
        LambdaQueryWrapper<ProductModbusJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ProductModbusJob::getProductId, subProductIdList);
        List<ProductModbusJob> productModbusJobList = productModbusJobMapper.selectList(queryWrapper);
        Map<Long, List<ProductModbusJob>> productModbusJobMap = productModbusJobList.stream().collect(Collectors.groupingBy(ProductModbusJob::getProductId));
        List<ModbusJob> modbusJobAddList = new ArrayList<>();
        for (ProductSubDeviceAddVO.SubProduct subProduct : vo.getSubProductList()) {
            Product product = productMapper.selectById(subProduct.getSubProductId());
            List<ProductModbusJob> productModbusJobList1 = productModbusJobMap.get(subProduct.getSubProductId());
            for (ProductSubDeviceAddVO.SubDevice subDevice : subProduct.getSubDeviceList()) {
                Device addDevice = new Device();
                addDevice.setDeviceName(subDevice.getDeviceName());
                addDevice.setProductId(product.getProductId());
                addDevice.setProductName(product.getProductName());
//                String deviceIp = null;
//                if (FastBeeConstant.PROTOCOL.ModbusTcp.equals(product.getProtocolCode())) {
//                    deviceIp = deviceVO.getDeviceIp() + "#" + productSubGatewayVO.getAddress();
//                }
//                addDevice.setDeviceIp(deviceIp);
                String serialNumber = this.generationDeviceNum(1);
                addDevice.setSerialNumber(serialNumber);
                addDevice.setTenantId(parentDevice.getTenantId());
                addDevice.setTenantName(parentDevice.getTenantName());
                addDevice.setCreateBy(getLoginUser().getUsername());
                addDevice.setFirmwareVersion(new BigDecimal(1));
                addDevice.setRssi(0);
                addDevice.setIsShadow(0);
                addDevice.setLocationWay(product.getLocationWay());
                addDevice.setImgUrl(product.getImgUrl());
                addDevice.setCreateTime(new Date());
                deviceMapper.insert(addDevice);
                SubGateway subGateway = new SubGateway();
                subGateway.setParentClientId(vo.getParentClientId());
                subGateway.setParentProductId(vo.getParentProductId());
                subGateway.setSubClientId(addDevice.getSerialNumber());
                subGateway.setSubProductId(addDevice.getProductId());
                subGateway.setAddress(subDevice.getAddress());
                subGateway.setCreateBy(addDevice.getCreateBy());
                subGateway.setCreateTime(new Date());
                subGatewayService.insertGateway(subGateway);
//                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(productModbusJobList1)) {
//                    for (ProductModbusJob productModbusJob : productModbusJobList1) {
//                        ModbusJob modbusJob = new ModbusJob();
//                        modbusJob.setDeviceId(addDevice.getDeviceId());
//                        modbusJob.setSerialNumber(addDevice.getSerialNumber());
//                        List<JSONObject> commands = JSON.parseArray(productModbusJob.getCommand(), JSONObject.class);
//                        for (JSONObject command : commands) {
//                            command.put("address", Integer.parseInt(subDevice.getAddress()));
//                        }
//                        modbusJob.setCommand(JacksonUtil.bean2Json(commands));
//                        modbusJob.setCommandType(productModbusJob.getCommandType());
////                        modbusJobMapper.insert(modbusJob);
//                        modbusJobAddList.add(modbusJob);
//                    }
//                }
            }
        }
//        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(modbusJobAddList)) {
//            this.handleGatewayModbusJob(modbusJobAddList, parentDevice.getDeviceId(), parentDevice.getSerialNumber());
//        }
    }

    @Override
    public AjaxResult addBatchByProduct(ProductSubDeviceAddVO vo) {
        String s = subGatewayService.checkRepeatAddress(vo.getSubProductList(), vo.getParentClientId());
        if (StringUtils.isNotEmpty(s)) {
            return AjaxResult.error(MessageUtils.message("subGateway.subDeviceAddress.is.repeat"));
        }
        this.handleSubProductDeviceAddBatch(vo);
        return AjaxResult.success();
    }

    /**
     * 根据分组id集合查询设备分组
     *
     * @param groupIds 设备分组主键
     * @return 设备分组
     */
    @Override
    public List<DeviceGroup> listDeviceGroupByGroupIds(List<Long> groupIds) {
        return deviceMapper.listDeviceGroupByGroupIds(groupIds);
    }

}
