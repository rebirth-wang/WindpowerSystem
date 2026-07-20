package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleScriptNode;
import com.fastbee.rule.domain.vo.RuleScriptNodeVO;

/**
 * 规则脚本节点Service接口
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
public interface IRuleScriptNodeService extends IService<RuleScriptNode>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则脚本节点列表
     *
     * @param ruleScriptNode 规则脚本节点
     * @return 规则脚本节点分页集合
     */
    Page<RuleScriptNodeVO> pageRuleScriptNodeVO(RuleScriptNode ruleScriptNode);

    /**
     * 查询规则脚本节点列表
     *
     * @param ruleScriptNode 规则脚本节点
     * @return 规则脚本节点集合
     */
    List<RuleScriptNodeVO> listRuleScriptNodeVO(RuleScriptNode ruleScriptNode);

    /**
     * 查询规则脚本节点
     *
     * @param id 主键
     * @return 规则脚本节点
     */
     RuleScriptNode selectRuleScriptNodeById(Long id);

    /**
     * 查询规则脚本节点
     *
     * @param id 主键
     * @return 规则脚本节点
     */
    RuleScriptNode queryByIdWithCache(Long id);

    /**
     * 新增规则脚本节点
     *
     * @param ruleScriptNode 规则脚本节点
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleScriptNode ruleScriptNode);

    /**
     * 修改规则脚本节点
     *
     * @param ruleScriptNode 规则脚本节点
     * @return 是否修改成功
     */
    Boolean updateWithCache(RuleScriptNode ruleScriptNode);

    /**
     * 校验并批量删除规则脚本节点信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
