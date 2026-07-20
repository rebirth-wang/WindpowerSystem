package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleLog;
import com.fastbee.rule.domain.vo.RuleLogVO;

/**
 * 规则执行记录Convert转换类
 *
 * @author fastbee
 * @date 2025-10-21
 */
@Mapper
public interface RuleLogConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleLogConvert INSTANCE = Mappers.getMapper(RuleLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleLog
     * @return 规则执行记录集合
     */
    RuleLogVO convertRuleLogVO(RuleLog ruleLog);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleLogVO
     * @return 规则执行记录集合
     */
    RuleLog convertRuleLog(RuleLogVO ruleLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleLogList
     * @return 规则执行记录集合
     */
    List<RuleLogVO> convertRuleLogVOList(List<RuleLog> ruleLogList);

    /**
     * VO类转换为实体类
     *
     * @param ruleLogVOList
     * @return 规则执行记录集合
     */
    List<RuleLog> convertRuleLogList(List<RuleLogVO> ruleLogVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleLogPage
     * @return 规则执行记录分页
     */
    Page<RuleLogVO> convertRuleLogVOPage(Page<RuleLog> ruleLogPage);

    /**
     * VO类转换为实体类
     *
     * @param ruleLogVOPage
     * @return 规则执行记录分页
     */
    Page<RuleLog> convertRuleLogPage(Page<RuleLogVO> ruleLogVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
