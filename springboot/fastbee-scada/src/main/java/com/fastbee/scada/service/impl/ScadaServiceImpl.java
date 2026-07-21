package com.fastbee.scada.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.constant.Constants;
import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.enums.DeviceLogTypeEnum;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.VerifyCodeUtils;
import com.fastbee.common.utils.bean.BeanUtils;
import com.fastbee.common.utils.file.FileUploadUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.convert.AlertLogConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.mapper.AlertLogMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModelItem.ThingsModel;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.scenemodel.SceneModelTagCacheVO;
import com.fastbee.iot.model.vo.AlertLogVO;
import com.fastbee.iot.model.vo.EventLogVO;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.service.IDeviceLogService;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.scada.convert.ScadaConvert;
import com.fastbee.scada.domain.Scada;
import com.fastbee.scada.domain.ScadaDeviceBind;
import com.fastbee.scada.domain.ScadaDeviceShare;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.enums.ScadaSharePassStatusEnum;
import com.fastbee.scada.enums.ScadaTypeEnum;
import com.fastbee.scada.mapper.ScadaDeviceBindMapper;
import com.fastbee.scada.mapper.ScadaDeviceShareMapper;
import com.fastbee.scada.mapper.ScadaGalleryMapper;
import com.fastbee.scada.mapper.ScadaMapper;
import com.fastbee.scada.service.IScadaDeviceShareService;
import com.fastbee.scada.service.IScadaService;
import com.fastbee.scada.utils.ScadaCollectionUtils;
import com.fastbee.scada.vo.*;
import com.fastbee.system.service.sys.SysLoginService;

/**
 * 组态中心Service业务层处理
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Service
public class ScadaServiceImpl extends ServiceImpl<ScadaMapper,Scada> implements IScadaService
{
    @Resource
    private ScadaMapper scadaMapper;
    @Resource
    private ScadaGalleryMapper scadaGalleryMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ScadaDeviceBindMapper scadaDeviceBindMapper;
    @Resource
    private IDeviceLogService deviceLogService;
    @Resource
    private IDeviceService deviceService;
    @Resource
    private IThingsModelService thingsModelService;
    @Resource
    private IScadaDeviceShareService scadaDeviceShareService;
    @Resource
    private SysLoginService sysLoginService;
    @Resource
    private ScadaDeviceShareMapper scadaDeviceShareMapper;
    @Resource
    private SceneModelTagCache sceneModelTagCache;
    @Resource
    private ITSLCache itslCache;
    @Resource
    private SceneModelTagMapper sceneModelTagMapper;
    @Resource
    private AlertLogMapper alertLogMapper;
    @Resource
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private IDeviceCache deviceCache;

    /**
     * 查询组态中心
     *
     * @param param 组态中心
     * @return 组态中心
     */
    @Override
    @DataScope
    public ScadaVO selectScadaById(Scada param)
    {
        LambdaQueryWrapper<Scada> queryWrapper = this.buildQueryWrapper(param);
        Scada scada = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(scada)) {
            return null;
        }
        ScadaVO scadaVO = ScadaConvert.INSTANCE.convertScadaVO(scada);
        if (ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scada.getType())) {
            Long productId = scadaMapper.selectProductByGuid(scada.getGuid());
            scadaVO.setProductId(productId);
        } else if (ScadaTypeEnum.SCENE_MODEL.getType().equals(scada.getType())) {
            Long sceneModelId = scadaMapper.selectSceneModelByGuid(scada.getGuid());
            scadaVO.setSceneModelId(sceneModelId);
        }
        return scadaVO;
    }

    /**
     * 查询组态页面分页列表
     *
     * @param scada 组态页面
     * @return 组态页面
     */
    @Override
    @DataScope()
    public Page<ScadaVO> pageScadaVO(Scada scada) {
        LambdaQueryWrapper<Scada> lqw = buildQueryWrapper(scada);
        lqw.orderByDesc(Scada::getCreateTime);
        Page<Scada> scadaPage = baseMapper.selectPage(new Page<>(scada.getPageNum(), scada.getPageSize()), lqw);
        Page<ScadaVO> scadaVoPage = ScadaConvert.INSTANCE.convertScadaVOPage(scadaPage);
        if (0 == scadaVoPage.getTotal()) {
            return scadaVoPage;
        }
        List<ScadaVO> scadaVOList = scadaVoPage.getRecords();
        List<String> scadaGuidList = scadaVOList.stream().filter(s -> ScadaTypeEnum.SCENE_MODEL.getType().equals(s.getType())).map(ScadaVO::getGuid).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(scadaGuidList)) {
            List<ScadaVariableSourceVO> sourceVOList = scadaMapper.selectListSceneByGuids(scadaGuidList);
            Map<String, Long> map = sourceVOList.stream().collect(Collectors.toMap(ScadaVariableSourceVO::getGuid, ScadaVariableSourceVO::getSceneModelId, (o, n) -> n));
            for (ScadaVO scada1 : scadaVOList) {
                Long sceneModeId = map.get(scada1.getGuid());
                if (null != sceneModeId) {
                    scada1.setSceneModelId(sceneModeId);
                }
            }
        }
        List<String> scadaProductList = scadaVOList.stream().filter(s -> ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(s.getType())).map(ScadaVO::getGuid).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(scadaProductList)) {
            List<ScadaVariableSourceVO> sourceVOList = scadaMapper.selectListProductByGuids(scadaProductList);
            Map<String, Long> map = sourceVOList.stream().collect(Collectors.toMap(ScadaVariableSourceVO::getGuid, ScadaVariableSourceVO::getProductId, (o, n) -> n));
            for (ScadaVO scada1 : scadaVOList) {
                Long sceneModeId = map.get(scada1.getGuid());
                if (null != sceneModeId) {
                    scada1.setProductId(sceneModeId);
                }
            }
        }
        return scadaVoPage;
    }

    /**
     * 查询组态页面列表
     *
     * @param scada 组态页面
     * @return 组态页面
     */
    @Override
    public List<ScadaVO> listScadaVO(Scada scada) {
        LambdaQueryWrapper<Scada> lqw = buildQueryWrapper(scada);
        List<Scada> scadaList = baseMapper.selectList(lqw);
        return ScadaConvert.INSTANCE.convertScadaVOList(scadaList);
    }

    /**
     * 新增组态中心
     *
     * @param scadaVO 组态中心
     * @return 结果
     */
    @Override
    public AjaxResult insertScada(ScadaVO scadaVO)
    {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error(MessageUtils.message("scada.please.login"));
        }
        if (ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scadaVO.getType())) {
            String guid = scadaMapper.selectGuidByProductId(scadaVO.getProductId());
            if (StringUtils.isNotEmpty(guid)){
                return AjaxResult.warn(MessageUtils.message("scada.product.has.relate.please.select.again"));
            }
        }
        if (ScadaTypeEnum.SCENE_MODEL.getType().equals(scadaVO.getType())) {
            String guid = scadaMapper.selectGuidBySceneModelId(scadaVO.getSceneModelId());
            if (StringUtils.isNotEmpty(guid)){
                return AjaxResult.warn(MessageUtils.message("scada.scene.has.relate.please.select.again"));
            }
        }
        // 多租户版本使用
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            scadaVO.setTenantId(user.getDept().getDeptUserId());
            scadaVO.setTenantName(user.getDept().getDeptName());
        } else {
            scadaVO.setTenantId(user.getUserId());
            scadaVO.setTenantName(user.getUserName());
        }
        scadaVO.setCreateBy(user.getUserName());
        UUID uuid = UUID.randomUUID();
        scadaVO.setGuid(uuid.toString());
        Scada scada = ScadaConvert.INSTANCE.convertScada(scadaVO);
        int result = scadaMapper.insert(scada);
        if (result >0){
            if (ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scadaVO.getType())) {
                scadaMapper.updateProductGuid(scadaVO.getProductId(), uuid.toString());
            }
            if (ScadaTypeEnum.SCENE_MODEL.getType().equals(scadaVO.getType())) {
                scadaMapper.updateSceneModelGuid(scadaVO.getSceneModelId(), uuid.toString());
            }
            return AjaxResult.success(uuid.toString());
        }else {
            return AjaxResult.error();
        }
    }

    /**
     * 修改组态中心
     *
     * @param scadaVO 组态中心
     * @return 结果
     */
    @Override
    public int updateScada(ScadaVO scadaVO)
    {
        if (ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scadaVO.getType()) && null != scadaVO.getProductId()) {
            String guid = scadaMapper.selectGuidByProductId(scadaVO.getProductId());
            if (StringUtils.isNotEmpty(guid) && !guid.equals(scadaVO.getGuid())){
                throw new ServiceException(MessageUtils.message("scada.product.has.relate.please.select.again"), HttpStatus.WARN);
            }
            if (StringUtils.isEmpty(guid)) {
                scadaMapper.deleteProductByGuids(Collections.singletonList(scadaVO.getGuid()));
                scadaMapper.updateProductGuid(scadaVO.getProductId(), scadaVO.getGuid());
            }
        }
        if (ScadaTypeEnum.SCENE_MODEL.getType().equals(scadaVO.getType()) && null != scadaVO.getSceneModelId()) {
            String guid = scadaMapper.selectGuidBySceneModelId(scadaVO.getSceneModelId());
            if (StringUtils.isNotEmpty(guid) && !guid.equals(scadaVO.getGuid())){
                throw new ServiceException(MessageUtils.message("scada.scene.has.relate.please.select.again"), HttpStatus.WARN);
            }
            if (StringUtils.isEmpty(guid)) {
                scadaMapper.deleteSceneModelByGuids(Collections.singletonList(scadaVO.getGuid()));
                scadaMapper.updateSceneModelGuid(scadaVO.getSceneModelId(), scadaVO.getGuid());
            }
        }
        Scada scada = ScadaConvert.INSTANCE.convertScada(scadaVO);
        scada.setUpdateBy(getUsername());
        return scadaMapper.updateById(scada);
    }

    /**
     * 批量删除组态中心
     *
     * @param ids 需要删除的组态中心主键
     * @return 结果
     */
    @Override
    public int deleteScadaByIds(Long[] ids)
    {
        List<Scada> scadaList = scadaMapper.selectScadaListByIds(ids);
        int i = scadaMapper.deleteBatchIds(Arrays.asList(ids));
        if (i > 0) {
            List<String> productGuidList = scadaList.stream().filter(s -> ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(s.getType())).map(Scada::getGuid).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(productGuidList)) {
                scadaMapper.deleteProductByGuids(productGuidList);
                LambdaQueryWrapper<ScadaDeviceShare> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(ScadaDeviceShare::getGuid, productGuidList);
                scadaDeviceShareMapper.delete(queryWrapper);
            }
            List<String> sceneGuidList = scadaList.stream().filter(s -> ScadaTypeEnum.SCENE_MODEL.getType().equals(s.getType())).map(Scada::getGuid).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(sceneGuidList)) {
                scadaMapper.deleteSceneModelByGuids(sceneGuidList);
            }
        }
        return i;
    }

    /**
     * 删除组态中心信息
     *
     * @param id 组态中心主键
     * @return 结果
     */
    @Override
    public int deleteScadaById(Long id)
    {
        return scadaMapper.deleteById(id);
    }

    /**
     * 根据guid获取组态详情
     * @param scadaDateQueryVO 组态查询参数
     * @return
     */
    @Override
    public ScadaVO selectScadaByGuid(ScadaDateQueryVO scadaDateQueryVO) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            if (StringUtils.isEmpty(scadaDateQueryVO.getSerialNumber())) {
                throw new ServiceException(MessageUtils.message("no.operate.permission"));
            }
            Device device = deviceService.selectDeviceBySerialNumber(scadaDateQueryVO.getSerialNumber());
            if (Objects.isNull(device)) {
                return null;
            }
            deviceService.checkDeviceDataScope(device.getDeviceId());
        }
        LambdaQueryWrapper<Scada> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scada::getGuid, scadaDateQueryVO.getGuid());
        Scada scada = scadaMapper.selectOne(queryWrapper);
        if (null == scada) {
            return new ScadaVO();
        }
        if (null != user.getDeptId() && !"fastbee_scada".equals(user.getUserName())) {
            SecurityUtils.checkUserOperatePermission(scada.getTenantId(), scada.getCreateBy());
        }
//        SysUser user = getLoginUser().getUser();
//        if (null != user.getDept()) {
//            Long deptUserId = user.getDept().getDeptUserId();
//            if (!"fastbee_scada".equals(user.getUserName()) && !deptUserId.equals(scada.getTenantId())) {
//                throw new ServiceException(MessageUtils.message("scada.not.allow.view.other.tenant.config"));
//            }
//        }
        if (null == scada.getType()) {
            scada.setType(ScadaTypeEnum.PUBLIC.getType());
        }
        ScadaTypeEnum scadaTypeEnum = ScadaTypeEnum.getByType(scada.getType());
        List<ScadaBindDeviceSimVO> simVOList = new ArrayList<>();
        JSONObject scadaDataVO = null;
        List<JSONObject> pcConfig = new ArrayList<>();
        List<JSONObject> mdConfig = new ArrayList<>();
        if (Boolean.TRUE.equals(scadaDateQueryVO.getInitializedData())) {
            if (StringUtils.isNotEmpty(scada.getScadaData())) {
                scadaDataVO = JSONObject.parseObject(scada.getScadaData());
                Object pcChecked = scadaDataVO.get("pcChecked");
                Object mdChecked = scadaDataVO.get("mdChecked");
                if (Boolean.TRUE.equals(pcChecked) && Boolean.TRUE.equals(mdChecked)) {
                    pcConfig = scadaDataVO.getJSONArray("pcConfig").toList(JSONObject.class);
                    mdConfig = scadaDataVO.getJSONArray("mdConfig").toList(JSONObject.class);
                } else if (Boolean.TRUE.equals(pcChecked) && Boolean.FALSE.equals(mdChecked)) {
                    pcConfig = scadaDataVO.getJSONArray("pcConfig").toList(JSONObject.class);
                } else if (Boolean.TRUE.equals(mdChecked) && Boolean.FALSE.equals(pcChecked)) {
                    mdConfig = scadaDataVO.getJSONArray("mdConfig").toList(JSONObject.class);
                }
            }
        }
        switch (Objects.requireNonNull(scadaTypeEnum)) {
            case PRODUCT_TEMPLATE:
                Long productId = scadaMapper.selectProductByGuid(scadaDateQueryVO.getGuid());
                ScadaBindDeviceSimVO scadaBindDeviceSimVO = new ScadaBindDeviceSimVO();
                scadaBindDeviceSimVO.setProductId(productId);
                scadaDateQueryVO.setProductId(productId);
                simVOList.add(scadaBindDeviceSimVO);
                break;
            case SCENE_MODEL:
                simVOList = scadaMapper.selectSceneModelDeviceListByGuid(scadaDateQueryVO.getGuid());
                break;
            case PUBLIC:
                simVOList = scadaDeviceBindMapper.listDeviceSimByGuid(scada.getGuid());
                break;
            default:
                break;
        }
        if (null != scadaDataVO) {
            if (CollectionUtils.isNotEmpty(pcConfig)) {
                Map<Integer, JSONObject> jsonObjectMap = this.findObjectsByName(pcConfig, scadaDateQueryVO.getPageName());
                for (Map.Entry<Integer, JSONObject> entry : jsonObjectMap.entrySet()) {
                    JSONObject jsonObject = pcConfig.get(entry.getKey());
                    List<JSONObject> components = jsonObject.getJSONArray("components").toList(JSONObject.class);
                    switch (Objects.requireNonNull(scadaTypeEnum)) {
                        case PRODUCT_TEMPLATE:
                            this.handleProductScadaDataModelValue(components, scadaDateQueryVO.getProductId(), scadaDateQueryVO.getSerialNumber());
                            break;
                        case SCENE_MODEL:
                            this.handleSceneScadaDataModelValue(components);
                            break;
                        case PUBLIC:
                            this.handlePublicScadaDataModelValue(components);
                            break;
                        default:
                            break;
                    }
                    jsonObject.put("components", components);
                    pcConfig.set(entry.getKey(), jsonObject);
                }
                scadaDataVO.put("pcConfig", pcConfig);
            }
            if (CollectionUtils.isNotEmpty(mdConfig)) {
                Map<Integer, JSONObject> jsonObjectMap = this.findObjectsByName(mdConfig, scadaDateQueryVO.getPageName());
                for (Map.Entry<Integer, JSONObject> entry : jsonObjectMap.entrySet()) {
                    JSONObject jsonObject = mdConfig.get(entry.getKey());
                    List<JSONObject> components = jsonObject.getJSONArray("components").toList(JSONObject.class);
                    switch (Objects.requireNonNull(scadaTypeEnum)) {
                        case PRODUCT_TEMPLATE:
                            this.handleProductScadaDataModelValue(components, scadaDateQueryVO.getProductId(), scadaDateQueryVO.getSerialNumber());
                            break;
                        case SCENE_MODEL:
                            this.handleSceneScadaDataModelValue(components);
                            break;
                        case PUBLIC:
                            this.handlePublicScadaDataModelValue(components);
                            break;
                        default:
                            break;
                    }
                    jsonObject.put("components", components);
                    mdConfig.set(entry.getKey(), jsonObject);
                }
                scadaDataVO.put("mdConfig", mdConfig);
            }
            scada.setScadaData(JSONObject.toJSONString(scadaDataVO));
        }
        ScadaVO scadaVO = ScadaConvert.INSTANCE.convertScadaVO(scada);
        scadaVO.setBindDeviceList(simVOList);
        return scadaVO;
    }

    /**
     * 从JSONObject列表中筛选对象
     * @param list 待搜索的JSONObject列表
     * @return Map<Integer, JSONObject> 键为索引，值为对应的JSONObject
     */
    public Map<Integer, JSONObject> findObjectsByName(List<JSONObject> list, String pageName) {
        Map<Integer, JSONObject> resultMap = new LinkedHashMap<>();
        if (StringUtils.isEmpty(pageName)) {
            resultMap.put(0, list.get(0));
            return resultMap;
        }

        if (list == null || list.isEmpty()) {
            return resultMap;
        }

        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = list.get(i);
            if (obj == null) continue;

            // 安全获取name属性（避免Key不存在或类型错误）
            try {
                String name = obj.getString("name");
                if (pageName.equals(name)) {
                    resultMap.put(i, obj);
                }
            } catch (Exception e) {
                // 可选：记录日志或处理异常
                System.err.println("Error processing object at index " + i + ": " + e.getMessage());
            }
        }

        return resultMap;
    }

    private void handleProductScadaDataModelValue(List<JSONObject> components, Long productId, String serialNumber) {
        Map<String, ValueItem> identifierMap = new HashMap<>(2);
        for (JSONObject component : components) {
            Object dataBind = component.get("dataBind");
            if (!Objects.isNull(dataBind)) {
                JSONObject data = JSONObject.parseObject(dataBind.toString());
                Object identifier = data.get("identifier");
                if (Objects.nonNull(identifier)) {
                    String identifierStr = identifier.toString();
                    ValueItem existValueItem = identifierMap.get(identifierStr);
                    if (null != existValueItem) {
                        data.put("modelValue",existValueItem.getValue());
                        data.put("modelTime",existValueItem.getTs());
                        component.put("dataBind",data);
                    } else {
                        ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, identifierStr);
                        data.put("modelValue", StringUtils.isEmpty(valueItem.getValue()) ? "" : valueItem.getValue());
                        data.put("modelTime", null != valueItem.getTs() ? valueItem.getTs() : null);
                        component.put("dataBind", data);
                        identifierMap.put(identifierStr, valueItem);
                    }
                }
                Object identifiers = data.get("identifiers");
                if (Objects.nonNull(identifiers)) {
                    String identifiersStr = identifiers.toString().replace("[", "").replace("]","").replace("\"","");
                    LinkedList<String> identifierList = new LinkedList<>(Arrays.asList(identifiersStr.split(",")));
                    LinkedList<String> valueList = new LinkedList<>();
                    LinkedList<Date> timeList = new LinkedList<>();
                    for (String i : identifierList) {
                        ValueItem existValueItem = identifierMap.get(i);
                        if (null != existValueItem) {
                            valueList.add(existValueItem.getValue());
                            timeList.add(existValueItem.getTs());
                        } else {
                            ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, i);
                            identifierMap.put(i, valueItem);
                            valueList.add(valueItem.getValue());
                            timeList.add(valueItem.getTs());
                        }
                    }
                    data.put("modelValues", valueList);
                    data.put("modelTimes", timeList);
                    component.put("dataBind", data);
                }
                JSONArray calcVariatesArray = (JSONArray) data.get("calcVariates");
                if (Objects.nonNull(calcVariatesArray) && !calcVariatesArray.isEmpty()) {
                    List<JSONObject> list = calcVariatesArray.toJavaList(JSONObject.class);
                    for (JSONObject jsonObject : list) {
                        ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, jsonObject.get("identifier").toString());
                        if (Objects.nonNull(valueItem) && StringUtils.isNotEmpty(valueItem.getValue())) {
                            jsonObject.put("modelValue", valueItem.getValue());
                        }
                    }
                    data.put("calcVariates", list);
                    component.put("dataBind", data);
                }
            }
            Object dataAction = component.get("dataAction");
            if (!Objects.isNull(dataAction)) {
                JSONObject dataA = JSONObject.parseObject(dataAction.toString());
                Object identifier = dataA.get("identifier");
                if (Objects.nonNull(identifier)) {
                    String identifierStr = identifier.toString();
                    ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, identifierStr);
                    dataA.put("modelValue", valueItem.getValue());
                    dataA.put("modelTime", valueItem.getTs());
                    component.put("dataAction", dataA);
                }
            }
        }
    }

    private void  handleSceneScadaDataModelValue(List<JSONObject> components) {
        for (JSONObject component : components) {
            Object dataBind = component.get("dataBind");
            if (!Objects.isNull(dataBind)) {
                JSONObject data = JSONObject.parseObject(dataBind.toString());
                Object oSceneModelId = data.get("sceneModelId");
                if (!ObjectUtil.isEmpty(oSceneModelId)) {
                    Long sceneModelId = Long.parseLong(oSceneModelId.toString());
                    Object identifier = data.get("identifier");
                    if (Objects.nonNull(identifier)) {
                        Integer variableType = Integer.parseInt(data.get("variableType").toString());
                        String identifierStr = identifier.toString();
                        String identifierValue = "";
                        String modelTime = null;
                        if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(variableType)) {
                            long productId = Long.parseLong(data.get("productId").toString());
                            ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, data.get("serialNumber").toString(), identifierStr);
                            if (null != valueItem) {
                                identifierValue = valueItem.getValue();
                                modelTime = Objects.nonNull(valueItem.getTs()) ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, valueItem.getTs()) : null;
                            }
                        } else {
                            SceneModelTagCacheVO sceneModelTagValue = sceneModelTagCache.getSceneModelTagValue(sceneModelId, Long.valueOf(identifierStr));
                            if (null != sceneModelTagValue) {
                                identifierValue = sceneModelTagValue.getValue();
                                modelTime = sceneModelTagValue.getTs();
                            }
                        }
                        data.put("modelValue", identifierValue);
                        data.put("modelTime", modelTime);
                        component.put("dataBind", data);
                    }
                    Object identifiers = data.get("identifiers");
                    if (Objects.nonNull(identifiers)) {
                        String identifiersStr = identifiers.toString().replace("[", "").replace("]","").replace("\"","");
                        LinkedList<String> identifierList = new LinkedList<>(Arrays.asList(identifiersStr.split(",")));
                        if (Objects.nonNull(data.get("sceneModelDeviceIds"))) {
                            String sceneModelDeviceIds = data.get("sceneModelDeviceIds").toString().replace("[", "").replace("]", "").replace("\"", "");
                            LinkedList<String> sceneModelDeviceIdList = new LinkedList<>(Arrays.asList(sceneModelDeviceIds.split(",")));
                            // 先单独查数据库，后面看看能不能优化
                            List<ScadaVariableDataVO> scadaVariableDataVOList = scadaMapper.selectSceneModelDataList(identifierList, sceneModelId, null);
                            LinkedList<String> valueList = new LinkedList<>();
                            LinkedList<Date> timeList = new LinkedList<>();
                            for (int i = 0; i < identifierList.size(); i++) {
                                String ident = identifierList.get(i);
                                String sceneModelDeviceId = sceneModelDeviceIdList.get(i);
                                ScadaVariableDataVO scadaVariableDataVO = scadaVariableDataVOList.stream().filter(s -> ident.equals(s.getIdentifier()) && sceneModelDeviceId.equals(s.getSceneModelDeviceId())).findFirst().orElse(null);
                                if (null == scadaVariableDataVO) {
                                    valueList.add("");
                                    timeList.add(null);
                                    continue;
                                }
                                String value = "";
                                Date time = null;
                                if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(scadaVariableDataVO.getVariableType())) {
                                    ValueItem valueItem = thingsModelService.getCacheIdentifierValue(scadaVariableDataVO.getProductId(), scadaVariableDataVO.getSerialNumber(), ident);
                                    if (null != valueItem) {
                                        value = valueItem.getValue();
                                        time = valueItem.getTs();
                                    }
                                } else {
                                    SceneModelTagCacheVO sceneModelTagValue = sceneModelTagCache.getSceneModelTagValue(sceneModelId, Long.valueOf(ident));
                                    if (null != sceneModelTagValue) {
                                        value = sceneModelTagValue.getValue();
                                        time = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, sceneModelTagValue.getTs());
                                    }
                                }
                                valueList.add(value);
                                timeList.add(time);
                            }
                            data.put("modelValues", valueList);
                            data.put("modelTimes", timeList);
                            component.put("dataBind", data);
                        }
                    }
                }
                JSONArray calcVariatesArray = (JSONArray) data.get("calcVariates");
                if (Objects.nonNull(calcVariatesArray) && !calcVariatesArray.isEmpty()) {
                    List<JSONObject> list = calcVariatesArray.toJavaList(JSONObject.class);
                    for (JSONObject jsonObject : list) {
                        Object variableType1 = jsonObject.get("variableType");
                        Object identifier = jsonObject.get("identifier");
                        Object sceneModelId = jsonObject.get("sceneModelId");
                        if (Objects.isNull(variableType1) || Objects.isNull(identifier) || Objects.isNull(sceneModelId)) {
                            continue;
                        }
                        Integer variableType = Integer.parseInt(variableType1.toString());
                        String identifierStr = identifier.toString();
                        Long sceneModelIdL = Long.parseLong(sceneModelId.toString());
                        if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(variableType)) {
                            long productId = Long.parseLong(jsonObject.get("productId").toString());
                            ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, jsonObject.get("serialNumber").toString(), identifierStr);
                            if (null != valueItem && StringUtils.isNotEmpty(valueItem.getValue())) {
                                jsonObject.put("modelValue", valueItem.getValue());
                            }
                        } else {
                            SceneModelTagCacheVO sceneModelTagValue = sceneModelTagCache.getSceneModelTagValue(sceneModelIdL, Long.valueOf(identifierStr));
                            if (null != sceneModelTagValue) {
                                jsonObject.put("modelValue", sceneModelTagValue.getValue());
                            }
                        }
                    }
                    data.put("calcVariates", list);
                    component.put("dataBind", data);
                }
            }
            Object dataAction = component.get("dataAction");
            if (!Objects.isNull(dataAction)) {
                JSONObject dataA = JSONObject.parseObject(dataAction.toString());
                Object oSceneModelId = dataA.get("sceneModelId");
                if (!ObjectUtil.isEmpty(oSceneModelId)) {
                    Long sceneModelId = Long.parseLong(oSceneModelId.toString());
                    Object identifier = dataA.get("identifier");
                    if (Objects.nonNull(identifier)) {
                        Integer variableType = Integer.parseInt(dataA.get("variableType").toString());
                        String identifierStr = identifier.toString();
                        String identifierValue = "";
                        Date modelTime = null;
                        if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(variableType)) {
                            long productId = Long.parseLong(dataA.get("productId").toString());
                            ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, dataA.get("serialNumber").toString(), identifierStr);
                            if (null != valueItem) {
                                identifierValue = valueItem.getValue();
                                modelTime = valueItem.getTs();
                            }
                        } else {
                            SceneModelTagCacheVO sceneModelTagValue = sceneModelTagCache.getSceneModelTagValue(sceneModelId, Long.valueOf(identifierStr));
                            if (null != sceneModelTagValue) {
                                identifierValue = sceneModelTagValue.getValue();
                                modelTime = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, sceneModelTagValue.getTs());
                            }
                        }
                        dataA.put("modelValue", identifierValue);
                        dataA.put("modelTime", modelTime);
                        component.put("dataAction", dataA);
                    }
                }
            }
        }
    }

    private void handlePublicScadaDataModelValue(List<JSONObject> components) {
        for (JSONObject component : components) {
            Object dataBind = component.get("dataBind");
            if (!Objects.isNull(dataBind)) {
                JSONObject data = JSONObject.parseObject(dataBind.toString());
                Object oProductId = data.get("productId");
                Object oSerialNumber = data.get("serialNumber");
                if (ObjectUtil.isNotEmpty(oProductId) && ObjectUtil.isNotEmpty(oSerialNumber)) {
                    Long productId = Long.parseLong(oProductId.toString());
                    String serialNumber = oSerialNumber.toString();
                    Object identifier = data.get("identifier");
                    if (Objects.nonNull(identifier)) {
                        String identifierStr = identifier.toString();
                        ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, identifierStr);
                        data.put("modelValue", StringUtils.isEmpty(valueItem.getValue()) ? "" : valueItem.getValue());
                        data.put("modelTime", null != valueItem.getTs() ? valueItem.getTs() : null);
                        component.put("dataBind", data);
                    }
                    Object identifiers = data.get("identifiers");
                    if (Objects.nonNull(identifiers)) {
                        String identifiersStr = identifiers.toString().replace("[", "").replace("]","").replace("\"","");
                        LinkedList<String> identifierList = new LinkedList<>(Arrays.asList(identifiersStr.split(",")));
                        LinkedList<String> valueList = new LinkedList<>();
                        LinkedList<Date> timeList = new LinkedList<>();
                        for (String i : identifierList) {
                            ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, i);
                            valueList.add(StringUtils.isEmpty(valueItem.getValue()) ? "" : valueItem.getValue());
                            timeList.add(null != valueItem.getTs() ? valueItem.getTs() : null);
                        }
                        data.put("modelValues", valueList);
                        data.put("modelTimes", timeList);
                        component.put("dataBind", data);
                    }
                }
                JSONArray calcVariatesArray = (JSONArray) data.get("calcVariates");
                if (Objects.nonNull(calcVariatesArray) && !calcVariatesArray.isEmpty()) {
                    List<JSONObject> list = calcVariatesArray.toJavaList(JSONObject.class);
                    for (JSONObject jsonObject : list) {
                        Object cProductId = jsonObject.get("productId");
                        Object cSerialNumber = jsonObject.get("serialNumber");
                        if (Objects.isNull(cProductId) || Objects.isNull(cSerialNumber)) {
                            continue;
                        }
                        ValueItem valueItem = thingsModelService.getCacheIdentifierValue(Long.parseLong(cProductId.toString()), cSerialNumber.toString(), jsonObject.get("identifier").toString());
                        if (Objects.nonNull(valueItem) && StringUtils.isNotEmpty(valueItem.getValue())) {
                            jsonObject.put("modelValue", valueItem.getValue());
                        }
                    }
                    data.put("calcVariates", list);
                    component.put("dataBind", data);
                }
            }
            Object dataAction = component.get("dataAction");
            if (!Objects.isNull(dataAction)) {
                JSONObject dataA = JSONObject.parseObject(dataAction.toString());
                Object oProductId = dataA.get("productId");
                Object oSerialNumber = dataA.get("serialNumber");
                if (ObjectUtil.isNotEmpty(oProductId) && ObjectUtil.isNotEmpty(oSerialNumber)) {
                    Long productId = Long.parseLong(oProductId.toString());
                    String serialNumber = oSerialNumber.toString();
                    Object identifier = dataA.get("identifier");
                    if (Objects.nonNull(identifier)) {
                        String identifierStr = identifier.toString();
                        ValueItem valueItem = thingsModelService.getCacheIdentifierValue(productId, serialNumber, identifierStr);
                        dataA.put("modelValue", StringUtils.isEmpty(valueItem.getValue()) ? "" : valueItem.getValue());
                        dataA.put("modelTime", null != valueItem.getTs() ? valueItem.getTs() : null);
                        component.put("dataAction", dataA);
                    }
                }
            }
        }
    }

    @Override
    public AjaxResult uploadGalleryFavorites(MultipartFile file, String categoryName) {
        LoginUser loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {
            return AjaxResult.error(MessageUtils.message("scada.please.login"));
        }
        // 上传文件路径
        String filePath;
        // 上传并返回新文件名称
        try{
            filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
        }catch (Exception e){
            return AjaxResult.error(500,StringUtils.format(MessageUtils.message("scada.upload.gallery.file.fail"), e));
        }
        String fileName = file.getOriginalFilename();
        ScadaGallery scadaGallery = new ScadaGallery();
        scadaGallery.setFileName(fileName);
        scadaGallery.setCategoryName(categoryName);
        scadaGallery.setResourceUrl(filePath);
        Long userId = loginUser.getUserId();
        scadaGallery.setTenantId(userId);
        scadaGallery.setTenantName(loginUser.getUsername());
        int i = scadaGalleryMapper.insert(scadaGallery);
        if (i <= 0) {
            return AjaxResult.error(MessageUtils.message("scada.upload.fail"));
        }
        String key = this.getGalleryFavoritesRedisKey(userId);
        redisCache.sAdd(key, scadaGallery.getId());
        return AjaxResult.success();
    }

    private String getGalleryFavoritesRedisKey(Long userId) {
        return "scada:gallery_favorites_userId_" + userId;
    }

    @Override
    public AjaxResult saveGalleryFavorites(FavoritesVO favoritesVO) {
        LoginUser loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {
            return AjaxResult.error(MessageUtils.message("scada.please.login"));
        }
        Long userId = loginUser.getUserId();
        List<String> idList = StringUtils.str2List(favoritesVO.getIdStr(), ",", true, true);
        String key = this.getGalleryFavoritesRedisKey(userId);
        for (String id : idList) {
            redisCache.sAdd(key, id);
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult deleteGalleryFavorites(Long[] ids) {
        LoginUser loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {
            return AjaxResult.error(MessageUtils.message("scada.please.login"));
        }
        Long userId = loginUser.getUserId();
        String key = this.getGalleryFavoritesRedisKey(userId);
        for (Long id : ids) {
            redisCache.setRemove(key, id);
        }
        // 删除图库
        scadaGalleryMapper.deleteBatchIds(Arrays.asList(ids));
        return AjaxResult.success();
    }

    @Override
    public Page<ScadaGallery> listGalleryFavorites(ScadaGallery scadaGallery) {
        LoginUser loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {
            return new Page<>();
        }
        Long userId = loginUser.getUserId();
        String key = this.getGalleryFavoritesRedisKey(userId);
        Set<Long> cacheSet = redisCache.getCacheSet(key);
        if (CollectionUtils.isEmpty(cacheSet)) {
            return new Page<>();
        }
        LambdaQueryWrapper<ScadaGallery> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ScadaGallery::getId ,cacheSet);
        return scadaGalleryMapper.selectPage(new Page<>(scadaGallery.getPageNum(), scadaGallery.getPageSize()), queryWrapper);
    }

    @Override
    public List<ScadaHistoryModelVO> listThingsModelHistory(ThingsModelHistoryParam param) {
        String serialNumber = param.getSerialNumber();
        List<ThingsModelHistoryParam.ThingsModelSim> thingsModelList = param.getThingsModelList();
        List<ScadaHistoryModelVO> historyModelList = new ArrayList<>();
        List<String> propertyIdentifierList = thingsModelList.stream().filter(t -> 1 == t.getType()).map(ThingsModelHistoryParam.ThingsModelSim::getIdentifier).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(propertyIdentifierList)) {
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setIdentityList(propertyIdentifierList);
            deviceLog.setSerialNumber(serialNumber);
            deviceLog.setBeginTime(param.getBeginTime());
            deviceLog.setEndTime(param.getEndTime());
            List<HistoryModel> historyModelList1 = deviceLogService.listHistory(deviceLog);
            for (HistoryModel historyModel : historyModelList1) {
                ScadaHistoryModelVO scadaHistoryModelVO = new ScadaHistoryModelVO();
                BeanUtils.copyProperties(historyModel, scadaHistoryModelVO);
                scadaHistoryModelVO.setIdentity(historyModel.getIdentify());
                historyModelList.add(scadaHistoryModelVO);
            }
        }
        List<String> functionIdentifierList = thingsModelList.stream().filter(t -> 2 == t.getType()).map(ThingsModelHistoryParam.ThingsModelSim::getIdentifier).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(functionIdentifierList)) {
            FunctionLogVO functionLogVO = new FunctionLogVO();
            functionLogVO.setIdentifyList(functionIdentifierList);
            functionLogVO.setSerialNumber(serialNumber);
            functionLogVO.setBeginTime(DateUtils.dateTime(DateUtils.YY_MM_DD_HH_MM_SS, param.getBeginTime()));
            functionLogVO.setEndTime(DateUtils.dateTime(DateUtils.YY_MM_DD_HH_MM_SS, param.getEndTime()));
            historyModelList.addAll(scadaMapper.listFunctionLogHistory(functionLogVO));
        }
        List<String> eventIdentifierList = thingsModelList.stream().filter(t -> 3 == t.getType()).map(ThingsModelHistoryParam.ThingsModelSim::getIdentifier).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(eventIdentifierList)) {
            EventLogVO eventLogVO = new EventLogVO();
            eventLogVO.setIdentityList(eventIdentifierList);
            eventLogVO.setSerialNumber(serialNumber);
            Map<String, Object> params = new HashMap<>(2);
            params.put("beginTime", param.getBeginTime());
            params.put("endTime",param.getEndTime());
            eventLogVO.setParams(params);
            historyModelList.addAll(scadaMapper.listEventLogHistory(eventLogVO));
        }
        return historyModelList;
    }

    @Override
    public Integer getStatusBySerialNumber(String serialNumber) {
        return scadaMapper.getStatusBySerialNumber(serialNumber);
    }

    @Override
    public ScadaStatisticVO selectStatistic(Long deptId) {
        SysUser user = getLoginUser().getUser();
//        List<SysRole> roles = user.getRoles();
//        for (int i = 0; i < roles.size(); i++) {
//            if (roles.get(i).getRoleKey().equals("tenant")) {
//                // 租户查看产品下所有设备
//                tenantId = user.getUserId();
//            } else if (roles.get(i).getRoleKey().equals("general")) {
//                // 用户查看自己设备
//                userId = user.getUserId();
//            }
//        }
        List<Long> tenantIdList = null;
        Long userId = null;
        if (null != user.getDeptId()) {
            tenantIdList = scadaMapper.selectUserIdByDeptId(user.getDeptId());
        } else {
            userId = user.getUserId();
        }
        // 获取设备、产品和告警数量
        ScadaStatisticVO statistic = scadaMapper.selectDeviceProductAlertCount(tenantIdList, userId);
        if (statistic == null) {
            statistic = new ScadaStatisticVO();
            return statistic;
        }
        return statistic;
    }

    @Override
    public List<ScadaVariableDataVO> listBindDeviceThingsModel(ScadaQueryParam scadaQueryParam) {
        List<ScadaVariableDataVO> list= new ArrayList<>();
        ScadaDeviceBind scadaDeviceBind = new ScadaDeviceBind();
        scadaDeviceBind.setScadaGuid(scadaQueryParam.getScadaGuid());
        scadaDeviceBind.setSerialNumber(scadaQueryParam.getSerialNumber());
        // 查询到组态绑定的设备
        List<ScadaDeviceBind> deviceBindList = scadaDeviceBindMapper.selectScadaDeviceBindList(scadaDeviceBind);
        List<String> serialNumberList = deviceBindList.stream().map(ScadaDeviceBind::getSerialNumber).collect(Collectors.toList());
        // 查询设备信息
        for (String bindSerialNumber : serialNumberList) {
            DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(bindSerialNumber);
            if (Objects.isNull(deviceMetaDataCache)) {
                continue;
            }
            List<ThingsModelValueItem> thingsModelList = itslCache.getThingsModelList(deviceMetaDataCache.getDevice().getProductId());
            if (CollectionUtils.isEmpty(thingsModelList)) {
                continue;
            }
            this.changeIdentifer(deviceMetaDataCache, list, thingsModelList, null);
        }
        if (StringUtils.isNotEmpty(scadaQueryParam.getModelName())) {
            list = list.stream().filter(s -> s.getModelName().contains(scadaQueryParam.getModelName())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Page<ScadaDeviceBindVO> listDeviceBind(ScadaDeviceBindVO scadaDeviceBindVO) {
        if (null == scadaDeviceBindVO.getScadaType()) {
            scadaDeviceBindVO.setScadaType(ScadaTypeEnum.PUBLIC.getType());
        }
        Page<ScadaDeviceBindVO> resultList = new Page<>();
        ScadaTypeEnum scadaTypeEnum = ScadaTypeEnum.getByType(scadaDeviceBindVO.getScadaType());
        switch (Objects.requireNonNull(scadaTypeEnum)) {
            case PRODUCT_TEMPLATE:
                resultList = new Page<>();
                break;
            case SCENE_MODEL:
                resultList = scadaDeviceBindMapper.selectScadaSceneModelDeviceList(new Page<>(scadaDeviceBindVO.getPageNum(), scadaDeviceBindVO.getPageSize()), scadaDeviceBindVO);
                break;
            case PUBLIC:
                resultList = scadaDeviceBindMapper.selectScadaDeviceBindInfoList(new Page<>(scadaDeviceBindVO.getPageNum(), scadaDeviceBindVO.getPageSize()), scadaDeviceBindVO);
                break;
            default:
                break;
        }
        return resultList;
    }

    @Override
    public TableDataInfo listVariable(ScadaQueryParam scadaQueryParam) {
        List<ScadaVariableDataVO> list;
        List resultList = new ArrayList();
        int total = 0;
        // 兼容老数据，默认查独立组态
        if (null == scadaQueryParam.getType()) {
            scadaQueryParam.setType(3);
        }
        ScadaTypeEnum scadaTypeEnum = ScadaTypeEnum.getByType(scadaQueryParam.getType());
        switch (Objects.requireNonNull(scadaTypeEnum)) {
            case PRODUCT_TEMPLATE:
                list = this.listProductVariable(scadaQueryParam);
                total = list.size();
                resultList = ScadaCollectionUtils.startPage(list, scadaQueryParam.getPage(), scadaQueryParam.getSize());
                break;
            case SCENE_MODEL:
                SceneVariableVO sceneVariableVO = this.listSceneModelVariable(scadaQueryParam);
                if (null != sceneVariableVO) {
                    resultList = sceneVariableVO.getList();
                    total = sceneVariableVO.getTotal();
                }
                break;
            case PUBLIC:
                list = this.listBindDeviceThingsModel(scadaQueryParam);
                total = list.size();
                resultList = ScadaCollectionUtils.startPage(list, scadaQueryParam.getPage(), scadaQueryParam.getSize());
                break;
            default:
                resultList = null;
                break;
        }
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg("查询成功");
        if (CollectionUtils.isEmpty(resultList)) {
            tableDataInfo.setTotal(0);
            tableDataInfo.setRows(new ArrayList<>());
        } else {
            tableDataInfo.setRows(resultList);
            tableDataInfo.setTotal(total);
        }
        return tableDataInfo;
    }

    @Override
    public Map<String, List<ScadaHistoryModelVO>> listVariableHistory(ThingsModelHistoryParam param) {
        List<ScadaHistoryModelVO> historyModelList;
        if (ScadaTypeEnum.SCENE_MODEL.getType().equals(param.getScadaType())) {
            String serialNumber = scadaMapper.selectSerialNumberBySceneModelDeviceId(param.getSceneModelDeviceId());
            if (StringUtils.isNotEmpty(serialNumber)) {
                param.setSerialNumber(serialNumber);
                historyModelList = this.listThingsModelHistory(param);
            } else {
                historyModelList = this.listSceneModelVariableHistory(param);
            }
        } else {
            historyModelList = this.listThingsModelHistory(param);
        }
        if (CollectionUtils.isEmpty(historyModelList)) {
            return new HashMap<>(2);
        }
        // 分组
        return historyModelList.stream().collect(Collectors.groupingBy(ScadaHistoryModelVO::getIdentity));
    }

    private List<ScadaHistoryModelVO> listSceneModelVariableHistory(ThingsModelHistoryParam param) {
        List<ScadaHistoryModelVO> historyModelList = new ArrayList<>();
        List<ThingsModelHistoryParam.ThingsModelSim> thingsModelList = param.getThingsModelList();
        if (CollectionUtils.isNotEmpty(thingsModelList)) {
            DeviceLog deviceLog = new DeviceLog();
            List<String> identifierList = thingsModelList.stream().map(ThingsModelHistoryParam.ThingsModelSim::getIdentifier).collect(Collectors.toList());
            deviceLog.setIdentityList(identifierList);
            deviceLog.setBeginTime(param.getBeginTime());
            deviceLog.setEndTime(param.getEndTime());
            deviceLog.setLogType(DeviceLogTypeEnum.SCENE_VARIABLE_REPORT.getType());
            List<HistoryModel> historyModelList1 = deviceLogService.listHistory(deviceLog);
            for (HistoryModel historyModel : historyModelList1) {
                ScadaHistoryModelVO scadaHistoryModelVO = new ScadaHistoryModelVO();
                BeanUtils.copyProperties(historyModel, scadaHistoryModelVO);
                scadaHistoryModelVO.setIdentity(historyModel.getIdentify());
                historyModelList.add(scadaHistoryModelVO);
            }
        }
        return historyModelList;
    }

    private SceneVariableVO listSceneModelVariable(ScadaQueryParam scadaQueryParam) {
        Long sceneModelId = scadaMapper.selectSceneModelByGuid(scadaQueryParam.getScadaGuid());
        if (null == sceneModelId) {
            return null;
        }
        SceneVariableVO sceneVariableVO = new SceneVariableVO();
        scadaQueryParam.setSceneModelId(sceneModelId);
        if (scadaQueryParam.getPage() <= 1) {
            scadaQueryParam.setPageStart(0);
        } else {
            scadaQueryParam.setPageStart(scadaQueryParam.getSize() * (scadaQueryParam.getPage() - 1));
        }
        List<ScadaVariableDataVO> voList = scadaMapper.selectListSceneModelData(scadaQueryParam);
        for (ScadaVariableDataVO variableDataVO : voList) {
            if (SceneModelVariableTypeEnum.THINGS_MODEL.getType().equals(variableDataVO.getVariableType())) {
                if (Objects.nonNull(variableDataVO.getProductId())) {
                    ThingsModelValueItem thingModels = itslCache.getSingleThingModels(variableDataVO.getProductId(), variableDataVO.getIdentifier());
                    if (Objects.nonNull(thingModels)) {
                        variableDataVO.setSpecs(JSON.toJSONString(thingModels.getDatatype()));
                    }
                }
            } else if (SceneModelVariableTypeEnum.INPUT_VARIABLE.getType().equals(variableDataVO.getVariableType())) {
                LambdaQueryWrapper<SceneModelTag> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SceneModelTag::getId, variableDataVO.getIdentifier());
                SceneModelTag sceneModelTag = sceneModelTagMapper.selectOne(queryWrapper);
                if (Objects.nonNull(sceneModelTag)) {
                    Datatype datatype = new Datatype();
                    datatype.setType("0".equals(sceneModelTag.getDataType()) ? "integer" : "string");
                    variableDataVO.setSpecs(JSON.toJSONString(datatype));
                }
            } else {
                Datatype datatype = new Datatype();
                datatype.setType("string");
                variableDataVO.setSpecs(JSON.toJSONString(datatype));
            }
        }
        sceneVariableVO.setList(voList);
        sceneVariableVO.setTotal(scadaMapper.countListSceneModelData(scadaQueryParam));
        return sceneVariableVO;
    }

    private List<ScadaVariableDataVO> listProductVariable(ScadaQueryParam scadaQueryParam) {
        Long productId = scadaMapper.selectProductByGuid(scadaQueryParam.getScadaGuid());
        if (null == productId) {
            return new ArrayList<>();
        }
        ScadaVariableDataVO productDataVO = scadaMapper.selectProductByProductId(productId);
        List<ScadaVariableDataVO> resultList = new ArrayList<>();
        List<ThingsModelDTO> list = thingsModelService.getCacheByProductId(productId, null);
        for (ThingsModelDTO thingsModelDTO : list) {
            ScadaVariableDataVO scadaVariableDataVO = new ScadaVariableDataVO();
            scadaVariableDataVO.setModelName(thingsModelDTO.getModelName());
            scadaVariableDataVO.setIdentifier(thingsModelDTO.getIdentifier());
            scadaVariableDataVO.setUnit(thingsModelDTO.getUnit());
            scadaVariableDataVO.setType(thingsModelDTO.getType());
            scadaVariableDataVO.setSpecs(thingsModelDTO.getSpecs());
            if (null != productDataVO) {
                scadaVariableDataVO.setProductId(productDataVO.getProductId());
                scadaVariableDataVO.setProductName(productDataVO.getProductName());
            }
            resultList.add(scadaVariableDataVO);
        }
        if (StringUtils.isNotEmpty(scadaQueryParam.getModelName())) {
            resultList = resultList.stream().filter(s -> s.getModelName().contains(scadaQueryParam.getModelName())).collect(Collectors.toList());
        }
        return resultList;
    }

    @Override
    public List<ScadaVariableDataVO> listVariableHistoryTable(ThingsModelHistoryParam param) {
        if (ScadaTypeEnum.SCENE_MODEL.getType().equals(param.getScadaType())) {
            return this.listSceneVariableHistoryTable(param);
        }
        return this.listDeviceVariableHistoryTable(param);
    }

    @Override
    public AjaxResult editShare(ScadaShareVO scadaShareVO, HttpServletRequest httpServletRequest) {
        String serverName = httpServletRequest.getServerName();
        String sharePass;
        Object scadaShareToken = redisCache.getCacheObject(Constants.SCADA_SHARE_KEY);
        String token = "";
        if (Objects.isNull(scadaShareToken)) {
            if (1 == scadaShareVO.getIsShare()) {
                token = sysLoginService.shareUserLogin("fastbee_scada", Constants.ZH_CN);
                redisCache.setCacheObject(Constants.SCADA_SHARE_KEY, token);
            }
        } else {
            token = scadaShareToken.toString();
        }

        // 用特定账号生成一个token
        if (ScadaSharePassStatusEnum.CLOSE.getStatus().equals(scadaShareVO.getSharePassStatus())) {
            sharePass = null;
        } else if (ScadaSharePassStatusEnum.EDIT.getStatus().equals(scadaShareVO.getSharePassStatus())) {
            sharePass = VerifyCodeUtils.generateVerifyCode(6);
        } else {
            sharePass = scadaShareVO.getSharePass();
        }
        scadaShareVO.setSharePass(sharePass);
        scadaShareVO.setSharePassStatus(ScadaSharePassStatusEnum.NO_CHANGE.getStatus());
        if (ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scadaShareVO.getType())) {
            ScadaDeviceShare scadaDeviceShare = scadaDeviceShareService.getByGuidAndSerialNumber(scadaShareVO);
            if (0 == scadaShareVO.getIsShare()) {
                if (null != scadaDeviceShare) {
                    scadaDeviceShareService.removeById(scadaDeviceShare.getId());
                    return AjaxResult.success();
                }
            } else {
                ScadaDeviceShare editScadaDeviceShare = new ScadaDeviceShare();
                BeanUtils.copyProperties(scadaShareVO, editScadaDeviceShare);
                if (null == scadaDeviceShare) {
                    String shareUrl =  "https://" + serverName + "/scada/topo/fullscreen?guid=" + scadaShareVO.getGuid() + "&type=" + scadaShareVO.getType() + "&serialNumber=" + scadaShareVO.getSerialNumber() + "&share=" + token;
                    String shareShortUrl =  "https://" + serverName + "/scada/topo/share?guid=" + scadaShareVO.getGuid() + "&type=" + scadaShareVO.getType() + "&serialNumber=" + scadaShareVO.getSerialNumber() + "&share=" + token;
                    editScadaDeviceShare.setShareUrl(shareUrl);
                    editScadaDeviceShare.setShareShortUrl(shareShortUrl);
                    scadaShareVO.setShareUrl(shareUrl);
                    scadaShareVO.setShareShortUrl(shareShortUrl);
                    scadaDeviceShareService.insertWithCache(editScadaDeviceShare);
                } else {
                    editScadaDeviceShare.setId(scadaDeviceShare.getId());
                    scadaDeviceShareService.updateOneById(editScadaDeviceShare);
                }
                return AjaxResult.success(scadaShareVO);
            }
        }
        if (0 == scadaShareVO.getIsShare()) {
            scadaShareVO.setShareUrl(null);
            scadaShareVO.setSharePass(null);
            scadaMapper.updateScadaShare(scadaShareVO);
        } else {
            String shareUrl = null;
            String shareShortUrl = null;
            if (ScadaTypeEnum.SCENE_MODEL.getType().equals(scadaShareVO.getType())) {
                Long sceneModelId = scadaMapper.selectSceneModelByGuid(scadaShareVO.getGuid());
                shareUrl =  "https://" + serverName + "/scada/topo/fullscreen?guid=" + scadaShareVO.getGuid() + "&type=" + scadaShareVO.getType() + "&sceneModelId=" + sceneModelId + "&share=" + token;
                shareShortUrl =  "https://" + serverName + "/scada/topo/share?guid=" + scadaShareVO.getGuid() + "&type=" + scadaShareVO.getType() + "&share=" + token;
            } else if (ScadaTypeEnum.PUBLIC.getType().equals(scadaShareVO.getType())) {
                shareUrl =  "https://" + serverName + "/scada/topo/fullscreen?guid=" + scadaShareVO.getGuid() + "&type=" + scadaShareVO.getType() + "&share=" + token;
                shareShortUrl =  "https://" + serverName + "/scada/topo/share?guid=" + scadaShareVO.getGuid() + "&type=" + scadaShareVO.getType() + "&share=" + token;
            }
            scadaShareVO.setShareUrl(shareUrl);
            scadaShareVO.setShareShortUrl(shareShortUrl);
            scadaMapper.updateScadaShare(scadaShareVO);
        }
        return AjaxResult.success(scadaShareVO);
    }

    @Override
    public ScadaShareVO getShare(ScadaShareVO scadaShareVO) {
        ScadaShareVO resultShareVO = new ScadaShareVO();
        resultShareVO.setType(scadaShareVO.getType());
        resultShareVO.setGuid(scadaShareVO.getGuid());
        resultShareVO.setSerialNumber(scadaShareVO.getSerialNumber());
        resultShareVO.setSharePassStatus(ScadaSharePassStatusEnum.NO_CHANGE.getStatus());
        if (ScadaTypeEnum.PRODUCT_TEMPLATE.getType().equals(scadaShareVO.getType())) {
            ScadaDeviceShare scadaDeviceShare = scadaDeviceShareService.getByGuidAndSerialNumber(scadaShareVO);
            if (null != scadaDeviceShare) {
                resultShareVO.setIsShare(scadaDeviceShare.getIsShare());
                resultShareVO.setShareUrl(scadaDeviceShare.getShareUrl());
                resultShareVO.setShareShortUrl(scadaDeviceShare.getShareShortUrl());
                resultShareVO.setSharePass(scadaDeviceShare.getSharePass());
            } else {
                resultShareVO.setIsShare(0);
            }
            return resultShareVO;
        }
        LambdaQueryWrapper<Scada> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scada::getGuid, scadaShareVO.getGuid());
        Scada scada = scadaMapper.selectOne(queryWrapper);
        resultShareVO.setIsShare(scada.getIsShare());
        resultShareVO.setShareUrl(scada.getShareUrl());
        resultShareVO.setShareShortUrl(scada.getShareShortUrl());
        resultShareVO.setSharePass(scada.getSharePass());
        return resultShareVO;
    }

    @Override
    public SysUser selectSysUser(Long userId) {
        return scadaMapper.selectSysUser(userId);
    }

    @Override
    public AjaxResult importJson(MultipartFile file, String guid) throws IOException {
        InputStream inputStream = file.getInputStream();
        if(file.isEmpty()){
            return AjaxResult.error(MessageUtils.message("scada.invalid.profile"));
        }
        if(file.getOriginalFilename().indexOf("json")==-1){
            return AjaxResult.error(MessageUtils.message("scada.invalid.profile"));
        }
        Scada scada = new Scada();
        try {
            scada = JSON.parseObject(inputStream, Scada.class);
        }catch (Exception e){
            return AjaxResult.error(MessageUtils.message("scada.invalid.profile"));
        }finally {
            inputStream.close();
        }
        Scada oldScada = new Scada();
        if (StringUtils.isNotEmpty(guid)) {
            Scada queryScada = new Scada();
            queryScada.setGuid(guid);
            List<Scada> scadaList = scadaMapper.selectScadaList(queryScada);
            if (CollectionUtils.isNotEmpty(scadaList)) {
                oldScada = scadaList.get(0);
            }
        }
        String importGuid = scada.getGuid();
        Long tenantId = scada.getTenantId();
        oldScada.setScadaData(scada.getScadaData());
        int i = scadaMapper.updateById(oldScada);
        if (i <= 0) {
            return AjaxResult.error(MessageUtils.message("import.fail"));
        }
        if (!tenantId.equals(oldScada.getTenantId())) {
            return AjaxResult.success(MessageUtils.message("scada.import.success.need.replace.variable"));
        }
        if (StringUtils.isNotEmpty(importGuid) && ScadaTypeEnum.PUBLIC.getType().equals(oldScada.getType())
                && ScadaTypeEnum.PUBLIC.getType().equals(scada.getType())) {
            List<String> guidList = Arrays.asList(guid, importGuid);
            LambdaQueryWrapper<ScadaDeviceBind> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ScadaDeviceBind::getScadaGuid, guidList);
            List<ScadaDeviceBind> scadaDeviceBinds = scadaDeviceBindMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(scadaDeviceBinds)) {
                List<String> serialNumbers = scadaDeviceBinds.stream().filter(d -> guid.equals(d.getScadaGuid())).map(ScadaDeviceBind::getSerialNumber).collect(Collectors.toCollection(ArrayList::new));
                List<String> importSerialNumbers = scadaDeviceBinds.stream().filter(d -> importGuid.equals(d.getScadaGuid())).map(ScadaDeviceBind::getSerialNumber).collect(Collectors.toCollection(ArrayList::new));
                importSerialNumbers.removeAll(serialNumbers);
                for (String importSerialNumber : importSerialNumbers) {
                    ScadaDeviceBind scadaDeviceBind = new ScadaDeviceBind();
                    scadaDeviceBind.setScadaGuid(guid);
                    scadaDeviceBind.setSerialNumber(importSerialNumber);
                    scadaDeviceBindMapper.insert(scadaDeviceBind);
                }
            }
            return AjaxResult.success(MessageUtils.message("import.success"));
        }
        return AjaxResult.success(MessageUtils.message("scada.import.success.need.replace.variable"));
    }

    @Override
    public Page<AlertLogVO> pageAlertLog(ScadaAlertLogParams scadaAlertLogParams) {
        List<String> serialNumberList = new ArrayList<>();
        if (ScadaTypeEnum.PUBLIC.getType().equals(scadaAlertLogParams.getScadaType())) {
            ScadaDeviceBind scadaDeviceBind = new ScadaDeviceBind();
            scadaDeviceBind.setScadaGuid(scadaAlertLogParams.getGuid());
            List<ScadaDeviceBind> scadaDeviceBindList = scadaDeviceBindMapper.selectScadaDeviceBindList(scadaDeviceBind);
            if (CollectionUtils.isEmpty(scadaDeviceBindList)) {
                return new Page<>();
            }
            serialNumberList.addAll(scadaDeviceBindList.stream().map(ScadaDeviceBind::getSerialNumber).collect(Collectors.toList()));
        } else if (ScadaTypeEnum.SCENE_MODEL.getType().equals(scadaAlertLogParams.getScadaType())) {
            LambdaQueryWrapper<SceneModelDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SceneModelDevice::getSceneModelId, scadaAlertLogParams.getSceneModelId());
            queryWrapper.eq(SceneModelDevice::getVariableType, SceneModelVariableTypeEnum.THINGS_MODEL.getType());
            List<SceneModelDevice> sceneModelDeviceList = sceneModelDeviceMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(sceneModelDeviceList)) {
                return new Page<>();
            }
            List<Long> deviceIdList = sceneModelDeviceList.stream().map(SceneModelDevice::getCusDeviceId).collect(Collectors.toList());
            LambdaQueryWrapper<Device> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(Device::getDeviceId, deviceIdList);
            List<Device> deviceList = deviceMapper.selectList(queryWrapper1);
            if (CollectionUtils.isEmpty(deviceList)) {
                return new Page<>();
            }
            serialNumberList.addAll(deviceList.stream().map(Device::getSerialNumber).collect(Collectors.toList()));
        } else {
            serialNumberList.add(scadaAlertLogParams.getSerialNumber());
        }
        LambdaQueryWrapper<AlertLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AlertLog::getSerialNumber, serialNumberList);
        Page<AlertLog> alertLogPage = alertLogMapper.selectPage(new Page<>(scadaAlertLogParams.getPageNum(), scadaAlertLogParams.getPageSize()), queryWrapper);
        return AlertLogConvert.INSTANCE.convertAlertLogVOPage(alertLogPage);
    }

    private List<ScadaVariableDataVO> listSceneVariableHistoryTable(ThingsModelHistoryParam param) {
        List<ThingsModelHistoryParam.ThingsModelSim> thingsModelList = param.getThingsModelList();
        if (CollectionUtils.isEmpty(thingsModelList)) {
            return new ArrayList<>();
        }
        List<ScadaVariableDataVO> variableDataVOList = new ArrayList<>();
        Map<Long, List<ThingsModelHistoryParam.ThingsModelSim>> variableMap = thingsModelList.stream().collect(Collectors.groupingBy(ThingsModelHistoryParam.ThingsModelSim::getSceneModelDeviceId));
        for (Map.Entry<Long, List<ThingsModelHistoryParam.ThingsModelSim>> entry : variableMap.entrySet()) {
            List<String> identifierList = entry.getValue().stream().map(ThingsModelHistoryParam.ThingsModelSim::getIdentifier).collect(Collectors.toList());
            variableDataVOList.addAll(scadaMapper.selectSceneModelDataList(identifierList, param.getSceneModelId(), entry.getKey()));
        }
        if (CollectionUtils.isEmpty(variableDataVOList)) {
            return new ArrayList<>();
        }
        List<ScadaVariableDataVO> resultList = new ArrayList<>();
        Map<String, List<ScadaVariableDataVO>> deviceVariabelMap = variableDataVOList.stream().filter(v -> 1 == v.getVariableType())
                .collect(Collectors.groupingBy(ScadaVariableDataVO::getSerialNumber));
        for (Map.Entry<String, List<ScadaVariableDataVO>> entry : deviceVariabelMap.entrySet()) {
            ThingsModelHistoryParam queryParam = getThingsModelHistoryParam(param, entry);
            resultList.addAll(this.listDeviceVariableHistoryTable(queryParam));
        }
        List<ScadaVariableDataVO> sceneVariableList = variableDataVOList.stream().filter(v -> 1 != v.getVariableType()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sceneVariableList)) {
            return resultList;
        }
        List<String> sceneIdentifierList = sceneVariableList.stream().map(ScadaVariableDataVO::getIdentifier).collect(Collectors.toList());
        List<ScadaVariableDataVO> sceneSimHistoryList = scadaMapper.selectSceneDataHistoryList(sceneIdentifierList, param.getBeginTime(), param.getEndTime());
        if (CollectionUtils.isEmpty(sceneSimHistoryList)) {
            return resultList;
        }
        Map<String, ScadaVariableDataVO> voMap = sceneVariableList.stream().collect(Collectors.toMap(ScadaVariableDataVO::getSerialNumber, Function.identity(), (o, n) -> n));
        for (ScadaVariableDataVO scadaVariableDataVO : sceneSimHistoryList) {
            ScadaVariableDataVO variableDataVO = voMap.get(scadaVariableDataVO.getIdentifier());
            if (null != variableDataVO) {
                BeanUtils.copyProperties(variableDataVO, scadaVariableDataVO);
            }
        }
        resultList.addAll(sceneSimHistoryList);
        return resultList;
    }

    private static ThingsModelHistoryParam getThingsModelHistoryParam(ThingsModelHistoryParam param, Map.Entry<String, List<ScadaVariableDataVO>> entry) {
        String serialNumber = entry.getKey();
        List<ScadaVariableDataVO> dataVOList = entry.getValue();
        ThingsModelHistoryParam queryParam = new ThingsModelHistoryParam();
        queryParam.setSerialNumber(serialNumber);
        queryParam.setBeginTime(param.getBeginTime());
        queryParam.setEndTime(param.getEndTime());
        List<ThingsModelHistoryParam.ThingsModelSim> simList = new ArrayList<>();
        for (ScadaVariableDataVO scadaVariableDataVO : dataVOList) {
            ThingsModelHistoryParam.ThingsModelSim thingsModelSim = new ThingsModelHistoryParam.ThingsModelSim();
            thingsModelSim.setIdentifier(scadaVariableDataVO.getIdentifier());
            thingsModelSim.setType(scadaVariableDataVO.getType());
            simList.add(thingsModelSim);
        }
        queryParam.setThingsModelList(simList);
        return queryParam;
    }

    private List<ScadaVariableDataVO> listDeviceVariableHistoryTable(ThingsModelHistoryParam param) {
        DeviceMetaData deviceMetaDataCache = deviceCache.getDeviceMetaDataCache(param.getSerialNumber());
        if (Objects.isNull(deviceMetaDataCache)) {
            return new ArrayList<>();
        }
        List<ThingsModelHistoryParam.ThingsModelSim> thingsModelList = param.getThingsModelList();
        if (CollectionUtils.isEmpty(thingsModelList)) {
            return new ArrayList<>();
        }
        Map<String, ThingsModelHistoryParam.ThingsModelSim> thingsModelSimMap = thingsModelList.stream().collect(Collectors.toMap(ThingsModelHistoryParam.ThingsModelSim::getIdentifier, Function.identity()));
        List<ScadaHistoryModelVO> list = this.listThingsModelHistory(param);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Map<String, ThingsModelValueItem> modelValueItemMap = itslCache.getCacheThMapByProductId(deviceMetaDataCache.getDevice().getProductId());
        List<ScadaVariableDataVO> resultList = new ArrayList<>();
        for (ScadaHistoryModelVO scadaHistoryModelVO : list) {
            ScadaVariableDataVO scadaVariableDataVO = new ScadaVariableDataVO();
            scadaVariableDataVO.setSerialNumber(deviceMetaDataCache.getDevice().getSerialNumber());
            scadaVariableDataVO.setDeviceName(deviceMetaDataCache.getDevice().getDeviceName());
            scadaVariableDataVO.setIdentifier(scadaHistoryModelVO.getIdentity());
            scadaVariableDataVO.setValue(scadaHistoryModelVO.getValue());
            scadaVariableDataVO.setTs(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, scadaHistoryModelVO.getTime()));
            ThingsModelHistoryParam.ThingsModelSim thingsModelSim = thingsModelSimMap.get(scadaHistoryModelVO.getIdentity());
            if (null != thingsModelSim) {
                scadaVariableDataVO.setSlaveId(thingsModelSim.getSlaveId());
                scadaVariableDataVO.setSlaveName(thingsModelSim.getSlaveName());
                scadaVariableDataVO.setUnit(thingsModelSim.getUnit());
            }
            ThingsModelValueItem thingsModelValueItem = modelValueItemMap.get(scadaHistoryModelVO.getIdentity());
            if (null != thingsModelValueItem) {
                scadaVariableDataVO.setModelName(thingsModelValueItem.getName());
            }
            resultList.add(scadaVariableDataVO);
        }
        return resultList;
    }


    private void changeIdentifer(DeviceMetaData deviceMetaData, List<ScadaVariableDataVO> list, List<ThingsModelValueItem> thingsModelList, Integer slaveId) {
        Device device = deviceMetaData.getDevice();
        Product product = deviceMetaData.getProduct();
        for (ThingsModelValueItem thingsModel : thingsModelList) {
            Datatype datatype = thingsModel.getDatatype();
            if ("array".equals(datatype.getType()) && "object".equals(datatype.getArrayType())) {
                List<ThingsModel>[] arrayParams = datatype.getArrayParams();
                for (int a = 0; a < arrayParams.length; a++) {
                    for (int i = 0; i < arrayParams[a].size(); i++) {
                        ThingsModel thingsModel1 = arrayParams[a].get(i);
                        ScadaVariableDataVO scadaVariableDataVO = new ScadaVariableDataVO();
                        scadaVariableDataVO.setProductId(device.getProductId());
                        scadaVariableDataVO.setProductName(product.getProductName());
                        scadaVariableDataVO.setSerialNumber(device.getSerialNumber());
                        scadaVariableDataVO.setDeviceName(device.getDeviceName());
                        scadaVariableDataVO.setStatus(device.getStatus());
                        if (i < 10) {
                            scadaVariableDataVO.setIdentifier("array_0" + a + "_" + thingsModel1.getId());
                        } else {
                            scadaVariableDataVO.setIdentifier("array_" + a + "_" + thingsModel1.getId());
                        }
                        scadaVariableDataVO.setModelName(thingsModel.getName() + (a + 1) + "_" + thingsModel1.getName());
                        scadaVariableDataVO.setUnit(thingsModel1.getDatatype().getUnit());
                        scadaVariableDataVO.setType(thingsModel1.getType());
                        scadaVariableDataVO.setSlaveId(slaveId);
                        list.add(scadaVariableDataVO);
                    }
                }
            } else if ("array".equals(datatype.getType())) {
                for (int i = 0; i < datatype.getArrayCount(); i++) {
                    ScadaVariableDataVO scadaVariableDataVO = new ScadaVariableDataVO();
                    scadaVariableDataVO.setProductId(device.getProductId());
                    scadaVariableDataVO.setProductName(product.getProductName());
                    scadaVariableDataVO.setSerialNumber(device.getSerialNumber());
                    scadaVariableDataVO.setDeviceName(device.getDeviceName());
                    scadaVariableDataVO.setStatus(device.getStatus());
                    if (i < 10) {
                        scadaVariableDataVO.setIdentifier("array_0" + i + "_" + thingsModel.getId());
                    } else {
                        scadaVariableDataVO.setIdentifier("array_" + i + "_" + thingsModel.getId());
                    }
                    scadaVariableDataVO.setModelName(thingsModel.getName() + (i+1));
                    scadaVariableDataVO.setUnit(datatype.getUnit());
                    scadaVariableDataVO.setType(thingsModel.getType());
                    scadaVariableDataVO.setSlaveId(slaveId);
                    list.add(scadaVariableDataVO);
                }
            } else if ("object".equals(datatype.getType())) {
                for (ThingsModelValueItem objectThingsModel : datatype.getParams()) {
                    ScadaVariableDataVO scadaVariableDataVO = new ScadaVariableDataVO();
                    scadaVariableDataVO.setProductId(device.getProductId());
                    scadaVariableDataVO.setProductName(product.getProductName());
                    scadaVariableDataVO.setSerialNumber(device.getSerialNumber());
                    scadaVariableDataVO.setDeviceName(device.getDeviceName());
                    scadaVariableDataVO.setStatus(device.getStatus());
                    scadaVariableDataVO.setIdentifier(objectThingsModel.getId());
                    scadaVariableDataVO.setModelName(thingsModel.getName() + "_" + objectThingsModel.getName());
                    scadaVariableDataVO.setUnit(objectThingsModel.getDatatype().getUnit());
                    scadaVariableDataVO.setType(thingsModel.getType());
                    scadaVariableDataVO.setSlaveId(slaveId);
                    list.add(scadaVariableDataVO);
                }
            } else {
                ScadaVariableDataVO scadaVariableDataVO = new ScadaVariableDataVO();
                scadaVariableDataVO.setProductId(device.getProductId());
                scadaVariableDataVO.setProductName(product.getProductName());
                scadaVariableDataVO.setSerialNumber(device.getSerialNumber());
                scadaVariableDataVO.setDeviceName(device.getDeviceName());
                scadaVariableDataVO.setStatus(device.getStatus());
                scadaVariableDataVO.setIdentifier(thingsModel.getId());
                scadaVariableDataVO.setModelName(thingsModel.getName());
                scadaVariableDataVO.setUnit(thingsModel.getDatatype().getUnit());
                scadaVariableDataVO.setType(thingsModel.getType());
                scadaVariableDataVO.setSlaveId(slaveId);
                scadaVariableDataVO.setSpecs(JSON.toJSONString(thingsModel.getDatatype()));
                list.add(scadaVariableDataVO);
            }
        }
    }

    private LambdaQueryWrapper<Scada> buildQueryWrapper(Scada query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Scada> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, Scada::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getGuid()), Scada::getGuid, query.getGuid());
        lqw.eq(StringUtils.isNotBlank(query.getScadaData()), Scada::getScadaData, query.getScadaData());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumbers()), Scada::getSerialNumbers, query.getSerialNumbers());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), Scada::getDeviceName, query.getDeviceName());
        lqw.eq(query.getIsMainPage() != null, Scada::getIsMainPage, query.getIsMainPage());
        lqw.like(StringUtils.isNotBlank(query.getPageName()), Scada::getPageName, query.getPageName());
        lqw.eq(StringUtils.isNotBlank(query.getPageResolution()), Scada::getPageResolution, query.getPageResolution());
        lqw.eq(query.getIsShare() != null, Scada::getIsShare, query.getIsShare());
        lqw.eq(StringUtils.isNotBlank(query.getShareUrl()), Scada::getShareUrl, query.getShareUrl());
        lqw.eq(StringUtils.isNotBlank(query.getSharePass()), Scada::getSharePass, query.getSharePass());
        lqw.eq(StringUtils.isNotBlank(query.getPageImage()), Scada::getPageImage, query.getPageImage());
        lqw.eq(query.getTenantId() != null, Scada::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), Scada::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), Scada::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, Scada::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), Scada::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, Scada::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, Scada::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), Scada::getRemark, query.getRemark());
        lqw.eq(query.getType() != null, Scada::getType, query.getType());
        lqw.eq(StringUtils.isNotBlank(query.getShareShortUrl()), Scada::getShareShortUrl, query.getShareShortUrl());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Scada::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }

        return lqw;
    }
}
