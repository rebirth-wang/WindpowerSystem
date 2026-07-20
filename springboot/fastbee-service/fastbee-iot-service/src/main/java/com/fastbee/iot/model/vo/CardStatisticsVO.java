package com.fastbee.iot.model.vo;

import java.util.List;

import lombok.Data;

/**
 * 卡片统计vo
 *
 * @author fastbee
 * @date 2025/12/11
 */
@Data
public class CardStatisticsVO {

    /**
     * 总卡数
     */
    private Integer totalCount;
    /**
     * 正常数
     */
    private Integer normalCount;
    /**
     * 待激活计数
     */
    private Integer pendingActivationCount;
    /**
     * 停机数
     */
    private Integer shutdownCount;
    /**
     * 销号数
     */
    private Integer accountTerminationCount;
    /**
     * 未知数
     */
    private Integer unknownCount;

    /**
     * 运营商分布
     */
    private List<OperatorVO> operatorVOList;
    /**
     * 卡平台分布
     */
    private List<PlatformVO> platformVOList;

    private List<CardVO> cardVOList;

    @Data
    public static class PlatformVO {
        private String platform;

        private String name;

        private Integer count;
    }

    @Data
    public static class OperatorVO {
        private String operator;

        private String name;

        private Integer count;
    }
}
