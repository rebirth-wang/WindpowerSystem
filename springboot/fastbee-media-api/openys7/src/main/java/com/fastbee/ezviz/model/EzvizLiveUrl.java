package com.fastbee.ezviz.model;

import lombok.Data;

/**
 * 萤石云直播流地址信息
 *
 * @author fastbee
 */
@Data
public class EzvizLiveUrl {

    /** ezopen 协议直播地址 */
    private String ezopen;

    /** HLS 直播地址 */
    private String hls;

    /** RTMP 直播地址 */
    private String rtmp;

    /** FLV 直播地址 */
    private String flv;

    /** 有效期截止时间（毫秒时间戳）*/
    private Long expireTime;

}
