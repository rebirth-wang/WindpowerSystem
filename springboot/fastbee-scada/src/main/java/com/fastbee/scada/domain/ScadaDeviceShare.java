package com.fastbee.scada.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 【请填写功能名称】对象 scada_device_share
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaDeviceShare", description = "【请填写功能名称】 scada_device_share")
@Data
@TableName("scada_device_share" )
public class ScadaDeviceShare extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 组态id */
    @ApiModelProperty("组态id")
    private String guid;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 是否分享 */
    @ApiModelProperty("是否分享")
    private Integer isShare;

    /** 分享链接 */
    @ApiModelProperty("分享链接")
    private String shareUrl;

    /** 分享密码 */
    @ApiModelProperty("分享密码")
    private String sharePass;

    /** 分享短链接 */
    @ApiModelProperty("分享短链接")
    private String shareShortUrl;

}
