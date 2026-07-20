package com.fastbee.iot.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.Script;
import com.fastbee.iot.model.ScriptCondition;
import com.fastbee.iot.model.vo.ScriptVO;
import com.fastbee.rule.context.MsgContext;
import com.fastbee.rule.parser.entity.node.Node;

/**
 * 规则引擎脚本Service接口
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
public interface IScriptService extends IService<Script>
{
    /**
     * 查询规则引擎脚本
     *
     * @param script 规则引擎脚本
     * @return 规则引擎脚本
     */
    public ScriptVO selectRuleScriptById(Script script);

    /**
     * 查询规则脚本日志
     *
     * @param scriptId 规则引擎脚本主键
     * @return 规则引擎脚本
     */
    public String selectRuleScriptLog(String type, String scriptId);
    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本集合
     */
    public Page<ScriptVO> selectRuleScriptList(Script ruleScript);
    public List<Script> selectExecRuleScriptList(ScriptCondition ruleScript);

    /**
     * 查询规则引擎脚本标识数组（设备用户和租户的脚本）
     *
     * @return 规则引擎脚本
     */
    public String[] selectRuleScriptIdArray(ScriptCondition scriptCondition);

    /**
     * 新增规则引擎脚本
     *
     * @param scriptVO 规则引擎脚本
     * @return 结果
     */
    public int insertRuleScript(ScriptVO scriptVO);

    /**
     * 修改规则引擎脚本
     *
     * @param scriptVO 规则引擎脚本
     * @return 结果
     */
    public int updateRuleScript(ScriptVO scriptVO);

    /**
     * 批量删除规则引擎脚本
     *
     * @param ids 需要删除的规则引擎脚本主键集合
     * @return 结果
     */
    public int deleteRuleScriptByIds(String[] ids);

    /**
     * 删除规则引擎脚本信息
     *
     * @param id 规则引擎脚本主键
     * @return 结果
     */
    public int deleteRuleScriptById(String id);


    /**
     * 验证脚本
     * @param ruleScript 脚本数据
     * @return
     */
    public AjaxResult validateScript(Script ruleScript);

    /**
     * 批量删除规则脚本
     *
     * @param sceneIds 需要删除的数据场景ID集合
     * @return 结果
     */
    public int deleteRuleScriptBySceneIds(Long[] sceneIds);

    /**
     * 批量新增场景脚本
     *
     * @param ruleScriptList 场景联动触发器集合
     * @return 结果
     */
    public int insertRuleScriptList(List<Script> ruleScriptList);
    public MsgContext execRuleScript(ScriptCondition scriptCondition, Object... contextBeanArray);
    Set<String> addAllowPackage(String allowPackage);
    Set<String> removeAllowPackage(String allowPackage);
    Set<String> getAllowPackage();

    int publishScriptByView(List<Node> scriptList);
    public MsgContext processRuleScript(String serialNumber, Integer event, String topic, String payload);
}
