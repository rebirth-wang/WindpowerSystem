package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.ThingsModel;
import com.fastbee.iot.model.ImportThingsModelInput;
import com.fastbee.iot.model.ThingsModelPerm;
import com.fastbee.iot.model.ThingsModels.IdentifierVO;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.modbus.ModbusAndThingsVO;
import com.fastbee.iot.model.vo.ThingsModelSimVO;
import com.fastbee.iot.model.vo.ThingsModelVO;

/**
 * 物模型Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IThingsModelService extends IService<ThingsModel> {

    /**
     * 查询物模型
     *
     * @param modelId 物模型主键
     * @return 物模型
     */
    public ThingsModelVO selectThingsModelVoByModelId(Long modelId);

    /**
     * 查询单个物模型
     *
     * @param thingsModel 物模型
     * @return 单个物模型
     */
    public ThingsModelVO selectSingleThingsModel(ThingsModel thingsModel);

    /**
     * 查询物模型列表
     *
     * @param thingsModelVO 物模型
     * @return 物模型分页集合
     */
    Page<ThingsModelVO> pageThingsModelVO(ThingsModelVO thingsModelVO);

    void topThingsModel(ThingsModelVO thingsModelVO);
    void moveUpThingsModel(ThingsModelVO thingsModelVO);
    void moveDownThingsModel(ThingsModelVO thingsModelVO);

    /**
     * 查询物模型列表
     *
     * @param thingsModelVO 物模型
     * @return 物模型集合
     */
    public List<ThingsModelVO> selectThingsModelList(ThingsModelVO thingsModelVO);

    /**
     * 查询物模型对应设备分享用户权限列表
     *
     * @param productId 产品ID
     */
    public List<ThingsModelPerm> selectThingsModelPermList(Long productId,Long deviceId, Long userId);


    /**
     * 新增物模型
     *
     * @param thingsModelVO 物模型
     * @return 结果
     */
    public int insertThingsModel(ThingsModelVO thingsModelVO);

    /**
     * 从模板导入物模型
     *
     * @param input
     * @return
     */
    public int importByTemplateIds(ImportThingsModelInput input);

    /**
     * 修改物模型
     *
     * @param thingsModelVO 物模型
     * @return 结果
     */
    public int updateThingsModel(ThingsModelVO thingsModelVO);

    /**
     * 批量删除物模型
     *
     * @param modelIds 需要删除的物模型主键集合
     * @return 结果
     */
    public int deleteThingsModelByModelIds(Long[] modelIds);

    /**
     * 删除物模型信息
     *
     * @param modelId 物模型主键
     * @return 结果
     */
    public int deleteThingsModelByModelId(Long modelId);


    /**
     * 获取单个物模型获取
     *
     * @param productId
     * @param identify
     * @return
     */
    public ThingsModelValueItem getSingleThingModels(Long productId, String identify);


    /**
     * 导入采集点数据
     *
     * @param lists     数据列表
     * @param productId 从机编码
     * @return 结果
     */
    public String importData(List<ThingsModelVO> lists, Long productId);

    /**
     * 根据产品id删除 产品物模型以及物模型缓存
     *
     * @param productId
     */
    public void deleteProductThingsModelAndCacheByProductId(Long productId);

    /**
     * 查询物模型数据定义
     *
     * @param productId  产品id
     * @param identifier 物模型id
     * @return
     */
    String getSpecsByProductIdAndIdentifier(Long productId, String identifier);

    /**
     * 批量查询产品物模型
     *
     * @param productIdList 产品id集合
     * @return
     */
    List<ThingsModelSimVO> listSimByProductIds(List<Long> productIdList);


    /**
     * 获取modbus配置可选择物模型
     *
     * @param productId
     * @return
     */
    Page<ModbusAndThingsVO> getModbusConfigUnSelectThingsModel(Long productId);

    /**
     * 还原数组或对象物模型标识
     *
     * @param identifier 物模标识 带array_
     * @return java.lang.String
     */
    IdentifierVO revertObjectOrArrayIdentifier(String identifier);

    /**
     * 获取单个物模缓存值
     *
     * @param productId 产品id
     * @return java.lang.String
     * @param: serialNumber 设备编号
     * @param: identifier 物模标识
     */
    ValueItem getCacheIdentifierValue(Long productId, String serialNumber, String identifier);

    /**
     * 获取产品缓存物模型并拆分数组
     *
     * @param productId    产品id
     * @param serialNumber 设备编号 可以不传
     * @return java.util.List<com.fastbee.iot.domain.ThingsModel>
     */
    List<ThingsModelDTO> getCacheByProductId(Long productId, String serialNumber);

    /**
     * 批量查询物模型
     *
     * @param modelIdList 物模id集合
     * @return java.util.List<com.fastbee.iot.domain.ThingsModel>
     */
    List<ThingsModel> selectThingsModelListByModelIds(List<Long> modelIdList);

    /**
     * 根据设备编号获取读写物模型
     *
     * @param deviceId
     * @return
     */
    Page<ThingsModel> selectThingsModelBySerialNumber(Long deviceId);

    /**
     * @description: 处理老物模型结构
     * @param: productId 产品id
     * @return: com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult refresh(Long productId);

    /**
     * 解析specs字段到unit和limitValue字段，用于导出
     * @param model 物模型对象
     */
    void parseSpecsForExport(ThingsModelVO model);

    /**
     * 根据标识符列表获取模板数据并转换为ThingsModelVO列表
     * @param identifiers 标识符列表
     * @return ThingsModelVO列表
     */
    List<ThingsModelVO> getTemplateExampleData(List<String> identifiers);
}
