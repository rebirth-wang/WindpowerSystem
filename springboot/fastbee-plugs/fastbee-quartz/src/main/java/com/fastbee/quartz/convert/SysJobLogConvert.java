package com.fastbee.quartz.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.quartz.domain.SysJobLog;
import com.fastbee.quartz.vo.SysJobLogVO;

/**
 * 定时任务调度日志Convert转换类
 *
 * @author gx_ma
 * @date 2024-12-24
 */
@Mapper
public interface SysJobLogConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysJobLogConvert INSTANCE = Mappers.getMapper(SysJobLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysJobLog
     * @return 定时任务调度日志集合
     */
    SysJobLogVO convertSysJobLogVO(SysJobLog sysJobLog);

    /**
     * VO类转换为实体类集合
     *
     * @param sysJobLogVO
     * @return 定时任务调度日志集合
     */
    SysJobLog convertSysJobLog(SysJobLogVO sysJobLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysJobLogList
     * @return 定时任务调度日志集合
     */
    List<SysJobLogVO> convertSysJobLogVOList(List<SysJobLog> sysJobLogList);

    /**
     * VO类转换为实体类
     *
     * @param sysJobLogVOList
     * @return 定时任务调度日志集合
     */
    List<SysJobLog> convertSysJobLogList(List<SysJobLogVO> sysJobLogVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysJobLogPage
     * @return 定时任务调度日志分页
     */
    Page<SysJobLogVO> convertSysJobLogVOPage(Page<SysJobLog> sysJobLogPage);

    /**
     * VO类转换为实体类
     *
     * @param sysJobLogVOPage
     * @return 定时任务调度日志分页
     */
    Page<SysJobLog> convertSysJobLogPage(Page<SysJobLogVO> sysJobLogVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
