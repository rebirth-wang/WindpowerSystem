package com.fastbee.quartz.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.quartz.domain.SysJob;
import com.fastbee.quartz.vo.SysJobVO;

/**
 * 定时任务调度Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@Mapper
public interface SysJobConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysJobConvert INSTANCE = Mappers.getMapper(SysJobConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysJob
     * @return 定时任务调度集合
     */
    SysJobVO convertSysJobVO(SysJob sysJob);

    /**
     * VO类转换为实体类集合
     *
     * @param sysJobVO
     * @return 定时任务调度集合
     */
    SysJob convertSysJob(SysJobVO sysJobVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysJobList
     * @return 定时任务调度集合
     */
    List<SysJobVO> convertSysJobVOList(List<SysJob> sysJobList);

    /**
     * VO类转换为实体类
     *
     * @param sysJobVOList
     * @return 定时任务调度集合
     */
    List<SysJob> convertSysJobList(List<SysJobVO> sysJobVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysJobPage
     * @return 定时任务调度分页
     */
    Page<SysJobVO> convertSysJobVOPage(Page<SysJob> sysJobPage);

    /**
     * VO类转换为实体类
     *
     * @param sysJobVOPage
     * @return 定时任务调度分页
     */
    Page<SysJob> convertSysJobPage(Page<SysJobVO> sysJobVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
