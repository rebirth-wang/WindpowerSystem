package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleTrigger;
import com.fastbee.rule.domain.vo.RuleTriggerVO;

/**
 * 规则触发条件Convert转换类
 *
 * @author fastbee
 * @date 2025-10-21
 */
@Mapper
public interface RuleTriggerConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleTriggerConvert INSTANCE = Mappers.getMapper(RuleTriggerConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleTrigger
     * @return 规则触发条件集合
     */
    RuleTriggerVO convertRuleTriggerVO(RuleTrigger ruleTrigger);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleTriggerVO
     * @return 规则触发条件集合
     */
    RuleTrigger convertRuleTrigger(RuleTriggerVO ruleTriggerVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleTriggerList
     * @return 规则触发条件集合
     */
    List<RuleTriggerVO> convertRuleTriggerVOList(List<RuleTrigger> ruleTriggerList);

    /**
     * VO类转换为实体类
     *
     * @param ruleTriggerVOList
     * @return 规则触发条件集合
     */
    List<RuleTrigger> convertRuleTriggerList(List<RuleTriggerVO> ruleTriggerVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleTriggerPage
     * @return 规则触发条件分页
     */
    Page<RuleTriggerVO> convertRuleTriggerVOPage(Page<RuleTrigger> ruleTriggerPage);

    /**
     * VO类转换为实体类
     *
     * @param ruleTriggerVOPage
     * @return 规则触发条件分页
     */
    Page<RuleTrigger> convertRuleTriggerPage(Page<RuleTriggerVO> ruleTriggerVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
