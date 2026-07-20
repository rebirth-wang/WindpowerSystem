package com.fastbee.isup.domain;

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

/**
 * 人脸库图片对象 isup_face_pic
 *
 * @author fastbee
 * @date 2026-03-05
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "IsupFacePic", description = "人脸库图片 isup_face_pic")
@Data
@TableName("isup_face_pic" )
public class IsupFacePic extends PageEntity {
    /** 主键id自增 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id自增")
    private Long id;

    /** 关联工人ID */
    @ApiModelProperty("关联工人ID")
    private Long workerId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 人脸库ID */
    @ApiModelProperty("人脸库ID")
    private String fdid;

    /** 人脸库图片ID */
    @ApiModelProperty("人脸库图片ID")
    private String pid;

    /** 图片地址 */
    @ApiModelProperty("图片地址")
    private String picUrl;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("过期时间")
    private Date expireTime;

    /** 删除标志（0代表存在 1代表删除） */
    @ApiModelProperty("删除标志")
    private Long delFlag;

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
