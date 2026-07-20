package com.fastbee.controller.runtime;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.data.service.IDeviceMessageService;
import com.fastbee.iot.data.service.IFunctionInvoke;
import com.fastbee.iot.data.service.IRuleEngine;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.model.vo.VariableReadVO;
import com.fastbee.iot.service.IDeviceRuntimeService;

/**
 * 设备运行时数据controller
 *
 * @author gsb
 * @date 2022/12/5 11:52
 */
@RestController
@RequestMapping("/iot/runtime")
@Api(tags = "设备运行数据")
public class DeviceRuntimeController extends BaseController {

    @Autowired
    private IFunctionInvoke functionInvoke;
    @Resource
    private IDeviceRuntimeService runtimeService;
    @Resource
    private IDeviceMessageService deviceMessageService;

    @Resource
    private IRuleEngine ruleEngine;

    /**
     * 服务下发返回回执
     */
    @PostMapping(value = "/service/invokeReply")
    //@PreAuthorize("@ss.hasPermi('iot:service:invokereply')")
    @ApiOperation(value = "服务下发返回回执", httpMethod = "POST", response = AjaxResult.class, notes = "服务下发并返回回执")
    public AjaxResult invokeReply(@Valid @RequestBody InvokeReqDto reqDto) {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
        reqDto.setUserId(userId);
        reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
        return functionInvoke.invokeReply(reqDto);
    }

    /**
     * 服务下发
     * 例如modbus 格式如下
     *
     * @see InvokeReqDto#getRemoteCommand()
     * key = 寄存器地址
     * value = 寄存器地址值
     * <p>
     * 其他协议 key = identifier
     * value = 值
     * {
     * "serialNumber": "860061060282358",
     * "productId": "2",
     * "identifier": "temp",
     * "remoteCommand": {
     * "4": "4"
     * }
     * }
     */
    @PostMapping("/service/invoke")
    @PreAuthorize("@ss.hasPermi('iot:service:invoke')")
    @ApiOperation(value = "服务下发", httpMethod = "POST", response = AjaxResult.class, notes = "服务下发")
    public AjaxResult invoke(@Valid @RequestBody InvokeReqDto reqDto) {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
        reqDto.setUserId(userId);
        return functionInvoke.invokeNoReply(reqDto);
    }

    /**
     * 执行场景联动
     * @param sceneId 场景联动id
     * @return
     */
    @PostMapping("/runScene")
    @PreAuthorize("@ss.hasPermi('iot:scene:run')")
    @ApiOperation(value = "服务下发", httpMethod = "POST", response = AjaxResult.class, notes = "场景联动服务下发")
    public AjaxResult runScene(@RequestParam Long sceneId) {
        if (sceneId == null) {
            return AjaxResult.success();
        }
        System.out.println("------------------[执行一次场景联动]---------------------");
        ruleEngine.ruleMatchBySceneId(sceneId);
        return AjaxResult.success();
    }


    /**
     * 实时状态
     * @param serialNumber 设备编号
     * @return 结果
     */
    @GetMapping(value = "/running")
    @ApiOperation(value = "实时状态")
    public AjaxResult runState(String serialNumber){
        List<ThingsModelValueItem> valueItemList = runtimeService.runtimeBySerialNumber(serialNumber);
        return AjaxResult.success(valueItemList);
    }

    /**
     * 根据messageId查询服务回执
     */
    @GetMapping(value = "fun/get")
    //@PreAuthorize("@ss.hasPermi('iot:service:get')")
    @ApiOperation(value = "根据messageId查询服务回执", httpMethod = "GET", response = AjaxResult.class, notes = "根据messageId查询服务回执")
    public AjaxResult reply(String serialNumber, String messageId) {
        if (StringUtils.isEmpty(messageId) || StringUtils.isEmpty(serialNumber)) {
            throw new ServiceException(MessageUtils.message("runtime.message.id.null"));
        }
        return AjaxResult.success();
    }

    /**
     * 设备服务下发日志
     */
    @GetMapping(value = "/funcLog")
    @ApiOperation(value = "设备服务下发日志")
    public TableDataInfo funcLog(String serialNumber){
        Page<FunctionLogVO> logList = runtimeService.runtimeReply(serialNumber);
        return getDataTable(logList.getRecords(), logList.getTotal());
    }

    @GetMapping(value = "/prop/get")
    @ApiOperation(value = "主动采集", httpMethod = "GET", response = AjaxResult.class, notes = "主动采集")
    public AjaxResult propGet(VariableReadVO readVO){
        deviceMessageService.readVariableValue(readVO);
        return AjaxResult.success();
    }


}
