package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 项目数据关联对象 iot_goview_project_data
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "GoviewProjectDataVO", description = "项目数据关联 iot_goview_project_data")
@Data
public class GoviewProjectDataVO{

    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private String id;

    /** 项目id */
    @Excel(name = "项目id")
    @ApiModelProperty("项目id")
    private String projectId;

    /** 存储数据 */
    @Excel(name = "存储数据")
    @ApiModelProperty("存储数据")
    private byte[] content;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 创建人id */
    @Excel(name = "创建人id")
    @ApiModelProperty("创建人id")
    private String createBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

}
