package com.fastbee.common.extend.utils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.spring.SpringUtils;

/**
 * 安全服务工具类
 *
 * @author ruoyi
 */
public class SecurityUtils
{

    public static final String SYS_DEPT_TENANT_ID = "sys_dept:";

    private static volatile RedisCache redisCache;

    /**
     * 用户ID
     **/
    public static Long getUserId()
    {
        try
        {
            return getLoginUser().getUserId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId()
    {
        try
        {
            return getLoginUser().getDeptId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 获取语言
     * @return
     */
    public static String getLanguage() {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            if (locale != null) {
                String lang = locale.toLanguageTag();
                if ("zh-CN".equalsIgnoreCase(lang) || "en-US".equalsIgnoreCase(lang)) {
                    return lang;
                }
                if ("zh".equalsIgnoreCase(locale.getLanguage())) {
                    return "zh-CN";
                }
                if ("en".equalsIgnoreCase(locale.getLanguage())) {
                    return "en-US";
                }
            }
            String language = getLoginUser().getLanguage();
            return StringUtils.isEmpty(language) ? "en-US" : language;
        } catch (Exception e) {
            return "en-US";
        }
    }


    public static String getDataScope() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String dataScope;
        if (user.isAdmin()) {
            dataScope = DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD;
        } else {
            List<SysRole> roles = user.getRoles();
            List<String> list = roles.stream().map(SysRole::getDataScope).distinct().collect(Collectors.toList());
            if (list.contains(DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD)) {
                dataScope = DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD;
            } else if (list.contains(DataScopeAspect.DATA_SCOPE_DEPT)) {
                dataScope = DataScopeAspect.DATA_SCOPE_DEPT;
            } else {
                dataScope = DataScopeAspect.DATA_SCOPE_SELF;
            }
        }
        return dataScope;
    }

    /**
     * 针对修改、删除接口
     * 检查用户操作权限
     *
     * @param tenantId 租户 ID
     * @param createBy 所产生
     */
    public static void checkUserOperatePermission(Long tenantId, String createBy) {
        LoginUser loginUser = getLoginUser();
        SysUser curUser = loginUser.getUser();
        if (SysUser.isAdmin(curUser.getUserId())) {
            return;
        }
        if (curUser.getUserName().equals(createBy)) {
            return;
        }
        if (null == curUser.getDeptId()) {
            throw new ServiceException(noOperatePermissionMessage());
        }
        String dataScope = getDataScope();
        if (DataScopeAspect.DATA_SCOPE_SELF.equals(dataScope)) {
            throw new ServiceException(noOperatePermissionMessage());
        } else if (DataScopeAspect.DATA_SCOPE_DEPT.equals(dataScope)) {
            if (!curUser.getDept().getDeptUserId().equals(tenantId)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        } else if (DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
            if (!curUser.getDept().getDeptUserId().equals(tenantId)) {
                RedisCache cache = getRedisCache();
                if (cache == null) {
                    throw new ServiceException(noOperatePermissionMessage());
                }
                SysDept sysDept = cache.getCacheObject(SYS_DEPT_TENANT_ID + tenantId);
                if (Objects.isNull(sysDept)) {
                    throw new ServiceException(noOperatePermissionMessage());
                }
                String ancestors = sysDept.getAncestors();
                if (!ancestors.contains(curUser.getDeptId().toString())) {
                    throw new ServiceException(noOperatePermissionMessage());
                }
            }
        }
    }

    private static RedisCache getRedisCache() {
        RedisCache cache = redisCache;
        if (cache != null) {
            return cache;
        }
        try {
            cache = SpringUtils.getBean(RedisCache.class);
            redisCache = cache;
            return cache;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String noOperatePermissionMessage() {
        try {
            return MessageUtils.message("no.operate.permission");
        } catch (Exception ignored) {
            return "暂无权限操作！";
        }
    }
}
