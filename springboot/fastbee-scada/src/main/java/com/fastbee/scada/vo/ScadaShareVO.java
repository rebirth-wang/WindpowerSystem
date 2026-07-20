package com.fastbee.scada.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * share对象 scada_device_share
 *
 * @author zhuangpeng.li
 * @date 2024-08-20
 */
@Data
public class ScadaShareVO implements Serializable {
private static final long serialVersionUID=1L;

    /** 组态id */
    private String guid;

    /**
     * 组态类型
     */
    private Integer type;

    /** 设备编号 */
    private String serialNumber;

    /** 是否分享 */
    private Integer isShare;


    /** 分享链接 */
    private String shareUrl;

    /** 分享短链接 */
    private String shareShortUrl;

    /** 分享密码 */
    private String sharePass;

    /**
     * 分享密码状态 0-关闭密码，1-修改密码，2-不变
     */
    private Integer sharePassStatus;

    private Long tenantId;

    private String createBy;

}
