package com.fastbee.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.extend.core.domin.entity.SysUser;

/**
 * 用户表 数据层
 *
 * @author ruoyi
 */
public interface SysUserMapper extends MPJBaseMapper<SysUser>
{

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllocatedList(SysUser user);
    public Page<SysUser> selectAllocatedList(Page<SysUser> page, @Param("user") SysUser user);
    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUnallocatedList(SysUser user);
    public Page<SysUser> selectUnallocatedList(Page<SysUser> page, @Param("user") SysUser user);
    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 通过手机号查询用户
     *
     * @param phoneNumber 手机号
     * @return 用户对象信息
     */
    SysUser selectUserByPhoneNumber(String phoneNumber);
    /**
     * 取消三方登录相关信息
     * @param sysUserId 系统用户id
     * @param sourceClientList 来源具体平台
     * @return
     */
    int deleteBySysUserIdAndSourceClient(@Param("sysUserId") Long sysUserId, @Param("sourceClientList") List<String> sourceClientList);

    /**
     * 取消三方登录相关信息
     * @param sysUserIds 系统用户id集合
     * @param sourceClientList 来源具体平台
     * @return
     */
    int deleteBySysUserIdsAndSourceClient(@Param("sysUserIds") Long[] sysUserIds, @Param("sourceClientList") List<String> sourceClientList);

    /**
     * 通过部门id删除用户
     * @param deptId 部门id
     * @return int
     */
    int deleteUserByDeptID(Long deptId);



    /**
     * 根据用户id获取当前机构的管理员账号
     * @param userId
     * @return
     */
    SysUser getDeptUserByUserId(Long userId);


    /**
     * 根据机构id获取当前机构所有非管理用户
     * @return
     */
    List<SysUser> selectByDeptId(Long deptId);

    /**
     * 删除用户分享给其他用户的设备
     * @param userIds
     */
    void deleteDeviceUserShareByUserIds(Long[] userIds);

    /**
     * 删除用户绑定设备
     * @param userIds
     */
    void deleteDeviceUserByUserIds(Long[] userIds);

    /**
     * 删除用户被分享设备
     * @param userIds
     */
    void deleteDeviceShareByUserIds(Long[] userIds);
}
