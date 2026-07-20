package com.fastbee.common.extend.core.card;

import java.util.List;

import lombok.Data;

/**
 * 有人云卡基础信息结果
 *
 * @author fastbee
 * @date 2025/12/17
 */
@Data
public class UsrCardDataPlanResult {

    private Integer status;
    private String desc;
    private List<DataPlanResult> data;

    @Data
    public static class DataPlanResult {
        /**
         * 	套餐id
         */
        private Integer packageId;
        /**
         * 套餐名称
         */
        private String packageName;
        /**
         * 	生效状态，1生效，2未生效，3失效
         */
        private Integer effectStatus;
        /**
         * 套餐生效日期
         */
        private String packageStartDate;
        /**
         * 套餐失效日期
         */
        private String packageEndDate;
        /**
         * 	套餐类型：1主套餐，2测试套餐，3加油包
         */
        private Integer packageType;

    }
}
