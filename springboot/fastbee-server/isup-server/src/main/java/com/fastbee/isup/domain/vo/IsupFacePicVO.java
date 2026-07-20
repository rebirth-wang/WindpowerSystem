package com.fastbee.isup.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 人脸库图片对象 isup_face_pic
 *
 * @author fastbee
 * @date 2026-03-05
 */

@ApiModel(value = "IsupFacePicVO", description = "人脸库图片 isup_face_pic")
@Data
public class IsupFacePicVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id自增 */
    @Excel(name = "主键id自增")
    @ApiModelProperty("主键id自增")
    private Long id;

    /** 关联工人ID */
    @Excel(name = "关联工人ID")
    @ApiModelProperty("关联工人ID")
    private Long workerId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 人脸库ID */
    @Excel(name = "人脸库ID")
    @ApiModelProperty("人脸库ID")
    private String fdid;

    /** 人脸库图片ID */
    @Excel(name = "人脸库图片ID")
    @ApiModelProperty("人脸库图片ID")
    private String pid;

    /** 图片地址 */
    @Excel(name = "图片地址")
    @ApiModelProperty("图片地址")
    private String picUrl;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("过期时间")
    @Excel(name = "过期时间")
    private Date expireTime;

    /** 删除标志（0代表存在 1代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private Long delFlag;

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
