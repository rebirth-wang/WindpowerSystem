package com.fastbee.system.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.system.convert.SysRoleConvert;
import com.fastbee.system.domain.SysRoleDept;
import com.fastbee.system.domain.SysRoleMenu;
import com.fastbee.system.domain.SysUserRole;
import com.fastbee.system.domain.vo.SysRoleDeptVO;
import com.fastbee.system.domain.vo.SysRoleVO;
import com.fastbee.system.mapper.*;
import com.fastbee.system.service.ISysDeptService;
import com.fastbee.system.service.ISysRoleService;

/**
 * 角色 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysRoleMenuMapper roleMenuMapper;

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Resource
    private SysRoleDeptMapper roleDeptMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private ISysDeptService deptService;

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询角色信息
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param roleId 主键
     * @return 角色信息
     */
    @Override
    @Cacheable(cacheNames = "SysRole", key = "#roleId")
    public SysRole queryByIdWithCache(Long roleId) {
        return this.getById(roleId);
    }

    /**
     * 查询角色信息
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param roleId 主键
     * @return 角色信息
     */
    @Override
    @Cacheable(cacheNames = "SysRole", key = "#roleId")
    public SysRole selectSysRoleById(Long roleId) {
        return this.getById(roleId);
    }

    /**
     * 查询角色信息分页列表
     *
     * @param sysRole 角色信息
     * @return 角色信息
     */
    @Override
    public Page<SysRoleVO> pageSysRoleVO(SysRole sysRole) {
        LambdaQueryWrapper<SysRole> lqw = buildQueryWrapper(sysRole);
        Page<SysRole> sysRolePage = baseMapper.selectPage(new Page<>(sysRole.getPageNum(), sysRole.getPageSize()), lqw);
        return SysRoleConvert.INSTANCE.convertSysRoleVOPage(sysRolePage);
    }

    /**
     * 查询角色信息列表
     *
     * @param sysRole 角色信息
     * @return 角色信息
     */
    @Override
    public List<SysRoleVO> listSysRoleVO(SysRole sysRole) {
        LambdaQueryWrapper<SysRole> lqw = buildQueryWrapper(sysRole);
        List<SysRole> sysRoleList = baseMapper.selectList(lqw);
        return SysRoleConvert.INSTANCE.convertSysRoleVOList(sysRoleList);
    }

    private LambdaQueryWrapper<SysRole> buildQueryWrapper(SysRole query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getRoleId() != null, SysRole::getRoleId, query.getRoleId());
        lqw.like(StringUtils.isNotBlank(query.getRoleName()), SysRole::getRoleName, query.getRoleName());
        lqw.eq(StringUtils.isNotBlank(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey());
        lqw.eq(query.getRoleSort() != null, SysRole::getRoleSort, query.getRoleSort());
        lqw.eq(StringUtils.isNotBlank(query.getDataScope()), SysRole::getDataScope, query.getDataScope());
        lqw.eq(query.getMenuCheckStrictly() != null, SysRole::getMenuCheckStrictly, query.getMenuCheckStrictly());
        lqw.eq(query.getDeptCheckStrictly() != null, SysRole::getDeptCheckStrictly, query.getDeptCheckStrictly());
        lqw.eq(query.getStatus() != null, SysRole::getStatus, query.getStatus());
        lqw.eq(query.getDelFlag() != null, SysRole::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysRole::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysRole::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysRole::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysRole::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysRole::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysRole::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增角色信息
     *
     * @param add 角色信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SysRole add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改角色信息
     *
     * @param update 角色信息
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SysRole", key = "#update.roleId")
    public Boolean updateWithCache(SysRole update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysRole entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除角色信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SysRole", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
//    @DataScope(deptAlias = "d")
    public Page<SysRole> selectRoleList(SysRole role) {
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        Long userDeptId = loginUser.getDeptId();
        String dataScope = getDataScope();
        Page<SysRole> rolePage;
        if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
            List<Long> roleIdList = loginUser.getUser().getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
            rolePage = roleMapper.selectRoleByIds(new Page<>(role.getPageNum(), role.getPageSize()), roleIdList, role.getRoleName(), role.getStatus());
            if (0 == rolePage.getTotal()) {
                return rolePage;
            }
            List<SysRole> sysRoleList = rolePage.getRecords();
            SysDept dept = loginUser.getUser().getDept();
            for (SysRole sysRole : sysRoleList) {
                if (Objects.nonNull(dept)) {
                    sysRole.setDeptId(dept.getDeptId());
                    sysRole.setDeptName(dept.getDeptName());
                }
                sysRole.setManager("manager".equals(sysRole.getRoleKey()));
            }
            return rolePage;
        } else {
            SysDept sysDept = new SysDept();
            sysDept.setDeptId(role.getDeptId());
            List<SysDept> sysDeptList = deptService.selectDeptList(sysDept);
            if (CollectionUtils.isNotEmpty(sysDeptList)) {
                Map<Long, String> deptMap = sysDeptList.stream().collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
                List<Long> deptIdList = sysDeptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
                List<SysRoleDeptVO> sysRoleDeptVOList = roleDeptMapper.selectRoleDeptByDeptIds(deptIdList);
                Map<Long, SysRoleDeptVO> roleDeptMap = sysRoleDeptVOList.stream().collect(Collectors.toMap(SysRoleDeptVO::getRoleId, Function.identity()));
                if (CollectionUtils.isEmpty(sysRoleDeptVOList)) {
                    return new Page<>();
                }
                List<Long> roleIdList = sysRoleDeptVOList.stream().map(SysRoleDeptVO::getRoleId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(roleIdList)) {
                    return new Page<>();
                }
                rolePage = roleMapper.selectRoleByIds(new Page<>(role.getPageNum(), role.getPageSize()), roleIdList, role.getRoleName(), role.getStatus());
                List<SysRole> sysRoleList = rolePage.getRecords();
                for (SysRole sysRole : sysRoleList) {
                    SysRoleDeptVO sysRoleDeptVO = roleDeptMap.get(sysRole.getRoleId());
                    Long deptId1 = sysRoleDeptVO.getDeptId();
                    String deptName = deptMap.get(deptId1);
                    sysRole.setDeptId(deptId1);
                    sysRole.setDeptName(deptName);
                    sysRole.setCanEditRole(!(userDeptId.equals(deptId1) && "manager".equals(sysRole.getRoleKey()) && !isAdmin(userId)));
                    if (isAdmin(sysRoleDeptVO.getDeptUserId())) {
                        sysRole.setManager("terminalRegister".equals(sysRole.getRoleKey()) || "scadaShare".equals(sysRole.getRoleKey())
                                || "manager".equals(sysRole.getRoleKey()) || "webRegister".equals(sysRole.getRoleKey()));
                    } else {
                        sysRole.setManager("manager".equals(sysRole.getRoleKey()));
                    }
                }

                return rolePage;
            } else {
                return new Page<>();
            }
        }
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll() {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole()).getRecords();
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        SysRole sysRole = roleMapper.selectRoleById(roleId);
        if (ObjectUtil.isNotNull(sysRole)) {
            LambdaQueryWrapper<SysRoleDept> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRoleDept::getRoleId, roleId);
            SysRoleDept sysRoleDept = roleDeptMapper.selectOne(queryWrapper);
            if (ObjectUtil.isNotNull(sysRoleDept)) {
                sysRole.setDeptId(sysRoleDept.getDeptId());
            }
        }
        return sysRole;
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        LambdaQueryWrapper<SysRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysRole::getRoleName, role.getRoleName());
        lqw.eq(SysRole::getDelFlag, '0');
        SysRole info = roleMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SysRole role) {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        LambdaQueryWrapper<SysRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysRole::getRoleKey, role.getRoleKey());
        lqw.eq(SysRole::getDelFlag, '0');
        SysRole info = roleMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        LoginUser loginUser = getLoginUser();
        SysUser curUser = loginUser.getUser();
        if (SysUser.isAdmin(curUser.getUserId())) {
            return;
        }
        List<SysRole> roles = curUser.getRoles();
        List<Long> roleIdList = roles.stream().map(SysRole::getRoleId).collect(Collectors.toList());
        if (roleIdList.contains(roleId)) {
            return;
        }
        if (null == curUser.getDeptId()) {
            throw new ServiceException("没有权限访问角色数据！");
        } else {
            String dataScope = getDataScope();
            LambdaQueryWrapper<SysRoleDept> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRoleDept::getRoleId, roleId);
            List<SysRoleDept> sysRoleDepts = roleDeptMapper.selectList(queryWrapper);
            SysRoleDept sysRoleDept = sysRoleDepts.get(0);
            if (Objects.isNull(sysRoleDept)) {
                throw new ServiceException("没有权限访问角色数据！");
            }
            Long bindDeptId = sysRoleDept.getDeptId();
            if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
                throw new ServiceException("没有权限访问角色数据！");
            } else if (DataScopeAspect.DATA_SCOPE_DEPT.equals(dataScope)) {
                if (!curUser.getDeptId().equals(bindDeptId)) {
                    throw new ServiceException("没有权限访问角色数据！");
                }
            } else if (DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                if (!curUser.getDeptId().equals(bindDeptId)) {
                    SysDept sysDept = sysDeptMapper.selectDeptById(bindDeptId);
                    String ancestors = sysDept.getAncestors();
                    if (!ancestors.contains(curUser.getDeptId().toString())) {
                        throw new ServiceException("没有权限访问角色数据！");
                    }
                }
            }
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        // 新增角色信息
        roleMapper.insertRole(role);
        // 绑定部门角色
        List<SysRoleDept> sysRoleDeptList = new ArrayList<>();
        SysRoleDept sysRoleDept = new SysRoleDept();
        sysRoleDept.setDeptId(role.getDeptId());
        sysRoleDept.setRoleId(role.getRoleId());
        sysRoleDeptList.add(sysRoleDept);
        if (CollectionUtils.isNotEmpty(sysRoleDeptList)) {
            roleDeptMapper.insertBatch(sysRoleDeptList);
        }
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int authDataScope(SysRole role) {
        // 修改角色信息
        return roleMapper.updateRole(role);
        // 删除角色与部门关联
//        LambdaQueryWrapper<SysRoleDept> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SysRoleDept::getRoleId, role.getRoleId());
//        roleDeptMapper.delete(queryWrapper);
//        // 新增角色和部门信息（数据权限）
//        return insertRoleDept(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public int insertRoleDept(SysRole role) {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (list.size() > 0) {
            rows = roleDeptMapper.insertBatch(list) ? 1 : 0;
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleById(Long roleId) {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        // 删除角色与部门关联
        LambdaQueryWrapper<SysRoleDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleDept::getRoleId, roleId);
        roleDeptMapper.delete(queryWrapper);
        return roleMapper.deleteById(roleId);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = selectRoleById(roleId);
            //if (countUserRoleByRoleId(roleId) > 0)
            //{
            //    throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            //}
        }
        // 删除角色与菜单关联
        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.in(SysRoleMenu::getRoleId, roleIds);
        roleMenuMapper.delete(roleMenuWrapper);
        // 删除角色与部门关联
        LambdaQueryWrapper<SysRoleDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRoleDept::getRoleId, roleIds);
        roleDeptMapper.delete(queryWrapper);
        return roleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (Long userId : userIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
    /** 自定义代码区域 END**/
}
