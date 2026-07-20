package com.fastbee.iot.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.domain.SceneModelData;
import com.fastbee.iot.domain.ThingsModel;
import com.fastbee.iot.model.DeviceHistoryParam;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryResultVO;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.DataCenterExportVO;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.service.*;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * @author fastb
 * @version 1.0
 * @description: 数据中心服务类
 * @date 2024-06-13 15:29
 */
@Service
public class DataCenterServiceImpl implements DataCenterService {

    @Resource
    private IDeviceLogService deviceLogService;
    @Resource
    private IFunctionLogService functionLogService;
    @Resource
    private ILogService logService;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private ISceneModelDataService sceneModelDataService;
    @Resource
    private IThingsModelService thingsModelService;
    @Resource
    private IAlertLogService alertLogService;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private IDeviceCache deviceCache;

    @Override
    public List<JSONObject> deviceHistory(DeviceHistoryParam deviceHistoryParam) {
        List<JSONObject> resultList = new ArrayList<>();
        List<DeviceHistoryParam.IdentifierVO> identifierVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(deviceHistoryParam.getIdentifierList())) {
            List<ThingsModelDTO> list = deviceService.listThingsModel(deviceHistoryParam.getDeviceId());
            for (ThingsModelDTO thingsModelDTO : list) {
                DeviceHistoryParam.IdentifierVO identifierVO = new DeviceHistoryParam.IdentifierVO();
                identifierVO.setIdentifier(thingsModelDTO.getIdentifier());
                identifierVO.setType(thingsModelDTO.getType());
                identifierVOList.add(identifierVO);
            }
        } else {
            identifierVOList = deviceHistoryParam.getIdentifierList();
        }
        if (CollectionUtils.isEmpty(identifierVOList)) {
            return resultList;
        }
        List<String> identifierList = identifierVOList.stream().map(DeviceHistoryParam.IdentifierVO::getIdentifier).collect(Collectors.toList());
        deviceHistoryParam.setIdentifierList(identifierVOList);
        List<HistoryModel> historyModelList = this.queryDeviceHistory(deviceHistoryParam);
        historyModelList.sort(Comparator.comparing(HistoryModel::getTime));
        return this.handleData(identifierList, historyModelList);
    }

    @Override
    public List<HistoryModel> queryDeviceHistory(DeviceHistoryParam deviceHistoryParam) {
        List<HistoryModel> historyModelList = new ArrayList<>();
        List<DeviceHistoryParam.IdentifierVO> identifierVOList = deviceHistoryParam.getIdentifierList();
        if (identifierVOList == null) {
            return historyModelList;
        }
        List<String> propertyIdentifierList = identifierVOList.stream().filter(t -> 1 == t.getType()).map(DeviceHistoryParam.IdentifierVO::getIdentifier).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(propertyIdentifierList)) {
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setIdentityList(propertyIdentifierList);
            deviceLog.setSerialNumber(deviceHistoryParam.getSerialNumber());
            deviceLog.setBeginTime(deviceHistoryParam.getBeginTime());
            deviceLog.setEndTime(deviceHistoryParam.getEndTime());
            List<HistoryModel> historyModelList1 = deviceLogService.listHistory(deviceLog);
            historyModelList.addAll(historyModelList1);
        }
        List<String> functionIdentifierList = identifierVOList.stream().filter(t -> 2 == t.getType()).map(DeviceHistoryParam.IdentifierVO::getIdentifier).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(functionIdentifierList)) {
            FunctionLogVO functionLogVO = new FunctionLogVO();
            functionLogVO.setIdentifyList(functionIdentifierList);
            functionLogVO.setSerialNumber(deviceHistoryParam.getSerialNumber());
            functionLogVO.setBeginTime(DateUtils.dateTime(DateUtils.YY_MM_DD_HH_MM_SS, deviceHistoryParam.getBeginTime()));
            functionLogVO.setEndTime(DateUtils.dateTime(DateUtils.YY_MM_DD_HH_MM_SS, deviceHistoryParam.getEndTime()));
            historyModelList.addAll(functionLogService.listHistory(functionLogVO));
        }
        List<String> eventIdentifierList = identifierVOList.stream().filter(t -> 3 == t.getType()).map(DeviceHistoryParam.IdentifierVO::getIdentifier).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(eventIdentifierList)) {
            DeviceLog eventLog = new DeviceLog();
            eventLog.setIdentityList(eventIdentifierList);
            eventLog.setSerialNumber(deviceHistoryParam.getSerialNumber());
            eventLog.setBeginTime(deviceHistoryParam.getBeginTime());
            eventLog.setEndTime(deviceHistoryParam.getEndTime());
            historyModelList.addAll(logService.listHistory(eventLog));
        }
        return historyModelList;
    }

    private List<JSONObject> handleData(List<String> identifierList, List<HistoryModel> historyModelList) {
        List<JSONObject> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(historyModelList)) {
            return resultList;
        }
        LinkedHashMap<Date, List<HistoryModel>> map = historyModelList.stream()
                .collect(Collectors.groupingBy(HistoryModel::getTime, LinkedHashMap::new, Collectors.toList()));
//        Map<String, HistoryModel> oldHistoryModelMap = new HashMap<>(2);
        for (Map.Entry<Date, List<HistoryModel>> entry : map.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            List<HistoryModel> value = entry.getValue();
            Map<String, HistoryModel> historyModelMap = value.stream().collect(Collectors.toMap(HistoryModel::getIdentify, Function.identity(), (o, n) -> n));
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for (String identifier : identifierList) {
                JSONObject jsonObject1 = new JSONObject();
                HistoryModel historyModel = historyModelMap.get(identifier);
                if (null != historyModel) {
//                    oldHistoryModelMap.put(identifier, historyModel);
                    jsonObject1.put(historyModel.getIdentify(), historyModel.getValue());
                } else {
//                    HistoryModel oldHistoryModel = oldHistoryModelMap.get(identifier);
//                    if (null != oldHistoryModel) {
//                        jsonObject1.put(identifier, oldHistoryModel.getValue());
//                    } else {
//                        jsonObject1.put(identifier, null);
//                    }
                    jsonObject1.put(identifier, null);
                }
                jsonObjectList.add(jsonObject1);
            }
            jsonObject.put(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, entry.getKey()), jsonObjectList);
            resultList.add(jsonObject);
        }
        return resultList;
    }

    @Override
    public List<JSONObject> sceneHistory(SceneHistoryParam sceneHistoryParam) {
        SceneHistoryResultVO sceneHistoryResultVO = this.querySceneHistory(sceneHistoryParam);
        if (Objects.isNull(sceneHistoryResultVO) || CollectionUtils.isEmpty(sceneHistoryResultVO.getHistoryModelList())) {
            return new ArrayList<>();
        }
        return this.handleData(sceneHistoryResultVO.getIdentifierList(), sceneHistoryResultVO.getHistoryModelList());
    }

    @Override
    public SceneHistoryResultVO querySceneHistory(SceneHistoryParam sceneHistoryParam) {
        SceneHistoryResultVO sceneHistoryResultVO = new SceneHistoryResultVO();
        sceneHistoryResultVO.setHistoryModelList(new  ArrayList<>());
        sceneHistoryResultVO.setIdentifierList(new ArrayList<>());
        if (SceneModelVariableTypeEnum.INPUT_VARIABLE.getType().equals(sceneHistoryParam.getVariableType())) {
            return sceneHistoryResultVO;
        }
        List<String> sceneModelDataIdList = StringUtils.str2List(sceneHistoryParam.getIds(), ",", true, true);
        List<SceneModelData> sceneModelDataList = sceneModelDataService.selectSceneModelDataListByIds(sceneModelDataIdList.stream().map(Long::parseLong).collect(Collectors.toList()));
        List<Long> datasourceIdList = sceneModelDataList.stream().map(SceneModelData::getDatasourceId).distinct().collect(Collectors.toList());
        if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(sceneHistoryParam.getVariableType())) {
            List<ThingsModel> thingsModelList = thingsModelService.selectThingsModelListByModelIds(datasourceIdList);
            Map<Long, Integer> thingsModelMap = thingsModelList.stream().collect(Collectors.toMap(ThingsModel::getModelId, ThingsModel::getType));
            DeviceHistoryParam deviceHistoryParam = new DeviceHistoryParam();
            List<DeviceHistoryParam.IdentifierVO> identifierVOList = new ArrayList<>();
            for (SceneModelData sceneModelData : sceneModelDataList) {
                Integer type = thingsModelMap.get(sceneModelData.getDatasourceId());
                if (null != type) {
                    DeviceHistoryParam.IdentifierVO identifierVO = new DeviceHistoryParam.IdentifierVO();
                    identifierVO.setIdentifier(sceneModelData.getIdentifier());
                    identifierVO.setType(type);
                    identifierVOList.add(identifierVO);
                }
            }
            deviceHistoryParam.setSerialNumber(sceneHistoryParam.getSerialNumber());
            deviceHistoryParam.setIdentifierList(identifierVOList);
            deviceHistoryParam.setBeginTime(sceneHistoryParam.getBeginTime());
            deviceHistoryParam.setEndTime(sceneHistoryParam.getEndTime());
            List<HistoryModel> historyModelList = this.queryDeviceHistory(deviceHistoryParam);
            sceneHistoryResultVO.setHistoryModelList(historyModelList);
            List<String> list = sceneModelDataList.stream().map(SceneModelData::getIdentifier).collect(Collectors.toList());
            sceneHistoryResultVO.setIdentifierList(list);
        } else {
            List<String> identifierList = datasourceIdList.stream().map(x -> x + "").collect(Collectors.toList());
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setIdentityList(identifierList);
            byte logType = 7;
            deviceLog.setLogType(logType);
            deviceLog.setBeginTime(sceneHistoryParam.getBeginTime());
            deviceLog.setEndTime(sceneHistoryParam.getEndTime());
            List<HistoryModel> historyModelList = deviceLogService.listHistory(deviceLog);
            if (CollectionUtils.isNotEmpty(historyModelList)) {
                sceneHistoryResultVO.setHistoryModelList(historyModelList);
            }
            sceneHistoryResultVO.setIdentifierList(identifierList);
        }
        if (CollectionUtils.isNotEmpty(sceneHistoryResultVO.getHistoryModelList())) {
            Map<String, String> nameMap = sceneModelDataList.stream().collect(Collectors.toMap(SceneModelData::getIdentifier, SceneModelData::getSourceName));
            sceneHistoryResultVO.getHistoryModelList().forEach(x -> x.setModerName(nameMap.get(x.getIdentify())));
        }
        return sceneHistoryResultVO;
    }

    @Override
    public List<HistoryModel> getDeviceHistoryDataForExport(DeviceHistoryParam deviceHistoryParam) {
        List<HistoryModel> resultList = new ArrayList<>();
        List<DeviceHistoryParam.IdentifierVO> identifierVOList = new ArrayList<>();
        DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(deviceHistoryParam.getSerialNumber());
        if (Objects.isNull(deviceMetaData)) {
            return new ArrayList<>();
        }
        List<ThingsModelValueItem> thingsModelList = itslCache.getThingsModelList(deviceMetaData.getDevice().getProductId());
        if (CollectionUtils.isEmpty(deviceHistoryParam.getIdentifierList())) {
            for (ThingsModelValueItem thingsModelValueItem : thingsModelList) {
                DeviceHistoryParam.IdentifierVO identifierVO = new DeviceHistoryParam.IdentifierVO();
                identifierVO.setIdentifier(thingsModelValueItem.getId());
                identifierVO.setType(thingsModelValueItem.getType());
                identifierVOList.add(identifierVO);
            }
        } else {
            identifierVOList = deviceHistoryParam.getIdentifierList();
        }
        if (CollectionUtils.isEmpty(identifierVOList)) {
            return resultList;
        }
        Map<String, String> identifierNameMap = thingsModelList.stream().collect(Collectors.toMap(ThingsModelValueItem::getId, ThingsModelValueItem::getName));
        deviceHistoryParam.setIdentifierList(identifierVOList);
        List<HistoryModel> historyModelList = this.queryDeviceHistory(deviceHistoryParam);
        for (HistoryModel historyModel : historyModelList) {
            String name = identifierNameMap.get(historyModel.getIdentify());
            if (StringUtils.isNotEmpty(name)) {
                historyModel.setModerName(name);
            }
        }
        historyModelList.sort(Comparator.comparing(HistoryModel::getTime));
        return historyModelList;
    }

    @Override
    public void exportHistoryDataReport(HttpServletResponse response, List<HistoryModel> dataList) throws IOException {
        // 设置响应头
        setupResponseHeader(response);

        // 1. 数据预处理 - 使用identify作为唯一标识
        Map<String, String> identifyToModelNameMap = dataList.stream()
                .collect(Collectors.toMap(
                        HistoryModel::getIdentify,
                        HistoryModel::getModerName,
                        (existing, replacement) -> existing
                ));

        // 获取所有时间点并排序
        List<Date> sortedTimes = dataList.stream()
                .map(HistoryModel::getTime)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 2. 获取所有唯一的identify并排序
        List<String> identifiers = new ArrayList<>(identifyToModelNameMap.keySet());
        Collections.sort(identifiers);

        // 3. 构建动态表头
        List<List<String>> head = buildTableHeader(identifiers, identifyToModelNameMap);

        // 4. 构建数据行
        List<List<Object>> excelData = buildExcelData(sortedTimes, dataList, head, identifiers);

        // 5. 设置样式
        HorizontalCellStyleStrategy styleStrategy = createCellStyleStrategy();

        // 6. 直接写入响应流
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("历史数据报表")
                    .head(head)
                    .registerWriteHandler(styleStrategy)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();

            excelWriter.write(excelData, writeSheet);
        }
    }

    /**
     * 设置响应头
     */
    private void setupResponseHeader(HttpServletResponse response) {
        String fileName = "历史数据报表_" + System.currentTimeMillis() + ".xlsx";

        // 设置响应类型
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        // 设置文件名，处理中文乱码
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            // 如果编码失败，使用原文件名
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
    }

    /**
     * 构建表头 - 使用identify和moderName
     */
    private List<List<String>> buildTableHeader(List<String> identifiers,
                                                Map<String, String> identifyToModelNameMap) {
        List<List<String>> head = new ArrayList<>();

        // 时间列
        head.add(Collections.singletonList("更新时间"));

        // 为每个identify创建列头，显示对应的moderName
        for (String identify : identifiers) {
            String modelName = identifyToModelNameMap.get(identify);
            head.add(Collections.singletonList(modelName));
        }

        return head;
    }

    /**
     * 构建Excel数据
     */
    private List<List<Object>> buildExcelData(List<Date> sortedTimes,
                                              List<HistoryModel> dataList,
                                              List<List<String>> head,
                                              List<String> identifiers) {
        List<List<Object>> excelData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 构建列索引映射 (identify -> 列索引)
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (int i = 0; i < identifiers.size(); i++) {
            columnIndexMap.put(identifiers.get(i), i + 1); // +1 因为第一列是时间
        }

        for (Date time : sortedTimes) {
            List<Object> rowData = new ArrayList<>();

            // 添加时间列
            rowData.add(sdf.format(time));

            // 初始化所有列为空值
            for (int i = 1; i < head.size(); i++) {
                rowData.add("");
            }

            // 填充实际数据
            for (HistoryModel model : dataList) {
                if (model.getTime().equals(time)) {
                    Integer index = columnIndexMap.get(model.getIdentify());
                    if (index != null && model.getValue() != null) {
                        rowData.set(index, model.getValue());
                    }
                }
            }

            excelData.add(rowData);
        }

        return excelData;
    }

    /**
     * 创建单元格样式策略
     */
    private HorizontalCellStyleStrategy createCellStyleStrategy() {
        // 表头样式
        WriteCellStyle headerStyle = new WriteCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

        WriteFont headerFont = new WriteFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setWriteFont(headerFont);

        // 内容样式
        WriteCellStyle contentStyle = new WriteCellStyle();
        contentStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);
        contentStyle.setBorderTop(BorderStyle.THIN);

        return new HorizontalCellStyleStrategy(headerStyle, contentStyle);
    }

    @Override
    public List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam) {
        return alertLogService.countAlertProcess(dataCenterParam);
    }

    @Override
    public List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam) {
        return alertLogService.countAlertLevel(dataCenterParam);
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        List<ThingsModelLogCountVO> resultList = new ArrayList<>();
        resultList.addAll(deviceLogService.countThingsModelInvoke(dataCenterParam));
        resultList.addAll(functionLogService.countThingsModelInvoke(dataCenterParam));
        return resultList;
    }

    @Override
    public List<DataCenterExportVO> deviceExport(DeviceHistoryParam deviceHistoryParam) {
        List<JSONObject> list = this.deviceHistory(deviceHistoryParam);
        return this.changeDataCenterExportVO(list);
    }

    @Override
    public List<DataCenterExportVO> sceneExport(SceneHistoryParam sceneHistoryParam) {
        List<JSONObject> list = this.sceneHistory(sceneHistoryParam);
        return this.changeDataCenterExportVO(list);
    }

    private List<DataCenterExportVO> changeDataCenterExportVO(List<JSONObject> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<DataCenterExportVO> resultList = new ArrayList<>();
        for (JSONObject jsonObject : list) {
            DataCenterExportVO dataCenterExportVO = new DataCenterExportVO();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                dataCenterExportVO.setTs(entry.getKey());
                dataCenterExportVO.setIdentifyJson(entry.getValue().toString());
            }
            resultList.add(dataCenterExportVO);
        }
        return resultList;
    }
}
