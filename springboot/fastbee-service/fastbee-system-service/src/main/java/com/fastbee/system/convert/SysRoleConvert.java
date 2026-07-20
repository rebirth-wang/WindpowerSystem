package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.system.domain.vo.SysRoleVO;

/**
 * 角色信息Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SysRoleConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysRole
     * @return 角色信息集合
     */
    SysRoleVO convertSysRoleVO(SysRole sysRole);

    /**
     * VO类转换为实体类集合
     *
     * @param sysRoleVO
     * @return 角色信息集合
     */
    SysRole convertSysRole(SysRoleVO sysRoleVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysRoleList
     * @return 角色信息集合
     */
    List<SysRoleVO> convertSysRoleVOList(List<SysRole> sysRoleList);

    /**
     * VO类转换为实体类
     *
     * @param sysRoleVOList
     * @return 角色信息集合
     */
    List<SysRole> convertSysRoleList(List<SysRoleVO> sysRoleVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysRolePage
     * @return 角色信息分页
     */
    Page<SysRoleVO> convertSysRoleVOPage(Page<SysRole> sysRolePage);

    /**
     * VO类转换为实体类
     *
     * @param sysRoleVOPage
     * @return 角色信息分页
     */
    Page<SysRole> convertSysRolePage(Page<SysRoleVO> sysRoleVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
