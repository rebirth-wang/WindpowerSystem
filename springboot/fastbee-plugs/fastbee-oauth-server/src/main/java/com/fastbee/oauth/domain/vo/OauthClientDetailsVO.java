package com.fastbee.oauth.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 【请填写功能名称】对象 oauth_client_details
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@ApiModel(value = "OauthClientDetailsVO", description = "【请填写功能名称】 oauth_client_details")
@Data
public class OauthClientDetailsVO{

    /** 编号 */
    @Excel(name = "编号")
    @ApiModelProperty("编号")
    private Long id;

    /** 客户端ID */
    @Excel(name = "客户端ID")
    @ApiModelProperty("客户端ID")
    private String clientId;

    /** 客户端所能访问的资源id集合,多个资源时用逗号(,)分隔 */
    @Excel(name = "客户端所能访问的资源id集合,多个资源时用逗号(,)分隔")
    @ApiModelProperty("客户端所能访问的资源id集合,多个资源时用逗号(,)分隔")
    private String resourceIds;

    /** 客户端秘钥 */
    @Excel(name = "客户端秘钥")
    @ApiModelProperty("客户端秘钥")
    private String clientSecret;

    /** 权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔 */
    @Excel(name = "权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔")
    @ApiModelProperty("权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔")
    private String scope;

    /** 授权模式，可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔 */
    @Excel(name = "授权模式，可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔")
    @ApiModelProperty("授权模式，可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔")
    private String authorizedGrantTypes;

    /** 回调地址 */
    @Excel(name = "回调地址")
    @ApiModelProperty("回调地址")
    private String webServerRedirectUri;

    /** 权限 */
    @Excel(name = "权限")
    @ApiModelProperty("权限")
    private String authorities;

    /** 设定客户端的access_token的有效时间值(单位:秒) */
    @Excel(name = "设定客户端的access_token的有效时间值(单位:秒)")
    @ApiModelProperty("设定客户端的access_token的有效时间值(单位:秒)")
    private Long accessTokenValidity;

    /** 设定客户端的refresh_token的有效时间值(单位:秒) */
    @Excel(name = "设定客户端的refresh_token的有效时间值(单位:秒)")
    @ApiModelProperty("设定客户端的refresh_token的有效时间值(单位:秒)")
    private Long refreshTokenValidity;

    /** 预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据 */
    @Excel(name = "预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据")
    @ApiModelProperty("预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据")
    private String additionalInformation;

    /** 设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false', 'read','write'.
     该字段只适用于grant_type="authorization_code"的情况,当用户登录成功后,若该值为'true'或支持的scope值,则会跳过用户Approve的页面, 直接授权.  */
    @Excel(name = "设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false', 'read','write' ")
    private String autoapprove;

    /** 1=小度(DuerOS),2=天猫精灵(ALiGenie),3=小米小爱 */
    @Excel(name = "1=小度(DuerOS),2=天猫精灵(ALiGenie),3=小米小爱")
    @ApiModelProperty("1=小度(DuerOS),2=天猫精灵(ALiGenie),3=小米小爱")
    private Long type;

    /** 启用状态 */
    @Excel(name = "启用状态")
    @ApiModelProperty("启用状态")
    private Long status;

    /** 图标 */
    @Excel(name = "图标")
    @ApiModelProperty("图标")
    private String icon;

    /** 云技能id */
    @Excel(name = "云技能id")
    @ApiModelProperty("云技能id")
    private String cloudSkillId;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    private String createBy;

}
