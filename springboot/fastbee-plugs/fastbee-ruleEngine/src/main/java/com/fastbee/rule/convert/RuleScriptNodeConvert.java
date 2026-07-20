package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleScriptNode;
import com.fastbee.rule.domain.vo.RuleScriptNodeVO;

/**
 * 规则脚本节点Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Mapper
public interface RuleScriptNodeConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleScriptNodeConvert INSTANCE = Mappers.getMapper(RuleScriptNodeConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleScriptNode
     * @return 规则脚本节点集合
     */
    RuleScriptNodeVO convertRuleScriptNodeVO(RuleScriptNode ruleScriptNode);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleScriptNodeVO
     * @return 规则脚本节点集合
     */
    RuleScriptNode convertRuleScriptNode(RuleScriptNodeVO ruleScriptNodeVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleScriptNodeList
     * @return 规则脚本节点集合
     */
    List<RuleScriptNodeVO> convertRuleScriptNodeVOList(List<RuleScriptNode> ruleScriptNodeList);

    /**
     * VO类转换为实体类
     *
     * @param ruleScriptNodeVOList
     * @return 规则脚本节点集合
     */
    List<RuleScriptNode> convertRuleScriptNodeList(List<RuleScriptNodeVO> ruleScriptNodeVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleScriptNodePage
     * @return 规则脚本节点分页
     */
    Page<RuleScriptNodeVO> convertRuleScriptNodeVOPage(Page<RuleScriptNode> ruleScriptNodePage);

    /**
     * VO类转换为实体类
     *
     * @param ruleScriptNodeVOPage
     * @return 规则脚本节点分页
     */
    Page<RuleScriptNode> convertRuleScriptNodePage(Page<RuleScriptNodeVO> ruleScriptNodeVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
