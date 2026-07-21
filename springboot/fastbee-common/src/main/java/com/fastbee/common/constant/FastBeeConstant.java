//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.constant;

public interface FastBeeConstant {
    public interface SERVER {
        String UFT8 = "UTF-8";
        String GB2312 = "GB2312";
        String MQTT = "mqtt";
        String PORT = "port";
        String ADAPTER = "adapter";
        String FRAMEDECODER = "frameDecoder";
        String DISPATCHER = "dispatcher";
        String DECODER = "decoder";
        String ENCODER = "encoder";
        String MAXFRAMELENGTH = "maxFrameLength";
        String SLICER = "slicer";
        String DELIMITERS = "delimiters";
        String IDLE = "idle";
        String WS_PREFIX = "web-";
        String WM_PREFIX = "server-";
        String FAST_PHONE = "phone-";
        Long DEVICE_PING_EXPIRED = 90000L;
    }

    public interface CHANNEL {
        String DEVICE_STATUS = "device_status";
        String PROP_READ = "prop_read";
        String PUBLISH = "publish";
        String FUNCTION_INVOKE = "function_invoke";
        String EVENT = "event";
        String OTHER = "other";
        String PUBLISH_ACK = "publish_ack";
        String PUB_REC = "pub_rec";
        String PUB_REL = "pub_rel";
        String PUB_COMP = "pub_comp";
        String UPGRADE = "upgrade";
        String SUFFIX = "group";
        String DEVICE_STATUS_GROUP = "device_statusgroup";
        String PROP_READ_GROUP = "prop_readgroup";
        String FUNCTION_INVOKE_GROUP = "function_invokegroup";
        String PUBLISH_GROUP = "publishgroup";
        String PUBLISH_ACK_GROUP = "publish_ackgroup";
        String PUB_REC_GROUP = "pub_recgroup";
        String PUB_REL_GROUP = "pub_relgroup";
        String PUB_COMP_GROUP = "pub_compgroup";
        String UPGRADE_GROUP = "upgradegroup";
    }

    public interface CLIENT {
        String TOKEN = "fastbee-smart!@#$123";
    }

    public interface MQTT {
        String PREDIX = "/+/+";
        String ONE_PREDIX = "/+";
        String OTA_REPLY = "/upgrade/reply";
    }

    public interface PROTOCOL {
        String ModbusRtu = "MODBUS-RTU";
        String YinErDa = "YinErDa";
        String JsonObject = "JSONOBJECT";
        String JsonArray = "JSON";
        String ModbusRtuPak = "MODBUS-RTU-PAK";
        String NetOTA = "OTA-NET";
        String FlowMeter = "FlowMeter";
        String RJ45 = "RJ45";
        String ModbusToJson = "MODBUS-JSON";
        String ModbusToJsonHP = "MODBUS-JSON-HP";
        String ModbusToJsonZQWL = "MODBUS-JSON-ZQWL";
        String JsonObject_ChenYi = "JSONOBJECT-CHENYI";
        String GEC6100D = "MODBUS-JSON-GEC6100D";
        String SGZ = "SGZ";
        String CH = "CH";
        String ModbusTcp = "MODBUS-TCP";
        String ModbusTcpOverRtu = "MODBUS-TCP-OVER-RTU";
        String JsonGateway = "JSON-GATEWAY";
    }

    public interface REDIS {
        String DEVICE_ONLINE_LIST = "device:online:list";
        String DEVICE_RUNTIME_DATA = "device:runtime:";
        String DEVICE_PROTOCOL_PARAM = "device:param:";
        String DEVICE_MESSAGE_ID = "device:messageId:";
        String FIRMWARE_VERSION = "device:firmware:";
        String DEVICE_MSG = "device:msg:";
        String PROP_READ_STORE = "prop:read:store:";
        String RECORDINFO_KEY = "sip:recordinfo:";
        String DEVICEID_KEY = "sip:deviceidset:";
        String CHANNELID_KEY = "sip:channelset:";
        String STREAM_KEY = "sip:stream:";
        String INVITE_KEY = "sip:invite:";
        String PROXY_KEY = "sip:proxy:";
        String SIP_CSEQ_PREFIX = "sip:CSEQ:";
        String DEFAULT_SIP_CONFIG = "sip:config";
        String DEFAULT_MEDIA_CONFIG = "sip:mediaconfig";
        String SENDRTP_KEY = "sip:sendrtp:";
        String SENDRTP_PORT_KEY = "sip:sendrtp:port:";
        String SENDRTP_CALLID_KEY = "sip:sendrtp:callid:";
        String SENDRTP_STREAM_KEY = "sip:sendrtp:stream:";
        String SENDRTP_CHANNEL_KEY = "sip:sendrtp:channel:";
        String RULE_SILENT_TIME = "rule:SilentTime";
        String MESSAGE_RETAIN_TOTAL = "message:retain:total";
        String MESSAGE_SEND_TOTAL = "message:send:total";
        String MESSAGE_RECEIVE_TOTAL = "message:receive:total";
        String MESSAGE_CONNECT_TOTAL = "message:connect:total";
        String MESSAGE_AUTH_TOTAL = "message:auth:total";
        String MESSAGE_SUBSCRIBE_TOTAL = "message:subscribe:total";
        String MESSAGE_RECEIVE_TODAY = "message:receive:today";
        String MESSAGE_SEND_TODAY = "message:send:today";
        String DEVICE_PRE_KEY = "TSLV:";
        String TSL_PRE_KEY = "TSL:";
        String MODBUS_PRE_KEY = "MODBUS:";
        String POLL_MODBUS_KEY = "MODBUS:POLL:";
        String MODBUS_RUNTIME = "MODBUS:RUNTIME:";
        String MODBUS_LOCK = "MODBUS:LOCK:";
        String NOTIFY_WECOM_APPLY_ACCESSTOKEN = "notify:wecom:apply:";
        String SCENE_MODEL_TAG_ID = "SMTV:";
        String MODBUS_TCP = "MODBUS:TCP:";
        String MODBUS_TCP_RUNTIME = "MODBUS:TCP:RUNTIME:";
        String DEVICE_OTA_DATA = "device:ota:";
    }

    public interface TASK {
        String DEVICE_STATUS_TASK = "deviceStatusTask";
        String DEVICE_UP_MESSAGE_TASK = "deviceUpMessageTask";
        String DEVICE_REPLY_MESSAGE_TASK = "deviceReplyMessageTask";
        String DEVICE_DOWN_MESSAGE_TASK = "deviceDownMessageTask";
        String FUNCTION_INVOKE_TASK = "functionInvokeTask";
        String DEVICE_FETCH_PROP_TASK = "deviceFetchPropTask";
        String DEVICE_OTHER_TASK = "deviceOtherMsgTask";
        String DEVICE_TEST_TASK = "deviceTestMsgTask";
        String MESSAGE_CONSUME_TASK = "messageConsumeTask";
        String MESSAGE_CONSUME_TASK_PUB = "messageConsumeTaskPub";
        String MESSAGE_CONSUME_TASK_FETCH = "messageConsumeTaskFetch";
        String DELAY_UPGRADE_TASK = "delayUpgradeTask";
        String OTA_THREAD_POOL = "otaThreadPoolTaskExecutor";
    }

    public interface TRANSPORT {
        String MQTT = "MQTT";
        String TCP = "TCP";
        String COAP = "COAP";
        String UDP = "UDP";
        String GB28181 = "GB28181";
    }

    public interface URL {
        String WX_MINI_PROGRAM_PUSH_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
        String WX_GET_ACCESS_TOKEN_URL_PREFIX = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String WX_MINI_PROGRAM_GET_USER_SESSION_URL_PREFIX = "https://api.weixin.qq.com/sns/jscode2session";
        String WX_MINI_PROGRAM_GET_ACCESS_TOKEN_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
        String WX_GET_USER_INFO_URL_PREFIX = "https://api.weixin.qq.com/sns/userinfo";
        String WX_GET_USER_PHONE_URL_PREFIX = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";
        String WECOM_GET_ACCESSTOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        String WECOM_APPLY_SEND = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
        String WX_PUBLIC_ACCOUNT_GET_USER_INFO_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/user/info";
        String WX_PUBLIC_ACCOUNT_TEMPLATE_SEND_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    }

    public interface WS {
        String HEART_BEAT = "heartbeat";
        String HTTP_SERVER_CODEC = "httpServerCodec";
        String AGGREGATOR = "aggregator";
        String COMPRESSOR = "compressor";
        String PROTOCOL = "protocol";
        String MQTT_WEBSOCKET = "mqttWebsocket";
        String DECODER = "decoder";
        String ENCODER = "encoder";
        String BROKER_HANDLER = "brokerHandler";
    }
}
