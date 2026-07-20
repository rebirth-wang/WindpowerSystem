package com.fastbee.isup.controller;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.sdk.acs.*;
import com.fastbee.isup.sdk.isapi.ISAPIService;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.service.IIsupFacePicService;

/**
 * 人脸检测接口
 */
@Slf4j
@Profile("isup")
@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class FaceDetectionController {

    private final ISAPIService isapiService;
    private final DeviceCacheService deviceCacheService;
    private final IIsupFacePicService isupDeviceService;

    /**
     * 异步导入人脸数据
     * 图片大小要求在200kb以下的jpg格式文件
     */
    @PostMapping("/import")
    public AjaxResult importData(
            @RequestParam String deviceId,
            @RequestParam String xmlUrl) {
        log.debug("导入人脸数据 - deviceId: {}, xmlUrl: {}", deviceId, xmlUrl);
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return AjaxResult.error("设备不存在");
        }
        IsupDevInfo device = deviceOpt.get();
        Integer loginId = deviceOpt.get().getLoginId();
        if (loginId == null) {
            return AjaxResult.error("设备未登录，无法导入数据");
        }
        return AjaxResult.success(isapiService.asyncImportDatas(loginId, xmlUrl));
    }

    @PostMapping("/upload")
    public AjaxResult upload(
            @RequestParam String path) {
        String filePath;
        if (StringUtils.isNotEmpty(path)) {
            filePath = RuoYiConfig.getProfile() + "/" + path;
        } else {
            filePath = RuoYiConfig.getUploadPath();
        }
        return AjaxResult.success(isupDeviceService.ssUploadPic(filePath));
    }

    @PostMapping("/test")
    public AjaxResult test(
            @RequestParam String deviceId,
            @RequestParam String command) {
        log.debug("导入人脸数据 - deviceId: {}, command: {}", deviceId, command);
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return AjaxResult.error("设备不存在");
        }
        IsupDevInfo device = deviceOpt.get();
        Integer loginId = device.getLoginId();
        if (loginId == null) {
            return AjaxResult.error("设备未登录，无法导入数据");
        }
        try {
            int channelId = 1;
            String FDID = "1";
            String FDType = "1";
            String picURL = "http://192.168.0.104:6120/kms/services/rest/dataInfoService/downloadFile?id=47445D2490A6029EBEF93DB4CDFF9BCC";
            String picturelD = "7";
            if (command.startsWith("s")) {
                switch (command) {
                    case "s1":
                        isapiService.getFDcapabilities(loginId);
                        break;
                    case "s2":
                        return AjaxResult.success(isapiService.getFDlib(loginId));
                    case "s3":
                        return AjaxResult.success(isapiService.addFDlib(loginId));
                    case "s4":
                        isapiService.FDSearch(loginId, FDID);
                        break;
                    case "s5":
                        isapiService.getPersonlnfoExtend(loginId);
                        break;
                    case "s6":
                        isapiService.getPsurplusCapacity(loginId, FDID, null);
                        break;
                    case "s7":
                        isapiService.getFaceSchedules(loginId, channelId, FDID, null);
                        break;
                    case "s8":
                        isapiService.getFaceTriggers(loginId, channelId, FDID, null);
                        break;
                    case "s9":
                        isapiService.getFaceContrast(loginId, channelId);
                        break;
                    case "s10":
                        return AjaxResult.success(isapiService.uploadPicture(loginId, FDID, picURL));
                    case "s11":
                        isapiService.delFDlibPicture(loginId, FDID, picturelD, null);
                        break;
                    case "s12":
                        isapiService.putPersonlnfoExtend(loginId);
                        break;
                }
            } else {
                AcsFunctiondispatch(command, loginId);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }


    public static void AcsFunctiondispatch(String command, int lLoginID) throws UnsupportedEncodingException {
        switch (command) {
            case "10001": {
                System.out.println("\n[Function]下发人员工号信息");
                AcsUserManagement.addEmployeeInfo(lLoginID);
                break;
            }
            case "10002": {
                System.out.println("\n[Function]删除人员工号信息");
                AcsUserManagement.deleteEmployeeInfo(lLoginID);
                break;
            }
            case "10003": {
                System.out.println("\n[Function]查询人员工号信息");
                AcsUserManagement.searchEmployeeInfo(lLoginID);
                break;
            }
            case "10004": {
                System.out.println("\n[Function]查询卡号信息");
                AcsCardManagement.searchCardInfo(lLoginID, "test001");
                break;
            }
            case "10011": {
                System.out.println("\n[Function]下发人脸图片数据");
                AcsFaceManagement.addFacePicInfo(lLoginID, "test001", "http://10.19.37.220:6011/pic?AE96D0B5814A0713D2B1C20291CFE6ACFDLib.jpg");
                break;
            }
            case "10012": {
                System.out.println("\n[Function]修改人脸图片数据");
                AcsFaceManagement.modifyFacePicInfo(lLoginID, "test001", "http://10.19.37.220:6011/pic?3505F0234A52A3B9E803A096EFC1F4FDFDLibNew.jpg");
                break;
            }
            case "10013": {
                System.out.println("\n[Function]删除人脸图片数据");
                AcsFaceManagement.deleteFacePicInfo(lLoginID, "test001");
                break;
            }
            case "10014": {
                System.out.println("\n[Function]查询人脸图片数据");
                AcsFaceManagement.searchFacePicInfo(lLoginID, "test001");
                break;
            }
            case "10021": {
                System.out.println("\n[Function]获取门禁参数");
                AcsParamCfg.getAcsCfg(lLoginID);
                break;
            }
            case "10022": {
                System.out.println("\n[Function]设置门禁参数");
                AcsParamCfg.setAcsCfg(lLoginID);
                break;
            }
            case "10023": {
                System.out.println("\n[Function]获取门禁主机状态");
                AcsParamCfg.getAcsStatus(lLoginID);
                break;
            }
            case "10024": {
                System.out.println("\n[Function]远程控门");
                AcsParamCfg.remoteControDoor(lLoginID);
                break;
            }
            case "10025": {
                System.out.println("\n[Function]查询门禁历史事件");
                AcsSearchEvent.searchAcsEventInfo(lLoginID);
                break;
            }
            case "10026": {
                System.out.println("\n[Function]查询门禁历史事件总条数");
                AcsSearchEvent.SearchAcsEventTotalNum(lLoginID);
                break;
            }
            case "10031":
                System.out.println("\n[Function]下发门禁指纹数据");
                AcsFingerPrintManagement.addFingerPrintInfo(lLoginID, "test001");
                break;
            case "10032": {
                System.out.println("\n[Function]查询门禁指纹数据");
                AcsFingerPrintManagement.searchFingerPrintInfo(lLoginID, "test001");
                break;
            }
            case "10033": {
                System.out.println("\n[Function]删除门禁指纹数据");
                AcsFingerPrintManagement.deleteFingerPrintInfo(lLoginID, "test001");
                break;
            }
            case "10034": {
                System.out.println("\n[Function]获取人员计划模板参数");
                AcsUserRightPlanTemplate.getUserRightPlanTemplateParm(lLoginID, 1);
                break;
            }
            case "10035": {
                System.out.println("\n[Function]获取人员周计划模板参数");
                AcsUserRightPlanTemplate.getUserRightWeekPlanCfg(lLoginID, 2);
                break;
            }
            case "10036": {
                System.out.println("\n[Function]设置人员计划周模板参数");
                AcsUserRightPlanTemplate.setUserRightWeekPlanCfg(lLoginID, 2);
                break;
            }
            case "10037": {
                System.out.println("\n[Function]设置人员计划模板参数");
                AcsUserRightPlanTemplate.setUserRightPlanTemplateParm(lLoginID, 2);
                break;
            }
        }
    }
}
