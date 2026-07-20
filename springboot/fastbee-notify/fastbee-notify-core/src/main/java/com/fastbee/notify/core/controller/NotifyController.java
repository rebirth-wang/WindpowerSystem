package com.fastbee.notify.core.controller;

import static com.fastbee.common.core.domain.AjaxResult.error;

import java.io.IOException;
import java.util.Objects;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.constant.CacheConstants;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.notify.core.service.NotifySendService;
import com.fastbee.notify.core.vo.SendParams;
import com.fastbee.system.service.ISysUserService;

/**
 * @author fastb
 * @version 1.0
 * @description: 通知控制器
 * @date 2023-12-15 11:44
 */
@Api(tags = "通知发送")
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Resource
    private NotifySendService notifySendService;
    @Resource
    private ISysUserService sysUserService;

    /**
     * @description: 通知测试发送接口
     * @param: sendParams 发送参数
     * @return: com.fastbee.common.core.domain.AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('notify:template:send')")
    @PostMapping("/send")
    @ApiOperation("通知模版测试发送接口")
    public AjaxResult send(@RequestBody SendParams sendParams){
        return notifySendService.send(sendParams);
    }

    /**
     * @description: 短信登录获取验证码
     * @param: phoneNumber 手机号
     * @return: com.fastbee.common.core.domain.AjaxResult
     */
    @GetMapping("/smsLoginCaptcha")
    @ApiOperation("短信登录获取验证码")
    public AjaxResult smsLoginCaptcha(String phoneNumber){
        return notifySendService.sendSmsCaptcha(phoneNumber, CacheConstants.LOGIN_SMS_CAPTCHA_PHONE);
    }

    /**
     * 企业微信验证url有效性
     * @param msgSignature
     * @param: timestamp
     * @param: nonce
     * @param: echostr
     * @param: response
     * @return void
     */
    @ApiOperation("企业微信验证url有效性")
    @GetMapping("/weComVerifyUrl")
    public void weComVerifyUrl(@RequestParam(value = "msg_signature") String msgSignature,
                                     @RequestParam(value = "timestamp") String timestamp,
                                     @RequestParam(value = "nonce") String nonce,
                                     @RequestParam(value = "echostr") String echostr,
                                     HttpServletResponse response) {
        String msg = notifySendService.weComVerifyUrl(msgSignature, timestamp, nonce, echostr);
        try {
            response.getWriter().print(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @description: 短信注册获取验证码
     * @param: phoneNumber 手机号
     * @return: com.fastbee.common.core.domain.AjaxResult
     */
    @GetMapping("/smsRegisterCaptcha")
    @ApiOperation("短信登录获取验证码")
    public AjaxResult smsRegisterCaptcha(String phoneNumber){
        return notifySendService.sendSmsCaptcha(phoneNumber, CacheConstants.REGISTER_SMS_CAPTCHA_PHONE);
    }

    /**
     * @description: 忘记密码获取手机验证码
     * @param: phoneNumber 手机号
     * @return: com.fastbee.common.core.domain.AjaxResult
     */
    @GetMapping("/smsForgetPassword")
    @ApiOperation("忘记密码获取验证码")
    public AjaxResult smsForgetPassword(String phoneNumber){
        SysUser sysUser = sysUserService.selectUserByPhoneNumber(phoneNumber);
        if (Objects.isNull(sysUser)) {
            return error(MessageUtils.message("user.not.exists"));
        }
        return notifySendService.sendSmsCaptcha(phoneNumber, CacheConstants.FORGOT_PASSWORD_SMS_VERIFY_PHONE);
    }

}
