package com.fastbee.rule.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.rule.domain.RuleCmp;
import com.fastbee.rule.domain.vo.RuleCmpVO;

/**
 * 规则组件Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Mapper
public interface RuleCmpConvert
{
    /** 代码生成区域 可直接覆盖**/
    RuleCmpConvert INSTANCE = Mappers.getMapper(RuleCmpConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ruleCmp
     * @return 规则组件集合
     */
    RuleCmpVO convertRuleCmpVO(RuleCmp ruleCmp);

    /**
     * VO类转换为实体类集合
     *
     * @param ruleCmpVO
     * @return 规则组件集合
     */
    RuleCmp convertRuleCmp(RuleCmpVO ruleCmpVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ruleCmpList
     * @return 规则组件集合
     */
    List<RuleCmpVO> convertRuleCmpVOList(List<RuleCmp> ruleCmpList);

    /**
     * VO类转换为实体类
     *
     * @param ruleCmpVOList
     * @return 规则组件集合
     */
    List<RuleCmp> convertRuleCmpList(List<RuleCmpVO> ruleCmpVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ruleCmpPage
     * @return 规则组件分页
     */
    Page<RuleCmpVO> convertRuleCmpVOPage(Page<RuleCmp> ruleCmpPage);

    /**
     * VO类转换为实体类
     *
     * @param ruleCmpVOPage
     * @return 规则组件分页
     */
    Page<RuleCmp> convertRuleCmpPage(Page<RuleCmpVO> ruleCmpVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
