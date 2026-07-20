package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * APP版本对象 app_version
 *
 * @author fastbee
 * @date 2025-08-11
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AppVersion", description = "APP版本 app_version")
@Data
@TableName("app_version" )
public class AppVersion extends PageEntity {
    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 版本号 */
    @ApiModelProperty("版本号")
    private String version;

    /** 版本名称 */
    @ApiModelProperty("版本名称")
    private String versionName;

    /** 是否热更新 */
    @ApiModelProperty("是否热更新")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private String isLiveUpdate;

    /** apk链接 */
    @ApiModelProperty("apk链接")
    private String apk;

    /** wgt链接 */
    @ApiModelProperty("wgt链接")
    private String wgt;

    /** 更新内容 */
    @ApiModelProperty("更新内容")
    private String updateContent;

    /** 删除标志(0-存在,1-删除) */
    @ApiModelProperty("删除标志(0-存在,1-删除)")
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

}
