package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.SceneModelConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.scenemodel.CusDeviceVO;
import com.fastbee.iot.model.vo.SceneModelDeviceVO;
import com.fastbee.iot.model.vo.SceneModelVO;
import com.fastbee.iot.model.vo.SipRelationVO;
import com.fastbee.iot.service.*;
import com.fastbee.system.service.ISysDeptService;

/**
 * 场景管理Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-20
 */
@Service
public class SceneModelServiceImpl extends ServiceImpl<SceneModelMapper,SceneModel> implements ISceneModelService
{
    @Resource
    private SceneModelMapper sceneModelMapper;

    @Resource
    private ISceneModelDeviceService sceneModelDeviceService;


    @Resource
    private SceneModelDataMapper sceneModelDataMapper;

    @Resource
    private SceneModelTagMapper sceneModelTagMapper;

    @Resource
    private SceneTagPointsMapper sceneTagPointsMapper;
    @Resource
    private ISipRelationService sipRelationService;
    @Resource
    private ISysDeptService sysDeptService;

    @Resource
    private IDeviceJobService deviceJobService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ISceneModelTagService sceneModelTagService;

    /**
     * 查询场景管理
     *
     * @param sceneModel 场景管理
     * @return 场景管理
     */
    @Override
    @DataScope(deptAlias = "sm", userAlias = "sm")
    public SceneModelVO selectSceneModelBySceneModelId(SceneModel sceneModel)
    {
        SceneModelVO sceneModelVO = sceneModelMapper.selectSceneModelBySceneModelId(sceneModel);
        if (null == sceneModelVO) {
            return null;
        }
        //查询关联的监控设备
        SipRelation sipRelation = new SipRelation();
        sipRelation.setReSceneModelId(sceneModelVO.getSceneModelId());
        List<SipRelationVO> sipRelationVOList = sipRelationService.selectSipRelationList(sipRelation).getRecords();
        sceneModelVO.setSipRelationVOList(sipRelationVOList);
        SceneModelDevice sceneModelDevice = new SceneModelDevice();
        sceneModelDevice.setSceneModelId(sceneModelVO.getSceneModelId());
        List<SceneModelDeviceVO> sceneModelDeviceVOList = sceneModelDeviceService.listSceneModelDeviceVO(sceneModelDevice);
        if (CollectionUtils.isEmpty(sceneModelDeviceVOList)) {
            return sceneModelVO;
        }
        List<CusDeviceVO> cusDeviceVOList = new ArrayList<>();
        for (SceneModelDeviceVO modelDevice : sceneModelDeviceVOList) {
            if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(modelDevice.getVariableType())) {
                CusDeviceVO cusDeviceVO = new CusDeviceVO();
                cusDeviceVO.setName(modelDevice.getName());
                cusDeviceVO.setProductId(modelDevice.getProductId());
                cusDeviceVO.setSerialNumber(modelDevice.getSerialNumber());
                cusDeviceVOList.add(cusDeviceVO);
            }
        }
        sceneModelVO.setCusDeviceList(cusDeviceVOList);
        sceneModelVO.setSceneModelDeviceVOList(CollectionUtils.isNotEmpty(sceneModelDeviceVOList) ? sceneModelDeviceVOList : new ArrayList<>());
        return sceneModelVO;
    }

    /**
     * 查询场景管理列表
     *
     * @param sceneModelVO 场景管理
     * @return 场景管理
     */
    @Override
    public List<SceneModelVO> selectSceneModelList(SceneModelVO sceneModelVO)
    {
        SysUser user = getLoginUser().getUser();
        if (null == sceneModelVO.getTenantId()) {
            sceneModelVO.setTenantId(user.getDept().getDeptUserId());
        }
        if (null != sceneModelVO.getDeptId()) {
            SysDept sysDept = sysDeptService.selectDeptById(sceneModelVO.getDeptId());
            sceneModelVO.setTenantId(sysDept.getDeptUserId());
        }
        SceneModel sceneModel = SceneModelConvert.INSTANCE.convertSceneModel(sceneModelVO);
        LambdaQueryWrapper<SceneModel> queryWrapper = this.buildQueryWrapper(sceneModel);
        List<SceneModel> modelList = baseMapper.selectList(queryWrapper);
        return SceneModelConvert.INSTANCE.convertSceneModelVOList(modelList);
    }

    /**
     * 查询场景管理分页列表
     *
     * @param sceneModelVO 场景管理
     * @return 场景管理
     */
    @Override
    @DataScope(deptAlias = "sm", userAlias = "sm")
    public Page<SceneModelVO> pageSceneModelVO(SceneModelVO sceneModelVO) {
        if (null != sceneModelVO.getDeptId()) {
            sysDeptService.checkDeptDataScope(sceneModelVO.getDeptId());
            SysDept sysDept = sysDeptService.getById(sceneModelVO.getDeptId());
            sceneModelVO.setTenantId(sysDept.getDeptUserId());
            sceneModelVO.setParams(null);
        }
        Page<SceneModelVO> voPage = sceneModelMapper.selectSceneModelVoPage(new Page<>(sceneModelVO.getPageNum(), sceneModelVO.getPageSize()), sceneModelVO);
        if (0 == voPage.getTotal()) {
            return new Page<>();
        }
        List<SceneModelVO> modelVOList = voPage.getRecords();
        List<String> guidList = modelVOList.stream().map(SceneModelVO::getGuid).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(guidList)) {
            List<SceneModelVO> scadaList = sceneModelMapper.selectListScadaIdByGuidS(guidList);
            Map<String, Long> map = scadaList.stream().collect(Collectors.toMap(SceneModelVO::getGuid, SceneModelVO::getScadaId));
            for (SceneModelVO sceneModel1 : modelVOList) {
                Long scadaId = map.get(sceneModel1.getGuid());
                sceneModel1.setScadaId(scadaId);
            }
        }
        return voPage;
    }

    private LambdaQueryWrapper<SceneModel> buildQueryWrapper(SceneModel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SceneModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getTenantId() != null, SceneModel::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getSceneModelName()), SceneModel::getSceneModelName, query.getSceneModelName());
        lqw.eq(query.getStatus() != null, SceneModel::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getGuid()), SceneModel::getGuid, query.getGuid());
        lqw.eq(StringUtils.isNotBlank(query.getSceneDesc()), SceneModel::getSceneDesc, query.getSceneDesc());
        lqw.eq(StringUtils.isNotBlank(query.getImgUrl()), SceneModel::getImgUrl, query.getImgUrl());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SceneModel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增场景管理
     *
     * @param sceneModel 场景管理
     * @return 结果
     */
    @Override
    public int insertSceneModel(SceneModel sceneModel)
    {
        SysUser user = getLoginUser().getUser();
        sceneModel.setTenantId(user.getDept().getDeptUserId());
        sceneModel.setCreateBy(user.getUserName());
        sceneModel.setUpdateBy(user.getUserName());
        int result = sceneModelMapper.insert(sceneModel);
        if (result > 0) {
            for (SceneModelVariableTypeEnum sceneModelVariableTypeEnum : SceneModelVariableTypeEnum.ADD_LIST) {
                SceneModelDevice sceneModelDevice = new SceneModelDevice();
                sceneModelDevice.setSceneModelId(sceneModel.getSceneModelId());
                sceneModelDevice.setVariableType(sceneModelVariableTypeEnum.getType());
                sceneModelDevice.setName(sceneModelVariableTypeEnum.getName());
                sceneModelDevice.setAllEnable(1);
                sceneModelDevice.setSort(sceneModelVariableTypeEnum.getType());
                sceneModelDevice.setCreateBy(user.getUserName());
                sceneModelDevice.setCreateBy(user.getUserName());
                sceneModelDeviceService.save(sceneModelDevice);
            }
        }
        return result;
    }

    /**
     * 修改场景管理
     *
     * @param sceneModel 场景管理
     * @return 结果
     */
    @Override
    public AjaxResult updateSceneModel(SceneModelVO sceneModel)
    {
        SysUser user = getLoginUser().getUser();
        sceneModel.setUpdateBy(user.getUserName());
        SceneModel oldSceneModel = sceneModelMapper.selectById(sceneModel.getSceneModelId());
        SysDept sysDept = sysDeptService.selectDeptById(sceneModel.getDeptId());
        if (!oldSceneModel.getTenantId().equals(sysDept.getDeptUserId())) {
            SceneModelDevice sceneModelDevice = new SceneModelDevice();
            sceneModelDevice.setSceneModelId(sceneModel.getSceneModelId());
            sceneModelDevice.setVariableType(SceneModelVariableTypeEnum.THINGS_MODEL.getType());
            List<SceneModelDeviceVO> sceneModelDeviceVOList = sceneModelDeviceService.listSceneModelDeviceVO(sceneModelDevice);
            boolean anyMatch = sceneModelDeviceVOList.stream().anyMatch(s -> !s.getTenantId().equals(sysDept.getDeptUserId()));
            if (anyMatch) {
                return AjaxResult.error(MessageUtils.message("sceneModel.device.not.belong.current.tenant"));
            } else {
                sceneModel.setTenantId(sysDept.getDeptUserId());
            }
        }
        SceneModel model = SceneModelConvert.INSTANCE.convertSceneModel(sceneModel);
        return sceneModelMapper.updateById(model) > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 批量删除场景管理
     *
     * @param sceneModelIds 需要删除的场景管理主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelBySceneModelIds(Long[] sceneModelIds)
    {
        List<Long> idList = Arrays.asList(sceneModelIds);
        int i = sceneModelMapper.deleteBatchIds(idList);
        if (i > 0) {
            sceneModelDeviceService.deleteBySceneModelIds(idList);
            LambdaQueryWrapper<SceneModelData> sceneModelDataWrapper = new LambdaQueryWrapper<>();
            sceneModelDataWrapper.in(SceneModelData::getSceneModelId, idList);
            sceneModelDataMapper.delete(sceneModelDataWrapper);
            sceneTagPointsMapper.deleteBySceneModelIds(sceneModelIds);
            LambdaQueryWrapper<SceneModelTag> sceneModelTagWrapper = new LambdaQueryWrapper<>();
            sceneModelTagWrapper.in(SceneModelTag::getSceneModelId, idList);
            List<SceneModelTag> sceneModelTags = sceneModelTagMapper.selectList(sceneModelTagWrapper);
            sceneModelTagMapper.delete(sceneModelTagWrapper);
            if (CollectionUtils.isNotEmpty(sceneModelTags)) {
                Long[] tagIds = sceneModelTags.stream().map(SceneModelTag::getId).toArray(Long[]::new);
                // 批量删除定时任务
                try {
                    deviceJobService.deleteJobByJobTypeAndDatasourceIds(tagIds, 4);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
            for (Long sceneModelId : sceneModelIds) {
                String key = RedisKeyBuilder.buildSceneModelTagCacheKey(sceneModelId);
                // 删除缓存
                redisCache.deleteObject(key);
            }
        }
        return i;
    }

    /**
     * 删除场景管理信息
     *
     * @param sceneModelId 场景管理主键
     * @return 结果
     */
    @Override
    public int deleteSceneModelBySceneModelId(Long sceneModelId)
    {
        return sceneModelMapper.deleteById(sceneModelId);
    }

    @Override
    @DataScope()
    public Page<SceneModelVO> delList(SceneModel sceneModel) {
        return sceneModelMapper.delList(new Page<>(sceneModel.getPageNum(), sceneModel.getPageSize()),sceneModel);
    }

    @Override
    public AjaxResult physicalDeleteSceneModel(Long[] sceneModelIds) {
        int ids = sceneModelMapper.phyDeleteBatchIds(Arrays.asList(sceneModelIds));
        if (ids > 0) {
            sceneModelDeviceService.phyDeleteBySceneModelIds(Arrays.asList(sceneModelIds));
            sceneModelDataMapper.phyDeleteBySceneModelIds(Arrays.asList(sceneModelIds));
            sceneTagPointsMapper.phyDeleteBySceneModelIds(sceneModelIds);
            sceneModelTagMapper.phyDeleteBySceneModelIds(Arrays.asList(sceneModelIds));
        }
        return ids > 0 ? AjaxResult.success() : AjaxResult.error();

    }

    @Override
    public AjaxResult restoreSceneModel(Long sceneModelId) {
        List<SceneModel> sceneModelList = sceneModelMapper.selectByModelId(sceneModelId);
        if (sceneModelList.size() > 1) {
            return AjaxResult.error("场景id重复,无法还原");
        }
        int restore = sceneModelMapper.restoreById(sceneModelId);
        if (restore > 0) {
            sceneModelDeviceService.restoreBySceneModelId(sceneModelId);
            sceneModelDataMapper.restoreBySceneModelId(sceneModelId);
            sceneModelTagMapper.restoreBySceneModelId(sceneModelId);
            List<SceneModelTag> sceneModelTagList = sceneModelTagService.selectEnableTagBySceneModelId(sceneModelId);
            if (CollectionUtils.isNotEmpty(sceneModelTagList)) {
                sceneModelTagService.restoreTagJob(sceneModelTagList);
            }
            sceneTagPointsMapper.restoreBySceneModelId(sceneModelId);
        }
        return restore > 0 ? AjaxResult.success() : AjaxResult.error("场景还原失败");
    }

}
