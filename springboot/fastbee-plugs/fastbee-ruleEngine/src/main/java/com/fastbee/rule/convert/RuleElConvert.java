package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.domain.vo.RuleElVO;

/**
 * 规则elConvert转换类
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Mapper
public interface RuleElConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleElConvert INSTANCE = Mappers.getMapper(RuleElConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleEl
     * @return 规则el集合
     */
    RuleElVO convertRuleElVO(RuleEl ruleEl);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleElVO
     * @return 规则el集合
     */
    RuleEl convertRuleEl(RuleElVO ruleElVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleElList
     * @return 规则el集合
     */
    List<RuleElVO> convertRuleElVOList(List<RuleEl> ruleElList);

    /**
     * VO类转换为实体类
     *
     * @param ruleElVOList
     * @return 规则el集合
     */
    List<RuleEl> convertRuleElList(List<RuleElVO> ruleElVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleElPage
     * @return 规则el分页
     */
    Page<RuleElVO> convertRuleElVOPage(Page<RuleEl> ruleElPage);

    /**
     * VO类转换为实体类
     *
     * @param ruleElVOPage
     * @return 规则el分页
     */
    Page<RuleEl> convertRuleElPage(Page<RuleElVO> ruleElVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
