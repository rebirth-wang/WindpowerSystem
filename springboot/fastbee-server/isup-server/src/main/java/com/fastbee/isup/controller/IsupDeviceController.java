package com.fastbee.isup.controller;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.isup.model.DeviceRemoteControl;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.model.xml.PpvspMessage;
import com.fastbee.isup.sdk.isapi.ISAPIService;
import com.fastbee.isup.sdk.service.impl.CmsUtil;
import com.fastbee.isup.service.DeviceCacheService;

/**
 * 设备管理接口
 */
@Slf4j
@Profile("isup")
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class IsupDeviceController {

    private final DeviceCacheService deviceCacheService;
    private final CmsUtil cmsUtil;
    private final ISAPIService iSAPIService;

    /**
     * 获取设备列表
     */
    @GetMapping
    public AjaxResult getDevices(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) Integer isOnline) {
        List<IsupDevInfo> devices = deviceCacheService.list(d -> {
            boolean match = true;
            if (deviceId != null && !deviceId.isEmpty()) {
                match = d.getDeviceId().contains(deviceId);
            }
            if (match && isOnline != null) {
                match = isOnline.equals(d.getIsOnline());
            }
            return match;
        });
        return AjaxResult.success(devices);
    }

    /**
     * 获取设备详情
     */
    @PostMapping("/ISAPI/{deviceId}")
    public AjaxResult testISAPI(@PathVariable String deviceId, @RequestParam String command) {
        log.debug("设备接口测试 - deviceId: {}, command: {}", deviceId, command);
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return AjaxResult.error("设备不存在");
        }
        IsupDevInfo device = deviceOpt.get();
        Integer loginId = device.getLoginId();
        if (loginId == null) {
            return AjaxResult.error("设备未登录，无法导入数据");
        }
        testISAPIdispatch(command, loginId);
        return deviceOpt.map(AjaxResult::success)
                .orElse(AjaxResult.error("设备不存在"));
    }


    @GetMapping("/{deviceId}")
    public AjaxResult getDevice(@PathVariable String deviceId) {
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        return deviceOpt.map(AjaxResult::success)
                .orElse(AjaxResult.error("设备不存在"));
    }

    /**
     * 获取设备远程控制信息
     */
    @GetMapping("/{deviceId}/remote-control")
    public AjaxResult getDeviceRemoteControl(@PathVariable String deviceId) {
        DeviceRemoteControl deviceRemoteControl = new DeviceRemoteControl();
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);

        if (deviceOpt.isPresent()) {
            IsupDevInfo device = deviceOpt.get();
            PpvspMessage ppvspMessage = cmsUtil.CMS_XMLRemoteControl(device.getLoginId());
            deviceRemoteControl.setIsOnline(1);
            String ch = ppvspMessage.getParams().getDeviceStatusXML().getChStatus().getCh();
            deviceRemoteControl.setLChannel(ch);
            return AjaxResult.success(deviceRemoteControl);
        }

        deviceRemoteControl.setIsOnline(0);
        return AjaxResult.success(deviceRemoteControl);
    }

    public void testISAPIdispatch(String command, int lLoginID) {
        switch (command) {
            case "1": {
                System.out.println("\n[Function]获取设备信息！");
                iSAPIService.getDevInfo(lLoginID);
                break;
            }
            case "2": {
                System.out.println("\n[Function]设备重启！");
                iSAPIService.reboot(lLoginID);
                break;
            }
            case "3": {
                System.out.println("\n[Function]切换算法test！");
                iSAPIService.VCAResourceTest(lLoginID,1, "smart");
                break;
            }
            case "4": {
                System.out.println("\n[Function]切换算法！");
                iSAPIService.PutVCAResource(lLoginID,1, "smart");
                break;
            }
            case "5": {
                System.out.println("\n[Function]获取所有数字通道状态！");
                iSAPIService.getAllDigitalChannelStatus(lLoginID);
                break;
            }
            case "6": {
                System.out.println("\n[Function]远程抓图！");
                iSAPIService.getPicByCloud(lLoginID);
                break;
            }
            case "8": {
                System.out.println("\n[Function]获取所有数字通道状态！");
                iSAPIService.searchLPListAudit(lLoginID,1);
                break;
            }
        }
    }
}
