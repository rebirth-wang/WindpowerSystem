package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SceneModelData;
import com.fastbee.iot.model.scenemodel.SceneDeviceThingsModelVO;
import com.fastbee.iot.model.scenemodel.SceneModelDataDTO;
import com.fastbee.iot.model.scenemodel.SceneModelDeviceDataDTO;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneModelDataMapper extends BaseMapperX<SceneModelData>
{

    /**
     * 统计统一关联来源未启用
     * @param sceneModelDeviceId
     * @return int
     */
    int countNoEnableBySceneModelDeviceId(Long sceneModelDeviceId);

    /**
     * 查询关联设备
     * @param idList 变量id集合
     * @return com.fastbee.iot.model.scenemodel.SceneModelDeviceVO
     */
    List<SceneModelDeviceDataDTO> selectSceneModelDeviceByDataIdList(@Param("idList") List<Long> idList);

    /**
     * 查询单个物模型来源
     * @param id 主键
     * @return com.fastbee.iot.model.scenemodel.SceneDeviceThingsModelVO
     */
    SceneDeviceThingsModelVO selectDeviceThingsModelById(Long id);

    /**
     * 查询录入型变量默认值
     * @param id 主键id
     * @return java.lang.String
     */
    String selectInputTagDefaultValueById(Long id);

    /**
     * 检查是否被应用到计算公式
     * @param datasourceIdList  数据源id集合
     * @param: type
     * @return int
     */
    int checkIsApplyAliasFormule(@Param("datasourceIdList") List<Long> datasourceIdList, @Param("variableType") Integer variableType);

    /**
     * 查询场景变量完整信息列表
     * @param sceneModelData 场景变量类
     * @return java.util.List<com.fastbee.iot.model.scenemodel.SceneModelDataDTO>
     */
    Page<SceneModelDataDTO> selectSceneModelDataDTOList(Page<SceneModelDataDTO> page, @Param("sceneModelData") SceneModelData sceneModelData);

    /**
     * 查询场景关联设备物模型
     * @param datasourceId 数据源id
     * @return java.util.List<com.fastbee.iot.domain.SceneModelData>
     */
    List<SceneModelData> selectSceneDeviceThingsModelList(Long datasourceId);

    /**
     * @description: 同步变量
     * @param: updateSceneModelData 变量信息
     * @return: void
     */
    void updateSceneModelDataByDatasourceId(SceneModelData updateSceneModelData);

    void phyDeleteBySceneModelIds(List<Long> sceneModelIds);

    int restoreBySceneModelId(Long sceneModelId);
}
