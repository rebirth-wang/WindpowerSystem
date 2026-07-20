package com.fastbee.system.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.fastbee.system.domain.SysMenu;
import com.fastbee.system.domain.model.TreeSelect;
import com.fastbee.system.domain.vo.RouterVo;
import com.fastbee.system.domain.vo.SysMenuVO;

/**
 * 菜单 业务层
 *
 * @author ruoyi
 */
public interface ISysMenuService
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询菜单权限列表
     *
     * @param sysMenu 菜单权限
     * @return 菜单权限分页集合
     */
    Page<SysMenuVO> pageSysMenuVO(SysMenu sysMenu);

    /**
     * 查询菜单权限列表
     *
     * @param sysMenu 菜单权限
     * @return 菜单权限集合
     */
    List<SysMenuVO> listSysMenuVO(SysMenu sysMenu);

    /**
     * 查询菜单权限
     *
     * @param menuId 主键
     * @return 菜单权限
     */
    SysMenu selectSysMenuById(Long menuId);

    /**
     * 查询菜单权限
     *
     * @param menuId 主键
     * @return 菜单权限
     */
    SysMenu queryByIdWithCache(Long menuId);

    /**
     * 新增菜单权限
     *
     * @param sysMenu 菜单权限
     * @return 是否新增成功
     */
    Boolean insertWithCache(SysMenu sysMenu);

    /**
     * 修改菜单权限
     *
     * @param sysMenu 菜单权限
     * @return 是否修改成功
     */
    Boolean updateWithCache(SysMenu sysMenu);

    /**
     * 校验并批量删除菜单权限信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/



    /** 自定义代码区域 **/

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @param language 语言
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId, String language);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public String checkMenuNameUnique(SysMenu menu);

    List<SysMenu> deptMenuTreeselect(Long deptId);

    List<SysMenu> deptRoleMenuTreeselect(Long deptId, Long roleId);
    /** 自定义代码区域 END**/
}
