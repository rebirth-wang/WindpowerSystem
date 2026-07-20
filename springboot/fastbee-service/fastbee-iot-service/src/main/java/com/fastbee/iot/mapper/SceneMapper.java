package com.fastbee.iot.mapper;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.SceneTerminalUserVO;

/**
 * 场景联动Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface SceneMapper extends BaseMapperX<Scene>
{

    /**
     * 查询场景用户信息
     * @param sceneIdSet 场景id
     * @return java.util.List<com.fastbee.iot.model.vo.SceneTerminalUserVO>
     */
    List<SceneTerminalUserVO> selectTerminalUserBySceneIds(Set<Long> sceneIdSet);
}
