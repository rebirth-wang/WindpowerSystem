package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.SceneModelTag;
import com.fastbee.iot.domain.SceneTagPoints;
import com.fastbee.iot.model.scenemodel.JobCronCycleVO;
import com.fastbee.iot.model.vo.SceneModelTagVO;

/**
 * 场景录入型变量Service接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface ISceneModelTagService extends IService<SceneModelTag>
{
    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    public SceneModelTagVO selectSceneModelTagById(Long id);

    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    SceneModelTagVO selectSceneModelTagAndTenantById(Long id);

    /**
     * 查询场景录入型变量列表
     *
     * @param sceneModelTag 场景录入型变量
     * @return 场景录入型变量分页集合
     */
    Page<SceneModelTagVO> pageSceneModelTagVO(SceneModelTag sceneModelTag);

    /**
     * 查询场景录入型变量列表
     *
     * @param sceneModelTag 场景录入型变量
     * @return 场景录入型变量集合
     */
    List<SceneModelTagVO> listSceneModelTagVO(SceneModelTag sceneModelTag);

    /**
     * 新增场景录入型变量
     *
     * @param sceneModelTagVO 场景录入型变量
     * @return 结果
     */
    public SceneModelTagVO insertSceneModelTag(SceneModelTagVO sceneModelTagVO);

    /**
     * 修改场景录入型变量
     *
     * @param sceneModelTagVO 场景录入型变量
     * @return 结果
     */
    public SceneModelTagVO updateSceneModelTag(SceneModelTagVO sceneModelTagVO);

    /**
     * 批量删除场景录入型变量
     *
     * @param ids 需要删除的场景录入型变量主键集合
     * @return 结果
     */
    public int deleteSceneModelTagByIds(Long[] ids);

    /**
     * 删除场景录入型变量信息
     *
     * @param id 场景录入型变量主键
     * @return 结果
     */
    public int deleteSceneModelTagById(Long id);

    /**
     * 校验变量是否被运用到计算公式
     * @param sceneModelTagVO 场景变量类
     * @return java.lang.String
     */
    String checkAliasFormule(SceneModelTagVO sceneModelTagVO);

    /**
     * 获取场景变量实时值
     * @param sceneTagPoints 场景变量类
     * @param: jobCronCycleVO 时间周期类
     * @return java.lang.String
     */
    String getSceneModelDataValue(SceneTagPoints sceneTagPoints, JobCronCycleVO jobCronCycleVO);

    List<SceneModelTag> selectEnableTagBySceneModelId(Long sceneModelId);

    void restoreTagJob(List<SceneModelTag> sceneModelTagList);
}
