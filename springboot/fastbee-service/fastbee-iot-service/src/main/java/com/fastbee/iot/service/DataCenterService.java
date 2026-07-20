package com.fastbee.iot.service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;

import com.fastbee.iot.model.DeviceHistoryParam;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryResultVO;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.DataCenterExportVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;

/**
 * 数据中心服务类
 * @author fastb
 * @date 2024-06-13 15:29
 * @version 1.0
 */
public interface DataCenterService {

    /**
     * 查询设备物模型的历史数据
     * @param deviceHistoryParam 传参
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    List<JSONObject> deviceHistory(DeviceHistoryParam deviceHistoryParam);

    /**
     * 查询场景变量的历史数据
     * @return java.util.List<com.alibaba.fastjson2.JSONObject>
     */
    List<JSONObject> sceneHistory(SceneHistoryParam sceneHistoryParam);

    /**
     * 统计告警处理信息
     * @param dataCenterParam 传参
     * @return com.fastbee.iot.model.vo.AlertCountVO
     */
    List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam);

    /**
     * 统计告警级别信息
     * @param dataCenterParam 传参
     * @return com.fastbee.iot.model.vo.AlertCountVO
     */
    List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam);

    /**
     * 统计设备物模型指令下发数量
     * @param dataCenterParam 传参
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam);

    List<DataCenterExportVO> deviceExport(DeviceHistoryParam deviceHistoryParam);

    List<DataCenterExportVO> sceneExport(SceneHistoryParam sceneHistoryParam);

    List<HistoryModel> queryDeviceHistory(DeviceHistoryParam deviceHistoryParam);

    SceneHistoryResultVO querySceneHistory(SceneHistoryParam sceneHistoryParam);

    List<HistoryModel> getDeviceHistoryDataForExport(DeviceHistoryParam deviceHistoryParam);

    void exportHistoryDataReport(HttpServletResponse response, List<HistoryModel> dataList) throws IOException;
}
