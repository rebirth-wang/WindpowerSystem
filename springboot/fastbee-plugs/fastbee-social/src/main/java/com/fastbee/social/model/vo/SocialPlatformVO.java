package com.fastbee.social.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 第三方登录平台控制对象 iot_social_platform
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@ApiModel(value = "SocialPlatformVO", description = "第三方登录平台控制 iot_social_platform")
@Data
public class SocialPlatformVO{

    /** 第三方登录平台主键 */
    @Excel(name = "第三方登录平台主键")
    @ApiModelProperty("第三方登录平台主键")
    private Long socialPlatformId;

    /** 第三方登录平台 */
    @Excel(name = "第三方登录平台")
    @ApiModelProperty("第三方登录平台")
    private String platform;

    /**  0:启用 ,1:禁用 */
    @Excel(name = " 0:启用 ,1:禁用")
    @ApiModelProperty(" 0:启用 ,1:禁用")
    private Integer status;

    /** 第三方平台申请Id */
    @Excel(name = "第三方平台申请Id")
    @ApiModelProperty("第三方平台申请Id")
    private String clientId;

    /** 第三方平台密钥 */
    @Excel(name = "第三方平台密钥")
    @ApiModelProperty("第三方平台密钥")
    private String secretKey;

    /** 用户认证后跳转地址 */
    @Excel(name = "用户认证后跳转地址")
    @ApiModelProperty("用户认证后跳转地址")
    private String redirectUri;

    /** 删除标记位(0代表存在，1代表删除) */
    @Excel(name = "删除标记位(0代表存在，1代表删除)")
    @ApiModelProperty("删除标记位(0代表存在，1代表删除)")
    private String delFlag;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 绑定注册登录uri,http://localhost/login?bindId= */
    @Excel(name = "绑定注册登录uri,http://localhost/login?bindId=")
    @ApiModelProperty("绑定注册登录uri,http://localhost/login?bindId=")
    private String bindUri;

    /** 跳转登录uri,http://localhost/login?loginId= */
    @Excel(name = "跳转登录uri,http://localhost/login?loginId=")
    @ApiModelProperty("跳转登录uri,http://localhost/login?loginId=")
    private String redirectLoginUri;

    /** 错误提示uri,http://localhost/login?errorId= */
    @Excel(name = "错误提示uri,http://localhost/login?errorId=")
    @ApiModelProperty("错误提示uri,http://localhost/login?errorId=")
    private String errorMsgUri;


}
