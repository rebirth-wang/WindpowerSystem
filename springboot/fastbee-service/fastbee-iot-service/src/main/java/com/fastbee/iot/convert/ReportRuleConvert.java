package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ReportRule;
import com.fastbee.iot.model.vo.ReportRuleVO;

/**
 * 报表规则Convert转换类
 *
 * @author zzy
 * @date 2025-07-10
 */
@Mapper
public interface ReportRuleConvert
{
    /** 代码生成区域 可直接覆盖**/
    ReportRuleConvert INSTANCE = Mappers.getMapper(ReportRuleConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param reportRule
     * @return 报表规则集合
     */
    ReportRuleVO convertReportRuleVO(ReportRule reportRule);

    /**
     * VO类转换为实体类集合
     *
     * @param reportRuleVO
     * @return 报表规则集合
     */
    ReportRule convertReportRule(ReportRuleVO reportRuleVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param reportRuleList
     * @return 报表规则集合
     */
    List<ReportRuleVO> convertReportRuleVOList(List<ReportRule> reportRuleList);

    /**
     * VO类转换为实体类
     *
     * @param reportRuleVOList
     * @return 报表规则集合
     */
    List<ReportRule> convertReportRuleList(List<ReportRuleVO> reportRuleVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param reportRulePage
     * @return 报表规则分页
     */
    Page<ReportRuleVO> convertReportRuleVOPage(Page<ReportRule> reportRulePage);

    /**
     * VO类转换为实体类
     *
     * @param reportRuleVOPage
     * @return 报表规则分页
     */
    Page<ReportRule> convertReportRulePage(Page<ReportRuleVO> reportRuleVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
