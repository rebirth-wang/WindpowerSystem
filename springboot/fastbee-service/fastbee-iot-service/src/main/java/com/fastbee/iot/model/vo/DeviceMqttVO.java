package com.fastbee.iot.model.vo;

/**
 * MQTT连接相关参数实体类
 * @author fastb
 * @date 2023-08-02 17:09
 */
public class DeviceMqttVO {

    /**
     * 设备主键id
     */
    private Long deviceId;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 用户id
     */
    private Long tenantId;

    /**
     * mqtt连接账号
     */
    private String account;

    /**
     * mqtt连接密码
     */
    private String authPassword;

    /**
     * 产品密匙
     */
    private String secret;

    /**
     * 是否启用授权码（0-否，1-是）
     */
    private Integer isAuthorize;

    private String transport;

    /**
     * 认证方式
     */
    private Integer vertificateMethod;

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public Integer getVertificateMethod() {
        return vertificateMethod;
    }

    public void setVertificateMethod(Integer vertificateMethod) {
        this.vertificateMethod = vertificateMethod;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getIsAuthorize() {
        return isAuthorize;
    }

    public void setIsAuthorize(Integer isAuthorize) {
        this.isAuthorize = isAuthorize;
    }
}
