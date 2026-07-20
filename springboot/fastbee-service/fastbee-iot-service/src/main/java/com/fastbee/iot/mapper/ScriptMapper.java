package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Script;
import com.fastbee.iot.model.ScriptCondition;
import com.fastbee.iot.model.vo.ScriptVO;

/**
 * 规则引擎脚本Mapper接口
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
@Repository
public interface ScriptMapper extends BaseMapperX<Script>
{
    /**
     * 查询规则引擎脚本
     *
     * @param script 规则引擎脚本
     * @return 规则引擎脚本
     */
    public ScriptVO selectRuleScriptById(@Param("script") Script script);

    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本集合
     */
    public List<ScriptVO> selectRuleScriptList(@Param("ruleScript")  Script ruleScript);

    /**
     * 查询规则引擎脚本列表
     *
     * @param ruleScript 规则引擎脚本
     * @return 规则引擎脚本集合
     */
    public Page<ScriptVO> selectRuleScriptList(Page<ScriptVO> page, @Param("ruleScript") Script ruleScript);
    public List<Script> selectExecRuleScriptList(ScriptCondition ruleScript);

    /**
     * 查询规则引擎脚本标识数组
     *
     * @return 规则引擎脚本数组
     */
    public String[] selectRuleScriptIdArray(ScriptCondition scriptCondition);

    /***
     * 查询scriptId的数量
     * @param scriptId
     * @return
     */
    public int selectRuleScriptIdCount(String scriptId);


    public int insertScriptBridge(@Param("scriptId")String scriptId, @Param("bridgeId")Long bridgeId);
    public int updateScriptBridge(@Param("scriptId")String scriptId, @Param("bridgeId")Long bridgeId);
    public int deleteScriptBridge(String scriptId);
}
