package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.page.TableDataExtendInfo;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.convert.SceneModelDataConvert;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.SceneModelData;
import com.fastbee.iot.domain.SceneModelDevice;
import com.fastbee.iot.domain.SceneModelTag;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.scenemodel.SceneModelDataDTO;
import com.fastbee.iot.model.scenemodel.SceneModelTagCacheVO;
import com.fastbee.iot.model.vo.SceneModelDataVO;
import com.fastbee.iot.service.IDeviceJobService;
import com.fastbee.iot.service.ISceneModelDataService;
import com.fastbee.iot.service.IThingsModelService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-21
 */
@Service
public class SceneModelDataServiceImpl extends ServiceImpl<SceneModelDataMapper,SceneModelData> implements ISceneModelDataService
{
    @Resource
    private SceneModelDataMapper sceneModelDataMapper;
    @Resource
    private SceneModelTagMapper sceneModelTagMapper;
    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Resource
    private SceneModelTagCache sceneModelTagCache;
    @Resource
    private IDeviceJobService jobService;
    @Resource
    private IThingsModelService thingsModelService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public SceneModelData selectSceneModelDataById(Long id)
    {
        return sceneModelDataMapper.selectById(id);
    }

    /**
     * 查询sceneModelData分页列表
     *
     * @param sceneModelData sceneModelData
     * @return sceneModelData
     */
    @Override
    public Page<SceneModelDataVO> pageSceneModelDataVO(SceneModelData sceneModelData) {
        LambdaQueryWrapper<SceneModelData> lqw = buildQueryWrapper(sceneModelData);
        Page<SceneModelData> sceneModelDataPage = baseMapper.selectPage(new Page<>(sceneModelData.getPageNum(), sceneModelData.getPageSize()), lqw);
        return SceneModelDataConvert.INSTANCE.convertSceneModelDataVOPage(sceneModelDataPage);
    }

    /**
     * 查询sceneModelData列表
     *
     * @param sceneModelData sceneModelData
     * @return sceneModelData
     */
    @Override
    public List<SceneModelDataVO> listSceneModelDataVO(SceneModelData sceneModelData) {
        LambdaQueryWrapper<SceneModelData> lqw = buildQueryWrapper(sceneModelData);
        List<SceneModelData> sceneModelDataList = baseMapper.selectList(lqw);
        return SceneModelDataConvert.INSTANCE.convertSceneModelDataVOList(sceneModelDataList);
    }

    /**
     * 查询场景变量列表
     *
     * @param sceneModelData 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public Page<SceneModelDataDTO> selectSceneModelDataDTOList(SceneModelData sceneModelData)
    {
        Page<SceneModelDataDTO> dtoPage = sceneModelDataMapper.selectSceneModelDataDTOList(new Page<>(sceneModelData.getPageNum(), sceneModelData.getPageSize()), sceneModelData);
        if (0 == dtoPage.getTotal()) {
            return dtoPage;
        }
        List<SceneModelDataDTO> sceneModelDataDTOList = dtoPage.getRecords();
        // 取设备物模型value值
        List<SceneModelDataDTO> dtoList = sceneModelDataDTOList.stream().filter(s -> StringUtils.isNotEmpty(s.getSerialNumber())).collect(Collectors.toList());
        Map<String, List<ThingsModelDTO>> thingsModelMap = new HashMap<>(5);
        for (SceneModelDataDTO sceneModelDataDTO : dtoList) {
            List<ThingsModelDTO> thingsModelDtoList = thingsModelService.getCacheByProductId(sceneModelDataDTO.getProductId(), sceneModelDataDTO.getSerialNumber());
            if (CollectionUtils.isNotEmpty(thingsModelDtoList)) {
                thingsModelMap.put(sceneModelDataDTO.getSerialNumber(), thingsModelDtoList);
            }
        }
        // 取场景录入、运算型变量
        List<SceneModelDataDTO> modelDataList = sceneModelDataDTOList.stream()
                .filter(s -> INPUT_VARIABLE.getType().equals(s.getVariableType()) || OPERATION_VARIABLE.getType().equals(s.getVariableType())).collect(Collectors.toList());
        Map<Long, SceneModelTag> sceneModelTagMap = new HashMap<>(2);
        if (CollectionUtils.isNotEmpty(modelDataList)) {
            LambdaQueryWrapper<SceneModelTag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SceneModelTag::getId, modelDataList.stream().map(SceneModelDataDTO::getDatasourceId).collect(Collectors.toList()));
            List<SceneModelTag> sceneModelTagList = sceneModelTagMapper.selectList(queryWrapper);
            sceneModelTagMap = sceneModelTagList.stream().collect(Collectors.toMap(SceneModelTag::getId, Function.identity()));
        }
        for (SceneModelDataDTO sceneModelDataDTO : sceneModelDataDTOList) {
            if (StringUtils.isNotEmpty(sceneModelDataDTO.getSerialNumber())) {
                List<ThingsModelDTO> thingsModelDTOList = thingsModelMap.get(sceneModelDataDTO.getSerialNumber());
                String identifier = sceneModelDataDTO.getIdentifier();
                ThingsModelDTO thingsModelDTO = thingsModelDTOList.stream().filter(t -> identifier.equals(t.getIdentifier())).findFirst().orElse(null);
                if (null != thingsModelDTO) {
                    sceneModelDataDTO.setValue(thingsModelDTO.getValue());
                    sceneModelDataDTO.setType(thingsModelDTO.getType());
                    sceneModelDataDTO.setUpdateTime(thingsModelDTO.getTs());
                    sceneModelDataDTO.setUnit(thingsModelDTO.getUnit());
                    sceneModelDataDTO.setIsReadonly(thingsModelDTO.getIsReadonly());
                    sceneModelDataDTO.setDatatype(thingsModelDTO.getDatatype());
                }
            }
            if (!THINGS_MODEL.getType().equals(sceneModelDataDTO.getVariableType())) {
                SceneModelTag sceneModelTag = sceneModelTagMap.get(sceneModelDataDTO.getDatasourceId());
                if (null != sceneModelTag) {
                    sceneModelDataDTO.setUnit(sceneModelTag.getUnit());
                    sceneModelDataDTO.setIsReadonly(sceneModelTag.getIsReadonly());
                    sceneModelDataDTO.setVariableDataType(sceneModelTag.getDataType());
                    sceneModelDataDTO.setValue(sceneModelTag.getDefaultValue());
                }
                SceneModelTagCacheVO sceneModelTagCacheVO = sceneModelTagCache.getSceneModelTagValue(sceneModelData.getSceneModelId(), sceneModelDataDTO.getDatasourceId());
                if (null != sceneModelTagCacheVO) {
                    sceneModelDataDTO.setValue(sceneModelTagCacheVO.getValue());
                    sceneModelDataDTO.setUpdateTime(sceneModelTagCacheVO.getTs());
                }
            }
        }
        return dtoPage;
    }

    @Override
    public TableDataExtendInfo listByType(SceneModelData sceneModelData) {
        // 查询全部启用
        SceneModelDevice sceneModelDevice;
        if (THINGS_MODEL.getType().equals(sceneModelData.getVariableType())) {
            sceneModelDevice = sceneModelDeviceMapper.selectById(sceneModelData.getSceneModelDeviceId());
        } else {
            LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SceneModelDevice::getSceneModelId, sceneModelData.getSceneModelId());
            queryWrapper.eq(SceneModelDevice::getVariableType, sceneModelData.getVariableType());
            sceneModelDevice = sceneModelDeviceMapper.selectOne(queryWrapper);
        }
        TableDataExtendInfo rspData = new TableDataExtendInfo();
        Page<SceneModelDataDTO> dtoPage = sceneModelDataMapper.selectSceneModelDataDTOList(new Page<>(sceneModelData.getPageNum(), sceneModelData.getPageSize()), sceneModelData);
        rspData.setAllEnable(null != sceneModelDevice ? sceneModelDevice.getAllEnable() : 0);
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("query.success");
        rspData.setTotal(dtoPage.getTotal());
        rspData.setRows(dtoPage.getRecords());
        return rspData;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param sceneModelData 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertSceneModelData(SceneModelData sceneModelData)
    {
        return sceneModelDataMapper.insert(sceneModelData);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param sceneModelData 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateSceneModelData(SceneModelData sceneModelData)
    {
        return sceneModelDataMapper.updateById(sceneModelData);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelDataByIds(Long[] ids)
    {
        return sceneModelDataMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelDataById(Long id)
    {
        return sceneModelDataMapper.deleteById(id);
    }

    @Override
    public int editEnable(SceneModelData sceneModelData) {
        Long sceneModelTagId = sceneModelData.getId();
        if (!THINGS_MODEL.getType().equals(sceneModelData.getVariableType())) {
            LambdaQueryWrapper<SceneModelData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SceneModelData::getDatasourceId, sceneModelData.getId());
            queryWrapper.eq(SceneModelData::getVariableType, sceneModelData.getVariableType());
            SceneModelData sceneModelData1 = sceneModelDataMapper.selectOne(queryWrapper);
            sceneModelData.setId(sceneModelData1.getId());
            sceneModelData.setSceneModelDeviceId(sceneModelData1.getSceneModelDeviceId());
        }
        SceneModelData updateSceneModelData = new SceneModelData();
        updateSceneModelData.setId(sceneModelData.getId());
        updateSceneModelData.setEnable(sceneModelData.getEnable());
        int i = sceneModelDataMapper.updateById(updateSceneModelData);
        if (i > 0) {
            SceneModelDevice sceneModelDevice = new SceneModelDevice();
            sceneModelDevice.setId(sceneModelData.getSceneModelDeviceId());
            if (0 == sceneModelData.getEnable()) {
                sceneModelDevice.setAllEnable(0);
                sceneModelDeviceMapper.updateById(sceneModelDevice);
            } else {
                int noEnable = sceneModelDataMapper.countNoEnableBySceneModelDeviceId(sceneModelData.getSceneModelDeviceId());
                if (noEnable <= 0) {
                    sceneModelDevice.setAllEnable(1);
                    sceneModelDeviceMapper.updateById(sceneModelDevice);
                }
            }
            // 更新定时任务
            if (OPERATION_VARIABLE.getType().equals(sceneModelData.getVariableType())) {
                List<DeviceJob> deviceJobList = jobService.selectListByJobTypeAndDatasourceIds(new Long[]{sceneModelTagId}, 4);
                try {
                    for (DeviceJob job : deviceJobList) {
                        DeviceJob deviceJob = new DeviceJob();
                        deviceJob.setJobId(job.getJobId());
                        deviceJob.setJobGroup(job.getJobGroup());
                        deviceJob.setStatus(1 == sceneModelData.getEnable() ? 0 : 1);
                        jobService.changeStatus(deviceJob);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return i;
    }

    @Override
    public List<SceneModelData> selectSceneModelDataListByIds(List<Long> ids) {
        return sceneModelDataMapper.selectBatchIds(ids);
    }

    private LambdaQueryWrapper<SceneModelData> buildQueryWrapper(SceneModelData query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SceneModelData> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SceneModelData::getId, query.getId());
        lqw.eq(query.getSceneModelId() != null, SceneModelData::getSceneModelId, query.getSceneModelId());
        lqw.eq(query.getSceneModelDeviceId() != null, SceneModelData::getSceneModelDeviceId, query.getSceneModelDeviceId());
        lqw.eq(query.getVariableType() != null, SceneModelData::getVariableType, query.getVariableType());
        lqw.eq(query.getDatasourceId() != null, SceneModelData::getDatasourceId, query.getDatasourceId());
        lqw.eq(query.getEnable() != null, SceneModelData::getEnable, query.getEnable());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), SceneModelData::getDelFlag, query.getDelFlag());
        lqw.like(StringUtils.isNotBlank(query.getSourceName()), SceneModelData::getSourceName, query.getSourceName());
        lqw.eq(StringUtils.isNotBlank(query.getIdentifier()), SceneModelData::getIdentifier, query.getIdentifier());
        lqw.eq(query.getType() != null, SceneModelData::getType, query.getType());
        lqw.eq(StringUtils.isNotBlank(query.getUnit()), SceneModelData::getUnit, query.getUnit());
        return lqw;
    }
}
