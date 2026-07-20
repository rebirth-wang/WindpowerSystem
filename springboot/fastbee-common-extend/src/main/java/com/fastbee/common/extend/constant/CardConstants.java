package com.fastbee.common.extend.constant;

/**
 * @author zzy
 * @description: 物联网卡常量类
 * @date 2025-11-19 11:16
 */
public class CardConstants {

    public static final String SUCCESS_STATUS = "0";
    public static final Integer SUCCESS_STATUS_NUM = 0;
    public static final Integer HAND_SHAKE_SUCCESS_STATUS = 1;
    public static final String UNICOM_SUCCESS_STATUS = "0000";

    public static final String MOBILE_OPENLINK_PB = "OneLink-PB";
    public static final String MOBILE_OPENLINK_CT = "OneLink-CT";

    // ****** 中国移动api ********************

    /**
     * 获取token
     */
    public static final String MOBILE_TOKEN = "card:mobile_token:";
    public static final String MOBILE_AUTH_SERVER = "/ec/get/token?appid={}&password={}&transid={}";
    public static final String MOBILE_SINGLE_INFO_QUERY = "/ec/query/sim-basic-info?transid={}&token={}&iccid={}";
    public static final String MOBILE_SINGLE_INFO_QUERY_DEV = "/ec/query/sim-basic-info?&token={}&iccid={}";
    public static final String MOBILE_BATCH_INFO_QUERY = "/ec/query/sim-card-info/batch?transid={}&token={}&iccids={}";
    public static final String MOBILE_PLATFORM_BATCH_QUERY = "/ec/query/sim-platform/batch?transid={}&token={}&iccids={}";
    public static final String MOBILE_PLATFORM_BATCH_QUERY_DEV = "/ec/query/sim-platform/batch?token={}&iccids={}";
    public static final String MOBILE_STATUS_QUERY = "/ec/query/sim-status?transid={}&token={}&iccid={}";
    public static final String MOBILE_STATUS_QUERY_DEV = "/ec/query/sim-status?&token={}&iccid={}";
    public static final String MOBILE_MONTH_FLOW_QUERY = "/ec/query/sim-data-margin?transid={}&token={}&iccid={}";
    public static final String MOBILE_MONTH_FLOW_QUERY_DEV = "/ec/query/sim-data-margin?token={}&iccid={}";

    // ****** 电信CMP5G API 接口 ****************
    /**
     * 接入号码查询
     */
    public static final String TELECOM_ACCESS_NUMBER_QUERY = "/openapi/v1/prodinst/getTelephone";
    /**
     * 电信设备识别码查询
     */
    public static final String TELECOM_DEVICE_IDENTIFY_CODE_QUERY = "/openapi/v1/prodinst/gete2einfo";
    /**
     * 电信三码查询
     */
    public static final String TELECOM_THREE_YARDS_QUERY = "/api/v1/dac/connect/getImsiInfo";
    /**
     * 电信流量查询
     */
    public static final String TELECOM_TRAFFIC_QUERY = "/api/v1/openbill/queryTraffic";
    /**
     * 卡主状态查询
     */
    public static final String TELECOM_STATUS_QUERY = "/openapi/v1/prodinst/queryCardMainStatus";
    /**
     * 已订购产品查询
     */
    public static final String TELECOM_COMBO_QUERY = "/api/v1/prod/qryProdInstInfo/{}";

    // ******** 中国联通api ********
    /**
     * 查询物联卡详情
     */
    public static final String UNICOM_BATCH_INFO_QUERY = "/wsGetTerminalDetails/V1/1Main/vV1.1";

    // ******** 有人云api ********
    /**
     * 有人云token redis
     */
    public static final String USR_TOKEN_REDIS = "card:usr_token:";
    /**
     * 有人云获取密钥token
     */
    public static final String USR_TOKEN_QUERY = "/api/development/loginCommon";
    /**
     * 有人云获取卡详情信息
     */
    public static final String USR_INFO_QUERY = "/api/development/getCardInfo";
    /**
     * 有人云获取卡内套餐列表
     */
    public static final String USR_DATA_PLAN_QUERY = "/api/development/getAssetPackageList";

    // ******** 智宇物联api ********
    /**
     * 卡信息查询
     */
    public static final String ZHI_YU_INFO_QUERY = "api.v2.card.info";
    /**
     * 测试卡信息查询
     */
    public static final String ZHI_YU_INFO_TEST_QUERY = "api.v2.trial.card.info";

    // ******** 握手物联api ********
    /**
     * 单卡信息查询
     */
    public static final String HAND_SHAKE_INFO_QUERY = "/interfaces/v1/card/info.action";

}
