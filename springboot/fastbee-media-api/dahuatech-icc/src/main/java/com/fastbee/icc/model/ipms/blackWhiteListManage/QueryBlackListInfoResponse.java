package com.fastbee.icc.model.ipms.blackWhiteListManage;

import java.util.List;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className QueryWhiteListInfoResponse
 * @Author 355079
 * @Date 2024/12/9
 * @Description 查询黑名单信息返回结果
 */
@Data
public class QueryBlackListInfoResponse extends IccResponse {
    private Data data;
    @lombok.Data
    public static class Data{
        /**
         * 当前页码
         */
        private String currentPage;
        /**
         * 总页数
         */
        private String totalPage;
        /**
         * 每页条数
         */
        private Integer pageSize;
        /**
         * 总条数
         */
        private String totalRows;
        /**
         * 返回页数据对象
         */
        private List<PageData> pageData;
        @lombok.Data
        public static class PageData{
            /**
             * 数据id
             */
            private Long id;
            /**
             * 车辆id
             */
            private Long carId;
            /**
             * 设备编码
             */
            private String deviceCode;
            /**
             * 有效期开始时间（格式yyyy-MM-dd）
             */
            private String validStartDay;
            /**
             * 有效期结束时间（格式yyyy-MM-dd）
             */
            private String validEndDay;
            /**
             * 车牌号
             */
            private String carNum;

        }
    }
}
