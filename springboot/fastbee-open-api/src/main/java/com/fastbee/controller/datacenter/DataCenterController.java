package com.fastbee.controller.datacenter;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.model.DeviceHistoryParam;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryResultVO;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.service.DataCenterService;

/**
 * @author fastb
 * @version 1.0
 * @description: 数据中心控制器
 * @date 2024-06-13 14:09
 */
@Api(tags = "数据中心管理")
@RestController
@RequestMapping("/data/center")
public class DataCenterController {

    @Resource
    private DataCenterService dataCenterService;

    /**
     * 查询设备物模型的历史数据
     * @param deviceHistoryParam 传参
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("查询设备的历史数据")
    @PreAuthorize("@ss.hasPermi('dataCenter:history:list')")
    @PostMapping("/deviceHistory")
    public AjaxResult deviceHistory(@RequestBody DeviceHistoryParam deviceHistoryParam)
    {
        if (StringUtils.isEmpty(deviceHistoryParam.getSerialNumber())) {
            return AjaxResult.error(MessageUtils.message("please.select.device"));
        }
        List<JSONObject> jsonObject = dataCenterService.deviceHistory(deviceHistoryParam);
        return AjaxResult.success(jsonObject);
    }

    @ApiOperation("查询场景变量历史数据")
    @PreAuthorize("@ss.hasPermi('dataCenter:history:list')")
    @GetMapping("/sceneHistory")
    public AjaxResult sceneHistory(SceneHistoryParam sceneHistoryParam)
    {
        List<JSONObject> jsonObject = dataCenterService.sceneHistory(sceneHistoryParam);
        return AjaxResult.success(jsonObject);
    }

    /**
     * 统计告警处理信息
     * @param dataCenterParam 传参
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("统计告警处理信息")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @GetMapping("/countAlertProcess")
    public AjaxResult countAlertProcess(DataCenterParam dataCenterParam)
    {
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        dataCenterParam.setTenantId(deptUserId);
        List<AlertCountVO> alertCountVO = dataCenterService.countAlertProcess(dataCenterParam);
        return AjaxResult.success(alertCountVO);
    }

    /**
     * 统计告警级别信息
     * @param dataCenterParam 传参
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("统计告警级别信息")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @GetMapping("/countAlertLevel")
    public AjaxResult countAlertLevel(DataCenterParam dataCenterParam)
    {
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        dataCenterParam.setTenantId(deptUserId);
        List<AlertCountVO> alertCountVO = dataCenterService.countAlertLevel(dataCenterParam);
        return AjaxResult.success(alertCountVO);
    }

    /**
     * 统计设备物模型指令下发数量
     * @param dataCenterParam 传参
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    @ApiOperation("统计设备物模型指令下发数量")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @GetMapping("/countThingsModelInvoke")
    public AjaxResult countThingsModelInvoke(DataCenterParam dataCenterParam)
    {
        if (StringUtils.isEmpty(dataCenterParam.getSerialNumber())) {
            return AjaxResult.error(MessageUtils.message("please.incoming.serialNumber"));
        }
        List<ThingsModelLogCountVO> list = dataCenterService.countThingsModelInvoke(dataCenterParam);
        return AjaxResult.success(list);
    }

    /**
     * 导出设备模型历史数据
     */
    @ApiOperation("导出设备模型历史数据")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @PostMapping("/deviceExport")
    public void deviceExport(HttpServletResponse response, DeviceHistoryParam deviceHistoryParam) {
        try {
            // 1. 解析参数
            List<DeviceHistoryParam.IdentifierVO> identifierVOList = JSON.parseArray(deviceHistoryParam.getIdentifierStr(), DeviceHistoryParam.IdentifierVO.class);
            deviceHistoryParam.setIdentifierList(identifierVOList);

            // 2. 获取数据（这里需要根据实际情况调整获取数据的方式）
            List<HistoryModel> dataList = dataCenterService.getDeviceHistoryDataForExport(deviceHistoryParam);

            // 5. 直接导出Excel
            dataCenterService.exportHistoryDataReport(response, dataList);

        } catch (Exception e) {
            // 异常处理
//            log.error("导出Excel失败", e);
            try {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                Map<String, String> map = new HashMap<>();
                map.put("status", "failure");
                map.put("message", "下载文件失败" + e.getMessage());
                response.getWriter().println(JSON.toJSONString(map));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
//                log.error("导出异常处理失败", ex);
            }
        }
    }

    /**
     * 导出场景变量历史数据
     */
    @ApiOperation("导出场景变量历史数据")
    @PreAuthorize("@ss.hasPermi('dataCenter:analysis:list')")
    @PostMapping("/sceneExport")
    public void export(HttpServletResponse response, SceneHistoryParam sceneHistoryParam) {
        try {

            // 2. 获取数据（这里需要根据实际情况调整获取数据的方式）
            SceneHistoryResultVO sceneHistoryResultVO = dataCenterService.querySceneHistory(sceneHistoryParam);

            // 5. 直接导出Excel
            dataCenterService.exportHistoryDataReport(response, sceneHistoryResultVO.getHistoryModelList());

        } catch (Exception e) {
            // 异常处理
//            log.error("导出Excel失败", e);
            try {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                Map<String, String> map = new HashMap<>();
                map.put("status", "failure");
                map.put("message", "下载文件失败" + e.getMessage());
                response.getWriter().println(JSON.toJSONString(map));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
//                log.error("导出异常处理失败", ex);
            }
        }
    }

}
