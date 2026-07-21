package com.fastbee.scada.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 组态图库对象 scada_gallery
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaGalleryVO", description = "组态图库 scada_gallery")
@Data
public class ScadaGalleryVO extends PageEntity {

    /** 图库ID */
    @Excel(name = "图库ID")
    @ApiModelProperty("图库ID")
    private Long id;

    /** 文件名称 */
    @Excel(name = "文件名称")
    @ApiModelProperty("文件名称")
    private String fileName;

    /** 分类名称 */
    @Excel(name = "分类名称")
    @ApiModelProperty("分类名称")
    private String categoryName;

    /** 资源请求路径 */
    @Excel(name = "资源请求路径")
    @ApiModelProperty("资源请求路径")
    private String resourceUrl;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    private Integer sysFlag;

}
