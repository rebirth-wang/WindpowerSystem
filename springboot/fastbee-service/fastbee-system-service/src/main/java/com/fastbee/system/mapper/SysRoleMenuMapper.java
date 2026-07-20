package com.fastbee.system.mapper;

import java.util.List;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.system.domain.SysRoleMenu;

/**
 * 角色与菜单关联表 数据层
 *
 * @author ruoyi
 */
public interface SysRoleMenuMapper extends BaseMapperX<SysRoleMenu>
{
    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int checkMenuExistRole(Long menuId);

    /**
     * 通过角色ID删除角色和菜单关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);

    /**
     * 查询角色权限
     * @param roleId 角色id
     * @return java.util.List<com.fastbee.system.domain.SysRoleMenu>
     */
    List<SysRoleMenu> selectRoleMenuList(Long roleId);
}
