package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ReportRuleData;
import com.fastbee.iot.model.vo.ReportRuleDataVO;

/**
 * 报表规则变量Convert转换类
 *
 * @author zzy
 * @date 2025-07-10
 */
@Mapper
public interface ReportRuleDataConvert
{
    /** 代码生成区域 可直接覆盖**/
    ReportRuleDataConvert INSTANCE = Mappers.getMapper(ReportRuleDataConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param reportRuleData
     * @return 报表规则变量集合
     */
    ReportRuleDataVO convertReportRuleDataVO(ReportRuleData reportRuleData);

    /**
     * VO类转换为实体类集合
     *
     * @param reportRuleDataVO
     * @return 报表规则变量集合
     */
    ReportRuleData convertReportRuleData(ReportRuleDataVO reportRuleDataVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param reportRuleDataList
     * @return 报表规则变量集合
     */
    List<ReportRuleDataVO> convertReportRuleDataVOList(List<ReportRuleData> reportRuleDataList);

    /**
     * VO类转换为实体类
     *
     * @param reportRuleDataVOList
     * @return 报表规则变量集合
     */
    List<ReportRuleData> convertReportRuleDataList(List<ReportRuleDataVO> reportRuleDataVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param reportRuleDataPage
     * @return 报表规则变量分页
     */
    Page<ReportRuleDataVO> convertReportRuleDataVOPage(Page<ReportRuleData> reportRuleDataPage);

    /**
     * VO类转换为实体类
     *
     * @param reportRuleDataVOPage
     * @return 报表规则变量分页
     */
    Page<ReportRuleData> convertReportRuleDataPage(Page<ReportRuleDataVO> reportRuleDataVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
