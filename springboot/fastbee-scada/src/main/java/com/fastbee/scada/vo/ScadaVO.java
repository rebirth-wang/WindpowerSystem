package com.fastbee.scada.vo;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 组态页面对象 scada
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaVO", description = "组态页面 scada")
@Data
public class ScadaVO extends PageEntity {

    /** id唯一标识 */
    @Excel(name = "id唯一标识")
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 组态id */
    @Excel(name = "组态id")
    @ApiModelProperty("组态id")
    private String guid;

    /** 组态信息 */
    @Excel(name = "组态信息")
    @ApiModelProperty("组态信息")
    private String scadaData;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumbers;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 是否主界面 */
    @Excel(name = "是否主界面")
    @ApiModelProperty("是否主界面")
    private Integer isMainPage;

    /** 页面名称 */
    @Excel(name = "页面名称")
    @ApiModelProperty("页面名称")
    private String pageName;

    /** 页面大小 */
    @Excel(name = "页面大小")
    @ApiModelProperty("页面大小")
    private String pageResolution;

    /** 是否分享 */
    @Excel(name = "是否分享")
    @ApiModelProperty("是否分享")
    private Integer isShare;

    /** 分享链接 */
    @Excel(name = "分享链接")
    @ApiModelProperty("分享链接")
    private String shareUrl;

    /** 分享密码 */
    @Excel(name = "分享密码")
    @ApiModelProperty("分享密码")
    private String sharePass;

    /** 页面图片 */
    @Excel(name = "页面图片")
    @ApiModelProperty("页面图片")
    private String pageImage;

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
    private Integer delFlag;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 组态类型 1- 模板组态 2- 场景组态 3-公共组态 */
    @Excel(name = "组态类型 1- 模板组态 2- 场景组态 3-公共组态")
    @ApiModelProperty("组态类型 1- 模板组态 2- 场景组态 3-公共组态")
    private Integer type;

    /** 分享短链接 */
    @Excel(name = "分享短链接")
    @ApiModelProperty("分享短链接")
    private String shareShortUrl;

    /**
     * 绑定的产品id
     */
    private Long productId;

    /**
     * 场景id
     */
    private Long sceneModelId;

    private List<ScadaBindDeviceSimVO> bindDeviceList = new ArrayList<>();

    private String base64;

    /**
     * 设备编号
     */
    private String serialNumber;

}
