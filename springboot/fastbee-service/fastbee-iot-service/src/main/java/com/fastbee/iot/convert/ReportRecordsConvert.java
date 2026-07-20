package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ReportRecords;
import com.fastbee.iot.model.vo.ReportRecordsVO;

/**
 * 报表记录Convert转换类
 *
 * @author zzy
 * @date 2025-07-09
 */
@Mapper
public interface ReportRecordsConvert
{
    /** 代码生成区域 可直接覆盖**/
    ReportRecordsConvert INSTANCE = Mappers.getMapper(ReportRecordsConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param reportRecords
     * @return 报表记录集合
     */
    ReportRecordsVO convertReportRecordsVO(ReportRecords reportRecords);

    /**
     * VO类转换为实体类集合
     *
     * @param reportRecordsVO
     * @return 报表记录集合
     */
    ReportRecords convertReportRecords(ReportRecordsVO reportRecordsVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param reportRecordsList
     * @return 报表记录集合
     */
    List<ReportRecordsVO> convertReportRecordsVOList(List<ReportRecords> reportRecordsList);

    /**
     * VO类转换为实体类
     *
     * @param reportRecordsVOList
     * @return 报表记录集合
     */
    List<ReportRecords> convertReportRecordsList(List<ReportRecordsVO> reportRecordsVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param reportRecordsPage
     * @return 报表记录分页
     */
    Page<ReportRecordsVO> convertReportRecordsVOPage(Page<ReportRecords> reportRecordsPage);

    /**
     * VO类转换为实体类
     *
     * @param reportRecordsVOPage
     * @return 报表记录分页
     */
    Page<ReportRecords> convertReportRecordsPage(Page<ReportRecordsVO> reportRecordsVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
