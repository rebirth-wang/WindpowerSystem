package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * APP版本对象 app_version
 *
 * @author fastbee
 * @date 2025-08-11
 */

@ApiModel(value = "AppVersionVO", description = "APP版本 app_version")
@Data
public class AppVersionVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 版本号 */
    @Excel(name = "版本号")
    @ApiModelProperty("版本号")
    private String version;

    /** 版本名称 */
    @Excel(name = "版本名称")
    @ApiModelProperty("版本名称")
    private String versionName;

    /** 是否热更新 */
    @Excel(name = "是否热更新")
    @ApiModelProperty("是否热更新")
    private String isLiveUpdate;

    /** apk链接 */
    @Excel(name = "apk链接")
    @ApiModelProperty("apk链接")
    private String apk;

    /** wgt链接 */
    @Excel(name = "wgt链接")
    @ApiModelProperty("wgt链接")
    private String wgt;

    /** 更新内容 */
    @Excel(name = "更新内容")
    @ApiModelProperty("更新内容")
    private String updateContent;

    /** 删除标志(0-存在,1-删除) */
    @Excel(name = "删除标志(0-存在,1-删除)")
    @ApiModelProperty("删除标志(0-存在,1-删除)")
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



    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
