package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleChain;
import com.fastbee.rule.domain.vo.RuleChainVO;

/**
 * 规则链Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Mapper
public interface RuleChainConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleChainConvert INSTANCE = Mappers.getMapper(RuleChainConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleChain
     * @return 规则链集合
     */
    RuleChainVO convertRuleChainVO(RuleChain ruleChain);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleChainVO
     * @return 规则链集合
     */
    RuleChain convertRuleChain(RuleChainVO ruleChainVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleChainList
     * @return 规则链集合
     */
    List<RuleChainVO> convertRuleChainVOList(List<RuleChain> ruleChainList);

    /**
     * VO类转换为实体类
     *
     * @param ruleChainVOList
     * @return 规则链集合
     */
    List<RuleChain> convertRuleChainList(List<RuleChainVO> ruleChainVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleChainPage
     * @return 规则链分页
     */
    Page<RuleChainVO> convertRuleChainVOPage(Page<RuleChain> ruleChainPage);

    /**
     * VO类转换为实体类
     *
     * @param ruleChainVOPage
     * @return 规则链分页
     */
    Page<RuleChain> convertRuleChainPage(Page<RuleChainVO> ruleChainVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
