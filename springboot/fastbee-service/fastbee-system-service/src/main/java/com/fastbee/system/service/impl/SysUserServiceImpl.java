package com.fastbee.system.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.*;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.validation.Validator;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.enums.SocialPlatformType;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StreamUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.bean.BeanValidators;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.framework.mybatis.helper.DataBaseHelper;
import com.fastbee.system.convert.SysUserConvert;
import com.fastbee.system.domain.SysPost;
import com.fastbee.system.domain.SysRoleDept;
import com.fastbee.system.domain.SysUserPost;
import com.fastbee.system.domain.SysUserRole;
import com.fastbee.system.domain.vo.SysUserVO;
import com.fastbee.system.mapper.*;
import com.fastbee.system.service.ISysConfigService;
import com.fastbee.system.service.ISysUserService;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);


    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysDeptMapper deptMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysPostMapper postMapper;

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Resource
    private SysUserPostMapper userPostMapper;

    @Resource
    private ISysConfigService configService;

    @Resource
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Resource
    protected Validator validator;
    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询用户信息
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param userId 主键
     * @return 用户信息
     */
    @Override
    @Cacheable(cacheNames = "SysUser", key = "#userId")
    public SysUser queryByIdWithCache(Long userId) {
        return this.getById(userId);
    }

    /**
     * 查询用户信息
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param userId 主键
     * @return 用户信息
     */
    @Override
    @Cacheable(cacheNames = "SysUser", key = "#userId")
    public SysUser selectSysUserById(Long userId) {
        return this.getById(userId);
    }

    /**
     * 查询用户信息分页列表
     *
     * @param sysUser 用户信息
     * @return 用户信息
     */
    @Override
    public Page<SysUserVO> pageSysUserVO(SysUser sysUser) {
        LambdaQueryWrapper<SysUser> lqw = buildQueryWrapper(sysUser);
        Page<SysUser> sysUserPage = baseMapper.selectPage(new Page<>(sysUser.getPageNum(), sysUser.getPageSize()), lqw);
        return SysUserConvert.INSTANCE.convertSysUserVOPage(sysUserPage);
    }

    /**
     * 查询用户信息列表
     *
     * @param sysUser 用户信息
     * @return 用户信息
     */
    @Override
    public List<SysUserVO> listSysUserVO(SysUser sysUser) {
        LambdaQueryWrapper<SysUser> lqw = buildQueryWrapper(sysUser);
        List<SysUser> sysUserList = baseMapper.selectList(lqw);
        return SysUserConvert.INSTANCE.convertSysUserVOList(sysUserList);
    }

    private LambdaQueryWrapper<SysUser> buildQueryWrapper(SysUser query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getUserId() != null, SysUser::getUserId, query.getUserId());
        lqw.eq(query.getDeptId() != null, SysUser::getDeptId, query.getDeptId());
        lqw.like(StringUtils.isNotBlank(query.getUserName()), SysUser::getUserName, query.getUserName());
        lqw.like(StringUtils.isNotBlank(query.getNickName()), SysUser::getNickName, query.getNickName());
        lqw.eq(StringUtils.isNotBlank(query.getUserType()), SysUser::getUserType, query.getUserType());
        lqw.eq(StringUtils.isNotBlank(query.getEmail()), SysUser::getEmail, query.getEmail());
        lqw.eq(StringUtils.isNotBlank(query.getPhonenumber()), SysUser::getPhonenumber, query.getPhonenumber());
        lqw.eq(StringUtils.isNotBlank(query.getSex()), SysUser::getSex, query.getSex());
        lqw.eq(StringUtils.isNotBlank(query.getAvatar()), SysUser::getAvatar, query.getAvatar());
        lqw.eq(StringUtils.isNotBlank(query.getPassword()), SysUser::getPassword, query.getPassword());
        lqw.eq(query.getStatus() != null, SysUser::getStatus, query.getStatus());
        lqw.eq(query.getDelFlag() != null, SysUser::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getLoginIp()), SysUser::getLoginIp, query.getLoginIp());
        lqw.eq(query.getLoginDate() != null, SysUser::getLoginDate, query.getLoginDate());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysUser::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysUser::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysUser::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysUser::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysUser::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysUser::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增用户信息
     *
     * @param add 用户信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SysUser add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改用户信息
     *
     * @param update 用户信息
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SysUser", key = "#update.userId")
    public Boolean updateWithCache(SysUser update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysUser entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SysUser", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /**
     * 根据条件分页查询用户列表
     *
     * @param query 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "t", userAlias = "t", fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_DEPT_ID)
    public Page<SysUser> selectUserList(SysUser query) {
        Page<SysUser> sysUserList = this.queryUserList(query);
        // List<SysUser> sysUserList = userMapper.selectUserList(user);
        for (SysUser sysUser : sysUserList.getRecords()) {
            if ("fastbee_scada".equals(sysUser.getUserName())) {
                sysUser.setManager(true);
                continue;
            }
            SysDept dept = sysUser.getDept();
            if (Objects.nonNull(dept)) {
                sysUser.setManager(sysUser.getUserId().equals(dept.getDeptUserId()));
            } else {
                sysUser.setManager(false);
            }
            if (CollectionUtils.isNotEmpty(sysUser.getRoles())) {
                sysUser.setRoleIdsStr(sysUser.getRoles().stream()
                        .map(SysRole::getRoleId)
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")));
            }
        }
        return sysUserList;
    }
    @Override
    @DataScope(deptAlias = "t", userAlias = "t", fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_DEPT_ID)
    public Page<SysUser> queryUserList(SysUser query) {
        MPJLambdaWrapper<SysUser> wrapper = JoinWrappers.lambda(SysUser.class);
        // select
        wrapper.select(SysUser::getUserId, SysUser::getDeptId, SysUser::getUserName,
                        SysUser::getNickName, SysUser::getEmail, SysUser::getPhonenumber,
                        SysUser::getStatus, SysUser::getDelFlag, SysUser::getLoginIp,
                        SysUser::getLoginDate, SysUser::getCreateTime, SysUser::getUpdateTime,
                        SysUser::getCreateBy, SysUser::getUpdateBy, SysUser::getRemark, SysUser::getSex,
                        SysUser::getAvatar)
                .select(SysDept::getDeptName, SysDept::getLeader, SysDept::getDeptUserId)
                .select(SysRole::getRoleId, SysRole::getRoleKey);
        wrapper.selectAssociation(SysDept.class, SysUser::getDept);
        wrapper.selectCollection(SysRole.class, SysUser::getRoles, map -> map
                .result(SysRole::getRoleId)
                .result(SysRole::getRoleKey));
        // join
        wrapper.leftJoin(SysDept.class, SysDept::getDeptId, SysUser::getDeptId)
                .leftJoin(SysUserRole.class, SysUserRole::getUserId, SysUser::getUserId)
                .leftJoin(SysRole.class, SysRole::getRoleId, SysUserRole::getRoleId);
        // where
        Map<String, Object> params = query.getParams();

        wrapper.eq(query.getUserId() != null, SysUser::getUserId, query.getUserId());
        wrapper.like(StringUtils.isNotBlank(query.getUserName()), SysUser::getUserName, query.getUserName());
        wrapper.like(StringUtils.isNotBlank(query.getPhonenumber()), SysUser::getPhonenumber, query.getPhonenumber());
        wrapper.eq(query.getStatus() != null, SysUser::getStatus, query.getStatus());
        if (isAdmin(getUserId())) {
            wrapper.isNotNull(SysUser::getDeptId);
        }
        //wrapper
        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            wrapper.between(SysUser::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        if (query.getDeptId() != null) {
//            wrapper.and(w -> {
//                w.eq(!query.getShowChild(), SysUser::getDeptId, query.getDeptId())
//                        .or(query.getShowChild(), w1 -> {
//                            List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
//                                    .select(SysDept::getDeptId)
//                                    .apply(DataBaseHelper.findInSet(query.getDeptId(), "ancestors")));
//                            List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
//                            ids.add(query.getDeptId());
//                            w1.in(SysUser::getDeptId, ids);
//                        });
//            });
            wrapper.eq(SysUser::getDeptId, query.getDeptId());
        } else {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
                wrapper.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        }
        return baseMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
//    @DataScope(deptAlias = "d", userAlias = "u")
    public Page<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(new Page<>(user.getPageNum(), user.getPageSize()),user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
//    @DataScope(deptAlias = "d", userAlias = "u")
    public Page<SysUser> selectUnallocatedList(SysUser user) {
        if (null != user.getRoleId()) {
            LambdaQueryWrapper<SysRoleDept> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysRoleDept::getRoleId, user.getRoleId());
            SysRoleDept sysRoleDept = sysRoleDeptMapper.selectOne(queryWrapper);
            user.setDeptId(sysRoleDept.getDeptId());
        } else {
            user.setDeptId(getLoginUser().getUser().getDeptId());
        }
        return userMapper.selectUnallocatedList(new Page<>(user.getPageNum(), user.getPageSize()),user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Cacheable(cacheNames = "sysUser", key = "#root.methodName + ':' + #userName", unless = "#result == null")
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Cacheable(cacheNames = "sysUser", key = "#root.methodName + ':' + #userId", unless = "#result == null")
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Cacheable(cacheNames = "sysUser", key = "#root.methodName + ':' + #userName", unless = "#result == null")
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Cacheable(cacheNames = "sysUser", key = "#root.methodName + ':' + #userName", unless = "#result == null")
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        LambdaQueryWrapper<SysUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUser::getUserName, user.getUserName())
                .eq(SysUser::getDelFlag, 0);
        SysUser info = userMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        LambdaQueryWrapper<SysUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUser::getPhonenumber, user.getPhonenumber())
                .eq(SysUser::getDelFlag, 0);
        SysUser info = userMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        LambdaQueryWrapper<SysUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysUser::getEmail, user.getEmail())
                .eq(SysUser::getDelFlag, 0);
        SysUser info = userMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        LoginUser loginUser = getLoginUser();
        SysUser curUser = loginUser.getUser();
        if (SysUser.isAdmin(curUser.getUserId())) {
            return;
        }
        if (curUser.getUserId().equals(userId)) {
            return;
        }
        if (SysUser.isAdmin(userId)) {
            throw new ServiceException("没有权限访问用户数据！");
        }
        SysUser user = SpringUtils.getAopProxy(this).selectUserById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException("没有权限访问用户数据！");
        }
        if (null == curUser.getDeptId() || null == user.getDeptId()) {
            throw new ServiceException("没有权限访问用户数据！");
        }
        String dataScope = getDataScope();
        if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
            throw new ServiceException("没有权限访问用户数据！");
        } else if (DataScopeAspect.DATA_SCOPE_DEPT.equals(dataScope)) {
            if (!curUser.getDeptId().equals(user.getDeptId())) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        } else if (DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
            if (!curUser.getDeptId().equals(user.getDeptId())) {
                String ancestors = user.getDept().getAncestors();
                if (!ancestors.contains(curUser.getDeptId().toString())) {
                    throw new ServiceException("没有权限访问用户数据！");
                }
            }
        }
        List<SysRole> roles = curUser.getRoles();
        List<String> roleKeyList = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        if (!roleKeyList.contains("manager") && user.getDept().getDeptUserId().equals(userId)) {
            throw new ServiceException("没有权限访问用户数据！");
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "sysUser", key = "'selectUserByUserName:' + #user.userName", condition = "#result != null"),
            @CacheEvict(cacheNames = "sysUser", key = "'selectUserById:' + #user.userId", condition = "#result != null"),
            @CacheEvict(cacheNames = "sysUser", key = "'selectUserByPhoneNumber:' + #user.phonenumber", condition = "#result != null")
    })
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "sysUser", key = "'selectUserByUserName:' + #user.userName", condition = "#result != null"),
            @CacheEvict(cacheNames = "sysUser", key = "'selectUserById:' + #user.userId", condition = "#result != null"),
            @CacheEvict(cacheNames = "sysUser", key = "'selectUserByPhoneNumber:' + #user.phonenumber", condition = "#result != null")
    })
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @CacheEvict(cacheNames = "sysUser", allEntries = true)
    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        // 删除用户与微信绑定,因为引入iot-service模块会产生循环依赖，所以先写在这个mapper里
        userMapper.deleteBySysUserIdAndSourceClient(userId, SocialPlatformType.listWechatPlatform);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @CacheEvict(cacheNames = "sysUser", allEntries = true)
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        // 删除用户与微信关联,因为引入iot-service模块会产生循环依赖，所以先写在这个mapper里
        userMapper.deleteBySysUserIdsAndSourceClient(userIds, SocialPlatformType.listWechatPlatform);
        // 删除用户与设备关联
        userMapper.deleteDeviceUserShareByUserIds(userIds);
        userMapper.deleteDeviceUserByUserIds(userIds);
        userMapper.deleteDeviceShareByUserIds(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                if (StringUtils.isNotEmpty(user.getRoleIdsStr())) {
                    user.setRoleIds(Arrays.stream(user.getRoleIdsStr().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::valueOf)
                            .toArray(Long[]::new));
                }
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, user);
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, user);
                    checkUserAllowed(user);
                    checkUserDataScope(user.getUserId());
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 通过手机号查询用户
     *
     * @param phoneNumber 手机号
     * @return 用户对象信息
     */
    @Cacheable(cacheNames = "sysUser", key = "#root.methodName + ':' + #phoneNumber", unless = "#result == null")
    @Override
    public SysUser selectUserByPhoneNumber(String phoneNumber) {
        return userMapper.selectUserByPhoneNumber(phoneNumber);
    }

    /**
     * 验证用户密码
     *
     * @param userPassword  用户原密码
     * @param inputPassword 用户输入密码
     * @return 结果
     */
    @Override
    public Boolean validatePassword(String userPassword, String inputPassword) {
        return SecurityUtils.matchesPassword(inputPassword, userPassword);
    }

    @CacheEvict(cacheNames = "sysUser", allEntries = true)
    @Override
    public int deleteUserByDeptID(Long deptId) {
        return userMapper.deleteUserByDeptID(deptId);
    }

    /**
     * 获取下级所有子节点用户
     *
     * @param deptId
     * @return
     */
    @Override
    public List<SysUser> selectAllSubUser(Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
        wrapper.select(SysUser::getUserId, SysUser::getDeptId, SysUser::getUserName,
                SysUser::getNickName, SysUser::getEmail, SysUser::getPhonenumber,
                SysUser::getStatus, SysUser::getDelFlag, SysUser::getLoginIp,
                SysUser::getLoginDate, SysUser::getCreateTime, SysUser::getCreateBy,
                SysUser::getRemark, SysUser::getSex,SysUser::getAvatar);
        wrapper.and(ObjectUtil.isNotNull(deptId), w -> {
            List<SysDept> deptList = deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                    .select(SysDept::getDeptId)
                    .apply(DataBaseHelper.findInSet(deptId, "ancestors")));
            List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
            ids.add(deptId);
            w.in(SysUser::getDeptId, ids);
        });
        return userMapper.selectList(wrapper);
    }

    /**
     * 根据用户id获取当前机构的管理员账号
     *
     * @param userId
     * @return
     */
    @Override
    public SysUser getDeptUserByUserId(Long userId) {
        return userMapper.getDeptUserByUserId(userId);
    }

    @Override
    public Page<SysUser> listTerminal(SysUser query) {
        MPJLambdaWrapper<SysUser> wrapper = JoinWrappers.lambda(SysUser.class);
        // select
        wrapper.select(SysUser::getUserId, SysUser::getDeptId, SysUser::getUserName,
                        SysUser::getNickName, SysUser::getEmail, SysUser::getPhonenumber,
                        SysUser::getStatus, SysUser::getDelFlag, SysUser::getLoginIp,
                        SysUser::getLoginDate, SysUser::getCreateTime, SysUser::getUpdateTime,
                        SysUser::getCreateBy, SysUser::getUpdateBy, SysUser::getRemark, SysUser::getSex,
                        SysUser::getAvatar)
                .select(SysDept::getDeptName, SysDept::getLeader, SysDept::getDeptUserId)
                .select(SysRole::getRoleId, SysRole::getRoleKey);
        wrapper.selectAssociation(SysDept.class, SysUser::getDept);
        wrapper.selectCollection(SysRole.class, SysUser::getRoles, map -> map
                .result(SysRole::getRoleId)
                .result(SysRole::getRoleKey));
        // join
        wrapper.leftJoin(SysDept.class, SysDept::getDeptId, SysUser::getDeptId)
                .leftJoin(SysUserRole.class, SysUserRole::getUserId, SysUser::getUserId)
                .leftJoin(SysRole.class, SysRole::getRoleId, SysUserRole::getRoleId);
        // where
        Map<String, Object> params = query.getParams();

        wrapper.eq(SysUser::getDelFlag, 0);
        wrapper.isNull(SysUser::getDeptId);
        wrapper.eq(query.getUserId() != null, SysUser::getUserId, query.getUserId());
        wrapper.like(StringUtils.isNotBlank(query.getUserName()), SysUser::getUserName, query.getUserName());
        wrapper.like(StringUtils.isNotBlank(query.getPhonenumber()), SysUser::getPhonenumber, query.getPhonenumber());
        wrapper.eq(query.getStatus() != null, SysUser::getStatus, query.getStatus());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            wrapper.between(SysUser::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        wrapper.orderByDesc(SysUser::getUserId);
        return baseMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    /**
     * 根据机构id获取当前机构所有非管理用户
     */
    @Override
    public Page<SysUser> selectByDeptId(SysUser user) {
        Long deptId = SecurityUtils.getDeptId();
        MPJLambdaWrapper<SysUser> wrapper = JoinWrappers.lambda(SysUser.class);
        // select
        wrapper.select(SysUser::getUserId, SysUser::getDeptId, SysUser::getUserName,
                SysUser::getNickName);
        // join
        wrapper.innerJoin(SysDept.class, SysDept::getDeptId, SysUser::getDeptId);
        // where
        String dataScope = getDataScope();
        if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
            wrapper.eq(SysUser::getUserId, user.getUserId());
        } else {
            wrapper.eq(SysUser::getDeptId, deptId);
            wrapper.ne(SysUser::getDeptId, SysDept::getDeptUserId);
        }
        return baseMapper.selectPage(new Page<>(1, Integer.MAX_VALUE), wrapper);
    }

    /** 自定义代码区域 END**/
}
