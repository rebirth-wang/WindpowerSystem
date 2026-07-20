package com.fastbee.media.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 流媒体服务器配置对象 media_server
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */

@ApiModel(value = "MediaServerVO", description = "流媒体服务器配置 media_server")
@Data
public class MediaServerVO{

    /** 流媒体配置ID */
    @Excel(name = "流媒体配置ID")
    @ApiModelProperty("流媒体配置ID")
    private Long id;

    /** 服务器标识 */
    @Excel(name = "服务器标识")
    @ApiModelProperty("服务器标识")
    private String serverId;

    /** 租户ID */
    @Excel(name = "租户ID")
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty(value = "是否系统通用", notes = "（0-否，1-是）")
    @Excel(name = "是否系统通用", readConverterExp = "0=-否，1-是")
    private Integer isSys;

    /** 使能开关 */
    @Excel(name = "使能开关")
    @ApiModelProperty("使能开关")
    private Integer enabled;

    /** 默认播放协议 */
    @Excel(name = "默认播放协议")
    @ApiModelProperty("默认播放协议")
    private String protocol;

    /** 服务器ip */
    @Excel(name = "服务器ip")
    @ApiModelProperty("服务器ip")
    private String ip;

    /** 服务器域名 */
    @Excel(name = "服务器域名")
    @ApiModelProperty("服务器域名")
    private String domainAlias;

    /** 回调服务器地址 */
    @Excel(name = "回调服务器地址")
    @ApiModelProperty("回调服务器地址")
    private String hookurl;

    /** 流媒体密钥 */
    @Excel(name = "流媒体密钥")
    @ApiModelProperty("流媒体密钥")
    private String secret;

    /** http端口 */
    @Excel(name = "http端口")
    @ApiModelProperty("http端口")
    private Long portHttp;

    /** https端口 */
    @Excel(name = "https端口")
    @ApiModelProperty("https端口")
    private Long portHttps;

    /** rtmp端口 */
    @Excel(name = "rtmp端口")
    @ApiModelProperty("rtmp端口")
    private Long portRtmp;

    /** rtsp端口 */
    @Excel(name = "rtsp端口")
    @ApiModelProperty("rtsp端口")
    private Long portRtsp;

    /** RTP收流端口 */
    @Excel(name = "RTP收流端口")
    @ApiModelProperty("RTP收流端口")
    private Long rtpProxyPort;

    /** 是否使用多端口模式 */
    @Excel(name = "是否使用多端口模式")
    @ApiModelProperty("是否使用多端口模式")
    private Integer rtpEnable;

    /** rtp端口范围 */
    @Excel(name = "rtp端口范围")
    @ApiModelProperty("rtp端口范围")
    private String rtpPortRange;

    /** 录像服务端口 */
    @Excel(name = "录像服务端口")
    @ApiModelProperty("录像服务端口")
    private Long recordPort;

    /** 是否自动同步配置ZLM */
    @Excel(name = "是否自动同步配置ZLM")
    @ApiModelProperty("是否自动同步配置ZLM")
    private Integer autoConfig;

    /** 状态 */
    @Excel(name = "状态")
    @ApiModelProperty("状态")
    private Integer status;

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

    /** ws端口 */
    @Excel(name = "ws端口")
    @ApiModelProperty("ws端口")
    private Long portWs;

}
