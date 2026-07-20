package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.SceneTagPoints;
import com.fastbee.iot.model.vo.SceneTagPointsVO;

/**
 * 运算型变量点Service接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface ISceneTagPointsService extends IService<SceneTagPoints>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询运算型变量点列表
     *
     * @param sceneTagPoints 运算型变量点
     * @return 运算型变量点分页集合
     */
    Page<SceneTagPointsVO> pageSceneTagPointsVO(SceneTagPoints sceneTagPoints);

    /**
     * 查询运算型变量点列表
     *
     * @param sceneTagPoints 运算型变量点
     * @return 运算型变量点集合
     */
    List<SceneTagPointsVO> listSceneTagPointsVO(SceneTagPoints sceneTagPoints);

    /**
     * 查询运算型变量点
     *
     * @param id 主键
     * @return 运算型变量点
     */
    SceneTagPoints selectSceneTagPointsById(Long id);

    /**
     * 查询运算型变量点
     *
     * @param id 主键
     * @return 运算型变量点
     */
    SceneTagPoints queryByIdWithCache(Long id);

    /**
     * 新增运算型变量点
     *
     * @param sceneTagPoints 运算型变量点
     * @return 是否新增成功
     */
    Boolean insertWithCache(SceneTagPoints sceneTagPoints);

    /**
     * 修改运算型变量点
     *
     * @param sceneTagPoints 运算型变量点
     * @return 是否修改成功
     */
    Boolean updateWithCache(SceneTagPoints sceneTagPoints);

    /**
     * 校验并批量删除运算型变量点信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
}
