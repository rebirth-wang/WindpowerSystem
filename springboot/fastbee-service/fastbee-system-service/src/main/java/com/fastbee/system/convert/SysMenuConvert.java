package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysMenu;
import com.fastbee.system.domain.vo.SysMenuVO;

/**
 * 菜单权限Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SysMenuConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysMenu
     * @return 菜单权限集合
     */
    SysMenuVO convertSysMenuVO(SysMenu sysMenu);

    /**
     * VO类转换为实体类集合
     *
     * @param sysMenuVO
     * @return 菜单权限集合
     */
    SysMenu convertSysMenu(SysMenuVO sysMenuVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysMenuList
     * @return 菜单权限集合
     */
    List<SysMenuVO> convertSysMenuVOList(List<SysMenu> sysMenuList);

    /**
     * VO类转换为实体类
     *
     * @param sysMenuVOList
     * @return 菜单权限集合
     */
    List<SysMenu> convertSysMenuList(List<SysMenuVO> sysMenuVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysMenuPage
     * @return 菜单权限分页
     */
    Page<SysMenuVO> convertSysMenuVOPage(Page<SysMenu> sysMenuPage);

    /**
     * VO类转换为实体类
     *
     * @param sysMenuVOPage
     * @return 菜单权限分页
     */
    Page<SysMenu> convertSysMenuPage(Page<SysMenuVO> sysMenuVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
