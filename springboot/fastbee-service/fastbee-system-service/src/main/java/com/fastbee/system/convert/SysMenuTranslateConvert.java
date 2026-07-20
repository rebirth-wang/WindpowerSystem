package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysMenuTranslate;
import com.fastbee.system.domain.vo.SysMenuTranslateVO;

/**
 * 菜单名称翻译Convert转换类
 *
 * @author fastbee
 * @date 2025-12-26
 */
@Mapper
public interface SysMenuTranslateConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysMenuTranslateConvert INSTANCE = Mappers.getMapper(SysMenuTranslateConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysMenuTranslate
     * @return 菜单名称翻译集合
     */
    SysMenuTranslateVO convertSysMenuTranslateVO(SysMenuTranslate sysMenuTranslate);

    /**
     * VO类转换为实体类集合
     *
     * @param sysMenuTranslateVO
     * @return 菜单名称翻译集合
     */
    SysMenuTranslate convertSysMenuTranslate(SysMenuTranslateVO sysMenuTranslateVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysMenuTranslateList
     * @return 菜单名称翻译集合
     */
    List<SysMenuTranslateVO> convertSysMenuTranslateVOList(List<SysMenuTranslate> sysMenuTranslateList);

    /**
     * VO类转换为实体类
     *
     * @param sysMenuTranslateVOList
     * @return 菜单名称翻译集合
     */
    List<SysMenuTranslate> convertSysMenuTranslateList(List<SysMenuTranslateVO> sysMenuTranslateVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysMenuTranslatePage
     * @return 菜单名称翻译分页
     */
    Page<SysMenuTranslateVO> convertSysMenuTranslateVOPage(Page<SysMenuTranslate> sysMenuTranslatePage);

    /**
     * VO类转换为实体类
     *
     * @param sysMenuTranslateVOPage
     * @return 菜单名称翻译分页
     */
    Page<SysMenuTranslate> convertSysMenuTranslatePage(Page<SysMenuTranslateVO> sysMenuTranslateVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
