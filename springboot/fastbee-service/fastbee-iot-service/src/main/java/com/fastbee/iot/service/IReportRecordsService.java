package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ReportRecords;
import com.fastbee.iot.model.vo.ReportRecordsVO;

/**
 * 报表记录Service接口
 *
 * @author zzy
 * @date 2025-07-09
 */
public interface IReportRecordsService extends IService<ReportRecords>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询报表记录列表
     *
     * @param reportRecordsVO 报表记录
     * @return 报表记录分页集合
     */
    Page<ReportRecordsVO> pageReportRecordsVO(ReportRecordsVO reportRecordsVO);

    /**
     * 查询报表记录列表
     *
     * @param reportRecords 报表记录
     * @return 报表记录集合
     */
    List<ReportRecordsVO> listReportRecordsVO(ReportRecords reportRecords);

    /**
     * 查询报表记录
     *
     * @param id 主键
     * @return 报表记录
     */
    ReportRecords selectReportRecordsById(Long id);

    /**
     * 查询报表记录
     *
     * @param id 主键
     * @return 报表记录
     */
    ReportRecords queryByIdWithCache(Long id);

    /**
     * 新增报表记录
     *
     * @param reportRecords 报表记录
     * @return 是否新增成功
     */
    Boolean insertWithCache(ReportRecords reportRecords);

    /**
     * 修改报表记录
     *
     * @param reportRecords 报表记录
     * @return 是否修改成功
     */
    Boolean updateWithCache(ReportRecords reportRecords);

    /**
     * 校验并批量删除报表记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

}
