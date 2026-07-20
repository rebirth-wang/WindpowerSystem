package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Report;
import com.fastbee.iot.model.vo.ReportVO;

/**
 * 报表管理Convert转换类
 *
 * @author zzy
 * @date 2025-07-09
 */
@Mapper
public interface ReportConvert
{
    /** 代码生成区域 可直接覆盖**/
    ReportConvert INSTANCE = Mappers.getMapper(ReportConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param report
     * @return 报表管理集合
     */
    ReportVO convertReportVO(Report report);

    /**
     * VO类转换为实体类集合
     *
     * @param reportVO
     * @return 报表管理集合
     */
    Report convertReport(ReportVO reportVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param reportList
     * @return 报表管理集合
     */
    List<ReportVO> convertReportVOList(List<Report> reportList);

    /**
     * VO类转换为实体类
     *
     * @param reportVOList
     * @return 报表管理集合
     */
    List<Report> convertReportList(List<ReportVO> reportVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param reportPage
     * @return 报表管理分页
     */
    Page<ReportVO> convertReportVOPage(Page<Report> reportPage);

    /**
     * VO类转换为实体类
     *
     * @param reportVOPage
     * @return 报表管理分页
     */
    Page<Report> convertReportPage(Page<ReportVO> reportVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
