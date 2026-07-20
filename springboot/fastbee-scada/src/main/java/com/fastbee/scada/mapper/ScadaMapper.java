package com.fastbee.scada.mapper;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.vo.DeviceVO;
import com.fastbee.iot.model.vo.EventLogVO;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.scada.domain.Scada;
import com.fastbee.scada.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组态中心Mapper接口
 *
 * @author kerwincui
 * @date 2023-11-10
 */
public interface ScadaMapper extends BaseMapperX<Scada>
{

    /**
     * 查询组态中心列表
     *
     * @param scada 组态中心
     * @return 组态中心集合
     */
    public List<Scada> selectScadaList(Scada scada);

    /**
     * 查询设备运行状态
     * @param serialNumber 设备编号
     * @return java.lang.String
     */
    Integer getStatusBySerialNumber(String serialNumber);

    ScadaStatisticVO selectDeviceProductAlertCount(@Param("tenantIdList") List<Long> tenantIdList, @Param("userId") Long userId);

    /**
     * 查询功能物模型历史数据
     * @param functionLogVO 功能物模型日志
     * @return java.util.List<com.fastbee.scada.vo.ScadaHistoryModelVO>
     */
    List<ScadaHistoryModelVO> listFunctionLogHistory(FunctionLogVO functionLogVO);

    /**
     * 查询时间物模型历史数据
     * @param eventLogVO 事件物模型日志
     * @return java.util.List<com.fastbee.scada.vo.ScadaHistoryModelVO>
     */
    List<ScadaHistoryModelVO> listEventLogHistory(EventLogVO eventLogVO);

    /**
     * 查询同部门用户
     * @param deptId 部门id
     * @return java.util.List<java.lang.Long>
     */
    List<Long> selectUserIdByDeptId(Long deptId);

    /**
     * 查询产品绑定的组态
     * @param productId 产品iD
     * @return java.lang.String
     */
    String selectGuidByProductId(Long productId);

    /**
     * 更新产品绑定的组态
     * @param productId 产品id
     * @param: guid
     * @return void
     */
    void updateProductGuid(@Param("productId") Long productId, @Param("guid") String guid);

    /**
     * 查询场景关联组态
     * @param sceneModelId 场景id
     * @return java.lang.String
     */
    String selectGuidBySceneModelId(Long sceneModelId);

    /**
     * 更新场景联动绑定组态
     * @param sceneModelId 场景联动id
     * @param: guid
     * @return void
     */
    void updateSceneModelGuid(@Param("sceneModelId") Long sceneModelId, @Param("guid") String guid);

    /**
     * 删除组态绑定产品
     * @param guids 组态guid
     * @return void
     */
    void deleteProductByGuids(@Param("guids") List<String> guids);

    /**
     * 删除场景绑定产品
     * @param guids 组态guid
     * @return void
     */
    void deleteSceneModelByGuids(@Param("guids") List<String> guids);

    /**
     * 查询组态绑定产品
     * @param guid 组态guid
     * @return java.lang.Long
     */
    Long selectProductByGuid(String guid);

    /**
     * 查询组态绑定场景
     * @param guid 组态guid
     * @return java.lang.Long
     */
    Long selectSceneModelByGuid(String guid);

    /**
     * 批量查询组态
     * @param ids 主键
     * @return java.util.List<com.fastbee.scada.domain.Scada>
     */
    List<Scada> selectScadaListByIds(Long[] ids);

    /**
     * 查询场景变量
     * @param scadaQueryParam 查询参数
     * @return java.util.List<com.fastbee.scada.vo.ScadaVariableDataVO>
     */
    List<ScadaVariableDataVO> selectListSceneModelData(ScadaQueryParam scadaQueryParam);

    /**
     * @description:  查询场景变量
     * @param: scadaQueryParam 查询参数
     * @return: java.lang.Integer
     */
    Integer countListSceneModelData(ScadaQueryParam scadaQueryParam);

    /**
     * 查询产品信息
     * @param productId 产品id
     * @return com.fastbee.scada.vo.ScadaVariableDataVO
     */
    ScadaVariableDataVO selectProductByProductId(Long productId);

    /**
     * 查询组态绑定场景id
     * @param scadaGuidList 组态guids
     * @return java.util.Map<java.lang.String,java.lang.Long>
     */
    List<ScadaVariableSourceVO> selectListSceneByGuids(@Param("scadaGuidList") List<String> scadaGuidList);

    /**
     * 查询组态绑定产品
     * @param scadaGuidList 组态guids
     * @return java.util.List<com.fastbee.scada.vo.ScadaVariableSourceVO>
     */
    List<ScadaVariableSourceVO> selectListProductByGuids(@Param("scadaGuidList") List<String> scadaGuidList);

    /**
     * 查询设备编号
     * @param sceneModelDeviceId 场景数据来源id
     * @return java.lang.String
     */
    String selectSerialNumberBySceneModelDeviceId(Long sceneModelDeviceId);

    /**
     * 查询场景数据来源
     * @param guid 组态guid
     * @return java.lang.String
     */
    List<ScadaBindDeviceSimVO> selectSceneModelDeviceListByGuid(String guid);

    /**
     * @description: 查询场景变量信息
     * @param: identifierList 标识符信息
     * @return: java.util.List<com.fastbee.scada.vo.ScadaVariableDataVO>
     */
    List<ScadaVariableDataVO> selectSceneModelDataList(@Param("identifierList") List<String> identifierList,
                                                       @Param("sceneModelId") Long sceneModelId,
                                                       @Param("sceneModelDeviceId") Long sceneModelDeviceId);

    /**
     * @description: 查询场景变量历史数据
     * @param: sceneVariableList 场景变量集合
     * @return: java.util.List<com.fastbee.scada.vo.ThingsModelHistoryParam.ThingsModelSim>
     */
    List<ScadaVariableDataVO> selectSceneDataHistoryList(@Param("sceneIdentifierList") List<String> sceneIdentifierList,
                                                                            @Param("beginTime") String beginTime, @Param("endTime") String endTime);
    /**
     * @description: 更新组态分享信息
     * @param: scadaVO 组态信息
     * @return: void
     */
    void updateScadaShare(ScadaShareVO scadaShareVO);

    SysUser selectSysUser(Long userId);
}
