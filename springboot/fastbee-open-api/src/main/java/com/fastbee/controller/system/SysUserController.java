package com.fastbee.controller.system;

import static com.fastbee.common.extend.utils.SecurityUtils.getDataScope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.constant.CacheConstants;
import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.domain.vo.SysUserEditVO;
import com.fastbee.system.mapper.SysRoleDeptMapper;
import com.fastbee.system.service.ISysDeptService;
import com.fastbee.system.service.ISysPostService;
import com.fastbee.system.service.ISysRoleService;
import com.fastbee.system.service.ISysUserService;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysDeptService deptService;

    @Resource
    private ISysPostService postService;

    @Resource
    private RedisCache redisCache;
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private SysRoleDeptMapper sysRoleDeptMapper;

    /** 自定义代码区域 **/

    /**
     * 获取用户列表
     */
    @ApiOperation("获取用户分页列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        if (null != user.getDeptId()) {
            deptService.checkDeptDataScope(user.getDeptId());
            String dataScope = getDataScope();
            if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
                user.setUserId(getUserId());
            }
        }
        Page<SysUser> list = sysUserService.selectUserList(user);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    @ApiOperation("导出用户列表")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        if (null != user.getDeptId()) {
            deptService.checkDeptDataScope(user.getDeptId());
            String dataScope = getDataScope();
            if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
                user.setUserId(getUserId());
            }
        }
        Page<SysUser> list = sysUserService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list.getRecords(), "用户数据");
    }

    @ApiOperation("批量导入用户")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = sysUserService.importUser(userList, updateSupport, operName);
        return success(message);
    }


    @ApiOperation("下载用户导入模板")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation("根据用户编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        sysUserService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            SysUser sysUser = sysUserService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            SysRole sysRole = new SysRole();
            sysRole.setDeptId(sysUser.getDeptId());
            sysRole.setShowChild(false);
            List<SysRole> sysRoleList = roleService.selectRoleList(sysRole).getRecords();
            ajax.put("roles", sysRoleList);
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        if (UserConstants.NOT_UNIQUE.equals(sysUserService.checkUserNameUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.add.failed.name.exists"), user.getUserName()));
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.add.failed.phone.exists"), user.getUserName()));
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(sysUserService.checkEmailUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.add.failed.email.exists"), user.getUserName()));
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(sysUserService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        sysUserService.checkUserAllowed(user);
        sysUserService.checkUserDataScope(user.getUserId());
        if (UserConstants.NOT_UNIQUE.equals(sysUserService.checkUserNameUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.update.failed.name.exists"), user.getUserName()));
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.update.failed.phone.exists"), user.getUserName()));
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(sysUserService.checkEmailUnique(user)))
        {
            return error(StringUtils.format(MessageUtils.message("user.update.failed.email.exists"), user.getUserName()));
        }
        user.setUpdateBy(getUsername());
        return toAjax(sysUserService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error(MessageUtils.message("user.delete.failed"));
        }
        return toAjax(sysUserService.deleteUserByIds(userIds));
    }

    /**
     * 注销用户
     */
    @ApiOperation("注销用户")
//    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/logout")
    public AjaxResult logout()
    {
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        if (Objects.isNull(userId)) {
            return error("请重新登录");
        }
        String username = loginUser.getUsername();
        if ("fastbee".equals(username)) {
            return error("演示账号fastbee不允许注销");
        }
        return toAjax(sysUserService.deleteUserByIds(new Long[]{userId}));
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置用户密码")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        sysUserService.checkUserAllowed(user);
        sysUserService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(sysUserService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @ApiOperation("修改用户状态")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        sysUserService.checkUserAllowed(user);
        sysUserService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(sysUserService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @ApiOperation("根据用户编号获取授权角色")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        sysUserService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        SysUser user = sysUserService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @ApiOperation("为用户授权角色")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        sysUserService.checkUserDataScope(userId);
        SysUser sysUser = sysUserService.selectSysUserById(userId);
        List<Long> deptRoleIds = sysRoleDeptMapper.selectByDeptId(sysUser.getDeptId());
        boolean b = deptRoleIds.removeAll(Arrays.asList(roleIds));
        if (!b) {
            return error("请检查角色id");
        }
        sysUserService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    @ApiOperation("获取部门树列表")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        if (Objects.nonNull(dept) && null != dept.getDeptId()) {
            deptService.checkDeptDataScope(dept.getDeptId());
        }
        return success(deptService.selectDeptTreeList(dept));
    }

    /**
     * 获取终端用户列表
     * @param user 用户信息
     * @return com.fastbee.common.core.page.TableDataInfo
     */
    @ApiOperation("获取用户分页列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/listTerminal")
    public TableDataInfo listTerminal(SysUser user)
    {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            return new TableDataInfo();
        }
        Page<SysUser> page = sysUserService.listTerminal(user);
        return getDataTable(page.getRecords(), page.getTotal());
    }


    /**
     * 根据机构id获取当前用户列表
     */
    @ApiOperation("根据机构id获取当前用户列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/getByDeptId")
    public TableDataInfo getByDeptId()
    {
        SysUser user = getLoginUser().getUser();
        Page<SysUser> list = sysUserService.selectByDeptId(user);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 重置密码
     */
    @ApiOperation("忘记密码重置用户密码")
    @PutMapping("/forgetPwdReset")
    public AjaxResult forgetPwdReset(@RequestBody SysUserEditVO sysUserEditVO)
    {
        SysUser sysUser = sysUserService.selectUserByPhoneNumber(sysUserEditVO.getPhoneNumber());
        if (Objects.isNull(sysUser)) {
            return error(MessageUtils.message("user.not.exists"));
        }
        String codeKey = CacheConstants.FORGOT_PASSWORD_SMS_VERIFY_PHONE + sysUserEditVO.getPhoneNumber();
        String redisCode = redisCache.getCacheObject(codeKey);
        if (StringUtils.isEmpty(redisCode)) {
            return error(MessageUtils.message("user.jcaptcha.expire"));
        }
        if (!sysUserEditVO.getCode().equals(redisCode)) {
            return error(MessageUtils.message("user.jcaptcha.error"));
        }
        sysUserService.checkUserAllowed(sysUser);
        SysUser editUser = new SysUser();
        editUser.setPassword(SecurityUtils.encryptPassword(sysUserEditVO.getPassword()));
        editUser.setUpdateBy(sysUser.getUserName());
        editUser.setUserId(sysUser.getUserId());
        int i = sysUserService.resetPwd(editUser);
        if (i > 0) {
            redisCache.deleteObject(codeKey);
        }
        return toAjax(i);
    }
    /** 自定义代码区域 END**/
}
