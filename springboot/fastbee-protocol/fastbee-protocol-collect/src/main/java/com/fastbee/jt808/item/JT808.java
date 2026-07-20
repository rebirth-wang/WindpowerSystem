package com.fastbee.jt808.item;


public interface JT808 {

    int DEVICE_COMMON_RESPONSE = 0x0001; // 平台通用应答
    int 终端心跳 = 0x0002;         // 终端心跳
    int 终端注销 = 0x0003;         // 终端注销
    int 终端补传分包请求 = 0x0005;  // 终端补传分包请求
    int REGISTRATION = 0x0100;   // 终端注册
    int AUTHENTICATION = 0x0102;        // 终端鉴权
    int PARAM_RESPONSE = 0x0104;  // 查询终端参数应答
    int ATTRI_RESPONSE = 0x0107;  // 查询终端属性应答
    int UPGRADE_RESULT = 0x0108;  // 终端升级结果通知
    int LOCALTION = 0x0200;     // 位置信息汇报
    int 位置信息查询应答 = 0x0201;  // 位置信息查询应答
    int 车辆控制应答 = 0x0500;     // 车辆控制应答
    int 行驶记录数据上传 = 0x0700;  // 行驶记录数据上传
    int 定位数据批量上传 = 0x0704;  // 定位数据批量上传


    int PLAT_COMMON_RESPONSE = 0x8001; //平台通用应答
    int SPLIT_BACKAGE_UP = 0x8003;//服务器补传分包请求
    int 查询服务器时间应答 = 0x8004;          // 查询服务器时间应答
    int DEVICE_REGISTER_RESPONSE = 0x8100; //设备注册应答

    int 终端控制 = 0x8105;         // 终端控制
    int 查询指定终端参数 = 0x8106;  // 查询指定终端参数

}
