package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleExecutor;
import com.fastbee.rule.domain.vo.RuleExecutorVO;

/**
 * 规则执行器Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Mapper
public interface RuleExecutorConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleExecutorConvert INSTANCE = Mappers.getMapper(RuleExecutorConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleExecutor
     * @return 规则执行器集合
     */
    RuleExecutorVO convertRuleExecutorVO(RuleExecutor ruleExecutor);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleExecutorVO
     * @return 规则执行器集合
     */
    RuleExecutor convertRuleExecutor(RuleExecutorVO ruleExecutorVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleExecutorList
     * @return 规则执行器集合
     */
    List<RuleExecutorVO> convertRuleExecutorVOList(List<RuleExecutor> ruleExecutorList);

    /**
     * VO类转换为实体类
     *
     * @param ruleExecutorVOList
     * @return 规则执行器集合
     */
    List<RuleExecutor> convertRuleExecutorList(List<RuleExecutorVO> ruleExecutorVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleExecutorPage
     * @return 规则执行器分页
     */
    Page<RuleExecutorVO> convertRuleExecutorVOPage(Page<RuleExecutor> ruleExecutorPage);

    /**
     * VO类转换为实体类
     *
     * @param ruleExecutorVOPage
     * @return 规则执行器分页
     */
    Page<RuleExecutor> convertRuleExecutorPage(Page<RuleExecutorVO> ruleExecutorVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
