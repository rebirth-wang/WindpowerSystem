//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.redis;

public class RedisKeyBuilder {
    public static String buildDeviceOnlineListKey() {
        return "device:online:list";
    }

    public static String buildDeviceRtCacheKey(String serialNumber) {
        return "device:runtime:" + serialNumber;
    }

    public static String buildDeviceRtParamsKey(String serialNumber) {
        return "device:param:" + serialNumber;
    }

    public static String buildFirmwareCachedKey(Long firmwareId) {
        return "device:firmware:" + firmwareId;
    }

    public static String buildPropReadCacheKey(String serialNumber) {
        return "prop:read:store:" + serialNumber;
    }

    public static String buildTSLVCacheKey(Long productId, String serialNumber) {
        return "TSLV:" + productId + "_" + serialNumber;
    }

    public static String buildTSLCacheKey(Long productId) {
        return "TSL:" + productId;
    }

    public static String buildModbusKey(Long productId) {
        return "MODBUS:" + productId;
    }

    public static String buildModbusKey(Long productId, String address) {
        return "MODBUS:" + productId + "_" + address;
    }

    public static String buildSipRecordinfoCacheKey(String recordKey) {
        return "sip:recordinfo:" + recordKey;
    }

    public static String buildSipDeviceidCacheKey(String id) {
        return "sip:deviceidset:" + id;
    }

    public static String buildSipChannelidCacheKey(String id) {
        return "sip:channelset:" + id;
    }

    public static String buildStreamCacheKey(String steamId) {
        return "sip:stream:" + steamId;
    }

    public static String buildStreamCacheKey(String deviceId, String channelId, String stream, String ssrc) {
        return "sip:stream:" + deviceId + ":" + channelId + ":" + stream + ":" + ssrc;
    }

    public static String buildInviteCacheKey(String type, String deviceId, String channelId, String stream, String ssrc) {
        return "sip:invite:" + type + ":" + deviceId + ":" + channelId + ":" + stream + ":" + ssrc;
    }

    public static String buildProxyCacheKey(String type, String stream) {
        return "sip:proxy:" + type + ":" + stream;
    }

    public static String buildSipCSEQCacheKey(String CSEQ) {
        return "sip:CSEQ:" + CSEQ;
    }

    public static String buildSilentTimeacheKey(String key) {
        return "rule:SilentTime" + key;
    }

    public static String buildModbusPollCacheKey(String serialNumebr) {
        return "MODBUS:POLL:" + serialNumebr;
    }

    public static String buildDownMessageIdCacheKey(String serialNumber) {
        return "device:messageId:" + serialNumber;
    }

    public static String buildDeviceMsgCacheKey(String serialNumber) {
        return "device:msg:" + serialNumber;
    }

    public static String buildSceneModelTagCacheKey(Long sceneModelId) {
        return "SMTV:" + sceneModelId;
    }

    public static String buildModbusRuntimeCacheKey(String serialNumber) {
        return "MODBUS:RUNTIME:" + serialNumber;
    }

    public static String buildModbusLockCacheKey(String serialNumber) {
        return "MODBUS:LOCK:" + serialNumber;
    }

    public static String buildModbusTcpCacheKey(String serialNumber) {
        return "MODBUS:TCP:" + serialNumber;
    }

    public static String buildModbusTcpRuntimeCacheKey(String serialNumber) {
        return "MODBUS:TCP:RUNTIME:" + serialNumber;
    }

    public static String buildDeviceOtaKey(String serialNumber) {
        return "device:ota:" + serialNumber;
    }
}
