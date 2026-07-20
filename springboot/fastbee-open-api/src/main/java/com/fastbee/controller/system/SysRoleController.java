package com.fastbee.controller.system;

import java.util.Comparator;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.domain.SysUserRole;
import com.fastbee.system.service.ISysDeptService;
import com.fastbee.system.service.ISysRoleService;
import com.fastbee.system.service.ISysUserService;
import com.fastbee.system.service.sys.SysPermissionService;
import com.fastbee.system.service.sys.TokenService;

/**
 * 角色信息
 *
 * @author ruoyi
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController
{

    @Resource
    private TokenService tokenService;

    @Resource
    private SysPermissionService permissionService;

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysDeptService deptService;

    /** 代码生成区域 可直接覆盖**/
    @Resource
    private ISysRoleService sysRoleService;

    /** 自定义代码区域 **/

    @ApiOperation("获取角色分页列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role, Integer pageNum, Integer pageSize)
    {
        if (null != role.getDeptId()) {
            deptService.checkDeptDataScope(role.getDeptId());
        }
        role.setPageNum(pageNum);
        role.setPageSize(pageSize);
        Page<SysRole> rolePage = sysRoleService.selectRoleList(role);

        // 按照 roleSort 字段排序
        rolePage.getRecords().sort(Comparator.comparingInt(SysRole::getRoleSort));

        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg(MessageUtils.message("query.success"));
        tableDataInfo.setRows(rolePage.getRecords());
        tableDataInfo.setTotal(rolePage.getTotal());

        return tableDataInfo;
    }

    @ApiOperation("导出角色列表")
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role)
    {
        if (null != role.getDeptId()) {
            deptService.checkDeptDataScope(role.getDeptId());
        }
        List<SysRole> list = sysRoleService.selectRoleList(role).getRecords();
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @ApiOperation("根据角色编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId)
    {
        sysRoleService.checkRoleDataScope(roleId);
        return success(sysRoleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @ApiOperation("新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRole role)
    {
        if ("manager".equals(role.getRoleKey())) {
            return error(MessageUtils.message("role.add.manager.failed"));
        }
        role.setCreateBy(getUsername());
        return toAjax(sysRoleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @ApiOperation("修改角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role)
    {
        sysRoleService.checkRoleAllowed(role);
        sysRoleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());

        if (sysRoleService.updateRole(role) > 0)
        {
            // 更新缓存用户权限
            LoginUser loginUser = getLoginUser();
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin())
            {
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                tokenService.setLoginUser(loginUser);
            }
            return success();
        }
        return error(StringUtils.format(MessageUtils.message("role.update.failed"), role.getRoleName()));
    }

    /**
     * 修改保存数据权限
     */
    @ApiOperation("修改保存数据权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role)
    {
        sysRoleService.checkRoleAllowed(role);
        sysRoleService.checkRoleDataScope(role.getRoleId());
        return toAjax(sysRoleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @ApiOperation("修改角色状态")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role)
    {
        sysRoleService.checkRoleAllowed(role);
        sysRoleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
        return toAjax(sysRoleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        return toAjax(sysRoleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @ApiOperation("获取角色选择框列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        SysRole sysRole = new SysRole();
        sysRole.setPageNum(1);
        sysRole.setPageSize(99);
        Page<SysRole> rolePage = sysRoleService.selectRoleList(sysRole);
        return success(rolePage.getRecords());
    }

    /**
     * 查询已分配用户角色列表
     */
    @ApiOperation("查询已分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUser user)
    {
        if (null != user.getRoleId()) {
            sysRoleService.checkRoleDataScope(user.getRoleId());
        }
        Page<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 查询未分配用户角色列表
     */
    @ApiOperation("查询未分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUser user)
    {
        if (null != user.getRoleId()) {
            sysRoleService.checkRoleDataScope(user.getRoleId());
        }
        Page<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 取消授权用户
     */
    @ApiOperation("取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        if (null != userRole.getRoleId()) {
            sysRoleService.checkRoleDataScope(userRole.getRoleId());
        }
        return toAjax(sysRoleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @ApiOperation("批量取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        if (null != roleId) {
            sysRoleService.checkRoleDataScope(roleId);
        }
        return toAjax(sysRoleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @ApiOperation("批量选择用户授权")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds)
    {
        sysRoleService.checkRoleDataScope(roleId);
        return toAjax(sysRoleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 获取对应角色部门树列表
     */
    @ApiOperation("获取对应角色部门树列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public AjaxResult deptTree(@PathVariable("roleId") Long roleId)
    {
        sysRoleService.checkRoleDataScope(roleId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
        return ajax;
    }
}
