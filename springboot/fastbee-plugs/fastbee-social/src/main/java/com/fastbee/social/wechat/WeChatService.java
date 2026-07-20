package com.fastbee.social.wechat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.wechat.WeChatLoginBody;
import com.fastbee.common.extend.wechat.WeChatLoginResult;
import com.fastbee.social.wechat.vo.WxBindReqVO;
import com.fastbee.social.wechat.vo.WxCancelBindReqVO;

/**
 * 微信相关服务类
 * @author fastb
 * @date 2023-07-31 11:34
 */
public interface WeChatService {

    /**
     * 移动应用微信登录
     * @param weChatLoginBody 微信登录参数
     * @return String
     */
    WeChatLoginResult mobileLogin(WeChatLoginBody weChatLoginBody, String language);

    /**
     * 小程序微信登录
     * @param weChatLoginBody 微信登录参数
     * @return 登录结果
     */
    WeChatLoginResult miniLogin(WeChatLoginBody weChatLoginBody, String language);

    /**
     * 取消所有相关微信绑定
     * @param wxCancelBindReqVO 微信解绑传参类型
     * @return 结果
     */
    AjaxResult cancelBind(WxCancelBindReqVO wxCancelBindReqVO);

    /**
     * 小程序、移动应用微信绑定
     * @param wxBindReqVO 微信绑定传参类型
     * @return 结果
     */
    AjaxResult bind(WxBindReqVO wxBindReqVO);

    /**
     * 网站应用获取微信绑定二维码信息
     * @return 二维码信息
     */
    AjaxResult getWxBindQr(HttpServletRequest httpServletRequest);

    /**
     * 网站应用内微信扫码绑定回调接口
     * @param code 用户凭证
     * @param state 时间戳
     * @param httpServletRequest 请求信息
     * @param httpServletResponse 响应信息
     */
    String wxBindCallback(String code, String state, String wxBindId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 网站应用获取微信绑定结果信息
     * @param wxBindMsgId 微信绑定结果信息id
     * @return msg
     */
    AjaxResult getWxBindMsg(String wxBindMsgId);
}
