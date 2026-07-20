package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 项目对象 iot_goview_project
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GoviewProject", description = "项目 iot_goview_project")
@Data
@TableName("iot_goview_project" )
public class GoviewProject extends PageEntity{
    /** 主键 */
    @TableId(value = "id")
    @ApiModelProperty("主键")
    private String id;

    /** 项目名称 */
    @ApiModelProperty("项目名称")
    private String projectName;

    /** 项目状态[0未发布,1发布] */
    @ApiModelProperty("项目状态[0未发布,1发布]")
    private Integer state;

    /** 首页图片 */
    @ApiModelProperty("首页图片")
    private String indexImage;

    /** 删除状态[1删除,-1未删除] */
    @ApiModelProperty("删除状态[1删除,-1未删除]")
    @TableLogic
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer delFlag;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 创建人id */
    @ApiModelProperty("创建人id")
    private String createBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 项目介绍 */
    @ApiModelProperty("项目介绍")
    private String remarks;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

}
