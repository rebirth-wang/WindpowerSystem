package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 协议对象 iot_protocol
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Protocol", description = "协议 iot_protocol")
@Data
@TableName("iot_protocol" )
public class Protocol extends PageEntity{
    private static final long serialVersionUID=1L;

    /** 自增id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("自增id")
    private Long id;

    /** 协议编码 */
    @ApiModelProperty("协议编码")
    private String protocolCode;

    /** 协议名称 */
    @ApiModelProperty("协议名称")
    private String protocolName;

    /** 协议jar包,js包，c程序上传地址 */
    @ApiModelProperty("协议jar包,js包，c程序上传地址")
    private String protocolFileUrl;

    /** 协议类型 0:未知 1:jar，2.js,3.c */
    @ApiModelProperty("协议类型 0:未知 1:jar，2.js,3.c")
    @AiSemanticField(
            semanticName = "协议类型 0",
            semanticType = AiSemanticType.ENUM
    )
    private Integer protocolType;

    /** 协议文件摘要(文件的md5) */
    @ApiModelProperty("协议文件摘要(文件的md5)")
    private String jarSign;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 0:草稿 1:启用 2:停用 */
    @ApiModelProperty("0:草稿 1:启用 2:停用")
    @AiSemanticField(
            semanticName = "0",
            semanticType = AiSemanticType.ENUM
    )
    private Integer protocolStatus;

    /** 0:正常 1:删除 */
    @ApiModelProperty("0:正常 1:删除")
    @TableLogic(value = "0", delval = "NULL")
    @AiSemanticField(
            semanticName = "0",
            semanticType = AiSemanticType.ENUM
    )
    private Integer delFlag;

    /** 显示，1-显示；0-不显示 */
    @ApiModelProperty("显示，1-显示；0-不显示")
    private Integer display;

    /** 协议数据格式 */
    @ApiModelProperty("协议数据格式")
    private String dataFormat;

}
