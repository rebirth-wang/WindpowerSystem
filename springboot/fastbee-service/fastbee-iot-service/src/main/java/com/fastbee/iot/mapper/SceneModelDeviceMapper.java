package com.fastbee.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SceneModelDevice;
import com.fastbee.iot.model.scenemodel.SceneHistoryParam;

/**
 * 场景管理关联设备Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneModelDeviceMapper extends BaseMapperX<SceneModelDevice>
{

    /**
     * 校验是否有使用计算公式
     * @param id 主键
     * @return int
     */
    int checkContainAliasFormule(Long id);

    /**
     * 查询场景关联设备
     * @param productId 产品id
     * @return java.util.List<com.fastbee.iot.domain.SceneModelDevice>
     */
    List<SceneModelDevice> listDeviceByProductId(Long productId);

    SceneHistoryParam selectReportRuleScene(Long id);

    /**
     * 批量查询场景关联设备信息
     * @param ids 场景设备ID列表
     * @return 场景历史参数列表
     */
    List<SceneHistoryParam> selectReportRuleSceneBatch(@Param("ids") List<Long> ids);

    int phyDeleteBySceneModelIds(List<Long> sceneModelIds);

    int restoreBySceneModelId(Long sceneModelId);
}
