package com.fastbee.media.model;

import lombok.Data;

@Data
public class CommonChannel {
    private String deviceId;
    private String channelId;
    private Integer streamType;

    // 国标-数据库自增ID
    private int gbId;

    // 国标-编码
    private String gbDeviceId;

    // 国标-名称
    private String gbName;

    // 国标-设备厂商
    private String gbManufacturer;

    // 国标-设备型号
    private String gbModel;

    // 国标-设备归属
    private String gbOwner;

    // 国标-行政区域
    private String gbCivilCode;

    // 国标-警区
    private String gbBlock;

    // 国标-安装地址
    private String gbAddress;

    // 国标-是否有子设备
    private Integer gbParental;

    // 国标-父节点ID
    private String gbParentId;

    // 2016
    // 国标-信令安全模式
    private Integer gbSafetyWay;

    // 国标-注册方式
    private Integer gbRegisterWay;

//    // 2016
//    // 国标-证书序列号
//    private String gbCertNum;
//
//    // 2016
//    // 国标-证书有效标识
//    private Integer gbCertifiable;
//
//    // 2016
//    // 国标-无效原因码(有证书且证书无效的设备必选)
//    private Integer gbErrCode;
//
//    // 2016
//    // 国标-证书终止有效期(有证书且证书无效的设备必选)
//    private String gbEndTime;

//    // 国标-保密属性(必选)缺省为0;0-不涉密,1-涉密
//    private Integer gbSecrecy;

    // 国标-设备/系统IPv4/IPv6地址
    private String gbIpAddress;

    // 国标-设备/系统端口
    private Integer gbPort;

    // 国标-设备口令
    private String gbPassword;

    // 国标-设备状态
    private String gbStatus;

    // 国标-经度 WGS-84坐标系
    private Double gbLongitude;

    // 国标-纬度 WGS-84坐标系
    private Double gbLatitude;

    private Double gpsAltitude;

    private Double gpsSpeed;

    private Double gpsDirection;

    private String gpsTime;

    // 国标-虚拟组织所属的业务分组ID
    private String gbBusinessGroupId;

    // 国标-摄像机结构类型,标识摄像机类型: 1-球机; 2-半球; 3-固定枪机; 4-遥控枪机;5-遥控半球;6-多目设备的全景/拼接通道;" +
    //            "7-多目设备的分割通道; 99-移动设备（非标）98-会议设备（非标）
    private Integer gbPtzType;

    // 2016
    // -摄像机位置类型扩展。1-省际检查站、2-党政机关、3-车站码头、4-中心广场、5-体育场馆、6-商业中心、7-宗教场所、" +
    //            "8-校园周边、9-治安复杂区域、10-交通干线。当目录项为摄像机时可选。
    private Integer gbPositionType;

    // 国标-摄像机安装位置室外、室内属性。1-室外、2-室内。
    private Integer gbRoomType;

    // 2016
    // 国标-用途属性
    private Integer gbUseType;

    // 国标-摄像机补光属性。1-无补光;2-红外补光;3-白光补光;4-激光补光;9-其他"
    private Integer gbSupplyLightType;

    // 国标-摄像机监视方位(光轴方向)属性。1-东(西向东)、2-西(东向西)、3-南(北向南)、4-北(南向北)、" +
    //            "5-东南(西北到东南)、6-东北(西南到东北)、7-西南(东北到西南)、8-西北(东南到西北)
    private Integer gbDirectionType;

    // 国标-摄像机支持的分辨率,可多值
    private String gbResolution;

    // 国标-下载倍速(可选),可多值
    private String gbDownloadSpeed;

    // 国标-空域编码能力,取值0-不支持;1-1级增强(1个增强层);2-2级增强(2个增强层);3-3级增强(3个增强层)
    private Integer gbSvcSpaceSupportMod;

    // 国标-时域编码能力,取值0-不支持;1-1级增强;2-2级增强;3-3级增强(可选)
    private Integer gbSvcTimeSupportMode;

    // 二进制保存的录制计划, 每一位表示每个小时的前半个小时
    private Long recordPLan;

    // 关联的数据类型
    private Integer dataType;

    // 关联的设备ID
    private Integer dataDeviceId;

    // 流唯一编号，存在表示正在直播"
    private String  streamId;

    // 是否支持对讲 1支持,0不支持
    private Integer enableBroadcast;
}
