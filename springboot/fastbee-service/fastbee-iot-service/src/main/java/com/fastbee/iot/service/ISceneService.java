package com.fastbee.iot.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.SceneTerminalUserVO;
import com.fastbee.iot.model.vo.SceneVO;
import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.parser.entity.node.Node;

/**
 * 场景联动Service接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
public interface ISceneService extends IService<Scene> {
    /**
     * 查询场景联动
     *
     * @param scene 场景联动
     * @return 场景联动
     */
    public SceneVO selectSceneBySceneId(Scene scene);

    public Scene selectScene(Long sceneId);
    public Scene selectSceneByChainName(String chainName);

    /**
     * 分页查询
     *
     * @param scene 场景实体类
     * @return
     */
    Page<SceneVO> pageSceneVO(Scene scene);

    /**
     * 查询场景联动列表
     *
     * @param scene 场景联动
     * @return 场景联动集合
     */
    public List<SceneVO> selectSceneVOList(Scene scene);

    /**
     * 新增场景联动
     *
     * @param sceneVO 场景联动
     * @return 结果
     */
    public int insertScene(SceneVO sceneVO);

    public int insertSceneByView(Scene scene, RuleEl ruleEl, List<Node> triggerList);


    /**
     * 修改场景联动
     *
     * @param sceneVO 场景联动
     * @return 结果
     */
    public int updateScene(SceneVO sceneVO);

    public int updateSceneByView(Scene scene, RuleEl ruleEl, List<Node> triggerList);
    public void updateSceneStatusByView(Scene scene, RuleEl ruleEl, List<Node> list);
    /**
     * 批量删除场景联动
     *
     * @param sceneIds 需要删除的场景联动主键集合
     * @return 结果
     */
    public int deleteSceneBySceneIds(Long[] sceneIds);

    /**
     * 删除场景联动信息
     *
     * @param sceneId 场景联动主键
     * @return 结果
     */
    public int deleteSceneBySceneId(Long sceneId);

    /**
     * 修改场景联动状态
     *
     * @param scene 场景联动
     * @return int
     */
    int updateStatus(Scene scene);


    /**
     * 查询场景用户信息
     *
     * @param sceneIdSet 场景id
     * @return java.util.List<com.fastbee.iot.model.vo.SceneTerminalUserVO>
     */
    List<SceneTerminalUserVO> selectTerminalUserBySceneIds(Set<Long> sceneIdSet);

    /**
     * 根据场景查询绑定的告警配置列表
     *
     * @param scene 场景ID
     * @return 告警配置列表
     */
    Page<Alert> selectAlarmConfigsByScene(SceneVO scene);

    /**
     * 为场景绑定告警配置
     * @param sceneId 场景ID
     * @param alertIds 告警配置ID列表
     * @return 结果
     */
    int bindAlertToScene(Long sceneId, List<Long> alertIds);

    /**
     * 解除场景与告警配置的绑定关系
     * @param sceneId 场景ID
     * @param alertIds 告警配置ID列表
     * @return 结果
     */
    int unbindAlertFromScene(Long sceneId, List<Long> alertIds);
}
