package com.fastbee.system.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getDataScope;
import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.text.Convert;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StreamUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.framework.mybatis.helper.DataBaseHelper;
import com.fastbee.system.domain.model.TreeSelect;
import com.fastbee.system.mapper.SysDeptMapper;
import com.fastbee.system.mapper.SysRoleDeptMapper;
import com.fastbee.system.mapper.SysRoleMapper;
import com.fastbee.system.service.ISysDeptService;

/**
 * 部门管理 服务实现
 *
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService
{
    @Resource
    private SysDeptMapper deptMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化机构信息到缓存
     */
    @PostConstruct
    public void init()
    {
        refreshCache();
    }

    /**
     * 查询部门管理数据
     *
     * @param sysDept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(fieldAlias = "deptId")
    public List<SysDept> selectDeptList(SysDept sysDept)
    {
        LambdaQueryWrapper<SysDept> lambdaQueryWrapper = Wrappers.lambdaQuery();

        // 添加固定的查询条件
        lambdaQueryWrapper.eq(SysDept::getDelFlag, "0");

        if (sysDept.getParentId() != null && sysDept.getParentId() != 0) {
            lambdaQueryWrapper.eq(SysDept::getParentId, sysDept.getParentId());
        }

        if (sysDept.getDeptName() != null && !sysDept.getDeptName().isEmpty()) {
            lambdaQueryWrapper.like(SysDept::getDeptName, sysDept.getDeptName());
        }

        if (sysDept.getStatus() != null) {
            lambdaQueryWrapper.eq(SysDept::getStatus, sysDept.getStatus());
        }

        if (null != sysDept.getDeptId()) {
            lambdaQueryWrapper.eq(SysDept::getDeptId, sysDept.getDeptId());
        } else {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(sysDept.getParams().get(DataScopeAspect.DATA_SCOPE))){
                lambdaQueryWrapper.apply((String) sysDept.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        }

        // 排序
        lambdaQueryWrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);

        // 执行查询
        return deptMapper.selectList(lambdaQueryWrapper);
    }

    public List<SysDept> listDeptAndChild(SysDept sysDept) {
        LambdaQueryWrapper<SysDept> lambdaQueryWrapper = Wrappers.lambdaQuery();

        // 添加固定的查询条件
        lambdaQueryWrapper.eq(SysDept::getDelFlag, "0");

        // 根据条件动态添加查询条件
        if (sysDept.getDeptId() != null && sysDept.getDeptId() != 0) {
            if (sysDept.getShowOwner() == null || sysDept.getShowOwner()) {
                lambdaQueryWrapper.and(wrapper -> wrapper.eq(SysDept::getDeptId, sysDept.getDeptId())
                        .or().apply(DataBaseHelper.findInSet(sysDept.getDeptId(), "ancestors")));
            } else {
                lambdaQueryWrapper.apply(DataBaseHelper.findInSet(sysDept.getDeptId(), "ancestors"));
            }
        }

        if (sysDept.getParentId() != null && sysDept.getParentId() != 0) {
            lambdaQueryWrapper.eq(SysDept::getParentId, sysDept.getParentId());
        }

        if (sysDept.getDeptName() != null && !sysDept.getDeptName().isEmpty()) {
            lambdaQueryWrapper.like(SysDept::getDeptName, sysDept.getDeptName());
        }

        if (sysDept.getStatus() != null) {
            lambdaQueryWrapper.eq(SysDept::getStatus, sysDept.getStatus());
        }

        // 排序
        lambdaQueryWrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);

        // 执行查询
        return deptMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<TreeSelect> selectDeptTreeList(SysDept dept)
    {
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        return buildDeptTreeSelect(depts);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts)
    {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts)
    {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Cacheable(cacheNames = "dept", key = "#root.methodName + ':' + #roleId", unless = "#result == null")
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    public List<Long> selectDeptUserIdList(Long deptId) {
        SysDept dept = deptMapper.selectOne(new LambdaQueryWrapper<SysDept>()
                .select(SysDept::getAncestors)
                .eq(SysDept::getDeptId, deptId));
        MPJLambdaWrapper<SysDept> wrapper = JoinWrappers.lambda(SysDept.class);
        wrapper.select(SysDept::getDeptUserId).setAlias("de");
        wrapper.apply(DataBaseHelper.findInSetColumn("de.dept_id", dept.getAncestors()));
        List<SysDept> deptList = deptMapper.selectList(wrapper);
        return StreamUtils.toList(deptList, SysDept::getDeptUserId);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Cacheable(cacheNames = "dept", key = "#root.methodName + ':' + #deptId", unless = "#result == null")
    @Override
    public SysDept selectDeptById(Long deptId)
    {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Cacheable(cacheNames = "dept", key = "#root.methodName + ':' + #deptId")
    @Override
    public int selectNormalChildrenDeptById(Long deptId)
    {
        LambdaQueryWrapper<SysDept> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDept::getStatus, UserConstants.DEPT_NORMAL);
        wrapper.eq(SysDept::getDelFlag, '0');
        wrapper.apply(DataBaseHelper.findInSet(deptId, "ancestors"));
        Long count = deptMapper.selectCount(wrapper);
        return count.intValue();
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId)
    {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId)
    {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept)
    {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysDept::getDeptName, dept.getDeptName());
        lqw.eq(SysDept::getParentId, dept.getParentId());
        lqw.eq(SysDept::getDelFlag, '0');
        SysDept info = deptMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId)
    {
        LoginUser loginUser = getLoginUser();
        SysUser curUser = loginUser.getUser();
        if (SysUser.isAdmin(curUser.getUserId())) {
            return;
        }
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        if (StringUtils.isEmpty(depts))
        {
            throw new ServiceException("没有权限访问部门数据！");
        }
        if (null == curUser.getDeptId()) {
            throw new ServiceException("没有权限访问部门数据！");
        } else {
            String dataScope = getDataScope();
            if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope) || DataScopeAspect.DATA_SCOPE_DEPT.equals(dataScope)) {
                if (!curUser.getDeptId().equals(deptId)) {
                    throw new ServiceException("没有权限访问部门数据！");
                }
            } else if (DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                if (!curUser.getDeptId().equals(deptId)) {
                    String ancestors = depts.get(0).getAncestors();
                    if (!ancestors.contains(curUser.getDeptId().toString())) {
                        throw new ServiceException("没有权限访问部门数据！");
                    }
                }
            }
        }
        return;
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept)
    {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        dept.setCreateTime(DateUtils.getNowDate());
        return deptMapper.insert(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "dept", key = "'selectDeptById:' + #dept.deptId"),
            @CacheEvict(cacheNames = "dept", key = "'selectNormalChildrenDeptById:' + #dept.deptId")
    })
    @Override
    public int updateDept(SysDept dept)
    {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        dept.setUpdateTime(DateUtils.getNowDate());
        int result = deptMapper.updateById(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors()))
        {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept)
    {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors)
    {
        LambdaQueryWrapper<SysDept> wrapper = Wrappers.lambdaQuery();
        wrapper.apply(DataBaseHelper.findInSet(deptId, "ancestors"));
        List<SysDept> children = deptMapper.selectList(wrapper);
        for (SysDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (!children.isEmpty())
        {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @CacheEvict(cacheNames = "dept", allEntries = true)
    @Override
    public int deleteDeptById(Long deptId)
    {
        return deptMapper.deleteById(deptId);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "dept", key = "'selectDeptById:' + #deptId"),
            @CacheEvict(cacheNames = "dept", key = "'selectNormalChildrenDeptById:'  + #deptId")
    })
    @Override
    public int updateDeptUserId(Long deptId, Long deptUserId) {
        SysDept sysDept = new SysDept();
        sysDept.setDeptId(deptId);
        sysDept.setDeptUserId(deptUserId);
        return deptMapper.updateById(sysDept);
    }

    @Override
    public List<SysRole> getRole(Long deptId) {
        List<Long> roleIdList = sysRoleDeptMapper.selectByDeptId(deptId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        return roleMapper.selectRoleByIds(new Page<>(), roleIdList, null, null).getRecords();
    }

    /**
     * 查询机构的所有上级绑定租户
     * @param deptId
     * @return
     */
    @Override
    public List<Long> selectSeniorDeptUser(Long deptId) {
        SysDept dept = deptMapper.selectOne(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getDeptId, deptId)
        );
        String findInSetStr = DataBaseHelper.findInSetColumn("de.dept_id", dept.getAncestors());
        return deptMapper.selectSeniorDeptUser(findInSetStr);
    }

    @Override
    public Long selectRoleIdByDeptId(Long deptId) {
        return deptMapper.selectRoleIdByDeptId(deptId);
    }

    @Override
    public SysDept selectByInvitationCode(Long deptId, String invitationCode) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(deptId)) {
            queryWrapper.ne(SysDept::getDeptId, deptId);
        }
        queryWrapper.eq(SysDept::getInvitationCode, invitationCode);
        return deptMapper.selectOne(queryWrapper);
    }

    @Override
    public SysDept selectDeptByInvitationCode(String invitationCode) {
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getInvitationCode, invitationCode);
        return deptMapper.selectOne(queryWrapper);
    }

    @Override
    public Long selectRegisterRoleByDeptId(Long deptId) {
        List<SysRole> roleList = deptMapper.selectRegisterRoleByDeptId(deptId);
        if (CollectionUtils.isEmpty(roleList)) {
            return null;
        }
        Map<String, Long> roleMap = roleList.stream().collect(Collectors.toMap(SysRole::getRoleKey, SysRole::getRoleId));
        return null != roleMap.get("register") ? roleMap.get("register") : roleMap.get("manager");
    }

    @Override
    public AjaxResult refreshCache() {
        List<SysDept> sysDeptList = deptMapper.selectList();
        for (SysDept sysDept : sysDeptList) {
            redisCache.setCacheObject(SecurityUtils.SYS_DEPT_TENANT_ID + sysDept.getDeptUserId(), sysDept);
        }
        return AjaxResult.success();
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t)
    {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t)
    {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext())
        {
            SysDept n = (SysDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t)
    {
        return getChildList(list, t).size() > 0;
    }

    private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDept query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getDeptId() != null, SysDept::getDeptId, query.getDeptId());
        lqw.eq(query.getDeptUserId() != null, SysDept::getDeptUserId, query.getDeptUserId());
        lqw.eq(query.getParentId() != null, SysDept::getParentId, query.getParentId());
        lqw.eq(StringUtils.isNotBlank(query.getAncestors()), SysDept::getAncestors, query.getAncestors());
        lqw.like(StringUtils.isNotBlank(query.getDeptName()), SysDept::getDeptName, query.getDeptName());
        lqw.eq(query.getOrderNum() != null, SysDept::getOrderNum, query.getOrderNum());
        lqw.eq(StringUtils.isNotBlank(query.getLeader()), SysDept::getLeader, query.getLeader());
        lqw.eq(StringUtils.isNotBlank(query.getPhone()), SysDept::getPhone, query.getPhone());
        lqw.eq(query.getStatus() != null, SysDept::getStatus, query.getStatus());
        lqw.eq(query.getDelFlag() != null, SysDept::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysDept::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysDept::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysDept::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysDept::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getEmail()), SysDept::getEmail, query.getEmail());
        lqw.eq(query.getDeptType() != null, SysDept::getDeptType, query.getDeptType());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysDept::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
