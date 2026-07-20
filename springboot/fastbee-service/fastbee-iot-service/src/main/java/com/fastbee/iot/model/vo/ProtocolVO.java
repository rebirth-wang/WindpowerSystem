package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 协议对象 iot_protocol
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@ApiModel(value = "ProtocolVO", description = "协议 iot_protocol")
@Data
public class ProtocolVO{
    /** 代码生成区域 可直接覆盖**/
    /** 自增id */
    @Excel(name = "自增id")
    @ApiModelProperty("自增id")
    private Long id;

    /** 协议编码 */
    @Excel(name = "协议编码")
    @ApiModelProperty("协议编码")
    private String protocolCode;

    /** 协议名称 */
    @Excel(name = "协议名称")
    @ApiModelProperty("协议名称")
    private String protocolName;

    /** 协议jar包,js包，c程序上传地址 */
    @Excel(name = "协议jar包,js包，c程序上传地址")
    @ApiModelProperty("协议jar包,js包，c程序上传地址")
    private String protocolFileUrl;

    /** 协议类型 0:未知 1:jar，2.js,3.c */
    @Excel(name = "协议类型 0:未知 1:jar，2.js,3.c")
    @ApiModelProperty("协议类型 0:未知 1:jar，2.js,3.c")
    private Integer protocolType;

    /** 协议文件摘要(文件的md5) */
    @Excel(name = "协议文件摘要(文件的md5)")
    @ApiModelProperty("协议文件摘要(文件的md5)")
    private String jarSign;

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

    /** 0:草稿 1:启用 2:停用 */
    @Excel(name = "0:草稿 1:启用 2:停用")
    @ApiModelProperty("0:草稿 1:启用 2:停用")
    private Integer protocolStatus;

    /** 0:正常 1:删除 */
    @Excel(name = "0:正常 1:删除")
    @ApiModelProperty("0:正常 1:删除")
    private Integer delFlag;

    /** 显示，1-显示；0-不显示 */
    @Excel(name = "显示，1-显示；0-不显示")
    @ApiModelProperty("显示，1-显示；0-不显示")
    private Integer display;

    /** 协议数据格式 */
    @Excel(name = "协议数据格式")
    @ApiModelProperty("协议数据格式")
    private String dataFormat;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
