package com.fastbee.isup.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Data
@Profile("isup")
@Component
@ConfigurationProperties(prefix = "hik.isup")
public class HikIsupProperties {
    private String listenIp;
    private String extendIp;
    private String isupKey;
    private String eventInfoPrintType;
    private String cmsPort;
    private String dasPort;
    private String smsPort;
    private String smsBackPort;
    private String voiceSmsPort;
    private AlarmServer alarmServer;
    private PicServer picServer;

    @Data
    public static class AlarmServer {
        private String type;
        private String listenTcpPort;
        private String listenUdpPort;
    }

    @Data
    public static class PicServer {
        private String type;
        private String listenPort;
        private String kmsUserName;
        private String kmsPassword;
        private String accessKey;
        private String secretKey;
    }
}
