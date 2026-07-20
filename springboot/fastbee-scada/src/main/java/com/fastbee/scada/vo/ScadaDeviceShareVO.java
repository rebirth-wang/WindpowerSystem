package com.fastbee.scada.vo;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 【请填写功能名称】对象 scada_device_share
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaDeviceShareVO", description = "【请填写功能名称】 scada_device_share")
@Data
public class ScadaDeviceShareVO extends PageEntity {

    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 组态id */
    @Excel(name = "组态id")
    @ApiModelProperty("组态id")
    private String guid;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

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

    /** 分享短链接 */
    @Excel(name = "分享短链接")
    @ApiModelProperty("分享短链接")
    private String shareShortUrl;


}
