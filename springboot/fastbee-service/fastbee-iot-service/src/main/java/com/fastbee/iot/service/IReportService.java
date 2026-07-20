package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.extend.core.domin.notify.ReportNotifyParams;
import com.fastbee.iot.domain.Report;
import com.fastbee.iot.model.vo.ReportVO;

/**
 * 报表管理Service接口
 *
 * @author zzy
 * @date 2025-07-09
 */
public interface IReportService extends IService<Report>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询报表管理列表
     *
     * @param report 报表管理
     * @return 报表管理分页集合
     */
    Page<ReportVO> pageReportVO(Report report);

    /**
     * 查询报表管理列表
     *
     * @param report 报表管理
     * @return 报表管理集合
     */
    List<ReportVO> listReportVO(Report report);

    /**
     * 查询报表管理
     *
     * @param id 主键
     * @return 报表管理
     */
     Report selectReportById(Long id);

    /**
     * 查询报表管理
     *
     * @param report 报表管理
     * @return 报表管理
     */
    ReportVO queryByIdWithCache(Report report);

    /**
     * 新增报表管理
     *
     * @param reportVO 报表管理
     * @return 是否新增成功
     */
    Boolean insertWithCache(ReportVO reportVO);

    /**
     * 修改报表管理
     *
     * @param reportVO 报表管理
     * @return 是否修改成功
     */
    Boolean updateWithCache(ReportVO reportVO);

    /**
     * 校验并批量删除报表管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * 执行报表任务
     *
     * @param datasourceId 数据源id
     * @return 是否执行成功
     */
    ReportNotifyParams executeJob(Long datasourceId) throws Exception;

}
