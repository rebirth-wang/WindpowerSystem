package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.core.text.Convert;
import com.fastbee.common.enums.Language;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.mybatis.LambdaQueryWrapperX;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.ServletUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.convert.ThingsModelConvert;
import com.fastbee.iot.convert.ThingsModelTagConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.enums.ThingsModelBoolEnum;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.ImportThingsModelInput;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModelPerm;
import com.fastbee.iot.model.ThingsModels.IdentifierVO;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.modbus.ModbusAndThingsVO;
import com.fastbee.iot.model.varTemp.EnumClass;
import com.fastbee.iot.model.vo.DeviceUserVO;
import com.fastbee.iot.model.vo.ThingsModelSimVO;
import com.fastbee.iot.model.vo.ThingsModelTagVO;
import com.fastbee.iot.model.vo.ThingsModelVO;
import com.fastbee.iot.service.IDeviceUserService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.iot.service.IThingsModelTemplateService;
import com.fastbee.iot.service.IThingsModelTranslateService;

/**
 * 物模型Service业务层处理
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Service
@Slf4j
public class ThingsModelServiceImpl extends ServiceImpl<ThingsModelMapper, ThingsModel> implements IThingsModelService {

    @Resource
    private ThingsModelMapper thingsModelMapper;
    @Resource
    private ThingsModelTemplateMapper thingsModelTemplateMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisCache redisCache;
    @Resource
    private SceneModelDataMapper sceneModelDataMapper;
    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Resource
    private ITSLValueCache thingModelCache;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private ThingsModelTranslateMapper thingsModelTranslateMapper;
    @Resource
    private DeviceShareMapper shareMapper;
    @Resource
    private IDeviceUserService deviceUserService;
    @Resource
    private ThingsModelTagMapper thingsModelTagMapper;
    @Resource
    private IThingsModelTranslateService translateService;
    @Resource
    IThingsModelTemplateService thingsModelTemplateService;


    /**
     * 查询物模型
     *
     * @param modelId 物模型主键
     * @return 物模型
     */
    @Override
    public ThingsModelVO selectThingsModelVoByModelId(Long modelId) {
        ThingsModel thingsModel = thingsModelMapper.selectById(modelId);
        if (null == thingsModel) {
            return null;
        }
        ThingsModelVO thingsModelVO = ThingsModelConvert.INSTANCE.convertThingsModelVO(thingsModel);
        ThingsModelTranslate thingsModelTranslate = thingsModelTranslateMapper.selectById(modelId);
        if (Language.EN.getValue().equals(SecurityUtils.getLanguage())) {
            thingsModelVO.setModelName(Objects.isNull(thingsModelTranslate) ? null : thingsModelTranslate.getEnUs());
        }
        thingsModelVO.setModelName_zh_CN(Objects.isNull(thingsModelTranslate) ? null : thingsModelTranslate.getZhCn());
        thingsModelVO.setModelName_en_US(Objects.isNull(thingsModelTranslate) ? null : thingsModelTranslate.getEnUs());
        LambdaQueryWrapper<ThingsModelTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThingsModelTag::getModelId, modelId);
        List<ThingsModelTag> thingsModelTagList = thingsModelTagMapper.selectList(queryWrapper);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(thingsModelTagList)) {
            thingsModelVO.setTagVOList(ThingsModelTagConvert.INSTANCE.convertThingsModelTagVOList(thingsModelTagList));
        }
        return thingsModelVO;
    }

    /**
     * 查询单个物模型
     *
     * @param thingsModel 物模型
     * @return 单个物模型
     */
    @Override
    public ThingsModelVO selectSingleThingsModel(ThingsModel thingsModel) {
        LambdaQueryWrapper<ThingsModel> queryWrapper = buildQueryWrapper(thingsModel);
        ThingsModel model = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(model)) {
            return null;
        }
        ThingsModelVO modelVO = ThingsModelConvert.INSTANCE.convertThingsModelVO(model);
        if (Language.EN.getValue().equals(SecurityUtils.getLanguage())) {
            ThingsModelTranslate thingsModelTranslate = thingsModelTranslateMapper.selectById(model.getModelId());
            modelVO.setModelName(Objects.isNull(thingsModelTranslate) ? null : thingsModelTranslate.getEnUs());
        }
        return modelVO;
    }

    /**
     * 查询物模型分页列表
     *
     * @param thingsModelVO 物模型
     * @return 物模型
     */
    @Override
    public Page<ThingsModelVO> pageThingsModelVO(ThingsModelVO thingsModelVO) {
        thingsModelVO.setLanguage(SecurityUtils.getLanguage());
        return baseMapper.pageThingsModelVO(new Page<>(thingsModelVO.getPageNum(), thingsModelVO.getPageSize()), thingsModelVO);
    }

    @Override
    public void topThingsModel(ThingsModelVO thingsModelVO) {
        // 置顶逻辑
        // 例如：将当前物模型的排序字段设置为最大值
        thingsModelMapper.updateSortToTop(thingsModelVO);
    }

    @Override
    public void moveUpThingsModel(ThingsModelVO thingsModelVO) {
        // 执行交换操作
        thingsModelMapper.swapSortWithPrevious(thingsModelVO);
    }

    @Override
    public void moveDownThingsModel(ThingsModelVO thingsModelVO) {
        // 执行交换操作
        thingsModelMapper.swapSortWithNext(thingsModelVO);
    }

    /**
     * 查询物模型列表
     *
     * @param thingsModelVO 物模型
     * @return 物模型
     */
    @Override
    public List<ThingsModelVO> selectThingsModelList(ThingsModelVO thingsModelVO) {
        thingsModelVO.setLanguage(SecurityUtils.getLanguage());
        return thingsModelMapper.selectThingsModelList(thingsModelVO);
    }

    /**
     * 查询物模型对应分享设备用户权限列表
     *
     * @param productId 产品编号
     * @return 物模型
     */
    @Override
    public List<ThingsModelPerm> selectThingsModelPermList(Long productId, Long deviceId, Long userId) {

        LambdaQueryWrapperX<DeviceShare> wrapperX = new LambdaQueryWrapperX<>();
        wrapperX.eq(DeviceShare::getDeviceId, deviceId)
                .eq(DeviceShare::getUserId, userId);
        DeviceShare share = shareMapper.selectOne(wrapperX);
        List<ThingsModelPerm> permList = thingsModelMapper.selectThingsModelPermList(productId, SecurityUtils.getLanguage());
        DeviceUserVO deviceUserVO = deviceUserService.selectDeviceUserByDeviceId(deviceId);
        if (deviceUserVO == null || !deviceUserVO.getUserId().equals(userId)) {
            if (share != null && StringUtils.isNotBlank(share.getPerms())) {
                Set<String> permSet = new HashSet<>(Arrays.asList(share.getPerms().split("\\s*,\\s*")));
                // 创建一个临时结果列表
                List<ThingsModelPerm> filteredList = new ArrayList<>();
                for (ThingsModelPerm perm : permList) {
                    if (perm.getIdentifier() != null && permSet.contains(perm.getIdentifier())) {
                        filteredList.add(perm);
                        permSet.remove(perm.getIdentifier());
                    }
                }
                // 将 permSet 中剩余的标识符作为新对象加入结果
                for (String identifier : permSet) {
                    ThingsModelPerm extraPerm = new ThingsModelPerm();
                    extraPerm.setIdentifier(identifier);
                    extraPerm.setModelName(identifier);
                    filteredList.add(extraPerm);
                }
                permList = filteredList;
            }
        }
        return permList;
    }


    /**
     * 新增物模型
     *
     * @param thingsModelVO 物模型
     * @return 结果
     */
    @Override
    public int insertThingsModel(ThingsModelVO thingsModelVO) {
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            thingsModelVO.setTenantId(user.getDept().getDeptUserId());
            thingsModelVO.setTenantName(user.getDept().getDeptName());
        } else {
            thingsModelVO.setTenantId(user.getUserId());
            thingsModelVO.setTenantName(user.getUserName());
        }
        thingsModelVO.setCreateBy(user.getUserName());
        thingsModelVO.setCreateTime(DateUtils.getNowDate());
        // 查询当前 productId 对应的 model_order 最大值

        ThingsModel thingsModel = ThingsModelConvert.INSTANCE.convertThingsModel(thingsModelVO);
        // 转换数组、对象、数组对象
        LinkedList<ThingsModel> thingsModelList = this.changeArrayOrObject(thingsModel);
        // 校验重复物模型标识
        List<String> identifierList = thingsModelList.stream().map(ThingsModel::getIdentifier).collect(Collectors.toList());
        List<String> repeatIdentifierList = thingsModelMapper.checkIdentifierList(thingsModel.getProductId(), identifierList);
        if (!CollectionUtils.isEmpty(repeatIdentifierList)) {
            return 2;
        }
        boolean result = baseMapper.insertBatch(thingsModelList);
        if (!result) {
            return 0;
        }
        for (ThingsModel model : thingsModelList) {
            ThingsModelTranslate translate = new ThingsModelTranslate();
            translate.setId(model.getModelId());
            translate.setZhCn(model.getModelName());
            translate.setEnUs(model.getModelName_en_US());
            translate.setProductId(model.getProductId());
            translateService.insertWithCache(translate);
        }

        Long modelId = thingsModelList.get(0).getModelId();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(thingsModelVO.getTagVOList())) {
            for (ThingsModelTagVO thingsModelTagVO : thingsModelVO.getTagVOList()) {
                ThingsModelTag thingsModelTag = ThingsModelTagConvert.INSTANCE.convertThingsModelTag(thingsModelTagVO);
                thingsModelTag.setModelId(modelId);
                thingsModelTagMapper.insert(thingsModelTag);
            }
        }
        // 更新redis缓存
        itslCache.setCacheThingsModelByProductId(thingsModel.getProductId());
        // 更新场景管理变量
        List<SceneModelDevice> sceneModelDeviceList = sceneModelDeviceMapper.listDeviceByProductId(thingsModel.getProductId());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(sceneModelDeviceList)) {
            return 1;
        }
        for (SceneModelDevice sceneModelDevice : sceneModelDeviceList) {
            List<SceneModelData> modelDataList = getSceneModelData(sceneModelDevice, thingsModelList);
            sceneModelDataMapper.insertBatch(modelDataList);
        }
        return 1;
    }

    private LinkedList<ThingsModel> changeArrayOrObject(ThingsModel thingsModel) {
        LinkedList<ThingsModel> resultList = new LinkedList<>();
        Datatype datatype = JSONObject.parseObject(thingsModel.getSpecs(), Datatype.class);
        LinkedList<Integer> arrayIndex = datatype.getArrayIndex();
        Integer maxModelOrder = thingsModelMapper.selectMaxModelOrderByProductId(thingsModel.getProductId());
        int startModelOrder = (maxModelOrder != null) ? maxModelOrder + 1 : 1;
        if ("array".equals(datatype.getType()) && "object".equals(datatype.getArrayType())) {
            List<ThingsModelValueItem> params = datatype.getParams();
            for (int i = 0; i < datatype.getArrayCount(); i++) {
                for (ThingsModelValueItem param : params) {
                    ThingsModel arrayObjectThingsModel = new ThingsModel();
                    BeanUtils.copyProperties(param, arrayObjectThingsModel);
                    arrayObjectThingsModel.setProductId(thingsModel.getProductId());
                    arrayObjectThingsModel.setProductName(thingsModel.getProductName());
                    arrayObjectThingsModel.setTenantId(thingsModel.getTenantId());
                    arrayObjectThingsModel.setTenantName(thingsModel.getTenantName());
                    int index;
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(arrayIndex)) {
                        index = arrayIndex.get(i);
                    } else {
                        index = i;
                    }
                    if (index < 10) {
                        arrayObjectThingsModel.setIdentifier("array_0" + index + "_" + param.getId());
                    } else {
                        arrayObjectThingsModel.setIdentifier("array_" + index + "_" + param.getId());
                    }
                    arrayObjectThingsModel.setModelName(thingsModel.getModelName() + index + "_" + param.getName());

                    arrayObjectThingsModel.setType(thingsModel.getType());
                    arrayObjectThingsModel.setDatatype(param.getDatatype().getType());
                    param.getDatatype().setParentType("3");
                    param.getDatatype().setParentName(thingsModel.getModelName());
                    param.getDatatype().setParentIdentifier(thingsModel.getIdentifier());
                    param.getDatatype().setParentIndexName(thingsModel.getModelName() + index);
                    arrayObjectThingsModel.setSpecs(JSONObject.toJSONString(param.getDatatype()));
                    arrayObjectThingsModel.setIsReadonly(param.getIsReadonly());
                    arrayObjectThingsModel.setIsSharePerm(param.getIsSharePerm());
                    arrayObjectThingsModel.setModelOrder(startModelOrder++);
                    if (null == arrayObjectThingsModel.getIsChart()) {
                        arrayObjectThingsModel.setIsChart(0);
                    }
                    if (null == arrayObjectThingsModel.getIsMonitor()) {
                        arrayObjectThingsModel.setIsMonitor(0);
                    }
                    arrayObjectThingsModel.setCreateBy(thingsModel.getCreateBy());
                    arrayObjectThingsModel.setUpdateBy(thingsModel.getUpdateBy());
                    arrayObjectThingsModel.setModelName_en_US(thingsModel.getModelName_en_US());
                    resultList.add(arrayObjectThingsModel);
                }
            }
        } else if ("array".equals(datatype.getType())) {
            for (int i = 0; i < datatype.getArrayCount(); i++) {
                ThingsModel arrayThingsModel = new ThingsModel();
                BeanUtils.copyProperties(thingsModel, arrayThingsModel);
                arrayThingsModel.setModelId(null);
                Datatype arrayDatatype = new Datatype();
                int index;
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(arrayIndex)) {
                    index = arrayIndex.get(i);
                } else {
                    index = i;
                }
                if (index < 10) {
                    arrayThingsModel.setIdentifier("array_0" + index + "_" + thingsModel.getIdentifier());
                } else {
                    arrayThingsModel.setIdentifier("array_" + index + "_" + thingsModel.getIdentifier());
                }
                arrayThingsModel.setModelName(thingsModel.getModelName() + index);
                arrayDatatype.setType(datatype.getArrayType());
                arrayDatatype.setArrayCount(null);
                arrayDatatype.setArrayType(null);
                arrayDatatype.setFalseText(null);
                arrayDatatype.setParentType("2");
                arrayDatatype.setParentName(thingsModel.getModelName());
                arrayDatatype.setParentIdentifier(thingsModel.getIdentifier());
                arrayThingsModel.setDatatype(datatype.getArrayType());
                arrayThingsModel.setSpecs(JSONObject.toJSONString(arrayDatatype));
                arrayThingsModel.setIsReadonly(thingsModel.getIsReadonly());
                arrayThingsModel.setIsSharePerm(thingsModel.getIsSharePerm());
                arrayThingsModel.setModelOrder(startModelOrder++);
                arrayThingsModel.setModelName_en_US(thingsModel.getModelName_en_US());
                if (null == arrayThingsModel.getIsChart()) {
                    arrayThingsModel.setIsChart(0);
                }
                if (null == arrayThingsModel.getIsMonitor()) {
                    arrayThingsModel.setIsMonitor(0);
                }
                resultList.add(arrayThingsModel);
            }
        } else if ("object".equals(datatype.getType())) {
            for (ThingsModelValueItem objectThingsModel : datatype.getParams()) {
                ThingsModel newObjectThingsModel = new ThingsModel();
                BeanUtils.copyProperties(objectThingsModel, newObjectThingsModel);
                newObjectThingsModel.setProductId(thingsModel.getProductId());
                newObjectThingsModel.setProductName(thingsModel.getProductName());
                newObjectThingsModel.setTenantId(thingsModel.getTenantId());
                newObjectThingsModel.setTenantName(thingsModel.getTenantName());
                newObjectThingsModel.setType(thingsModel.getType());
                newObjectThingsModel.setIdentifier(objectThingsModel.getId());
                newObjectThingsModel.setModelName(thingsModel.getModelName() + "_" + objectThingsModel.getName());
                newObjectThingsModel.setDatatype(objectThingsModel.getDatatype().getType());
                objectThingsModel.getDatatype().setParentType("1");
                objectThingsModel.getDatatype().setParentName(thingsModel.getModelName());
                objectThingsModel.getDatatype().setParentIdentifier(thingsModel.getIdentifier());
                newObjectThingsModel.setSpecs(JSONObject.toJSONString(objectThingsModel.getDatatype()));
                newObjectThingsModel.setModelOrder(startModelOrder++);
                newObjectThingsModel.setIsChart(objectThingsModel.getIsChart());
                newObjectThingsModel.setIsMonitor(objectThingsModel.getIsMonitor());
                newObjectThingsModel.setIsReadonly(objectThingsModel.getIsReadonly());
                newObjectThingsModel.setIsSharePerm(objectThingsModel.getIsSharePerm());
                newObjectThingsModel.setModelName_en_US(thingsModel.getModelName_en_US());
                if (null == newObjectThingsModel.getIsChart()) {
                    newObjectThingsModel.setIsChart(0);
                }
                if (null == newObjectThingsModel.getIsMonitor()) {
                    newObjectThingsModel.setIsMonitor(0);
                }
                newObjectThingsModel.setCreateBy(thingsModel.getCreateBy());
                newObjectThingsModel.setUpdateBy(thingsModel.getUpdateBy());
                resultList.add(newObjectThingsModel);
            }
        } else {
            thingsModel.setModelId(null);
            thingsModel.setModelOrder(startModelOrder);
            resultList.add(thingsModel);
        }
        return resultList;
    }

    private static List<SceneModelData> getSceneModelData(SceneModelDevice sceneModelDevice, LinkedList<ThingsModel> thingsModelList) {
        List<SceneModelData> modelDataList = new ArrayList<>();
        for (ThingsModel thingsModel : thingsModelList) {
            SceneModelData addSceneModelData = new SceneModelData();
            addSceneModelData.setSceneModelId(sceneModelDevice.getSceneModelId());
            addSceneModelData.setSceneModelDeviceId(sceneModelDevice.getId());
            addSceneModelData.setVariableType(SceneModelVariableTypeEnum.THINGS_MODEL.getType());
            addSceneModelData.setDatasourceId(thingsModel.getModelId());
            addSceneModelData.setEnable(1);
            addSceneModelData.setIdentifier(thingsModel.getIdentifier());
            addSceneModelData.setSourceName(thingsModel.getModelName());
            addSceneModelData.setType(thingsModel.getType());
            Datatype datatype = JSON.parseObject(thingsModel.getSpecs(), Datatype.class);
            addSceneModelData.setUnit(datatype.getUnit());
            modelDataList.add(addSceneModelData);
        }
        return modelDataList;
    }

    /**
     * 导入通用物模型
     *
     * @param input
     * @return
     */
    @Override
    public int importByTemplateIds(ImportThingsModelInput input) {
        ThingsModelVO thingsModelVO = new ThingsModelVO();
        thingsModelVO.setProductId(input.getProductId());
        List<ThingsModelVO> thingsModelVOList = this.selectThingsModelList(thingsModelVO);
        SysUser user = getLoginUser().getUser();

        // 查询当前 productId 对应的 model_order 最大值
        Integer maxModelOrder = thingsModelMapper.selectMaxModelOrderByProductId(input.getProductId());
        int startModelOrder = (maxModelOrder != null) ? maxModelOrder+1 : 1;
        // 根据ID集合获取通用物模型列表
        List<ThingsModelTemplate> templateList = thingsModelTemplateMapper.selectThingsModelTemplateByTemplateIds(input.getTemplateIds(), SecurityUtils.getLanguage());
        //转换为产品物模型，并批量插入
        LinkedList<ThingsModel> list = new LinkedList<>();
        int repeatCount = 0;
        if (templateList.size() > 1) {
            Set<String> identifierSet = new HashSet<>(templateList.size());
            for (ThingsModelTemplate template : templateList) {
                String currentIdentifier = template.getIdentifier();
                if (identifierSet.contains(currentIdentifier)) {
                    repeatCount = repeatCount + 1;
                    return repeatCount;
                }
                identifierSet.add(currentIdentifier);
            }
        }
        for (ThingsModelTemplate template : templateList) {
            ThingsModel thingsModel = new ThingsModel();
            BeanUtils.copyProperties(template, thingsModel);
            if (user.getDept() == null) {
                throw new ServiceException(MessageUtils.message("user.dept.not.exists"));
            }
            thingsModel.setTenantId(user.getDept().getDeptUserId());
            thingsModel.setTenantName(user.getDept().getDeptUserName());
            thingsModel.setCreateBy(user.getUserName());
            thingsModel.setCreateTime(DateUtils.getNowDate());
            thingsModel.setProductId(input.getProductId());
            thingsModel.setProductName(input.getProductName());
            thingsModel.setModelId(template.getTemplateId());
            thingsModel.setModelName(template.getTemplateName());
            thingsModel.setIsReadonly(template.getIsReadonly());
            thingsModel.setIsMonitor(template.getIsMonitor());
            thingsModel.setIsChart(template.getIsChart());
            thingsModel.setIsHistory(template.getIsHistory());
            thingsModel.setModelName_en_US(template.getTemplateName_en_US());

            LinkedList<ThingsModel> thingsModels = this.changeArrayOrObject(thingsModel);
            for (ThingsModel model : thingsModels) {
                boolean isRepeat = thingsModelVOList.stream().anyMatch(x -> x.getIdentifier().equals(model.getIdentifier()));
                if (isRepeat) {
                    repeatCount = repeatCount + 1;
                } else {
                    model.setModelOrder(startModelOrder++);
                    list.add(model);
                }
            }
        }
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
            return repeatCount;
        }
        boolean result = thingsModelMapper.insertBatch(list);
        for (ThingsModel model : list) {
            ThingsModelTranslate translate = new ThingsModelTranslate();
            translate.setId(model.getModelId());
            translate.setZhCn(model.getModelName());
            translate.setEnUs(model.getModelName_en_US());
            translate.setProductId(model.getProductId());
            translateService.insertWithCache(translate);
        }
        if (result) {
            // 更新场景管理变量
            List<SceneModelDevice> sceneModelDeviceList = sceneModelDeviceMapper.listDeviceByProductId(input.getProductId());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(sceneModelDeviceList)) {
                List<SceneModelData> sceneModelDataList = new ArrayList<>();
                for (SceneModelDevice sceneModelDevice : sceneModelDeviceList) {
                    sceneModelDataList.addAll(getSceneModelData(sceneModelDevice, list));
                }
                sceneModelDataMapper.insertBatch(sceneModelDataList);
            }
        }
        //更新redis缓存
        itslCache.setCacheThingsModelByProductId(input.getProductId());
        return repeatCount;
    }

    /**
     * 修改物模型
     *
     * @param thingsModelVO 物模型
     * @return 结果
     */
    @Override
    public int updateThingsModel(ThingsModelVO thingsModelVO) {
        ThingsModel thingsModel = ThingsModelConvert.INSTANCE.convertThingsModel(thingsModelVO);
        // 校验场景是否被应用到计算公式
        int count = sceneModelDataMapper.checkIsApplyAliasFormule(Collections.singletonList(thingsModel.getModelId()), SceneModelVariableTypeEnum.THINGS_MODEL.getType());
        if (count > 0) {
            throw new ServiceException(MessageUtils.message("things.model.update.fail.quote.the.scene.variable.formula.please.delete"));
        }
        thingsModel.setUpdateBy(getUsername());
        ThingsModelVO queryThingsModel = new ThingsModelVO();
        queryThingsModel.setProductId(thingsModel.getProductId());
        List<ThingsModelVO> list = this.selectThingsModelList(queryThingsModel);
        boolean isRepeat = list.stream().anyMatch(x -> x.getIdentifier().equals(thingsModel.getIdentifier()) && x.getModelId().longValue() != thingsModel.getModelId());
        if (!isRepeat) {
            thingsModel.setUpdateTime(DateUtils.getNowDate());
            int result;
            LinkedList<ThingsModel> thingsModelList = new LinkedList<>();
            if ("array".equals(thingsModel.getDatatype()) || "object".equals(thingsModel.getDatatype())) {
                result = baseMapper.deleteById(thingsModel.getModelId());
                translateService.deleteWithCacheByIds(new Long[]{thingsModel.getModelId()}, false);
                thingsModelList = this.changeArrayOrObject(thingsModel);
                baseMapper.insertBatch(thingsModelList);
                for (ThingsModel model : thingsModelList) {
                    ThingsModelTranslate translate = new ThingsModelTranslate();
                    translate.setId(model.getModelId());
                    translate.setZhCn(model.getModelName());
                    translate.setEnUs(model.getModelName_en_US());
                    translate.setProductId(model.getProductId());
                    translateService.insertWithCache(translate);
                }

            } else {
                result = baseMapper.updateById(thingsModel);
                if (result > 0) {
                    ThingsModelTranslate translate = new ThingsModelTranslate();
                    translate.setId(thingsModel.getModelId());
                    translate.setZhCn(thingsModel.getModelName());
                    translate.setEnUs(thingsModel.getModelName_en_US());
                    translate.setProductId(thingsModel.getProductId());
                    translateService.updateWithCache(translate);
                }
                LambdaQueryWrapper<ThingsModelTag> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ThingsModelTag::getModelId, thingsModel.getModelId());
                thingsModelTagMapper.delete(queryWrapper);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(thingsModelVO.getTagVOList())) {
                    for (ThingsModelTagVO thingsModelTagVO : thingsModelVO.getTagVOList()) {
                        ThingsModelTag thingsModelTag = ThingsModelTagConvert.INSTANCE.convertThingsModelTag(thingsModelTagVO);
                        thingsModelTag.setModelId(thingsModel.getModelId());
                        thingsModelTagMapper.insert(thingsModelTag);
                    }
                }
                thingsModelList.add(thingsModel);
            }
            // 更新redis缓存
            itslCache.setCacheThingsModelByProductId(thingsModel.getProductId());
            if (result > 0) {
                List<SceneModelData> sceneModelDataList = sceneModelDataMapper.selectSceneDeviceThingsModelList(thingsModel.getModelId());
                if (org.apache.commons.collections4.CollectionUtils.isEmpty(sceneModelDataList)) {
                    return result;
                }
                LambdaQueryWrapper<SceneModelData> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SceneModelData::getDatasourceId, thingsModel.getModelId());
                queryWrapper.eq(SceneModelData::getVariableType, SceneModelVariableTypeEnum.THINGS_MODEL.getType());
                sceneModelDataMapper.delete(queryWrapper);
                for (SceneModelData sceneModelData : sceneModelDataList) {
                    SceneModelDevice sceneModelDevice = new SceneModelDevice();
                    sceneModelDevice.setSceneModelId(sceneModelData.getSceneModelId());
                    sceneModelDevice.setId(sceneModelData.getSceneModelDeviceId());
                    List<SceneModelData> modelDataList = getSceneModelData(sceneModelDevice, thingsModelList);
                    sceneModelDataMapper.insertBatch(modelDataList);
                }
            }
            return result;
        }
        return 2;
    }

    /**
     * 批量删除物模型
     *
     * @param modelIds 需要删除的物模型主键
     * @return 结果
     */
    @Override
    public int deleteThingsModelByModelIds(Long[] modelIds) {
        // 校验场景是否被应用到计算公式
        int count = sceneModelDataMapper.checkIsApplyAliasFormule(Arrays.asList(modelIds), SceneModelVariableTypeEnum.THINGS_MODEL.getType());
        if (count > 0) {
            throw new ServiceException(MessageUtils.message("things.model.delete.fail.quote.the.scene.variable.formula.please.delete"));
        }
        ThingsModelVO thingsModelVO = this.selectThingsModelVoByModelId(modelIds[0]);
        int result = thingsModelMapper.deleteBatchIds(Arrays.asList(modelIds));
        if (result > 0) {
            LambdaQueryWrapper<SceneModelData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SceneModelData::getDatasourceId, Arrays.asList(modelIds));
            queryWrapper.eq(SceneModelData::getVariableType, SceneModelVariableTypeEnum.THINGS_MODEL.getType());
            sceneModelDataMapper.delete(queryWrapper);
        }
        // 更新redis缓存
        if (thingsModelVO != null) {
            itslCache.setCacheThingsModelByProductId(thingsModelVO.getProductId());
        }

        return result;
    }

    /**
     * 删除物模型信息
     *
     * @param modelId 物模型主键
     * @return 结果
     */
    @Override
    public int deleteThingsModelByModelId(Long modelId) {
        ThingsModelVO thingsModelVO = this.selectThingsModelVoByModelId(modelId);
        int result = thingsModelMapper.deleteById(modelId);
        // 更新redis缓存
        if (thingsModelVO != null) {
            itslCache.setCacheThingsModelByProductId(thingsModelVO.getProductId());
        }
        return result;
    }

    /**
     * 获取单个物模型获取
     *
     * @param productId
     * @param identify
     * @return
     */
    @Override
    public ThingsModelValueItem getSingleThingModels(Long productId, String identify) {
        ThingsModelValueItem item = itslCache.getSingleThingModels(productId, identify);
        if (!Objects.isNull(item)) {
            return item;
        }
        ThingsModel thingsModel = new ThingsModel();
        thingsModel.setIdentifier(identify);
        thingsModel.setProductId(productId);
        ThingsModelVO selectModel = this.selectSingleThingsModel(thingsModel);
        item = new ThingsModelValueItem();
        if (null != selectModel) {
            BeanUtils.copyProperties(selectModel, item);
            item.setId(selectModel.getIdentifier());
            item.setName(selectModel.getModelName());
            item.setDatatype(JSONObject.parseObject(selectModel.getSpecs(), Datatype.class));
            item.setOrder(selectModel.getModelOrder());
            item.setFormula(selectModel.getFormula());
            itslCache.setCacheThingsModelByProductId(productId);
            return item;
        } else {
            return null;
        }
    }


    /**
     * 导入采集点数据
     *
     * @param lists 数据列表
     * @return 结果
     */
    public String importData(List<ThingsModelVO> lists, Long productId) {
        if (null == productId || CollectionUtils.isEmpty(lists)) {
            throw new ServiceException(MessageUtils.message("things.model.import.data.exception"));
        }
        int success = 0;
        int failure = 0;
        StringBuilder succSb = new StringBuilder();
        StringBuilder failSb = new StringBuilder();
        boolean result = false;
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new ServiceException(MessageUtils.message("things.model.import.data.exception"));
        }
        Integer maxModelOrder = thingsModelMapper.selectMaxModelOrderByProductId(productId);
        int startModelOrder = (maxModelOrder != null) ? maxModelOrder+1 : 1;
        for (ThingsModelVO model : lists) {
            try {
                model.setProductId(product.getProductId());
                model.setProductName(product.getProductName());

                model.setType(ThingsModelTypeEnum.getCodeByText(model.getTypeStr()));
                model.setIsChart(ThingsModelBoolEnum.getCodeByText(model.getIsChartStr()));
                model.setIsMonitor(ThingsModelBoolEnum.getCodeByText(model.getIsMonitorStr()));
                model.setIsHistory(ThingsModelBoolEnum.getCodeByText(model.getIsHistoryStr()));
                model.setIsReadonly(ThingsModelBoolEnum.getCodeByText(model.getIsReadonlyStr()));
                model.setIsSharePerm(ThingsModelBoolEnum.getCodeByText(model.getIsSharePermStr()));
                model.setModelOrder(startModelOrder++);

                //处理数据定义
                this.parseSpecs(model);
                result = this.importData(model);
                success++;
                if (StringUtils.isNotEmpty(succSb.toString())) {
                    succSb.append("<br/>");
                }
                succSb.append("第").append(success).append("个物模型：").append(model.getModelName());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("导入错误：", e);
                failure++;
                String errorMsg = e instanceof IllegalArgumentException ? e.getMessage() : "导入解析失败";
                failSb.append("<br/>").append("第").append(failure).append("个物模型：").append(model.getModelName()).append("，失败原因：").append(errorMsg);
            }
        }
        if (result) {
            log.error("标识符不能重复");
            failure++;
            failSb.append("<br/>").append("第").append(failure).append("个物模型：").append("标识符不能重复");
            throw new ServiceException(failSb.toString());
        }
        if (failure > 0) {
            throw new ServiceException(failSb.toString());
        }
        if (success > 0) {
            // 更新redis缓存
            itslCache.setCacheThingsModelByProductId(productId);
        }
        return succSb.toString();
    }


    /**
     * 导入单个物模型
     */
    private boolean importData(ThingsModelVO thingsModelVO) {
        LambdaQueryWrapper<ThingsModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThingsModel::getProductId, thingsModelVO.getProductId());
        List<ThingsModel> thingsModelList = baseMapper.selectList(queryWrapper);
        Boolean isRepeat = thingsModelList.stream().anyMatch(x -> x.getIdentifier().equals(thingsModelVO.getIdentifier()));
        if (!isRepeat) {
            SysUser user = getLoginUser().getUser();
            if (null != user.getDeptId()) {
                thingsModelVO.setTenantId(user.getDept().getDeptUserId());
                thingsModelVO.setTenantName(user.getDept().getDeptUserName());
            } else {
                thingsModelVO.setTenantId(user.getUserId());
                thingsModelVO.setTenantName(user.getUserName());
            }
            thingsModelVO.setCreateBy(user.getUserName());
            thingsModelVO.setCreateTime(DateUtils.getNowDate());
            ThingsModel thingsModel = ThingsModelConvert.INSTANCE.convertThingsModel(thingsModelVO);
            thingsModelMapper.insert(thingsModel);

            ThingsModelTranslate translate = new ThingsModelTranslate();
            translate.setId(thingsModel.getModelId());
            translate.setZhCn(thingsModel.getModelName());
            translate.setEnUs(thingsModel.getModelName_en_US());
            translate.setProductId(thingsModel.getProductId());
            translateService.insertWithCache(translate);

            return false;
        }
        return true;
    }

    private void parseSpecs(ThingsModelVO model) {
        JSONObject specs = new JSONObject();
        String datatype = model.getDatatype();
        String limitValue = model.getLimitValue();
        if (limitValue != null && !"".equals(limitValue)) {
            String[] values = limitValue.split("/");
            switch (datatype) {
                case "integer":
                case "decimal":
                    specs.put("max", values[1]);
                    specs.put("min", values[0]);
                    specs.put("type", datatype);
                    specs.put("unit", model.getUnit());
                    specs.put("step", 1);
                    break;
                case "bool":
                    specs.put("type", datatype);
                    specs.put("trueText", values[1]);
                    specs.put("falseText", values[0]);
                    break;
                case "string":
                    specs.put("type", datatype);
                    specs.put("maxLength", values[1]);
                    break;
                case "enum":
                    List<EnumClass> list = new ArrayList<>();
                    for (String value : values) {
                        String[] params = value.split(":");
                        EnumClass enumCls = new EnumClass();
                        enumCls.setText(params[1]);
                        enumCls.setValue(params[0]);
                        list.add(enumCls);
                    }
                    specs.put("showWay","select");
                    specs.put("type", datatype);
                    specs.put("enumList", list);
                    break;
            }
            model.setSpecs(specs.toJSONString());
        } else {
            switch (datatype) {
                case "integer":
                case "decimal":
                    specs.put("max", "100");
                    specs.put("min", "0");
                    specs.put("type", datatype);
                    specs.put("unit", model.getUnit() != null ? model.getUnit() : "");
                    specs.put("step", 1);
                    break;
                case "bool":
                    specs.put("type", datatype);
                    specs.put("trueText", "打开");
                    specs.put("falseText", "关闭");
                    break;
                case "string":
                    specs.put("type", datatype);
                    specs.put("maxLength", "1024");
                    break;
                case "enum":
                    String[] values = new String[0];
                    if (limitValue != null) {
                        values = limitValue.split("/");
                    }
                    List<EnumClass> list = new ArrayList<>();
                    for (String value : values) {
                        String[] params = value.split(":");
                        EnumClass enumCls = new EnumClass();
                        enumCls.setText(params[1]);
                        enumCls.setValue(params[0]);
                        list.add(enumCls);
                    }
                    specs.put("showWay","select");
                    specs.put("type", datatype);
                    specs.put("enumList", list);
                    break;
            }
            model.setSpecs(specs.toJSONString());
        }

    }

    @Override
    public void parseSpecsForExport(ThingsModelVO model) {
        String specs = model.getSpecs();
        if (StringUtils.isEmpty(specs)) {
            return;
        }

        JSONObject specsObj = JSONObject.parseObject(specs);
        String datatype = model.getDatatype();

        switch (datatype) {
            case "integer":
            case "decimal":
                // 导出单位和限制值
                model.setUnit(specsObj.getString("unit"));
                String min = specsObj.getString("min");
                String max = specsObj.getString("max");
                if (StringUtils.isNotEmpty(min) && StringUtils.isNotEmpty(max)) {
                    model.setLimitValue(min + "/" + max);
                }
                break;
            case "bool":
                // 导出布尔值的文本描述
                String trueText = specsObj.getString("trueText");
                String falseText = specsObj.getString("falseText");
                if (StringUtils.isNotEmpty(trueText) && StringUtils.isNotEmpty(falseText)) {
                    model.setLimitValue(falseText + "/" + trueText);
                }
                break;
            case "string":
                // 导出字符串的最大长度
                String maxLength = specsObj.getString("maxLength");
                if (StringUtils.isNotEmpty(maxLength)) {
                    model.setLimitValue("0/" + maxLength);
                }
                break;
            case "enum":
                // 导出枚举值列表
                JSONArray enumList = specsObj.getJSONArray("enumList");
                if (enumList != null && !enumList.isEmpty()) {
                    StringBuilder limitValue = new StringBuilder();
                    for (int i = 0; i < enumList.size(); i++) {
                        JSONObject enumItem = enumList.getJSONObject(i);
                        String value = enumItem.getString("value");
                        String text = enumItem.getString("text");
                        if (i > 0) {
                            limitValue.append("/");
                        }
                        limitValue.append(value).append(":").append(text);
                    }
                    model.setLimitValue(limitValue.toString());
                }
                break;
        }
        // 1. 模型类别：type → typeStr（导出标准文字：属性/功能/事件）
        model.setTypeStr(ThingsModelTypeEnum.getTextByCode(model.getType()));
        // 2. 布尔型字段：数字→文字（统一导出：是/否）
        model.setIsChartStr(ThingsModelBoolEnum.getTextByCode(model.getIsChart()));
        model.setIsMonitorStr(ThingsModelBoolEnum.getTextByCode(model.getIsMonitor()));
        model.setIsHistoryStr(ThingsModelBoolEnum.getTextByCode(model.getIsHistory()));
        model.setIsReadonlyStr(ThingsModelBoolEnum.getTextByCode(model.getIsReadonly()));
        model.setIsSharePermStr(ThingsModelBoolEnum.getTextByCode(model.getIsSharePerm()));
    }

    @Override
    public List<ThingsModelVO> getTemplateExampleData(List<String> identifiers) {
        // 查询模板数据
        List<ThingsModelTemplate> templateList = thingsModelTemplateService.selectByIdentifiers(identifiers);

        // 转换为 ThingsModelVO 列表
        List<ThingsModelVO> exampleList = new ArrayList<>();
        for (ThingsModelTemplate template : templateList) {
            ThingsModelVO vo = new ThingsModelVO();
            vo.setModelName(template.getTemplateName());
            vo.setIdentifier(template.getIdentifier());
            vo.setModelName_en_US(template.getTemplateName_en_US() != null ? template.getTemplateName_en_US() : template.getIdentifier());
            vo.setType(template.getType());
            vo.setDatatype(template.getDatatype());
            vo.setSpecs(template.getSpecs());
            vo.setFormula(template.getFormula());
            vo.setIsChart(template.getIsChart());
            vo.setIsMonitor(template.getIsMonitor());
            vo.setIsHistory(template.getIsHistory());
            vo.setIsReadonly(template.getIsReadonly());
            vo.setIsSharePerm(template.getIsSharePerm());
            this.parseSpecsForExport(vo);
            exampleList.add(vo);
        }

        return exampleList;
    }

    /**
     * 根据产品id删除 产品物模型以及物模型缓存
     *
     * @param productId
     */
    @Override
    public void deleteProductThingsModelAndCacheByProductId(Long productId) {
        LambdaQueryWrapper<ThingsModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThingsModel::getProductId, productId);
        thingsModelMapper.delete(queryWrapper);
        String cacheKey = RedisKeyBuilder.buildTSLCacheKey(productId);
        redisCache.deleteObject(cacheKey);
    }


    @Override
    public List<ThingsModelSimVO> listSimByProductIds(List<Long> productIdList) {
        return thingsModelMapper.listSimByProductIds(productIdList, SecurityUtils.getLanguage());
    }

    @Override
    public String getSpecsByProductIdAndIdentifier(Long productId, String identifier) {
        LambdaQueryWrapper<ThingsModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThingsModel::getProductId, productId);
        queryWrapper.eq(ThingsModel::getIdentifier, identifier);
        queryWrapper.select(ThingsModel::getSpecs);
        return baseMapper.selectOne(queryWrapper).getSpecs();
    }

    /**
     * 获取modbus配置可选择物模型
     *
     * @param productId
     * @return
     */
    @Override
    public Page<ModbusAndThingsVO> getModbusConfigUnSelectThingsModel(Long productId) {
        Integer pageNum = Convert.toInt(ServletUtils.getParameter("pageNum"), 1);
        Integer pageSize = Convert.toInt(ServletUtils.getParameter("pageSize"), Integer.MAX_VALUE);
        Page<ModbusAndThingsVO> modbusAndThingsVOList = thingsModelMapper.getModbusConfigUnSelectThingsModel(new Page<>(pageNum, pageSize), productId, SecurityUtils.getLanguage());
        for (ModbusAndThingsVO thingsVO : modbusAndThingsVOList.getRecords()) {
            thingsVO.setIsSelectIo(false);
            thingsVO.setIsSelectData(false);
            if (thingsVO.getIsSelect()) {
                if (thingsVO.getDataType().equals("bool")) {
                    thingsVO.setIsSelectIo(true);
                } else {
                    thingsVO.setIsSelectData(true);
                }
            }
        }
        return modbusAndThingsVOList;
    }


    @Override
    public IdentifierVO revertObjectOrArrayIdentifier(String identifier) {
        IdentifierVO identifierVO = new IdentifierVO();
        if (identifier.startsWith("array_")) {
            int i1 = org.apache.commons.lang3.StringUtils.ordinalIndexOf(identifier, "_", 1);
            int i2 = org.apache.commons.lang3.StringUtils.ordinalIndexOf(identifier, "_", 2);
            identifierVO.setIdentifier(identifier.substring(i2 + 1));
            identifierVO.setIndex(Integer.parseInt(identifier.substring(++i1, i2)));
        } else {
            identifierVO.setIdentifier(identifier);
        }
        return identifierVO;
    }

    @Override
    public ValueItem getCacheIdentifierValue(Long productId, String serialNumber, String identifier) {
        ValueItem valueItem = thingModelCache.getCacheIdentifier(productId, serialNumber, identifier);
        if (null == valueItem) {
            valueItem = new ValueItem();
            valueItem.setValue("");
        }
        return valueItem;
    }

    @Override
    public List<ThingsModelDTO> getCacheByProductId(Long productId, String serialNumber) {
        if (null == productId) {
            return new ArrayList<>();
        }
        Map<String, ThingsModelValueItem> map = itslCache.getCacheThMapByProductId(productId);
        List<ThingsModelDTO> thingsModelDTOList = new ArrayList<>();
        for (Map.Entry<String, ThingsModelValueItem> entry : map.entrySet()) {
            thingsModelDTOList.add(this.changeThingsModelDTO(entry.getValue()));
        }
        thingsModelDTOList.sort(Comparator.comparing(ThingsModelDTO::getModelOrder));
        if (StringUtils.isEmpty(serialNumber)) {
            return thingsModelDTOList;
        }
        // 获取缓存值
        List<ValueItem> thingsModelValueItems = thingModelCache.getCacheDeviceStatus(productId, serialNumber);
        Map<String, ValueItem> thingsModelValueItemMap = thingsModelValueItems.stream().collect(Collectors.toMap(ValueItem::getId, Function.identity()));
        for (ThingsModelDTO thingsModelDTO : thingsModelDTOList) {
            String identifier = thingsModelDTO.getIdentifier();
            ValueItem thingsModelValueItem = thingsModelValueItemMap.get(identifier);
            if (null == thingsModelValueItem) {
                thingsModelDTO.setValue("");
                continue;
            }
            thingsModelDTO.setValue(thingsModelValueItem.getValue());
            thingsModelDTO.setShadow(thingsModelValueItem.getShadow());
            if (!Objects.isNull(thingsModelValueItem.getTs())) {
                thingsModelDTO.setTs(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, thingsModelValueItem.getTs()));
            }
        }
        return thingsModelDTOList;
    }

    private ThingsModelDTO changeThingsModelDTO(ThingsModelValueItem valueItem) {
        ThingsModelDTO thingsModelDTO = ThingsModelConvert.INSTANCE.convertThingsModelDTO(valueItem);
        thingsModelDTO.setIdentifier(valueItem.getId());
        thingsModelDTO.setModelName(valueItem.getName());
        thingsModelDTO.setModelOrder(valueItem.getOrder());
        thingsModelDTO.setSpecs(JSON.toJSONString(valueItem.getDatatype()));
        if (Objects.nonNull(valueItem.getConfig()) && StringUtils.isNotEmpty(valueItem.getConfig().getAddress())) {
            thingsModelDTO.setAddress(valueItem.getConfig().getAddress());
        }
        return thingsModelDTO;
    }

    @Override
    public List<ThingsModel> selectThingsModelListByModelIds(List<Long> modelIdList) {
        return thingsModelMapper.selectThingsModelListByModelIds(modelIdList);
    }

    /**
     * 根据设备编号获取读写物模型
     *
     * @param deviceId
     * @return
     */
    @Override
    public Page<ThingsModel> selectThingsModelBySerialNumber(Long deviceId) {
        Integer pageNum = Convert.toInt(ServletUtils.getParameter("pageNum"), 1);
        Integer pageSize = Convert.toInt(ServletUtils.getParameter("pageSize"), Integer.MAX_VALUE);
        return thingsModelMapper.selectThingsModelBySerialNumber(new Page<>(pageNum, pageSize), deviceId, SecurityUtils.getLanguage());
    }

    @Override
    public AjaxResult refresh(Long productId) {
        // 查出对象、数组类老物模型
        LambdaQueryWrapper<ThingsModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThingsModel::getProductId, productId);
        List<ThingsModel> thingsModelList = baseMapper.selectList(queryWrapper);
        List<ThingsModel> modelList = thingsModelList.stream().filter(t -> "array".equals(t.getDatatype()) || "object".equals(t.getDatatype())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(modelList)) {
            return AjaxResult.success(MessageUtils.message("thingsModel.array.or.object.no.need.update"));
        }
        // 先判断有没有物模型被场景运算变量使用
        List<Long> modelIdList = modelList.stream().map(ThingsModel::getModelId).collect(Collectors.toList());
        int count = sceneModelDataMapper.checkIsApplyAliasFormule(modelIdList, SceneModelVariableTypeEnum.THINGS_MODEL.getType());
        if (count > 0) {
            throw new ServiceException(MessageUtils.message("things.model.update.fail.quote.the.scene.variable.formula.please.delete"));
        }
        // 转换成新的物模型
        LinkedList<ThingsModel> addThingsModelList = new LinkedList<>();
        for (ThingsModel thingsModel : modelList) {
            addThingsModelList.addAll(this.changeArrayOrObject(thingsModel));
        }
        boolean result = thingsModelMapper.insertBatch(addThingsModelList);
        if (!result) {
            return AjaxResult.error(MessageUtils.message("sync.fail.please.try.again"));
        }
        // 删除老物模型
        Long[] modelIds = modelIdList.toArray(new Long[0]);
        thingsModelMapper.deleteBatchIds(Arrays.asList(modelIds));
        // 刷新缓存
        // 更新redis缓存
        itslCache.setCacheThingsModelByProductId(productId);
        return AjaxResult.success(MessageUtils.message("sync.success"));
    }

    private LambdaQueryWrapper<ThingsModel> buildQueryWrapper(ThingsModel query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ThingsModel> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getModelId() != null, ThingsModel::getModelId, query.getModelId());
        lqw.like(StringUtils.isNotBlank(query.getModelName()), ThingsModel::getModelName, query.getModelName());
        lqw.eq(query.getProductId() != null, ThingsModel::getProductId, query.getProductId());
        lqw.like(StringUtils.isNotBlank(query.getProductName()), ThingsModel::getProductName, query.getProductName());
        lqw.eq(query.getTenantId() != null, ThingsModel::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), ThingsModel::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getIdentifier()), ThingsModel::getIdentifier, query.getIdentifier());
        lqw.eq(query.getType() != null, ThingsModel::getType, query.getType());
        lqw.eq(StringUtils.isNotBlank(query.getDatatype()), ThingsModel::getDatatype, query.getDatatype());
        lqw.eq(StringUtils.isNotBlank(query.getSpecs()), ThingsModel::getSpecs, query.getSpecs());
        lqw.eq(StringUtils.isNotBlank(query.getFormula()), ThingsModel::getFormula, query.getFormula());
        lqw.eq(query.getIsChart() != null, ThingsModel::getIsChart, query.getIsChart());
        lqw.eq(query.getIsMonitor() != null, ThingsModel::getIsMonitor, query.getIsMonitor());
        lqw.eq(query.getIsHistory() != null, ThingsModel::getIsHistory, query.getIsHistory());
        lqw.eq(query.getIsReadonly() != null, ThingsModel::getIsReadonly, query.getIsReadonly());
        lqw.eq(query.getIsSharePerm() != null, ThingsModel::getIsSharePerm, query.getIsSharePerm());
        lqw.eq(query.getIsApp() != null, ThingsModel::getIsApp, query.getIsApp());
        lqw.eq(query.getModelOrder() != null, ThingsModel::getModelOrder, query.getModelOrder());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), ThingsModel::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ThingsModel::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, ThingsModel::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ThingsModel::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, ThingsModel::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), ThingsModel::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ThingsModel::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

}
