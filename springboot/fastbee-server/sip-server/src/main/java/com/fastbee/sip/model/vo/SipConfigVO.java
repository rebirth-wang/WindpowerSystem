package com.fastbee.sip.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * sip系统配置对象 sip_config
 *
 * @author gx_ma
 * @date 2024-12-24
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SipConfigVO", description = "sip系统配置 sip_config")
@Data
public class SipConfigVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 使能开关 */
    @Excel(name = "使能开关")
    @ApiModelProperty("使能开关")
    private Integer enabled;

    /** 系统默认配置 */
    @Excel(name = "系统默认配置")
    @ApiModelProperty("系统默认配置")
    private Integer isdefault;

    /** 拓展sdp */
    @Excel(name = "拓展sdp")
    @ApiModelProperty("拓展sdp")
    private Integer seniorSdp;

    /** 服务器域 */
    @Excel(name = "服务器域")
    @ApiModelProperty("服务器域")
    private String domainAlias;

    /** 服务器sipid */
    @Excel(name = "服务器sipid")
    @ApiModelProperty("服务器sipid")
    private String serverSipid;

    /** sip认证密码 */
    @Excel(name = "sip认证密码")
    @ApiModelProperty("sip认证密码")
    private String password;

    /** sip接入IP */
    @Excel(name = "sip接入IP")
    @ApiModelProperty("sip接入IP")
    private String ip;

    /** sip接入端口号 */
    @Excel(name = "sip接入端口号")
    @ApiModelProperty("sip接入端口号")
    private Integer port;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
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

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty(value = "是否系统通用", notes = "（0-否，1-是）")
    @Excel(name = "是否系统通用", readConverterExp = "0=-否，1-是")
    private Integer isSys;

    /** 自定义代码区域 END**/
}
