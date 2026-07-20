package com.fastbee.oss.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 对象存储配置对象 oss_config
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OssConfig", description = "对象存储配置 oss_config")
@Data
@TableName("oss_config" )
public class OssConfig extends PageEntity
{
    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 配置key */
    @ApiModelProperty("配置key")
    private String configKey;

    /** accessKey */
    @ApiModelProperty("accessKey")
    private String accessKey;

    /** 秘钥 */
    @ApiModelProperty("秘钥")
    private String secretKey;

    /** 桶名称 */
    @ApiModelProperty("桶名称")
    private String bucketName;

    /** 前缀 */
    @ApiModelProperty("前缀")
    private String prefix;

    /** 访问站点 */
    @ApiModelProperty("访问站点")
    private String endpoint;

    /** 自定义域名 */
    @ApiModelProperty("自定义域名")
    private String domainAlias;

    /** 是否https（Y=是,N=否） */
    @ApiModelProperty("是否https")
    @AiSemanticField(
            semanticName = "是否https",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_yes_no"
    )
    private String isHttps;

    /** 域 */
    @ApiModelProperty("域")
    private String region;

    /** 桶权限类型(0=private 1=public 2=custom) */
    @ApiModelProperty("桶权限类型(0=private 1=public 2=custom)")
    private String accessPolicy;

    /** 是否默认（0=是,1=否） */
    @ApiModelProperty("是否默认")
    @AiSemanticField(
            semanticName = "是否默认",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 扩展字段 */
    @ApiModelProperty("扩展字段")
    private String ext1;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
