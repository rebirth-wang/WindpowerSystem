package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysConfig;
import com.fastbee.system.domain.vo.SysConfigVO;

/**
 * 参数配置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-12
 */
@Mapper
public interface SysConfigConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysConfigConvert INSTANCE = Mappers.getMapper(SysConfigConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysConfig
     * @return 参数配置集合
     */
    SysConfigVO convertSysConfigVO(SysConfig sysConfig);

    /**
     * VO类转换为实体类集合
     *
     * @param sysConfigVO
     * @return 参数配置集合
     */
    SysConfig convertSysConfig(SysConfigVO sysConfigVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysConfigList
     * @return 参数配置集合
     */
    List<SysConfigVO> convertSysConfigVOList(List<SysConfig> sysConfigList);

    /**
     * VO类转换为实体类
     *
     * @param sysConfigVOList
     * @return 参数配置集合
     */
    List<SysConfig> convertSysConfigList(List<SysConfigVO> sysConfigVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysConfigPage
     * @return 参数配置分页
     */
    Page<SysConfigVO> convertSysConfigVOPage(Page<SysConfig> sysConfigPage);

    /**
     * VO类转换为实体类
     *
     * @param sysConfigVOPage
     * @return 参数配置分页
     */
    Page<SysConfig> convertSysConfigPage(Page<SysConfigVO> sysConfigVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
