package com.fastbee.isup.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChanStatusObj {
    @JsonProperty("ChanStatusList")
    private ChanStatusList ChanStatusList;

    @Data
    public static class ChanStatusList {
        @JsonProperty("ChanStatus")
        private List<ChanStatus> ChanStatus;
        @JsonProperty("HDStatus")
        private List<HDStatus> HDStatus;
        @JsonProperty("IOStatus")
        private IOStatus IOStatus;
    }

    @Data
    public static class ChanStatus {
        private Integer chanNo;
        private String name;
        private Integer online;
        private Integer record;
        private Integer recordStatus;
        private Integer signal;
        private Integer linkNum;
        private Integer bitRate;
        private List<String> ClientIPList;
    }

    @Data
    public static class HDStatus {
        @JsonProperty("HdNo")
        private Integer HdNo;
        private Integer status;
        private Integer volume;
        private Integer freeSpace;
    }

    @Data
    public static class IOStatus {
        private List<String> IOInTrig;
        private List<String> IOOutTrig;
    }

}
