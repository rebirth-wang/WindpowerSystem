package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.quartz.SchedulerException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.job.TaskException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.notify.ReportNotifyParams;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.extend.enums.report.DataDimensionEnum;
import com.fastbee.common.extend.enums.report.ReportDataTypeEnum;
import com.fastbee.common.extend.enums.report.ReportRuleDataOperationEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.date.LocalDateTimeUtils;
import com.fastbee.iot.convert.ReportConvert;
import com.fastbee.iot.convert.ReportRuleConvert;
import com.fastbee.iot.convert.ReportRuleDataConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.DeviceHistoryParam;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.scenemodel.JobCronCycleVO;
import com.fastbee.iot.model.scenemodel.SceneHistoryParam;
import com.fastbee.iot.model.scenemodel.SceneHistoryResultVO;
import com.fastbee.iot.model.vo.*;
import com.fastbee.iot.service.DataCenterService;
import com.fastbee.iot.service.IDeviceJobService;
import com.fastbee.iot.service.IReportService;
import com.fastbee.iot.util.JobCronUtils;
import com.fastbee.quartz.util.CronUtils;
import com.fastbee.system.mapper.SysUserMapper;

/**
 * 报表管理Service业务层处理
 *
 * @author zzy
 * @date 2025-07-09
 */
@Slf4j
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper,Report> implements IReportService {

    @Resource
    private ReportRuleMapper reportRuleMapper;
    @Resource
    private ReportRuleDataMapper reportRuleDataMapper;
    @Resource
    private IDeviceJobService deviceJobService;
    @Resource
    private ReportRecordsMapper reportRecordsMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ThingsModelMapper thingsModelMapper;
    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Resource
    private DataCenterService dataCenterService;
    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 查询报表管理
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param param 报表管理
     * @return 报表管理
     */
    @Override
    @DataScope
//    @Cacheable(cacheNames = "Report", key = "#id")
    public ReportVO queryByIdWithCache(Report param){
        LambdaQueryWrapper<Report> queryWrapper = this.buildQueryWrapper(param);
        Report report = this.getOne(queryWrapper);
        if (Objects.isNull(report)) {
            return null;
        }
        return this.changeReportVO(report);
    }

    private ReportVO changeReportVO(Report report) {
        ReportVO reportVO = ReportConvert.INSTANCE.convertReportVO(report);
        LambdaQueryWrapper<ReportRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportRule::getReportId, report.getId());
        List<ReportRule> reportRuleList = reportRuleMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(reportRuleList)) {
            return reportVO;
        }
        reportVO.setReportRuleVOList(ReportRuleConvert.INSTANCE.convertReportRuleVOList(reportRuleList));
        LambdaQueryWrapper<ReportRuleData> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ReportRuleData::getReportId, report.getId());
        List<Long> reportRuleIdList = reportRuleList.stream().map(ReportRule::getId).collect(Collectors.toList());
        queryWrapper1.in(ReportRuleData::getReportRuleId, reportRuleIdList);
        List<ReportRuleData> reportRuleDataList = reportRuleDataMapper.selectList(queryWrapper1);
        if (CollectionUtils.isNotEmpty(reportRuleDataList)) {
            List<ReportRuleDataVO> ruleDataVOList = ReportRuleDataConvert.INSTANCE.convertReportRuleDataVOList(reportRuleDataList);
            Map<Long, List<ReportRuleDataVO>> map = ruleDataVOList.stream().collect(Collectors.groupingBy(ReportRuleDataVO::getReportRuleId));
            for (ReportRuleVO reportRuleVO : reportVO.getReportRuleVOList()) {
                reportRuleVO.setReportRuleDataVOList(map.get(reportRuleVO.getId()));
            }
        }
        return reportVO;
    }

    /**
     * 查询报表管理
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表管理
     */
    @Override
    @Cacheable(cacheNames = "Report", key = "#id")
    public Report selectReportById(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表管理分页列表
     *
     * @param report 报表管理
     * @return 报表管理
     */
    @Override
    @DataScope
    public Page<ReportVO> pageReportVO(Report report) {
        LambdaQueryWrapper<Report> lqw = buildQueryWrapper(report);
        lqw.orderByDesc(Report::getCreateTime);
        Page<Report> reportPage = baseMapper.selectPage(new Page<>(report.getPageNum(), report.getPageSize()), lqw);
        return ReportConvert.INSTANCE.convertReportVOPage(reportPage);
    }

    /**
     * 查询报表管理列表
     *
     * @param report 报表管理
     * @return 报表管理
     */
    @Override
    public List<ReportVO> listReportVO(Report report) {
        LambdaQueryWrapper<Report> lqw = buildQueryWrapper(report);
        List<Report> reportList = baseMapper.selectList(lqw);
        return ReportConvert.INSTANCE.convertReportVOList(reportList);
    }

    private LambdaQueryWrapper<Report> buildQueryWrapper(Report query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Report> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, Report::getId, query.getId());
                    lqw.like(StringUtils.isNotBlank(query.getName()), Report::getName, query.getName());
                    lqw.eq(query.getDataType() != null, Report::getDataType, query.getDataType());
                    lqw.eq(query.getCycleType() != null, Report::getCycleType, query.getCycleType());
                    lqw.eq(StringUtils.isNotBlank(query.getCycle()), Report::getCycle, query.getCycle());
                    lqw.eq(query.getAggregateUnits() != null, Report::getAggregateUnits, query.getAggregateUnits());
                    lqw.eq(query.getDataDimension() != null, Report::getDataDimension, query.getDataDimension());
                    lqw.eq(query.getExportFormat() != null, Report::getExportFormat, query.getExportFormat());
                    lqw.eq(query.getTenantId() != null, Report::getTenantId, query.getTenantId());
                    lqw.like(StringUtils.isNotBlank(query.getTenantName()), Report::getTenantName, query.getTenantName());
                    lqw.eq(query.getDelFlag() != null, Report::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), Report::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, Report::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), Report::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, Report::getUpdateTime, query.getUpdateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), Report::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(Report::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增报表管理
     *
     * @param reportVO 报表管理
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ReportVO reportVO) {
        Report report = ReportConvert.INSTANCE.convertReport(reportVO);
        SysUser sysUser = getLoginUser().getUser();
        report.setTenantId(sysUser.getDept().getDeptUserId());
        report.setTenantName(sysUser.getDept().getDeptName());
        report.setCreateBy(sysUser.getUserName());
        boolean save = this.save(report);
        if (!save) {
            return false;
        }
        reportVO.setId(report.getId());
        this.handleAddRuleData(reportVO);
        // 处理定时任务
        JobCronCycleVO jobCronCycleVO = JobCronUtils.handleCronCycle(reportVO.getCycleType(), reportVO.getCycle());
        jobCronCycleVO.setStatus(1);
        this.createReportTask(report.getId(), jobCronCycleVO, report.getCreateBy());
        return true;
    }

    /**
     * 创建报表定时任务
     *
     * @param reportId 报表id
     * @param jobCronCycleVO cron
     */
    private void createReportTask(Long reportId, JobCronCycleVO jobCronCycleVO, String createBy) {
        // 创建定时任务
        try {
            if (!CronUtils.isValid(jobCronCycleVO.getCron())) {
                log.error("创建报表定时任务失败，Cron表达式不正确");
                throw new Exception("创建报表定时任务失败，Cron表达式不正确");
            }
            DeviceJob deviceJob = new DeviceJob();
            deviceJob.setJobName(DeviceJobTypeEnum.REPORT.getDesc());
            deviceJob.setJobType(DeviceJobTypeEnum.REPORT.getType());
            deviceJob.setJobGroup("REPORT");
            deviceJob.setConcurrent("1");
            deviceJob.setMisfirePolicy("2");
            deviceJob.setStatus(1 == jobCronCycleVO.getStatus() ? 0 : 1);
            deviceJob.setCronExpression(jobCronCycleVO.getCron());
            deviceJob.setIsAdvance(1);
            deviceJob.setDatasourceId(reportId);
            deviceJob.setCreateBy(createBy);
            deviceJobService.insertJob(deviceJob);
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAddRuleData(ReportVO reportVO) {
        List<ReportRuleVO> reportRuleVOList = reportVO.getReportRuleVOList();
        if (CollectionUtils.isNotEmpty(reportRuleVOList)) {
            for (ReportRuleVO reportRuleVO : reportRuleVOList) {
                ReportRule reportRule = ReportRuleConvert.INSTANCE.convertReportRule(reportRuleVO);
                reportRule.setReportId(reportVO.getId());
                reportRuleMapper.insert(reportRule);
                if (CollectionUtils.isNotEmpty(reportRuleVO.getReportRuleDataVOList())) {
                    List<ReportRuleData> reportRuleDataList = ReportRuleDataConvert.INSTANCE.convertReportRuleDataList(reportRuleVO.getReportRuleDataVOList());
                    for (ReportRuleData reportRuleData : reportRuleDataList) {
                        reportRuleData.setReportId(reportVO.getId());
                        reportRuleData.setReportRuleId(reportRule.getId());
                    }
                    reportRuleDataMapper.insertBatch(reportRuleDataList);
                }

            }
        }
    }

    /**
     * 修改报表管理
     *
     * @param reportVO 报表管理
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "Report", key = "#update.id")
    public Boolean updateWithCache(ReportVO reportVO) {
        Report report = ReportConvert.INSTANCE.convertReport(reportVO);
        boolean b = this.updateById(report);
        if (!b) {
            return false;
        }
        this.handleDeleteRuleData(reportVO.getId());
        this.handleAddRuleData(reportVO);
        // 删除并新增定时任务
        // 删除定时任务
        try {
            deviceJobService.deleteJobByJobTypeAndDatasourceIds(new Long[]{report.getId()}, DeviceJobTypeEnum.REPORT.getType());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        JobCronCycleVO jobCronCycleVO = JobCronUtils.handleCronCycle(report.getCycleType(), report.getCycle());
        jobCronCycleVO.setStatus(1);
        this.createReportTask(report.getId(), jobCronCycleVO, report.getCreateBy());
        return true;
    }

    private void handleDeleteRuleData(Long reportId) {
        LambdaQueryWrapper<ReportRule> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ReportRule::getReportId, reportId);
        reportRuleMapper.delete(queryWrapper1);
        LambdaQueryWrapper<ReportRuleData> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(ReportRuleData::getReportId, reportId);
        reportRuleDataMapper.delete(queryWrapper2);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Report entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除报表管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "Report", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        boolean b = this.removeByIds(Arrays.asList(ids));
        if (b) {
            LambdaQueryWrapper<ReportRecords> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ReportRecords::getReportId, Arrays.stream(ids).collect(Collectors.toList()));
            reportRecordsMapper.delete(queryWrapper);
            for (Long id : ids) {
                // 删除定时任务
                try {
                    deviceJobService.deleteJobByJobTypeAndDatasourceIds(new Long[]{id}, DeviceJobTypeEnum.REPORT.getType());
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }

    @Override
    public ReportNotifyParams executeJob(Long reportId) throws Exception {
        // 1. 获取报表信息
        Report report = this.getById(reportId);
        if (Objects.isNull(report)) {
            log.error("报表不存在，reportId={}", reportId);
            return null;
        }
        ReportVO reportVO = this.changeReportVO(report);
        if (CollectionUtils.isEmpty(reportVO.getReportRuleVOList())) {
            log.error("报表规则为空，reportId={}", reportId);
            return null;
        }

        // 2. 计算时间范围
        JobCronCycleVO jobCronCycleVO = JobCronUtils.handleTimeCycle(
                reportVO.getCycleType(), reportVO.getCycle(), LocalDateTime.now());
        String beginTime = LocalDateTimeUtils.localDateTimeToStr(
                jobCronCycleVO.getBeginTime(), LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS);
        String endTime = LocalDateTimeUtils.localDateTimeToStr(
                jobCronCycleVO.getEndTime(), LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS);

        // 3. 收集历史数据
        List<HistoryModel> resultList = collectHistoryData(reportVO, beginTime, endTime);

        // 4. 生成报表文件
        String fileUrl = generateReportFile(reportVO, resultList, beginTime, endTime);

        // 5. 保存报表记录
        ReportRecords reportRecords = new ReportRecords();
        reportRecords.setReportId(reportVO.getId());
        reportRecords.setReportFilePath(fileUrl);
        reportRecords.setTimeCycle(beginTime + "-" + endTime);
        reportRecordsMapper.insert(reportRecords);

        // 6. 构建通知参数
        return buildNotifyParams(reportVO, fileUrl);
    }

    /**
     * 收集历史数据（批量查询优化）
     */
    private List<HistoryModel> collectHistoryData(ReportVO reportVO, String beginTime, String endTime) {
        List<HistoryModel> resultList = new ArrayList<>();
        List<ReportRuleVO> reportRuleVOList = reportVO.getReportRuleVOList();
        Integer dataDimension = reportVO.getDataDimension();
        boolean isAggregateData = ReportDataTypeEnum.AGGREGATE_DATA.getType().equals(reportVO.getDataType());

        if (DataDimensionEnum.DEVICE.getType() == dataDimension) {
            collectDeviceData(resultList, reportRuleVOList, beginTime, endTime, isAggregateData);
        } else if (DataDimensionEnum.SCENE.getType() == dataDimension) {
            collectSceneData(resultList, reportRuleVOList, beginTime, endTime);
        }

        return resultList;
    }

    /**
     * 收集设备历史数据（批量查询优化）
     */
    private void collectDeviceData(List<HistoryModel> resultList, List<ReportRuleVO> reportRuleVOList,
                                   String beginTime, String endTime, boolean isAggregateData) {
        // 收集所有需要查询的设备ID
        List<Long> deviceIds = reportRuleVOList.stream()
                .map(ReportRuleVO::getCusSourceId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询设备信息
        Map<Long, Device> deviceMap = deviceMapper.selectBatchIds(deviceIds)
                .stream().collect(Collectors.toMap(Device::getDeviceId, Function.identity(), (k1, k2) -> k1));

        // 收集所有需要查询的物模型ID（按产品ID分组）
        Map<Long, List<Long>> productIdToModelIdsMap = new HashMap<>();
        for (ReportRuleVO reportRuleVO : reportRuleVOList) {
            Device device = deviceMap.get(reportRuleVO.getCusSourceId());
            if (Objects.isNull(device) || CollectionUtils.isEmpty(reportRuleVO.getReportRuleDataVOList())) {
                continue;
            }
            List<Long> dataIds = reportRuleVO.getReportRuleDataVOList().stream()
                    .map(ReportRuleDataVO::getCusDataId)
                    .collect(Collectors.toList());
            productIdToModelIdsMap.computeIfAbsent(device.getProductId(), k -> new ArrayList<>()).addAll(dataIds);
        }

        // 批量查询物模型信息
        Map<Long, Map<Long, ThingsModel>> productIdToThingsModelMap = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : productIdToModelIdsMap.entrySet()) {
            LambdaQueryWrapper<ThingsModel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ThingsModel::getModelId, entry.getValue());
            queryWrapper.eq(ThingsModel::getProductId, entry.getKey());
            List<ThingsModel> thingsModelList = thingsModelMapper.selectList(queryWrapper);
            Map<Long, ThingsModel> thingsModelMap = thingsModelList.stream()
                    .collect(Collectors.toMap(ThingsModel::getModelId, Function.identity(), (k1, k2) -> k1));
            productIdToThingsModelMap.put(entry.getKey(), thingsModelMap);
        }

        // 处理每个规则
        for (ReportRuleVO reportRuleVO : reportRuleVOList) {
            if (CollectionUtils.isEmpty(reportRuleVO.getReportRuleDataVOList())) {
                continue;
            }

            Device device = deviceMap.get(reportRuleVO.getCusSourceId());
            if (Objects.isNull(device)) {
                log.warn("设备不存在，deviceId={}", reportRuleVO.getCusSourceId());
                continue;
            }

            // 构建操作类型映射
            Map<Long, Integer> operationMap = new HashMap<>();
            if (isAggregateData) {
                operationMap = reportRuleVO.getReportRuleDataVOList().stream()
                        .collect(Collectors.toMap(ReportRuleDataVO::getCusDataId, ReportRuleDataVO::getOperation));
            }

            // 构建查询参数
            DeviceHistoryParam deviceHistoryParam = buildDeviceHistoryParam(
                    device, reportRuleVO, productIdToThingsModelMap, operationMap, beginTime, endTime);

            // 查询历史数据
            List<HistoryModel> historyModelList = dataCenterService.queryDeviceHistory(deviceHistoryParam);
            Map<Long, ThingsModel> thingsModelMap = productIdToThingsModelMap.getOrDefault(device.getProductId(), Collections.emptyMap());
            Map<String, ThingsModel> identifyMap = thingsModelMap.values().stream()
                    .collect(Collectors.toMap(ThingsModel::getIdentifier, Function.identity(), (k1, k2) -> k1));

            // 设置数据属性
            for (HistoryModel historyModel : historyModelList) {
                ThingsModel thingsModel = identifyMap.get(historyModel.getIdentify());
                if (Objects.nonNull(thingsModel)) {
                    historyModel.setModerName(thingsModel.getModelName());
                }
                historyModel.setDeviceName(device.getDeviceName());
                historyModel.setSceneModelDeviceId(reportRuleVO.getCusSourceId());
                historyModel.setOperation(buildOperationIdentifierMap(reportRuleVO, thingsModelMap)
                        .get(historyModel.getIdentify()));
                resultList.add(historyModel);
            }
        }
    }

    /**
     * 构建设备历史查询参数
     */
    private DeviceHistoryParam buildDeviceHistoryParam(Device device, ReportRuleVO reportRuleVO,
                                                       Map<Long, Map<Long, ThingsModel>> productIdToThingsModelMap,
                                                       Map<Long, Integer> operationMap,
                                                       String beginTime, String endTime) {
        DeviceHistoryParam deviceHistoryParam = new DeviceHistoryParam();
        deviceHistoryParam.setDeviceId(device.getDeviceId());
        deviceHistoryParam.setSerialNumber(device.getSerialNumber());
        deviceHistoryParam.setBeginTime(beginTime);
        deviceHistoryParam.setEndTime(endTime);

        List<DeviceHistoryParam.IdentifierVO> identifierVOList = new ArrayList<>();
        Map<Long, ThingsModel> thingsModelMap = productIdToThingsModelMap.getOrDefault(device.getProductId(), Collections.emptyMap());

        for (ReportRuleDataVO reportRuleDataVO : reportRuleVO.getReportRuleDataVOList()) {
            ThingsModel thingsModel = thingsModelMap.get(reportRuleDataVO.getCusDataId());
            if (Objects.isNull(thingsModel)) {
                continue;
            }
            DeviceHistoryParam.IdentifierVO identifierVO = new DeviceHistoryParam.IdentifierVO();
            identifierVO.setIdentifier(thingsModel.getIdentifier());
            identifierVO.setType(thingsModel.getType());
            identifierVOList.add(identifierVO);
        }

        deviceHistoryParam.setIdentifierList(identifierVOList);
        return deviceHistoryParam;
    }

    /**
     * 构建操作标识映射
     */
    private Map<String, Integer> buildOperationIdentifierMap(ReportRuleVO reportRuleVO,
                                                              Map<Long, ThingsModel> thingsModelMap) {
        Map<String, Integer> operationIdentifierMap = new HashMap<>();
        if (CollectionUtils.isEmpty(reportRuleVO.getReportRuleDataVOList())) {
            return operationIdentifierMap;
        }

        for (ReportRuleDataVO reportRuleDataVO : reportRuleVO.getReportRuleDataVOList()) {
            ThingsModel thingsModel = thingsModelMap.get(reportRuleDataVO.getCusDataId());
            if (Objects.nonNull(thingsModel) && Objects.nonNull(reportRuleDataVO.getOperation())) {
                operationIdentifierMap.put(thingsModel.getIdentifier(), reportRuleDataVO.getOperation());
            }
        }
        return operationIdentifierMap;
    }

    /**
     * 收集场景历史数据（批量查询优化）
     */
    private void collectSceneData(List<HistoryModel> resultList, List<ReportRuleVO> reportRuleVOList,
                                  String beginTime, String endTime) {
        // 收集所有需要查询的场景设备ID
        List<Long> sceneModelDeviceIds = reportRuleVOList.stream()
                .map(ReportRuleVO::getSceneModelDeviceId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询场景信息
        List<SceneHistoryParam> sceneHistoryParams = sceneModelDeviceMapper.selectReportRuleSceneBatch(sceneModelDeviceIds);
        Map<Long, SceneHistoryParam> sceneParamMap = sceneHistoryParams.stream()
                .collect(Collectors.toMap(SceneHistoryParam::getSceneModelDeviceId, Function.identity(), (k1, k2) -> k1));

        // 处理每个规则
        for (ReportRuleVO reportRuleVO : reportRuleVOList) {
            if (CollectionUtils.isEmpty(reportRuleVO.getReportRuleDataVOList())) {
                continue;
            }

            SceneHistoryParam sceneHistoryParam = sceneParamMap.get(reportRuleVO.getSceneModelDeviceId());
            if (Objects.isNull(sceneHistoryParam)) {
                log.warn("场景信息不存在，sceneModelDeviceId={}", reportRuleVO.getSceneModelDeviceId());
                continue;
            }

            List<Long> dataIdList = reportRuleVO.getReportRuleDataVOList().stream()
                    .map(ReportRuleDataVO::getCusDataId)
                    .collect(Collectors.toList());

            sceneHistoryParam.setBeginTime(beginTime);
            sceneHistoryParam.setEndTime(endTime);
            sceneHistoryParam.setIds(dataIdList.stream().map(String::valueOf).collect(Collectors.joining(",")));

            SceneHistoryResultVO sceneHistoryResultVO = dataCenterService.querySceneHistory(sceneHistoryParam);
            String deviceName = sceneHistoryParam.getSceneModelName() + "(" + sceneHistoryParam.getDeviceName() + ")";

            for (HistoryModel historyModel : sceneHistoryResultVO.getHistoryModelList()) {
                historyModel.setDeviceName(deviceName);
                historyModel.setSceneModelDeviceId(reportRuleVO.getSceneModelDeviceId());
                resultList.add(historyModel);
            }
        }
    }

    /**
     * 生成报表文件
     */
    private String generateReportFile(ReportVO reportVO, List<HistoryModel> resultList,
                                      String beginTime, String endTime) throws Exception {
        if (ReportDataTypeEnum.HISTORY_DATA.getType().equals(reportVO.getDataType())) {
            return this.generateHistoryDataReport(resultList, reportVO, endTime);
        } else if (ReportDataTypeEnum.AGGREGATE_DATA.getType().equals(reportVO.getDataType())) {
            Date beTime = DateUtils.parseDate(beginTime, DateUtils.YYYY_MM_DD_HH_MM_SS);
            Date enTime = DateUtils.parseDate(endTime, DateUtils.YYYY_MM_DD_HH_MM_SS);
            return this.generateAggregateDataReport(resultList, beTime, enTime, reportVO);
        }
        throw new IllegalArgumentException("不支持的数据类型：" + reportVO.getDataType());
    }

    /**
     * 构建通知参数
     */
    private ReportNotifyParams buildNotifyParams(ReportVO reportVO, String fileUrl) {
        ReportNotifyParams reportNotifyParams = new ReportNotifyParams();
        if (StringUtils.isEmpty(reportVO.getNotifyUsers())) {
            return reportNotifyParams;
        }

        reportNotifyParams.setName(reportVO.getName());
        reportNotifyParams.setStatusDesc("生成成功");
        reportNotifyParams.setTenantId(reportVO.getTenantId());
        reportNotifyParams.setUploadPath(fileUrl);

        List<String> userIds = StringUtils.str2List(reportVO.getNotifyUsers(), ",", true, true);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysUser::getUserId, userIds);
        List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
        Set<String> emailSet = sysUserList.stream().map(SysUser::getEmail).collect(Collectors.toSet());
        reportNotifyParams.setSendEmail(emailSet);

        return reportNotifyParams;
    }

    /**
     * @description: 主处理方法
     * @author zzy
     * @date 2025-07-31 17:52
     */
    public String generateAggregateDataReport(List<HistoryModel> dataList, Date beginTime, Date endTime,
                                ReportVO reportVO) throws Exception {
        // 1. 分割时间段
        List<ReportTimeRangeVO> timeRanges = splitTimeRanges(beginTime, endTime, reportVO.getAggregateUnits());

        // 2. 数据分组：设备ID -> 标识符 -> 数据列表
        Map<Long, Map<String, List<HistoryModel>>> groupedData = groupData(dataList);

        // 3. 准备Excel数据结构
        ReportAggregateHistoryVO exportData = prepareExportData(groupedData, timeRanges);

        // 4. 生成Excel并返回URL
        return generateExcel(exportData, endTime, reportVO.getName());
    }

    /**
     * @description: 时间段分割方法
     * @author zzy
     * @date 2025-07-31 17:52
     */
    private List<ReportTimeRangeVO> splitTimeRanges(Date begin, Date end, int unit) {
        List<ReportTimeRangeVO> ranges = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        while (cal.getTime().before(end)) {
            Date startTime = cal.getTime();

            // 根据单位增加时间
            switch (unit) {
                case 1: cal.add(Calendar.MINUTE, 1); break;
                case 2: cal.add(Calendar.HOUR, 1); break;
                case 3: cal.add(Calendar.DAY_OF_MONTH, 1); break;
                case 4: cal.add(Calendar.MONTH, 1); break;
                default: throw new IllegalArgumentException("无效聚合单位");
            }

            Date endTime = cal.getTime().after(end) ? end : cal.getTime();
            ranges.add(new ReportTimeRangeVO(startTime, endTime));
        }
        return ranges;
    }

    /**
     * @description: 准备导出数据
     * @author zzy
     * @date 2025-07-31 15:41
     */
    private ReportAggregateHistoryVO prepareExportData(
            Map<Long, Map<String, List<HistoryModel>>> groupedData,
            List<ReportTimeRangeVO> timeRanges) {

        ReportAggregateHistoryVO dto = new ReportAggregateHistoryVO();

        // === 完全重构的表头结构 ===
        List<List<String>> head = new ArrayList<>();

        // 第一行表头：设备名称
        List<String> headerRow1 = new ArrayList<>();
        // A1单元格
        headerRow1.add("设备");
        headerRow1.add("变量");
        head.add(headerRow1);

        // 收集所有列的顺序（设备ID + 标识符）
        List<Pair<Long, String>> columnOrder = new ArrayList<>();

        // 遍历所有设备
        for (Map.Entry<Long, Map<String, List<HistoryModel>>> deviceEntry : groupedData.entrySet()) {
            Long deviceId = deviceEntry.getKey();
            Map<String, List<HistoryModel>> identifyMap = deviceEntry.getValue();

            // 遍历设备下的所有标识符
            List<String> headIdentifierList = new ArrayList<>();
            for (Map.Entry<String, List<HistoryModel>> identifyEntry : identifyMap.entrySet()) {
                String identify = identifyEntry.getKey();
                List<HistoryModel> models = identifyEntry.getValue();
                HistoryModel sample = models.get(0);

                if (models.isEmpty()) continue;

                if (!headIdentifierList.contains(sample.getIdentify())) {
                    headIdentifierList.add(sample.getIdentify());
                    List<String> headerRow = new ArrayList<>();
                    // 添加到表头
                    headerRow.add(sample.getDeviceName());
                    // 使用模型名称 + 统计方式作为变量名称
                    String operationName = ReportRuleDataOperationEnum.getByCode(sample.getOperation()).getDesc();
                    String variableName = sample.getModerName() + "(" + operationName + ")";
                    headerRow.add(variableName);
                    head.add(headerRow);
                }

                // 记录列顺序
                columnOrder.add(new Pair<>(deviceId, identify));
            }

        }


        // 准备数据行
        List<List<Object>> data = new ArrayList<>();

        // 为每个时间段创建数据行
        for (ReportTimeRangeVO range : timeRanges) {
            List<Object> row = new ArrayList<>();
            // 添加时间段
            row.add(formatTimeRange(range));

            // 按列顺序添加值
            for (Pair<Long, String> column : columnOrder) {
                Long deviceId = column.getKey();
                String identify = column.getValue();

                // 获取对应的数据
                List<HistoryModel> models = groupedData.get(deviceId).get(identify);

                if (models == null || models.isEmpty()) {
                    row.add("");
                    continue;
                }

                // 获取统计方式
                Integer operation = models.get(0).getOperation();

                // 计算值
                Double value = calculateValue(models, range, operation);
                row.add(value != null ? value : "");
            }

            data.add(row);
        }

        dto.setHead(head);
        dto.setData(data);
        return dto;
    }

    /**
     * @description: 辅助方法：根据标识符查找数据
     * @author zzy
     * @date 2025-07-31 15:39
     */
    private List<HistoryModel> findModelsForIdentify(
            Map<Long, Map<String, List<HistoryModel>>> groupedData,
            String identify) {

        for (Map<String, List<HistoryModel>> identifyMap : groupedData.values()) {
            if (identifyMap.containsKey(identify)) {
                return identifyMap.get(identify);
            }
        }
        return null;
    }

    /**
     * @description: 时间段格式化
     * @author zzy
     * @date 2025-07-31 15:38
     */
    private String formatTimeRange(ReportTimeRangeVO range) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(range.getStart()) + " ~ " + sdf.format(range.getEnd());
    }

    /**
     * @description: 数据分组
     * @author zzy
     * @date 2025-07-31 15:38
     */
    private Map<Long, Map<String, List<HistoryModel>>> groupData(List<HistoryModel> dataList) {
        return dataList.stream().collect(
                Collectors.groupingBy(HistoryModel::getSceneModelDeviceId,
                        Collectors.groupingBy(HistoryModel::getIdentify))
        );
    }

    /**
     * @description: 计算聚合值
     * @author zzy
     * @date 2025-07-31 15:39
     */
    private Double calculateValue(List<HistoryModel> models, ReportTimeRangeVO range, Integer operation) {
        // 过滤时间段内数据
        List<HistoryModel> filtered = models.stream()
                .filter(m -> !m.getTime().before(range.getStart())
                        && !m.getTime().after(range.getEnd()))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) return null;

        // 转换为数值列表
        List<Double> values = filtered.stream()
                .map(m -> {
                    try { return Double.parseDouble(m.getValue()); }
                    catch (Exception e) { return Double.NaN; }
                })
                .filter(d -> !d.isNaN())
                .collect(Collectors.toList());

        if (values.isEmpty()) return null;

        // 根据操作类型计算
        ReportRuleDataOperationEnum operationEnum = ReportRuleDataOperationEnum.getByCode(operation);
        switch (Objects.requireNonNull(operationEnum)) {
            // 平均值
            case AVERAGE:
                return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            // 最大值
            case MAX:
                return values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            // 最小值
            case MIN:
                return values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            // 累计值
            case CUMULATIVE:
                return values.stream().mapToDouble(Double::doubleValue).sum();
            // 差值（首尾差）
            case DIFFERENCE:
                return values.get(values.size()-1) - values.get(0);
            // 极差值
            case RANGE:
                double max = Double.MIN_VALUE;
                double min = Double.MAX_VALUE;
                for (Double value : values) {
                    if (value > max) {
                        max = value;
                    }
                    if (value < min) {
                        min = value;
                    }
                }
                return max - min;
            default:
                throw new IllegalArgumentException("无效操作类型");
        }
    }

    private String generateHistoryDataReport(List<HistoryModel> dataList, ReportVO reportVO, String endTime) throws ParseException, IOException {

        // 1. 数据预处理
        Map<Long, String> deviceIdToNameMap = dataList.stream()
                .collect(Collectors.toMap(
                        HistoryModel::getSceneModelDeviceId,
                        HistoryModel::getDeviceName,
                        (existing, replacement) -> existing
                ));

        Map<String, String> deviceModelToNameMap = dataList.stream()
                .collect(Collectors.toMap(
                        model -> model.getSceneModelDeviceId() + "_" + model.getIdentify(),
                        HistoryModel::getModerName,
                        (existing, replacement) -> existing
                ));

        // 获取所有时间点并排序
        List<Date> sortedTimes = dataList.stream()
                .map(HistoryModel::getTime)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 2. 获取设备列表并排序
        List<Long> deviceIds = new ArrayList<>(deviceIdToNameMap.keySet());
        Collections.sort(deviceIds);

        // 3. 构建动态表头
        List<List<String>> head = new ArrayList<>();

        // 时间列
        head.add(Collections.singletonList("时间"));

        // 为每个设备创建列头
        Map<String, Integer> columnIndexMap = new HashMap<>(2);
        // 从1开始（0是时间列）
        int columnIndex = 1;

        for (Long deviceId : deviceIds) {
            String deviceName = deviceIdToNameMap.get(deviceId);

            // 获取该设备的所有模型标识
            Set<String> modelIdentifiers = dataList.stream()
                    .filter(m -> m.getSceneModelDeviceId().equals(deviceId))
                    .map(HistoryModel::getIdentify)
                    .collect(Collectors.toSet());

            for (String modelId : modelIdentifiers) {
                String modelName = deviceModelToNameMap.get(deviceId + "_" + modelId);

                // 添加表头：设备名 + 模型名
                head.add(Collections.singletonList(deviceName + "\n" + modelName));

                // 记录列位置对应的设备+模型
                columnIndexMap.put(deviceId + "_" + modelId, columnIndex);
                columnIndex++;
            }
        }

        // 4. 构建数据行
        List<List<Object>> excelData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Date time : sortedTimes) {
            List<Object> rowData = new ArrayList<>();
            // 时间列直接格式化为字符串
            rowData.add(sdf.format(time));

            // 初始化所有列为空值
            for (int i = 1; i < columnIndex; i++) {
                rowData.add("");
            }

            // 填充实际数据
            for (HistoryModel model : dataList) {
                if (model.getTime().equals(time)) {
                    String key = model.getSceneModelDeviceId() + "_" + model.getIdentify();
                    Integer index = columnIndexMap.get(key);
                    if (index != null && model.getValue() != null) {
                        rowData.set(index, model.getValue());
                    }
                }
            }

            excelData.add(rowData);
        }

        // 5. 设置表头样式
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

        // 注册样式策略
        HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(headerStyle, contentStyle);

        // 3. 保存文件到服务器
        String time = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, DateUtils.parseDate(endTime, DateUtils.YYYY_MM_DD_HH_MM_SS));
        String historyDir = RuoYiConfig.getProfile() + "/report/history/";
        // === 新增：确保目录存在 ===
        File targetDir = new File(historyDir);
        if (!targetDir.exists()) {
            // 创建目录（包括所有不存在的父目录）
            boolean dirsCreated = targetDir.mkdirs();
            if (!dirsCreated) {
                throw new IOException("无法创建目录: " + targetDir.getAbsolutePath());
            }
        }
        String fileName = "/report/history/" + time + "_" + reportVO.getName() + ".xlsx";
        String filePath = RuoYiConfig.getProfile() + fileName;

        // 6. 写入Excel
        try (ExcelWriter excelWriter = EasyExcel.write(new File(filePath)).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("历史数据报表")
                    .head(head)
                    .registerWriteHandler(styleStrategy)
                    // 自动列宽
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();

            excelWriter.write(excelData, writeSheet);
        }

        return "/profile" + fileName;
    }

    private String generateExcel(ReportAggregateHistoryVO exportData, Date endTime, String name) throws Exception {
        String time = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, endTime);
        String historyDir = RuoYiConfig.getProfile() + "/report/aggregate/";
        // === 新增：确保目录存在 ===
        File targetDir = new File(historyDir);
        if (!targetDir.exists()) {
            // 创建目录（包括所有不存在的父目录）
            boolean dirsCreated = targetDir.mkdirs();
            if (!dirsCreated) {
                throw new IOException("无法创建目录: " + targetDir.getAbsolutePath());
            }
        }
        String fileName = "/report/aggregate/" + time + "_" + name + ".xlsx";
        String filePath = RuoYiConfig.getProfile() + fileName;
        // 使用EasyExcel写入
        try (ExcelWriter excelWriter = EasyExcel.write(filePath).build()) {
            // 表头样式
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

            WriteFont headWriteFont = new WriteFont();
            headWriteFont.setBold(true);
            headWriteFont.setFontHeightInPoints((short)12);
            headWriteCellStyle.setWriteFont(headWriteFont);

            // 内容样式
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // 使用 HorizontalCellStyleStrategy 替代 CellWriteHandler
            HorizontalCellStyleStrategy styleStrategy =
                    new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

            WriteSheet sheet = EasyExcel.writerSheet("聚合数据")
                    .head(exportData.getHead())
                    // 自动列宽
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 注册样式
                    .registerWriteHandler(styleStrategy)
                    .build();
            excelWriter.write(exportData.getData(), sheet);
        }

        return "/profile" + fileName;
    }

    /**
     * @description: 简单的Pair实现
     * @author zzy
     * @date 2025-07-31 15:38
     */
    @Getter
    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

}
