package com.fastbee.oss.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 对象存储配置对象 oss_config
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@ApiModel(value = "OssConfigVO", description = "对象存储配置 oss_config")
@Data
public class OssConfigVO{

    /** id */
    @Excel(name = "id")
    @ApiModelProperty("id")
    private Long id;

    /** 租户ID */
    @Excel(name = "租户ID")
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 配置key */
    @Excel(name = "配置key")
    @ApiModelProperty("配置key")
    private String configKey;

    /** accessKey */
    @Excel(name = "accessKey")
    @ApiModelProperty("accessKey")
    private String accessKey;

    /** 秘钥 */
    @Excel(name = "秘钥")
    @ApiModelProperty("秘钥")
    private String secretKey;

    /** 桶名称 */
    @Excel(name = "桶名称")
    @ApiModelProperty("桶名称")
    private String bucketName;

    /** 前缀 */
    @Excel(name = "前缀")
    @ApiModelProperty("前缀")
    private String prefix;

    /** 访问站点 */
    @Excel(name = "访问站点")
    @ApiModelProperty("访问站点")
    private String endpoint;

    /** 自定义域名 */
    @Excel(name = "自定义域名")
    @ApiModelProperty("自定义域名")
    private String domainAlias;

    /** 是否https（Y=是,N=否） */
    @ApiModelProperty("是否https")
    @Excel(name = "是否https")
    private String isHttps;

    /** 域 */
    @Excel(name = "域")
    @ApiModelProperty("域")
    private String region;

    /** 桶权限类型(0=private 1=public 2=custom) */
    @Excel(name = "桶权限类型(0=private 1=public 2=custom)")
    @ApiModelProperty("桶权限类型(0=private 1=public 2=custom)")
    private String accessPolicy;

    /** 是否默认（0=是,1=否） */
    @ApiModelProperty("是否默认")
    @Excel(name = "是否默认")
    private Integer status;

    /** 扩展字段 */
    @Excel(name = "扩展字段")
    @ApiModelProperty("扩展字段")
    private String ext1;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
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

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


}
