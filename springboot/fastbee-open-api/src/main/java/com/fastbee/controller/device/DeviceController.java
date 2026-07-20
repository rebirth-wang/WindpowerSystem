package com.fastbee.controller.device;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.DeviceConvert;
import com.fastbee.iot.data.service.IMqttMessagePublish;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceRelateUserInput;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO;
import com.fastbee.iot.model.vo.*;
import com.fastbee.iot.service.IDeviceExtParamValueService;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.iot.service.ISubGatewayService;
import com.fastbee.mqtt.manager.SessionManger;
import com.fastbee.mqttclient.EmqxApiClient;

/**
 * 设备Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "设备管理")
@RestController
@RequestMapping("/iot/device")
public class DeviceController extends BaseController {

    @Resource
    private IDeviceService deviceService;
    @Resource
    private ISubGatewayService subGatewayService;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Value("${server.broker.enabled}")
    private boolean mqttBroker;
    @Resource
    private ISessionStore sessionStore;
    @Resource
    private EmqxApiClient emqxApiClient;
    @Resource
    private IDeviceUpdateService deviceUpdateService;
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Resource
    private IDeviceExtParamValueService deviceExtParamValueService;


    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/list")
    @ApiOperation("设备分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer token. permission: iot:device:list", required = true, paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "pageNum", value = "Page number.", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "Page size.", paramType = "query", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", value = "Device name fuzzy query.", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "serialNumber", value = "Device serial number fuzzy query.", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "productId", value = "Product ID.", paramType = "query", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "status", value = "Device status.", paramType = "query", dataTypeClass = Integer.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Paged response: { code, msg, rows, total }. Empty data returns rows: [] and total: 0. Business failures return HTTP 200 with non-200 code and msg.", response = DeviceContractSchemas.DevicePageResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized or login expired."),
            @ApiResponse(code = 403, message = "Missing permission: iot:device:list."),
            @ApiResponse(code = 500, message = "Server error. Use X-Request-Id to locate backend logs.")
    })
    public TableDataInfo list(DeviceVO deviceVO) {
        Page<DeviceVO> voPage = deviceService.pageDeviceVO(deviceVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询未分配授权码设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/unAuthlist")
    @ApiOperation("设备分页列表")
    public TableDataInfo unAuthlist(DeviceVO deviceVO) {
        Page<Device> devicePage = deviceService.selectUnAuthDeviceList(deviceVO);
        return getDataTable(devicePage.getRecords(), devicePage.getTotal());
    }

    /**
     * 查询分组可添加设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/listByGroup")
    @ApiOperation("查询分组可添加设备分页列表")
    public TableDataInfo listByGroup(DeviceVO deviceVO) {
        LoginUser loginUser = getLoginUser();
        if (null == loginUser.getDeptId()) {
            deviceVO.setTenantId(loginUser.getUserId());
            return getDataTable(deviceService.listTerminalUserByGroup(deviceVO));
        }
        Page<DeviceVO> devicePage = deviceService.selectDeviceListByGroup(deviceVO);
        return getDataTable(devicePage.getRecords(), devicePage.getTotal());
    }

    /**
     * 查询设备简短列表，主页列表数据
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/shortList")
    @ApiOperation("设备分页简短列表")
    public TableDataInfo shortList(DeviceVO deviceVO) {
        LoginUser loginUser = getLoginUser();
        if (null == loginUser.getDeptId()) {
            // 终端用户查询设备
            deviceVO.setTenantId(loginUser.getUserId());
            Page<DeviceShortOutput> deviceShortOutputPage = deviceService.listTerminalUser(deviceVO);
            return getDataTable(deviceShortOutputPage.getRecords(), deviceShortOutputPage.getTotal());
        }
        if (Objects.isNull(deviceVO.getTenantId())) {
            deviceVO.setTenantId(getLoginUser().getUserId());
        }
        Page<DeviceShortOutput> deviceShortOutputPage = deviceService.selectDeviceShortList(deviceVO);
        return getDataTable(deviceShortOutputPage.getRecords(), deviceShortOutputPage.getTotal());
    }

    /**
     * 查询所有设备简短列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/all")
    @ApiOperation("查询所有设备简短列表")
    public TableDataInfo allShortList(DeviceVO deviceVO) {
        DeviceVO queryDevice = new DeviceVO();
        return getDataTable(deviceService.selectAllDeviceShortList(queryDevice));
    }

    /**
     * 导出设备列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出设备")
    public void export(HttpServletResponse response, DeviceVO deviceVO) {
        Page<DeviceVO> voPage = deviceService.pageDeviceVO(deviceVO);
        ExcelUtil<DeviceVO> util = new ExcelUtil<DeviceVO>(DeviceVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备数据");
    }

    /**
     * 获取GB28181设备接入排障信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/access-diagnostics/{deviceId}")
    @ApiOperation("获取GB28181设备接入排障信息")
    public AjaxResult getAccessDiagnostics(@PathVariable("deviceId") Long deviceId) {
        DeviceVO deviceVO = deviceService.selectDeviceByDeviceId(deviceId);
        if (Objects.isNull(deviceVO)) {
            return AjaxResult.error("设备不存在");
        }
        if (!Objects.equals(3, deviceVO.getDeviceType()) || !"GB28181".equals(deviceVO.getTransport())) {
            return AjaxResult.error("当前设备不是GB28181监控设备");
        }
        String latestKey = DeviceAccessDiagnosticsBuilder.buildDeviceStatusLatestKey(deviceVO.getSerialNumber());
        String historyKey = DeviceAccessDiagnosticsBuilder.buildDeviceStatusHistoryKey(deviceVO.getSerialNumber());
        Object latest = redisTemplate.opsForValue().get(latestKey);
        List<Object> history = redisTemplate.opsForList().range(historyKey, 0, -1);
        return AjaxResult.success(DeviceAccessDiagnosticsBuilder.build(deviceVO, latest, history));
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/{deviceId}")
    @ApiOperation("获取设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer token. permission: iot:device:query", required = true, paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "deviceId", value = "Device primary key.", required = true, paramType = "path", dataTypeClass = Long.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Detail response: { code, msg, data }. If the device does not exist, data is empty. Business failures return HTTP 200 with non-200 code and msg.", response = DeviceContractSchemas.DeviceDetailResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized or login expired."),
            @ApiResponse(code = 403, message = "Missing permission: iot:device:query."),
            @ApiResponse(code = 500, message = "Server error. Use X-Request-Id to locate backend logs.")
    })
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId) {
        DeviceVO deviceVO = deviceService.selectDeviceByDeviceId(deviceId);
        if (Objects.isNull(deviceVO)) {
            return success();
        }
        // 判断当前用户是否有设备分享权限 （设备所属机构管理员和设备所属用户有权限）
        LoginUser loginUser = getLoginUser();
        List<SysRole> roles = loginUser.getUser().getRoles();
        //判断当前用户是否为设备所属机构管理员
        if (roles.stream().anyMatch(a -> "admin".equals(a.getRoleKey()))) {
            deviceVO.setIsOwner(1);
        } else {
            //判断当前用户是否是设备所属用户
            if (Objects.equals(deviceVO.getTenantId(), loginUser.getUserId())) {
                deviceVO.setIsOwner(1);
            } else {
                deviceVO.setIsOwner(deviceVO.getCreateBy().equals(loginUser.getUsername()) ? 1 : 0);
            }
        }
        return AjaxResult.success(deviceVO);
    }

    /**
     * 根据设备编号详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/getDeviceBySerialNumber/{serialNumber}")
    @ApiOperation("根据设备编号获取设备详情")
    public AjaxResult getInfoBySerialNumber(@PathVariable("serialNumber") String serialNumber) {
        Device device = new Device();
        device.setSerialNumber(serialNumber);
        return AjaxResult.success(deviceService.selectAndCheckBySerialNumber(device));
    }

    /**
     * 查询设备属性、功能、事件统计
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/statistic")
    @ApiOperation("查询设备属性、功能、事件统计信息")
    public AjaxResult getDeviceStatistic() {
        return AjaxResult.success(deviceService.selectDeviceStatistic());
    }


    /**
     * 获取设备、产品和告警数量
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/deviceProductAlertCount")
    @ApiOperation("获取设备、产品和告警数量统计信息")
    public AjaxResult selectDeviceProductAlertCount() {
        return AjaxResult.success(deviceService.selectDeviceProductAlertCount());
    }

    /**
     * 获取操作记录统计
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/functionLogCount")
    @ApiOperation("获取操作记录统计")
    public AjaxResult selectFunctionLogCount() {
        return AjaxResult.success(deviceService.selectFunctionLogCount());
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping(value = "/runningStatus")
    @ApiOperation("获取设备详情和运行状态")
    public AjaxResult getRunningStatusInfo(Long deviceId) {
        return AjaxResult.success(deviceService.selectDeviceRunningStatusByDeviceId(deviceId));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @Log(title = "添加设备", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer token. permission: iot:device:add", required = true, paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "deviceVO", value = "Device create payload. Typical fields: deviceName, productId, serialNumber, deviceType, transport and location fields.", required = true, paramType = "body", dataTypeClass = DeviceVO.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Create response: { code, msg, data }. Business failures return HTTP 200 with non-200 code and msg, for example duplicated serialNumber or invalid product.", response = DeviceContractSchemas.DeviceWriteResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized or login expired."),
            @ApiResponse(code = 403, message = "Missing permission: iot:device:add."),
            @ApiResponse(code = 500, message = "Server error. Use X-Request-Id to locate backend logs.")
    })
    public AjaxResult add(@RequestBody DeviceVO deviceVO) {
        return AjaxResult.success(deviceService.insertDevice(deviceVO));
    }

    /**
     * 新增网关与子设备关联 - 新版本,通过产品
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @PostMapping("/addBatchByProduct")
    @ApiOperation("批量新增网关与子设备关联")
    public AjaxResult addBatchByProduct(@RequestBody ProductSubDeviceAddVO vo) {
        return deviceService.addBatchByProduct(vo);
    }

    /**
     * TODO --APP
     * 终端用户绑定设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @Log(title = "设备关联用户", businessType = BusinessType.UPDATE)
    @PostMapping("/relateUser")
    @ApiOperation("终端-设备关联用户")
    public AjaxResult relateUser(@RequestBody DeviceRelateUserInput deviceRelateUserInput) {
        if (deviceRelateUserInput.getUserId() == 0 || deviceRelateUserInput.getUserId() == null) {
            return AjaxResult.error(MessageUtils.message("device.user.id.null"));
        }
        if (deviceRelateUserInput.getDeviceNumberAndProductIds() == null || deviceRelateUserInput.getDeviceNumberAndProductIds().size() == 0) {
            return AjaxResult.error(MessageUtils.message("device.product.id.null"));
        }
        return deviceService.deviceRelateUser(deviceRelateUserInput);
    }

    /**
     * 修改设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:edit')")
    @Log(title = "修改设备", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer token. permission: iot:device:edit", required = true, paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "deviceVO", value = "Device update payload. deviceId is required.", required = true, paramType = "body", dataTypeClass = DeviceVO.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update response: { code, msg, data }. Business failures return HTTP 200 with non-200 code and msg, for example device not found, duplicated serialNumber, or unauthorized data scope.", response = DeviceContractSchemas.DeviceWriteResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized or login expired."),
            @ApiResponse(code = 403, message = "Missing permission: iot:device:edit."),
            @ApiResponse(code = 500, message = "Server error. Use X-Request-Id to locate backend logs.")
    })
    public AjaxResult edit(@RequestBody DeviceVO deviceVO) {
        Device device = DeviceConvert.INSTANCE.convertDevice(deviceVO);
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            deviceService.checkDeviceDataScope(device.getDeviceId());
        } else {
            SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
        }
        device.setUpdateBy(getUsername());
        // 设备编号唯一检查
        Device oldDevice = deviceService.getById(device.getDeviceId());
        if (Objects.isNull(oldDevice)) {
            return AjaxResult.error("设备不存在，修改失败", 0);
        }
        if (!oldDevice.getSerialNumber().equals(device.getSerialNumber())) {
            Device existDevice = deviceService.selectDeviceBySerialNumber(device.getSerialNumber());
            if (existDevice != null) {
                return AjaxResult.error("设备编号：" + device.getSerialNumber() + " 已经存在，修改失败", 0);
            }
            subGatewayService.updateSubCliectId(oldDevice.getSerialNumber(), device.getSerialNumber());
        }
        if (StringUtils.isNotEmpty(device.getDeviceIp()) && Objects.nonNull(device.getDevicePort())) {
            Device existDevice = deviceService.getDeviceByIp(device.getDeviceIp());
            if (Objects.nonNull(existDevice) &&
                    !existDevice.getDeviceId().equals(device.getDeviceId())) {
                throw new ServiceException(StringUtils.format(MessageUtils.message("device.insert.fail.device.ip.already.exist"), existDevice.getSerialNumber()));
            }
        }
        int i = deviceUpdateService.updateDevice(device);
        if (i <= 0) {
            return AjaxResult.error("修改失败", 0);
        }
        if (deviceVO.getDeviceExtParamValueList() != null && !deviceVO.getDeviceExtParamValueList().isEmpty()) {
            deviceVO.getDeviceExtParamValueList().forEach(deviceExtParamValue -> {
                deviceExtParamValue.setDeviceId(device.getDeviceId());
                deviceExtParamValue.setUpdateBy(getUsername());
                deviceExtParamValueService.saveOrUpdate(deviceExtParamValue);
            });
        }
        device.setProductId(oldDevice.getProductId());
        if (!Objects.equals(device.getStatus(), oldDevice.getStatus())) {
            if (DeviceStatus.FORBIDDEN.getType() == device.getStatus()) {
                // 禁用，判断是netty还是emqx，去除客户端
                if (mqttBroker) {
                    if (sessionStore.containsKey(device.getSerialNumber())) {
                        SessionManger.removeClient(device.getSerialNumber(), DeviceStatus.FORBIDDEN);
                    } else {
                        //推送到前端
                        mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.FORBIDDEN);
                    }
                } else {
                    // 剔除客户端 todo
                    DeviceMqttConnectVO mqttConnectData = deviceService.getMqttConnectData(device.getDeviceId());
                    emqxApiClient.kickClient(mqttConnectData.getClientId());
                    mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.FORBIDDEN);
                }
            } else if (DeviceStatus.OFFLINE.getType() == device.getStatus()) {
                //推送到前端
                mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.OFFLINE);
            }
        }
        return AjaxResult.success("修改成功", 1);
    }

    /**
     * 重置设备状态
     */
    @PreAuthorize("@ss.hasPermi('iot:device:edit')")
    @Log(title = "重置设备状态", businessType = BusinessType.UPDATE)
    @PutMapping("/reset/{serialNumber}")
    @ApiOperation("重置设备状态")
    public AjaxResult resetDeviceStatus(@PathVariable String serialNumber) {
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (Objects.isNull(device)) {
            return AjaxResult.success();
        }
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            deviceService.checkDeviceDataScope(device.getDeviceId());
        } else {
            SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
        }
        return toAjax(deviceService.resetDeviceStatus(serialNumber));
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermi('iot:device:remove')")
    @Log(title = "删除设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceIds}")
    @ApiOperation("批量删除设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer token. permission: iot:device:remove", required = true, paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "deviceIds", value = "Comma-separated device IDs, such as 1001,1002.", required = true, paramType = "path", dataTypeClass = String.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Delete response: { code, msg }. Business failures return HTTP 200 with non-200 code and msg, for example devices still referenced by scene/model data.", response = DeviceContractSchemas.DeviceDeleteResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized or login expired."),
            @ApiResponse(code = 403, message = "Missing permission: iot:device:remove."),
            @ApiResponse(code = 500, message = "Server error. Use X-Request-Id to locate backend logs.")
    })
    public AjaxResult remove(@PathVariable Long[] deviceIds) throws SchedulerException {
        SysUser user = getLoginUser().getUser();
        List<Device> deviceList = deviceService.listByIds(Arrays.asList(deviceIds));
        for (Device device : deviceList) {
            if (null == user.getDeptId()) {
                deviceService.checkDeviceDataScope(device.getDeviceId());
            } else {
                SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
            }
        }

        List<Long> ids = new ArrayList<>();
        ArrayList<Long> sceneModelIds = new ArrayList<>();
        for (Long deviceId : deviceIds) {
            int i = deviceService.deleteDeviceByDeviceId(deviceId);
            if (1 == i) {
                ids.add(deviceId);
            }
            if (2 == i) {
                sceneModelIds.add(deviceId);
            }
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("device.delete.fail.please.delete.device.scene"), JSONObject.toJSONString(ids)));
        }
        if (CollectionUtils.isNotEmpty(sceneModelIds)) {
            return AjaxResult.error(StringUtils.format(MessageUtils.message("delete.fail.please.delete.scene.model"), JSONObject.toJSONString(sceneModelIds)));
        }
        return AjaxResult.success();
    }

    /**
     * 生成设备编号
     */
    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @GetMapping("/generator")
    @ApiOperation("生成设备编号")
    public AjaxResult generatorDeviceNum(Integer type) {
        return AjaxResult.success(MessageUtils.message("operate.success"), deviceService.generationDeviceNum(type));
    }

    /**
     * 获取设备MQTT连接参数
     *
     * @param deviceId 设备主键id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping("/getMqttConnectData")
    @ApiOperation("获取设备MQTT连接参数")
    public AjaxResult getMqttConnectData(Long deviceId) {
        Device device = deviceService.getById(deviceId);
        if (Objects.isNull(device)) {
            return AjaxResult.success();
        }
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            deviceService.checkDeviceDataScope(device.getDeviceId());
        } else {
            SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
        }
        return AjaxResult.success(deviceService.getMqttConnectData(deviceId));
    }

    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping("/getHttpAuthData")
    @ApiOperation("获取设备HTTP认证参数")
    public AjaxResult getHttpAuthData(Long deviceId) {
        Device device = deviceService.getById(deviceId);
        if (Objects.isNull(device)) {
            return AjaxResult.success();
        }
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            deviceService.checkDeviceDataScope(device.getDeviceId());
        } else {
            SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
        }
        return AjaxResult.success(deviceService.getHttpAuthData(deviceId));
    }

    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @ApiOperation("下载设备导入模板")
    @PostMapping("/uploadTemplate")
    public void uploadTemplate(HttpServletResponse response, @RequestParam(name = "type") Integer type) {
        // 1-设备导入；2-设备分配
        if (1 == type) {
            ExcelUtil<DeviceImportVO> util = new ExcelUtil<>(DeviceImportVO.class);
            util.importTemplateExcel(response, "设备导入");
        } else if (2 == type) {
            ExcelUtil<DeviceAssignmentVO> util = new ExcelUtil<>(DeviceAssignmentVO.class);
            util.importTemplateExcel(response, "设备分配");
        }
    }

    @PreAuthorize("@ss.hasPermi('iot:device:add')")
    @ApiOperation("批量导入设备")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam("file") MultipartFile file, @RequestParam("productId") Long productId) throws Exception {
        if (null == file) {
            return error(MessageUtils.message("import.failed.file.null"));
        }
        ExcelUtil<DeviceImportVO> util = new ExcelUtil<>(DeviceImportVO.class);
        List<DeviceImportVO> deviceImportVOList = util.importExcel(file.getInputStream());
        if (CollectionUtils.isEmpty(deviceImportVOList)) {
            return error(MessageUtils.message("import.failed.data.null"));
        }
        DeviceImportVO deviceImportVO = deviceImportVOList.stream().filter(d -> StringUtils.isEmpty(d.getDeviceName())).findAny().orElse(null);
        if (null != deviceImportVO) {
            return error(MessageUtils.message("import.failed.device.name.null"));
        }
        String message = deviceService.importDevice(deviceImportVOList, productId);
        return StringUtils.isEmpty(message) ? success(MessageUtils.message("import.success")) : error(message);
    }

    @PreAuthorize("@ss.hasPermi('iot:device:assignment')")
    @ApiOperation("批量导入分配设备")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importAssignmentData")
    public AjaxResult importAssignmentData(@RequestParam("file") MultipartFile file,
                                           @RequestParam("productId") Long productId,
                                           @RequestParam("deptId") Long deptId) throws Exception {
        if (null == file) {
            return error(MessageUtils.message("import.failed.file.null"));
        }
        ExcelUtil<DeviceAssignmentVO> util = new ExcelUtil<>(DeviceAssignmentVO.class);
        List<DeviceAssignmentVO> deviceAssignmentVOS = util.importExcel(file.getInputStream());
        if (CollectionUtils.isEmpty(deviceAssignmentVOS)) {
            return error(MessageUtils.message("import.failed.device.name.null"));
        }
        DeviceAssignmentVO deviceAssignmentVO = deviceAssignmentVOS.stream().filter(d -> StringUtils.isEmpty(d.getDeviceName())).findAny().orElse(null);
        if (null != deviceAssignmentVO) {
            return error(MessageUtils.message("import.failed.device.name.null"));
        }
        return deviceService.importAssignmentDevice(deviceAssignmentVOS, productId, deptId);
    }

    /**
     * 分配设备
     *
     * @param deptId 机构id
     * @return com.fastbee.common.core.domain.AjaxResult
     * @param: deviceIds 设备id字符串
     */
    @PreAuthorize("@ss.hasPermi('iot:device:assignment')")
    @ApiOperation("分配设备")
    @PostMapping("/assignment")
    public AjaxResult assignment(@RequestParam("deptId") Long deptId,
                                 @RequestParam("deviceIds") String deviceIds) {
        if (null == deptId) {
            return error(MessageUtils.message("device.dept.id.null"));
        }
        if (StringUtils.isEmpty(deviceIds)) {
            return error(MessageUtils.message("device.id.null"));
        }
        return deviceService.assignment(deptId, deviceIds);
    }

    /**
     * 回收设备
     *
     * @return com.fastbee.common.core.domain.AjaxResult
     * @param: deviceIds 设备id字符串
     */
    @PreAuthorize("@ss.hasPermi('iot:device:recovery')")
    @ApiOperation("回收设备")
    @PostMapping("/recovery")
    public AjaxResult recovery(@RequestBody Map<Long, Long> deviceMap) {
        if (MapUtils.isEmpty(deviceMap)) {
            return error(MessageUtils.message("device.not.select"));
        }
        return deviceService.recovery(deviceMap);
    }

    /**
     * 批量生成设备编号
     */
    @PreAuthorize("@ss.hasPermi('iot:device:batchGenerator')")
    @PostMapping("/batchGenerator")
    @ApiOperation("批量生成设备编号")
    public void batchGeneratorDeviceNum(HttpServletResponse response,
                                        @RequestParam("count") Integer count, @RequestParam("type") Integer type) {
        if (count > 200) {
            throw new ServiceException(MessageUtils.message("device.serialNumber.allow.generate.max.number"));
        }
        List<SerialNumberVO> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SerialNumberVO serialNumberVO = new SerialNumberVO();
            String serialNumber = deviceService.generationDeviceNum(type);
            serialNumberVO.setSerialNumber(serialNumber);
            list.add(serialNumberVO);
        }
        ExcelUtil<SerialNumberVO> util = new ExcelUtil<>(SerialNumberVO.class);
        util.exportExcel(response, list, "设备编号");
    }

    /**
     * 查询变量概况
     */
    @PreAuthorize("@ss.hasPermi('iot:device:query')")
    @GetMapping("/listThingsModel")
    @ApiOperation("查询变量概况")
    public TableDataInfo listThingsModel(Integer pageNum, Integer pageSize, Long deviceId, String modelName, Integer type, Integer isMonitor, Integer isReadonly) {
        Device device = deviceService.getById(deviceId);
        if (Objects.isNull(device)) {
            return new TableDataInfo();
        }
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            deviceService.checkDeviceDataScope(device.getDeviceId());
        } else {
            SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
        }
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        List<ThingsModelDTO> thingsModelDTOList = deviceService.listThingsModel(deviceId);
        if (CollectionUtils.isEmpty(thingsModelDTOList)) {
            rspData.setRows(thingsModelDTOList);
            rspData.setTotal(thingsModelDTOList.size());
            return rspData;
        }
        List<Predicate<ThingsModelDTO>> predicateList = new ArrayList<>();
        if (StringUtils.isNotEmpty(modelName)) {
            predicateList.add(o -> o.getModelName().contains(modelName));
        }
        if (null != type) {
            predicateList.add(o -> Objects.equals(o.getType(), type));
        }
        if (null != isMonitor) {
            predicateList.add(o -> Objects.equals(isMonitor, o.getIsMonitor()));
        }
        if (null != isReadonly) {
            predicateList.add(o -> Objects.equals(isReadonly, o.getIsReadonly()));
            predicateList.add(o -> !Objects.equals(3, o.getType()));
        }
        Stream<ThingsModelDTO> stream = thingsModelDTOList.stream();
        for (Predicate<ThingsModelDTO> predicate : predicateList) {
            stream = stream.filter(predicate);
        }
        List<ThingsModelDTO> filterList = stream.collect(Collectors.toList());
        filterList.sort(Comparator.comparing(ThingsModelDTO::getModelOrder).reversed().thenComparing(ThingsModelDTO::getModelId));
        if (CollectionUtils.isNotEmpty(filterList)) {
            List resultList = com.fastbee.common.utils.collection.CollectionUtils.startPage(filterList, pageNum, pageSize);
            rspData.setRows(resultList);
        } else {
            rspData.setRows(new ArrayList<>());
        }
        rspData.setTotal(filterList.size());
        return rspData;
    }

    /**
     * 查询ModbusTcp设备主站列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:list')")
    @GetMapping("/pageModbusTcpHost")
    @ApiOperation("查询ModbusTcp设备主站列表")
    public TableDataInfo pageModbusTcpHost(DeviceVO deviceVO) {
        // 限制当前用户机构
        if (null == deviceVO.getTenantId()) {
            SysUser pageUser = getLoginUser().getUser();
            if (pageUser.getDept() != null) {
                deviceVO.setTenantId(pageUser.getDept().getDeptUserId());
            } else {
                deviceVO.setTenantId(pageUser.getUserId());
            }
        }
        Page<DeviceVO> voPage = deviceService.pageModbusTcpHost(deviceVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询已删除设备列表（逻辑删除：del_flag = NULL）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:device')")
    @GetMapping("/list/deleted")
    @ApiOperation("查询已删除设备列表")
    public TableDataInfo listDeletedDevices(DeviceVO device) {
        Page<Device> voPage = deviceService.pageDeleteDeviceVO(device);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 还原已删除设备（del_flag = NULL -> 0）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:restore')")
    @Log(title = "还原设备", businessType = BusinessType.UPDATE)
    @PutMapping("/restore")
    @ApiOperation("还原逻辑删除设备")
    public AjaxResult restoreDevice(Long deviceId) {
        return deviceService.restoreDeviceByDeviceId(deviceId);
    }

    /**
     * 物理删除逻辑删除的设备（del_flag = NULL 的设备）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:delete')")
    @Log(title = "物理删除设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/physical/delete/{deviceIds}")
    @ApiOperation("物理删除逻辑删除的设备")
    public AjaxResult deleteDeviceBySerialNumber(@PathVariable Long[] deviceIds) {
        return deviceService.deleteDeviceByIds(deviceIds);
    }

    /**
     * 分配设备
     *
     * @param userId 被分配用户id
     * @return com.fastbee.common.core.domain.AjaxResult
     * @param: deviceIds 设备id字符串
     */
    @PreAuthorize("@ss.hasPermi('iot:device:assignment')")
    @ApiOperation("分配设备")
    @PostMapping("/user/assignment")
    public AjaxResult userAssignment(@RequestParam("userId") Long userId,
                                 @RequestParam("deviceIds") String deviceIds) {
        if (Objects.isNull(userId)) {
            return error(MessageUtils.message("device.user.id.null"));
        }
        if (StringUtils.isEmpty(deviceIds)) {
            return error(MessageUtils.message("device.id.null"));
        }
        return deviceService.userAssignment(userId, deviceIds);
    }

}
