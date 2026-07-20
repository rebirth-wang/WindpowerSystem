package com.fastbee.iot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.ReportRecords;
import com.fastbee.iot.model.vo.ReportRecordsVO;

/**
 * 报表记录Mapper接口
 *
 * @author zzy
 * @date 2025-07-09
 */
public interface ReportRecordsMapper extends BaseMapperX<ReportRecords>
{


    Page<ReportRecordsVO> selectReportRecordsPage(Page<ReportRecordsVO> page, @Param("reportRecordsVO") ReportRecordsVO reportRecordsVO);
}
