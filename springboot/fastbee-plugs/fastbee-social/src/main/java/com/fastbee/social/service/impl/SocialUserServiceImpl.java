package com.fastbee.social.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.constant.CacheConstants;
import com.fastbee.common.constant.Constants;
import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.user.CaptchaException;
import com.fastbee.common.exception.user.CaptchaExpireException;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.framework.manager.AsyncManager;
import com.fastbee.social.domain.SocialUser;
import com.fastbee.social.mapper.SocialUserMapper;
import com.fastbee.social.model.RegisterUserInput;
import com.fastbee.social.model.RegisterUserOutput;
import com.fastbee.social.service.ISocialUserService;
import com.fastbee.system.factory.AsyncFactory;
import com.fastbee.system.service.ISysConfigService;
import com.fastbee.system.service.ISysDeptService;
import com.fastbee.system.service.ISysUserService;

/**
 * 用户第三方用户信息Service业务层处理
 *
 * @author json
 * @date 2022-04-18
 */
@Service
public class SocialUserServiceImpl extends ServiceImpl<SocialUserMapper, SocialUser> implements ISocialUserService
{
    @Resource
    private RedisCache redisCache;
    @Resource
    private SocialUserMapper socialUserMapper;

    @Resource
    private ISysConfigService configService;

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysDeptService sysDeptService;


    /**
     * 查询用户第三方用户信息
     *
     * @param socialUserId 用户第三方用户信息主键
     * @return 用户第三方用户信息
     */
    @Override
    public SocialUser selectSocialUserBySocialUserId(Long socialUserId)
    {
        return socialUserMapper.selectById(socialUserId);
    }

    /**
     * 查询用户第三方用户信息列表
     *
     * @param socialUser 用户第三方用户信息
     * @return 用户第三方用户信息
     */
    @Override
    public List<SocialUser> selectSocialUserList(SocialUser socialUser)
    {
        return socialUserMapper.selectSocialUserList(socialUser);
    }

    /**
     * 新增用户第三方用户信息
     *
     * @param socialUser 用户第三方用户信息
     * @return 结果
     */
    @Override
    public int insertSocialUser(SocialUser socialUser)
    {
        socialUser.setCreateTime(DateUtils.getNowDate());
        return socialUserMapper.insert(socialUser);
    }

    /**
     * 修改用户第三方用户信息
     *
     * @param socialUser 用户第三方用户信息
     * @return 结果
     */
    @Override
    public int updateSocialUser(SocialUser socialUser)
    {
        socialUser.setUpdateTime(DateUtils.getNowDate());
        return socialUserMapper.updateById(socialUser);
    }

    /**
     * 批量删除用户第三方用户信息
     *
     * @param socialUserIds 需要删除的用户第三方用户信息主键
     * @return 结果
     */
    @Override
    public int deleteSocialUserBySocialUserIds(Long[] socialUserIds)
    {
        return socialUserMapper.deleteBatchIds(Arrays.asList(socialUserIds));
    }

    /**
     * 删除用户第三方用户信息信息
     *
     * @param socialUserId 用户第三方用户信息主键
     * @return 结果
     */
    @Override
    public int deleteSocialUserBySocialUserId(Long socialUserId)
    {
        return socialUserMapper.deleteById(socialUserId);
    }

    /**
     * 根据openId或unionId获取用户第三方信息
     * @param openId
     * @param unionId
     * @return
     */
    @Override
    public SocialUser selectOneByOpenIdAndUnionId(String openId, String unionId) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getOpenId, openId);
        if (StringUtils.isNotEmpty(unionId)) {
            queryWrapper.eq(SocialUser::getUnionId, unionId);
        }
        return socialUserMapper.selectOne(queryWrapper);
    }

    /**
     * 通过unionId查询
     *
     * @param unionId
     * @return
     */
    @Override
    public Long selectSysUserIdByUnionId(String unionId) {
        if (StringUtils.isEmpty(unionId)) {
            return null;
        }
        IPage<Long> page = new Page<>(1,1);
        IPage<Long> ret = socialUserMapper.selectSysUserIdByUnionId(page,unionId);
        if(ret.getTotal() > 0) {
            return ret.getRecords().get(0);
        } else {
            return null;
        }
    }

    /**
     * 通过系统用户id查询已绑定信息
     * @param sysUserId 系统用户id
     * @return
     */
    @Override
    public List<SocialUser> selectBySysUserId(Long sysUserId) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getSysUserId, sysUserId);
        return socialUserMapper.selectList(queryWrapper);
    }

    /**
     * 取消三方登录相关信息
     * @param sysUserId 系统用户id
     * @param sourceClientList 来源具体平台
     * @return
     */
    @Override
    public int cancelBind(Long sysUserId, List<String> sourceClientList) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getSysUserId, sysUserId);
        queryWrapper.in(SocialUser::getSourceClient, sourceClientList);
        return socialUserMapper.delete(queryWrapper);
    }

    /**
     * 取消三方登录相关信息
     * @param sysUserIds 系统用户id集合
     * @param sourceClientList 来源具体平台
     * @return
     */
    @Override
    public int batchCancelBind(Long[] sysUserIds, List<String> sourceClientList) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SocialUser::getSysUserId, Arrays.asList(sysUserIds));
        queryWrapper.in(SocialUser::getSourceClient, sourceClientList);
        return socialUserMapper.delete(queryWrapper);
    }

    @Override
    public SocialUser selectByUserIdAndSourceClient(Long userId, String sourceClient) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getSysUserId, userId);
        queryWrapper.eq(SocialUser::getSourceClient, sourceClient);
        return socialUserMapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteByOpenIdAndSourceClient(String openId, String sourceClient) {
        LambdaQueryWrapper<SocialUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialUser::getOpenId, openId);
        queryWrapper.eq(SocialUser::getSourceClient, sourceClient);
        return socialUserMapper.delete(queryWrapper);
    }

    @Override
    public List<SocialUser> listWechatPublicAccountOpenId(List<String> userIdList) {
        return socialUserMapper.listWechatPublicAccountOpenId(userIdList);
    }

    /**
     * 注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterUserInput registerBody) {
        String msg = "";
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String phonenumber = registerBody.getPhonenumber();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        boolean captchaEnabled = configService.selectCaptchaEnabled();
        // 验证码开关
        if (captchaEnabled) {
            if (1 == registerBody.getVerifyType()) {
                validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
            } else if (2 == registerBody.getVerifyType()) {
                String captchaKey = CacheConstants.REGISTER_SMS_CAPTCHA_PHONE + registerBody.getPhonenumber();
                Object captcha = redisCache.getCacheObject(captchaKey);
                if (Objects.isNull(captcha)) {
                    return "验证码已失效，请重新获取";
                }
                if (!captcha.toString().equals(registerBody.getCode())) {
                    return "验证码错误，请重试";
                }
            }
        }

        if (StringUtils.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(sysUser))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else if (UserConstants.NOT_UNIQUE.equals(checkPhoneUnique(phonenumber))) {
            msg = "保存用户'" + username + "'失败，注册手机号码已存在";
        } else {
            sysUser.setNickName(username);
            sysUser.setPhonenumber(phonenumber);
            sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
            // web注册默认绑定第一个机构
            Long roleId = null;
            if (StringUtils.isNotEmpty(registerBody.getInvitationCode())) {
                SysDept sysDept = sysDeptService.selectDeptByInvitationCode(registerBody.getInvitationCode());
                if (Objects.isNull(sysDept)) {
                    msg = "邀请码错误，请联系机构管理员核对";
                } else {
                    sysUser.setDeptId(sysDept.getDeptId());
                    roleId = sysDeptService.selectRegisterRoleByDeptId(sysDept.getDeptId());
                }
            } else {
                if (null != registerBody.getSourceType() && 1 == registerBody.getSourceType()) {
                    sysUser.setDeptId(100L);
                    roleId = sysDeptService.selectRoleIdByDeptId(100L);
                } else {
                    roleId = 3L;
                }
            }
            if (null == roleId) {
                msg = "注册失败,请联系管理人员检查机构角色是否存在";
            }
            Long[] roleIds = new Long[]{roleId};
            boolean regFlag = userService.registerUser(sysUser);
            //分配普通用户角色(1=超级管理员，2=设备租户，3=普通用户，4=游客)
            userService.insertUserAuth(sysUser.getUserId(), roleIds);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                        MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterUserOutput registerNoCaptcha(RegisterUserInput registerBody) {
        RegisterUserOutput registerUserOutput = new RegisterUserOutput();
        String msg = "";
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String phonenumber = registerBody.getPhonenumber();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        if (StringUtils.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(sysUser))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else if (UserConstants.NOT_UNIQUE.equals(checkPhoneUnique(phonenumber))) {
            msg = "保存用户'" + username + "'失败，注册手机号码已存在";
        } else {
            sysUser.setNickName(username);
            sysUser.setPhonenumber(phonenumber);
            String encryptPassword = SecurityUtils.encryptPassword(registerBody.getPassword());
            sysUser.setPassword(encryptPassword);
            if (ObjectUtil.isNotNull(registerBody.getDeptId())) {
                sysUser.setDeptId(registerBody.getDeptId());
            }
            boolean regFlag = userService.registerUser(sysUser);
            //分配普通用户角色(1=超级管理员，2=设备租户，3=普通用户，4=游客)
            Long[] roleIds;
            if (ObjectUtil.isNotNull(registerBody.getRoleIds())) {
                roleIds = registerBody.getRoleIds();
            } else {
                roleIds = new Long[]{3L};
            }
            userService.insertUserAuth(sysUser.getUserId(), roleIds);
            registerUserOutput.setSysUserId(sysUser.getUserId());
            registerUserOutput.setEncryptPassword(encryptPassword);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                        MessageUtils.message("user.register.success")));
            }
        }
        registerUserOutput.setMsg(msg);
        return registerUserOutput;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return
     */
    public String checkPhoneUnique(String phonenumber) {
        SysUser sysUser = new SysUser();
        sysUser.setPhonenumber(phonenumber);
        sysUser.setDelFlag(0);
        return userService.checkPhoneUnique(sysUser);
//        SysUser info = userMapper.checkPhoneUnique(phonenumber);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException();
        }
    }
}
