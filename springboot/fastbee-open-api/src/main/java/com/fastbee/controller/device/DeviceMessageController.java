package com.fastbee.controller.device;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceMessage;
import com.fastbee.iot.data.service.IDeviceMessageService;
import com.fastbee.modbus.model.ModbusRtu;

@RestController
@RequestMapping("/iot/message")
@Api(tags = "设备消息")
public class DeviceMessageController extends BaseController {

    @Resource
    private IDeviceMessageService deviceMessageService;

    @PreAuthorize("@ss.hasPermi('iot:message:post')")
    @PostMapping(value = "/post")
    @ApiOperation(value = "平台下发指令")
    public AjaxResult messagePost(@RequestBody DeviceMessage deviceMessage) {
        deviceMessageService.messagePost(deviceMessage);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('iot:message:encode')")
    @GetMapping(value = "/encode")
    @ApiOperation(value = "指令编码")
    public AjaxResult messageEncode(ModbusRtu modbusRtu) {
        return AjaxResult.success(deviceMessageService.messageEncode(modbusRtu));
    }

    @PreAuthorize("@ss.hasPermi('iot:message:decode')")
    @GetMapping(value = "/decode")
    @ApiOperation(value = "指令解码")
    public AjaxResult messageDecode(DeviceMessage deviceMessage) {
        return AjaxResult.success(deviceMessageService.messageDecode(deviceMessage));
    }

    @PreAuthorize("@ss.hasPermi('iot:message:encode')")
    @PostMapping(value = "/commandGenerate")
    @ApiOperation(value = "指令生成")
    public AjaxResult commandGenerate(@RequestBody MQSendMessageBo messageBo) {
        if (ObjectUtil.isEmpty(messageBo.getParams())) {
            return error("请选择物模型！");
        }
        return AjaxResult.success(deviceMessageService.commandGenerate(messageBo));
    }
}
