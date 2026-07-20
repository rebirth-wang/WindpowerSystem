package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.enums.DeviceLogTypeEnum;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.exception.job.TaskException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.extend.enums.scenemodel.SceneModelTagOpreationEnum;
import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.date.LocalDateTimeUtils;
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.convert.SceneModelTagConvert;
import com.fastbee.iot.convert.SceneTagPointsConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.scenemodel.JobCronCycleVO;
import com.fastbee.iot.model.scenemodel.SceneDeviceThingsModelVO;
import com.fastbee.iot.model.scenemodel.SceneModelDeviceDataDTO;
import com.fastbee.iot.model.scenemodel.SceneModelTagCacheVO;
import com.fastbee.iot.model.vo.SceneModelTagVO;
import com.fastbee.iot.model.vo.SceneTagPointsVO;
import com.fastbee.iot.service.IDeviceJobService;
import com.fastbee.iot.service.ISceneModelTagService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.iot.tsdb.service.ILogService;
import com.fastbee.iot.util.JobCronUtils;
import com.fastbee.quartz.util.CronUtils;

/**
 * 场景录入型变量Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-21
 */
@Slf4j
@Service
public class SceneModelTagServiceImpl extends ServiceImpl<SceneModelTagMapper,SceneModelTag> implements ISceneModelTagService
{
    @Resource
    private SceneModelTagMapper sceneModelTagMapper;

    @Resource
    private SceneModelDataMapper sceneModelDataMapper;

    @Resource
    private SceneTagPointsMapper sceneTagPointsMapper;

    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;

    @Resource
    private SceneModelTagCache sceneModelTagCache;

    @Resource
    private ILogService logService;

    @Resource
    private IDeviceJobService deviceJobService;

    @Resource
    private IThingsModelService thingsModelService;

    @Resource
    private RedisCache redisCache;

    /**
     * 计算表达式变量英文大写字母
     */
    private final static String PATTERN_ENGLISH_LETTER = "[a-zA-Z]";

    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    @Override
    public SceneModelTagVO selectSceneModelTagById(Long id)
    {
        SceneModelTagVO sceneModelTagVO = sceneModelTagMapper.selectSceneModelTagById(id);
        if (null == sceneModelTagVO) {
            return null;
        }
        this.handlePoints(sceneModelTagVO);
        return sceneModelTagVO;
    }

    private void handlePoints(SceneModelTagVO sceneModelTagVO) {
        LambdaQueryWrapper<SceneTagPoints> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneTagPoints::getTagId, sceneModelTagVO.getId());
        List<SceneTagPoints> sceneTagPointsList = sceneTagPointsMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sceneTagPointsList)) {
            List<Long> dataSourceIdList = sceneTagPointsList.stream().map(SceneTagPoints::getSceneModelDataId).collect(Collectors.toList());
            List<SceneModelDeviceDataDTO> sceneModelDeviceDataDTOList = sceneModelDataMapper.selectSceneModelDeviceByDataIdList(dataSourceIdList);
            Map<Long, SceneModelDeviceDataDTO> sceneModelDeviceMap = sceneModelDeviceDataDTOList.stream().collect(Collectors.toMap(SceneModelDeviceDataDTO::getSceneModelDataId, Function.identity()));
            List<SceneTagPointsVO> sceneTagPointsVOList = SceneTagPointsConvert.INSTANCE.convertSceneTagPointsVOList(sceneTagPointsList);
            for (SceneTagPointsVO sceneTagPointsVO : sceneTagPointsVOList) {
                SceneModelDeviceDataDTO sceneModelDeviceDataDTO = sceneModelDeviceMap.get(sceneTagPointsVO.getSceneModelDataId());
                if (null != sceneModelDeviceDataDTO) {
                    sceneTagPointsVO.setSceneModelDeviceId(sceneModelDeviceDataDTO.getSceneModelDeviceId());
                    sceneTagPointsVO.setSceneModelDeviceName(sceneModelDeviceDataDTO.getSceneModelDeviceName());
                }
            }
            sceneModelTagVO.setTagPointsVOList(sceneTagPointsVOList);
        }
    }

    @Override
    public SceneModelTagVO selectSceneModelTagAndTenantById(Long id) {
        SceneModelTagVO sceneModelTagVO = sceneModelTagMapper.selectSceneModelTagAndTenantById(id);
        if (null == sceneModelTagVO) {
            return null;
        }
        this.handlePoints(sceneModelTagVO);
        return sceneModelTagVO;
    }

    /**
     * 查询场景录入型变量分页列表
     *
     * @param sceneModelTag 场景录入型变量
     * @return 场景录入型变量
     */
    @Override
    public Page<SceneModelTagVO> pageSceneModelTagVO(SceneModelTag sceneModelTag) {
        LambdaQueryWrapper<SceneModelTag> lqw = buildQueryWrapper(sceneModelTag);
        Page<SceneModelTag> sceneModelTagPage = baseMapper.selectPage(new Page<>(sceneModelTag.getPageNum(), sceneModelTag.getPageSize()), lqw);
        Page<SceneModelTagVO> voPage = SceneModelTagConvert.INSTANCE.convertSceneModelTagVOPage(sceneModelTagPage);
        if (0 == voPage.getTotal()) {
            return voPage;
        }
        List<SceneModelTagVO> sceneModelTagVOList = voPage.getRecords();
        LambdaQueryWrapper<SceneModelData> sceneModelDataWrapper = new LambdaQueryWrapper<>();
        sceneModelDataWrapper.eq(SceneModelData::getSceneModelId, sceneModelTag.getSceneModelId());
        sceneModelDataWrapper.eq(SceneModelData::getVariableType, sceneModelTag.getVariableType());
        List<SceneModelData> sceneModelDataList = sceneModelDataMapper.selectList(sceneModelDataWrapper);
        Map<Long, SceneModelData> map = sceneModelDataList.stream().collect(Collectors.toMap(SceneModelData::getDatasourceId, Function.identity()));
        for (SceneModelTagVO modelTagVO : sceneModelTagVOList) {
            SceneModelData sceneModelData1 = map.get(modelTagVO.getId());
            if (null != sceneModelData1) {
                modelTagVO.setEnable(sceneModelData1.getEnable());
            }
        }
        return voPage;
    }

    /**
     * 查询场景录入型变量列表
     *
     * @param sceneModelTag 场景录入型变量
     * @return 场景录入型变量
     */
    @Override
    public List<SceneModelTagVO> listSceneModelTagVO(SceneModelTag sceneModelTag) {
        LambdaQueryWrapper<SceneModelTag> lqw = buildQueryWrapper(sceneModelTag);
        List<SceneModelTag> sceneModelTagList = baseMapper.selectList(lqw);
        return SceneModelTagConvert.INSTANCE.convertSceneModelTagVOList(sceneModelTagList);
    }

    /**
     * 新增场景录入型变量
     *
     * @param sceneModelTagVO 场景录入型变量
     * @return 结果
     */
    @Override
    public SceneModelTagVO insertSceneModelTag(SceneModelTagVO sceneModelTagVO)
    {
        if (SceneModelVariableTypeEnum.OPERATION_VARIABLE.getType().equals(sceneModelTagVO.getVariableType())) {
            String msg =  this.checkAliasFormule(sceneModelTagVO);
            if (StringUtils.isNotEmpty(msg)) {
                throw new ServiceException(msg);
            }
        }
        if (!SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(sceneModelTagVO.getVariableType())) {
            LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SceneModelDevice::getSceneModelId, sceneModelTagVO.getSceneModelId());
            queryWrapper.eq(SceneModelDevice::getVariableType, sceneModelTagVO.getVariableType());
            SceneModelDevice sceneModelDevice = sceneModelDeviceMapper.selectOne(queryWrapper);
            sceneModelTagVO.setSceneModelDeviceId(sceneModelDevice.getId());
        }
        SysUser user = getLoginUser().getUser();
        sceneModelTagVO.setCreateBy(user.getCreateBy());
        sceneModelTagVO.setUpdateBy(user.getUpdateBy());
        SceneModelTag sceneModelTag = SceneModelTagConvert.INSTANCE.convertSceneModelTag(sceneModelTagVO);
        int result = sceneModelTagMapper.insert(sceneModelTag);
        sceneModelTagVO.setId(sceneModelTag.getId());
        if (result <= 0) {
            return null;
        }
        SceneModelData sceneModelData = new SceneModelData();
        sceneModelData.setSceneModelId(sceneModelTag.getSceneModelId());
        sceneModelData.setSceneModelDeviceId(sceneModelTagVO.getSceneModelDeviceId());
        sceneModelData.setVariableType(sceneModelTag.getVariableType());
        sceneModelData.setEnable(1);
        sceneModelData.setDatasourceId(sceneModelTag.getId());
        sceneModelData.setIdentifier(sceneModelTag.getId().toString());
        sceneModelData.setSourceName(sceneModelTag.getName());
        sceneModelData.setUnit(sceneModelTag.getUnit());
        sceneModelDataMapper.insert(sceneModelData);
        for (SceneTagPointsVO sceneTagPointsVO : sceneModelTagVO.getTagPointsVOList()) {
            sceneTagPointsVO.setTagId(sceneModelTag.getId());
            SceneTagPoints sceneTagPoints = SceneTagPointsConvert.INSTANCE.convertSceneTagPoints(sceneTagPointsVO);
            sceneTagPointsMapper.insert(sceneTagPoints);
        }
        // 添加定时任务
        String cacheValue;
        if (SceneModelVariableTypeEnum.OPERATION_VARIABLE.getType().equals(sceneModelTag.getVariableType())) {
            JobCronCycleVO jobCronCycleVO = JobCronUtils.handleCronCycle(sceneModelTag.getCycleType(), sceneModelTag.getCycle());
            jobCronCycleVO.setStatus(1);
            this.createSceneTagTask(sceneModelTag.getId(), jobCronCycleVO, sceneModelTagVO.getCreateBy());
            cacheValue = "0";
        } else {
            if (StringUtils.isNotEmpty(sceneModelTag.getDefaultValue())) {
                cacheValue = sceneModelTag.getDefaultValue();
            } else {
                cacheValue = "0".equals(sceneModelTag.getDataType()) ? "0" : "";
            }
        }
        sceneModelTagVO.setCacheValue(cacheValue);
        return sceneModelTagVO;
    }

    /**
     * 校验计算公式
     * @param sceneModelTagVO 变量实体类
     * @return java.lang.String
     */
    @Override
    public String checkAliasFormule(SceneModelTagVO sceneModelTagVO) {
        if (StringUtils.isEmpty(sceneModelTagVO.getAliasFormule()) && CollectionUtils.isNotEmpty(sceneModelTagVO.getTagPointsVOList())) {
            return MessageUtils.message("sceneModel.formula.cannot.empty");
        }
        if (StringUtils.isNotEmpty(sceneModelTagVO.getAliasFormule()) && CollectionUtils.isEmpty(sceneModelTagVO.getTagPointsVOList())) {
            return MessageUtils.message("sceneModel.variable.cannot.empty");
        }
        String aliasFormule = sceneModelTagVO.getAliasFormule();
        Pattern pattern = Pattern.compile(PATTERN_ENGLISH_LETTER);
        Matcher matcher = pattern.matcher(aliasFormule);
        List<String> aliasList = new ArrayList<>();
        while (matcher.find()) {
            if (!aliasList.contains(matcher.group())) {
                aliasList.add(matcher.group());
            }
        }
        List<SceneTagPointsVO> tagPointsVOList = sceneModelTagVO.getTagPointsVOList();
        List<String> aliasList1 = tagPointsVOList.stream().map(SceneTagPointsVO::getAlias).collect(Collectors.toList());
        Collections.sort(aliasList);
        Collections.sort(aliasList1);
        if (!aliasList.equals(aliasList1)) {
            return MessageUtils.message("sceneModel.formula.and.variable.number.inconsistent");
        }
        return "";
    }

    /**
     * 修改场景录入型变量
     *
     * @param sceneModelTagVO 场景录入型变量
     * @return 结果
     */
    @Override
    public SceneModelTagVO updateSceneModelTag(SceneModelTagVO sceneModelTagVO)
    {
        SceneModelTag checkName = sceneModelTagMapper.checkName(sceneModelTagVO);
        if (null != checkName) {
            throw new ServiceException(MessageUtils.message("sceneModel.update.fail.variable.name.exist"));
        }
        if (SceneModelVariableTypeEnum.OPERATION_VARIABLE.getType().equals(sceneModelTagVO.getVariableType())) {
            String msg =  this.checkAliasFormule(sceneModelTagVO);
            if (StringUtils.isNotEmpty(msg)) {
                throw new ServiceException(msg);
            }
        }
        SysUser user = getLoginUser().getUser();
        sceneModelTagVO.setUpdateBy(user.getUserName());
        SceneModelTag sceneModelTag = SceneModelTagConvert.INSTANCE.convertSceneModelTag(sceneModelTagVO);
        int i = sceneModelTagMapper.updateById(sceneModelTag);
        if (i < 0) {
            return null;
        }
        LambdaQueryWrapper<SceneTagPoints> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SceneTagPoints::getTagId, sceneModelTag.getId());
        sceneTagPointsMapper.delete(queryWrapper);
        for (SceneTagPointsVO sceneTagPointsVO : sceneModelTagVO.getTagPointsVOList()) {
            sceneTagPointsVO.setTagId(sceneModelTag.getId());
            SceneTagPoints sceneTagPoints = SceneTagPointsConvert.INSTANCE.convertSceneTagPoints(sceneTagPointsVO);
            sceneTagPoints.setId(null);
            sceneTagPointsMapper.insert(sceneTagPoints);
        }
        // 添加定时任务
        String cacheValue;
        if (SceneModelVariableTypeEnum.OPERATION_VARIABLE.getType().equals(sceneModelTag.getVariableType())) {
            // 删除定时任务
            try {
                deviceJobService.deleteJobByJobTypeAndDatasourceIds(new Long[]{sceneModelTag.getId()}, 4);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            JobCronCycleVO jobCronCycleVO = JobCronUtils.handleCronCycle(sceneModelTag.getCycleType(), sceneModelTag.getCycle());
            jobCronCycleVO.setStatus(sceneModelTagVO.getEnable());
            this.createSceneTagTask(sceneModelTag.getId(), jobCronCycleVO, sceneModelTagVO.getCreateBy());
            cacheValue = "0";
        } else {
            if (StringUtils.isNotEmpty(sceneModelTag.getDefaultValue())) {
                cacheValue = sceneModelTag.getDefaultValue();
            } else {
                cacheValue = "0".equals(sceneModelTag.getDataType()) ? "0" : "";
            }
        }
        sceneModelTagVO.setCacheValue(cacheValue);
        // 同步变量
        SceneModelData updateSceneModelData = new SceneModelData();
        updateSceneModelData.setSceneModelId(sceneModelTag.getSceneModelId());
        updateSceneModelData.setVariableType(sceneModelTag.getVariableType());
        updateSceneModelData.setDatasourceId(sceneModelTag.getId());
        updateSceneModelData.setSourceName(sceneModelTag.getName());
        updateSceneModelData.setUnit(sceneModelTag.getUnit());
        sceneModelDataMapper.updateSceneModelDataByDatasourceId(updateSceneModelData);
        return sceneModelTagVO;
    }

    /**
     * 批量删除场景录入型变量
     *
     * @param ids 需要删除的场景录入型变量主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelTagByIds(Long[] ids)
    {
        List<Long> idList = Arrays.asList(ids);
        SceneModelTag sceneModelTag = baseMapper.selectById(ids[0]);
        int i = baseMapper.deleteBatchIds(idList);
        if (i > 0) {
            LambdaQueryWrapper<SceneModelData> sceneModelDataWrapper = new LambdaQueryWrapper<>();
            sceneModelDataWrapper.in(SceneModelData::getDatasourceId, idList);
            sceneModelDataMapper.delete(sceneModelDataWrapper);
            LambdaQueryWrapper<SceneTagPoints> sceneTagPointsWrapper = new LambdaQueryWrapper<>();
            sceneTagPointsWrapper.in(SceneTagPoints::getTagId, idList);
            sceneTagPointsMapper.delete(sceneTagPointsWrapper);
            // 批量删除定时任务
            try {
                deviceJobService.deleteJobByJobTypeAndDatasourceIds(ids, 4);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            // 删除缓存
            if (null != sceneModelTag && null != sceneModelTag.getSceneModelId()) {
                String key = RedisKeyBuilder.buildSceneModelTagCacheKey(sceneModelTag.getSceneModelId());
                for (Long id : ids) {
                    redisCache.deleteCacheMapValue(key, id.toString());
                }
            }

        }
        return i;
    }

    /**
     * 删除场景录入型变量信息
     *
     * @param id 场景录入型变量主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelTagById(Long id)
    {
        return sceneModelTagMapper.deleteById(id);
    }

    /**
     * 获取物模型的值
     * @param sceneTagPoints 变量
     * @return java.lang.String
     */
    @Override
    public String getSceneModelDataValue(SceneTagPoints sceneTagPoints, JobCronCycleVO jobCronCycleVO) {
        Integer operation = sceneTagPoints.getOperation();
        String value = " ";
        SceneDeviceThingsModelVO sceneDeviceThingsModelVO = new SceneDeviceThingsModelVO();
        SceneModelData sceneModelData = new SceneModelData();
        if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(sceneTagPoints.getVariableType())) {
            sceneDeviceThingsModelVO = sceneModelDataMapper.selectDeviceThingsModelById(sceneTagPoints.getSceneModelDataId());
        } else {
            // 运算型
            sceneModelData = sceneModelDataMapper.selectById(sceneTagPoints.getSceneModelDataId());
        }
        if (!SceneModelTagOpreationEnum.ORIGINAL_VALUE.getCode().equals(operation)) {
            DeviceLog deviceLog = new DeviceLog();
            if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(sceneTagPoints.getVariableType())) {
                deviceLog.setLogType(DeviceLogTypeEnum.ATTRIBUTE_REPORT.getType().byteValue());
                deviceLog.setIdentify(sceneDeviceThingsModelVO.getIdentifier());
                deviceLog.setSerialNumber(sceneDeviceThingsModelVO.getSerialNumber());
            } else {
                deviceLog.setLogType(DeviceLogTypeEnum.SCENE_VARIABLE_REPORT.getType().byteValue());
                deviceLog.setIdentify(null != sceneModelData ? sceneModelData.getDatasourceId().toString() : "");
            }
            deviceLog.setBeginTime(LocalDateTimeUtils.localDateTimeToStr(jobCronCycleVO.getBeginTime(), LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS));
            deviceLog.setEndTime(LocalDateTimeUtils.localDateTimeToStr(jobCronCycleVO.getEndTime(), LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS));
            deviceLog.setOperation(operation);
            List<String> valueList = logService.selectStatsValue(deviceLog);
            if (CollectionUtils.isNotEmpty(valueList)) {
                value = this.handleValueByOperation(valueList, operation);
            }
        } else {
            if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(sceneTagPoints.getVariableType())) {
                if (null != sceneDeviceThingsModelVO) {
                    value = thingsModelService.getCacheIdentifierValue(sceneDeviceThingsModelVO.getProductId(), sceneDeviceThingsModelVO.getSerialNumber(), sceneDeviceThingsModelVO.getIdentifier()).getValue();
                }
            } else if (SceneModelVariableTypeEnum.OPERATION_VARIABLE.getType().equals(sceneTagPoints.getVariableType())) {
                SceneModelTagCacheVO sceneModelTagCacheVO = sceneModelTagCache.getSceneModelTagValue(sceneModelData.getSceneModelId(), sceneModelData.getDatasourceId());
                value = null != sceneModelTagCacheVO ? sceneModelTagCacheVO.getValue() : "";
            } else {
                SceneModelTagCacheVO sceneModelTagCacheVO = sceneModelTagCache.getSceneModelTagValue(sceneModelData.getSceneModelId(), sceneModelData.getDatasourceId());
                if (Objects.isNull(sceneModelTagCacheVO)) {
                    value = sceneModelDataMapper.selectInputTagDefaultValueById(sceneTagPoints.getSceneModelDataId());
                } else {
                    value = sceneModelTagCacheVO.getValue();
                }
            }
        }
        return value;
    }

    @Override
    public List<SceneModelTag> selectEnableTagBySceneModelId(Long sceneModelId) {
        return sceneModelTagMapper.selectEnableTagBySceneModelId(sceneModelId);
    }

    @Override
    public void restoreTagJob(List<SceneModelTag> sceneModelTagList) {
        for (SceneModelTag sceneModelTag : sceneModelTagList) {
            JobCronCycleVO jobCronCycleVO = JobCronUtils.handleCronCycle(sceneModelTag.getCycleType(), sceneModelTag.getCycle());
            jobCronCycleVO.setStatus(1);
            this.createSceneTagTask(sceneModelTag.getId(), jobCronCycleVO, sceneModelTag.getCreateBy());
        }
    }

    /**
     * 统计方式计算值
     * @param valueList 值集合
     * @param: operation 统计方式
     * @return java.lang.String
     */
    private String handleValueByOperation(List<String> valueList, Integer operation) {
        String resultValue = "";
        List<BigDecimal> numberList = valueList.stream()
                .map(BigDecimal::new)
                .collect(Collectors.toList());
        SceneModelTagOpreationEnum opreationEnum = SceneModelTagOpreationEnum.getByCode(operation);
        switch (Objects.requireNonNull(opreationEnum)) {
            case CUMULATIVE:
                BigDecimal sum = numberList.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                resultValue = String.valueOf(sum);
                break;
            case AVERAGE_VALUE:
                BigDecimal sum1 = numberList.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal average = sum1.divide(new BigDecimal(numberList.size()), 2, RoundingMode.HALF_UP);
                resultValue = String.valueOf(average);
                break;
            case MAX_VALUE:
                BigDecimal max = numberList.stream()
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                resultValue = String.valueOf(max);
                break;
            case MIN_VALUE:
                BigDecimal min = numberList.stream()
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                resultValue = String.valueOf(min);
                break;
            default:
                break;
        }
        return resultValue;
    }

    /**
     * 创建场景运算变量定时任务
     *
     * @param sceneModelTagId 运算变量id
     * @param jobCronCycleVO cron
     */
    private void createSceneTagTask(Long sceneModelTagId, JobCronCycleVO jobCronCycleVO, String createBy) {
        // 创建定时任务
        try {
            if (!CronUtils.isValid(jobCronCycleVO.getCron())) {
                log.error("新增场景运算变量定时任务失败，Cron表达式不正确");
                throw new Exception("新增场景运算变量定时任务失败，Cron表达式不正确");
            }
            DeviceJob deviceJob = new DeviceJob();
            deviceJob.setJobName(DeviceJobTypeEnum.SCENE_MODEL.getDesc());
            deviceJob.setJobType(DeviceJobTypeEnum.SCENE_MODEL.getType());
            deviceJob.setJobGroup("SCENE");
            deviceJob.setConcurrent("1");
            deviceJob.setMisfirePolicy("2");
            deviceJob.setStatus(1 == jobCronCycleVO.getStatus() ? 0 : 1);
            deviceJob.setCronExpression(jobCronCycleVO.getCron());
            deviceJob.setIsAdvance(1);
            deviceJob.setDatasourceId(sceneModelTagId);
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

    private LambdaQueryWrapper<SceneModelTag> buildQueryWrapper(SceneModelTag query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SceneModelTag> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SceneModelTag::getId, query.getId());
        lqw.eq(query.getSceneModelId() != null, SceneModelTag::getSceneModelId, query.getSceneModelId());
        lqw.like(StringUtils.isNotBlank(query.getName()), SceneModelTag::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getUnit()), SceneModelTag::getUnit, query.getUnit());
        lqw.eq(StringUtils.isNotBlank(query.getDataType()), SceneModelTag::getDataType, query.getDataType());
        lqw.eq(StringUtils.isNotBlank(query.getDefaultValue()), SceneModelTag::getDefaultValue, query.getDefaultValue());
        lqw.eq(query.getIsReadonly() != null, SceneModelTag::getIsReadonly, query.getIsReadonly());
        lqw.eq(query.getStorage() != null, SceneModelTag::getStorage, query.getStorage());
        lqw.eq(query.getVariableType() != null, SceneModelTag::getVariableType, query.getVariableType());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), SceneModelTag::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SceneModelTag::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SceneModelTag::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SceneModelTag::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SceneModelTag::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SceneModelTag::getRemark, query.getRemark());
        lqw.eq(query.getCycleExecuted() != null, SceneModelTag::getCycleExecuted, query.getCycleExecuted());
        lqw.eq(StringUtils.isNotBlank(query.getFormule()), SceneModelTag::getFormule, query.getFormule());
        lqw.eq(StringUtils.isNotBlank(query.getAliasFormule()), SceneModelTag::getAliasFormule, query.getAliasFormule());
        lqw.eq(query.getCycleType() != null, SceneModelTag::getCycleType, query.getCycleType());
        lqw.eq(StringUtils.isNotBlank(query.getCycle()), SceneModelTag::getCycle, query.getCycle());
        lqw.eq(query.getIsSceneAttribute() != null, SceneModelTag::getIsSceneAttribute, query.getIsSceneAttribute());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SceneModelTag::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
