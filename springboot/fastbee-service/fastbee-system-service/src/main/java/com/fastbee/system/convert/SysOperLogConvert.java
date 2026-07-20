package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysOperLog;
import com.fastbee.system.domain.vo.SysOperLogVO;

/**
 * 操作日志记录Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@Mapper
public interface SysOperLogConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysOperLogConvert INSTANCE = Mappers.getMapper(SysOperLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysOperLog
     * @return 操作日志记录集合
     */
    SysOperLogVO convertSysOperLogVO(SysOperLog sysOperLog);

    /**
     * VO类转换为实体类集合
     *
     * @param sysOperLogVO
     * @return 操作日志记录集合
     */
    SysOperLog convertSysOperLog(SysOperLogVO sysOperLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysOperLogList
     * @return 操作日志记录集合
     */
    List<SysOperLogVO> convertSysOperLogVOList(List<SysOperLog> sysOperLogList);

    /**
     * VO类转换为实体类
     *
     * @param sysOperLogVOList
     * @return 操作日志记录集合
     */
    List<SysOperLog> convertSysOperLogList(List<SysOperLogVO> sysOperLogVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysOperLogPage
     * @return 操作日志记录分页
     */
    Page<SysOperLogVO> convertSysOperLogVOPage(Page<SysOperLog> sysOperLogPage);

    /**
     * VO类转换为实体类
     *
     * @param sysOperLogVOPage
     * @return 操作日志记录分页
     */
    Page<SysOperLog> convertSysOperLogPage(Page<SysOperLogVO> sysOperLogVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
