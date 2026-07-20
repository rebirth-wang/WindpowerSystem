package com.fastbee.controller.system;

import static com.fastbee.common.constant.Constants.LANGUAGE;

import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.constant.Constants;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginBody;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.system.domain.AppPreferences;
import com.fastbee.system.domain.SysMenu;
import com.fastbee.system.service.IAppPreferencesService;
import com.fastbee.system.service.ISysMenuService;
import com.fastbee.system.service.sys.SysLoginService;
import com.fastbee.system.service.sys.SysPermissionService;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@Api(tags = "登录验证")
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private IAppPreferencesService appPreferencesService;


    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AjaxResult login(HttpServletRequest request, @RequestBody LoginBody loginBody)
    {
        try {
            AjaxResult ajax = AjaxResult.success();
            // 生成令牌
            String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                    loginBody.getUuid(), loginBody.getSourceType(), request.getHeader(LANGUAGE));
            ajax.put(Constants.TOKEN, token);
            ajax.put("expireTime", loginService.getTokenExpireTime(token));
            return ajax;
        } catch (ServiceException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String dataScope = SecurityUtils.getDataScope();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AppPreferences appPreferences = appPreferencesService.selectAppPreferencesByUserId(user.getUserId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("mqtt",enabled);
        ajax.put("language", appPreferences.getLanguage());
        ajax.put("dataScope", dataScope);
        return ajax;
    }


    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @ApiOperation("获取路由信息")
    @GetMapping("getRouters")
    public AjaxResult getRouters(HttpServletRequest request)
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId, request.getHeader(LANGUAGE));
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
