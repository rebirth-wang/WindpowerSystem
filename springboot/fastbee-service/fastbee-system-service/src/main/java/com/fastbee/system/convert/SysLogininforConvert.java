package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysLogininfor;
import com.fastbee.system.domain.vo.SysLogininforVO;

/**
 * 系统访问记录Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@Mapper
public interface SysLogininforConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysLogininforConvert INSTANCE = Mappers.getMapper(SysLogininforConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysLogininfor
     * @return 系统访问记录集合
     */
    SysLogininforVO convertSysLogininforVO(SysLogininfor sysLogininfor);

    /**
     * VO类转换为实体类集合
     *
     * @param sysLogininforVO
     * @return 系统访问记录集合
     */
    SysLogininfor convertSysLogininfor(SysLogininforVO sysLogininforVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysLogininforList
     * @return 系统访问记录集合
     */
    List<SysLogininforVO> convertSysLogininforVOList(List<SysLogininfor> sysLogininforList);

    /**
     * VO类转换为实体类
     *
     * @param sysLogininforVOList
     * @return 系统访问记录集合
     */
    List<SysLogininfor> convertSysLogininforList(List<SysLogininforVO> sysLogininforVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysLogininforPage
     * @return 系统访问记录分页
     */
    Page<SysLogininforVO> convertSysLogininforVOPage(Page<SysLogininfor> sysLogininforPage);

    /**
     * VO类转换为实体类
     *
     * @param sysLogininforVOPage
     * @return 系统访问记录分页
     */
    Page<SysLogininfor> convertSysLogininforPage(Page<SysLogininforVO> sysLogininforVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
