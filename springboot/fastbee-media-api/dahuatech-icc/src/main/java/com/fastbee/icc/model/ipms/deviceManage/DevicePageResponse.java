package com.fastbee.icc.model.ipms.deviceManage;

import java.util.List;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className DevicePageResponse
 * @Author 355079
 * @Date 2024/12/10
 * @Description 设备列表查询接口返回结果
 */
@Data
public class DevicePageResponse extends IccResponse {

    private Data data;

    @lombok.Data
    public static class Data {

        /**
         * 当前页
         */
        private Integer currentPage;
        /**
         * 每页条数
         */
        private Integer pageSize;
        /**
         * 总页数
         */
        private Integer totalPage;
        /**
         * 总条数
         */
        private Integer totalRows;
        /**
         * 分页数据
         */
        private List<PageData> pageData;

        @lombok.Data
        public static class PageData {
            /**
             * 设备id
             */
            private Long id;
            /**
             * 设备编码
             */
            private String deviceCode;
            /**
             * 设备名称
             */
            private String deviceName;
            /**
             * 设备名称描述
             */
            private String deviceCategoryStr;
            /**
             * 设备类型
             */
            private String deviceType;
            /**
             * 设备类型描述
             */
            private String deviceTypeStr;
            /**
             * 厂商类型
             */
            private String deviceManufacturer;
            /**
             * 厂商类型描述
             */
            private String deviceManufacturerStr;
            /**
             * 设备厂商伪装类型
             */
            private String deviceManufacturerDisguiseType;
            /**
             * 设备ip
             */
            private String deviceIp;
            /**
             * 设备端口
             */
            private Integer devicePort;
            /**
             * 组织编码
             */
            private String ownerCode;
            /**
             * 组织名称
             */
            private String orgName;
            /**
             * 设备添加方式 1：ip添加 6：主动注册
             */
            private String loginType;
            /**
             * 设备用户名
             */
            private String loginName;
            /**
             * 注册服务id
             */
            private Long registServiceId;
            /**
             * 视频服务编码
             */
            private String videoServerCode;
            /**
             * 图片服务信息
             */
            private String pictureServerCode;
            /**
             * 是否在线 0：离线 1：在线
             */
            private Integer isOnline;
            /**
             * 在线描述
             */
            private String isOnlineStr;
            /**
             * 创建时间
             */
            private String createDate;
            /**
             * 更新时间
             */
            private String updateDate;
            /**
             * 是否添加道闸
             */
            private Boolean isAddSluice;
            /**
             * 注册服务名称
             */
            private String registServiceName;
            /**
             * 是否管理-fales:否，true:是
             */
            private Boolean administered;
            /**
             * 设备序列号
             */
            private String serialNumber;
            /**
             * mac地址
             */
            private String macNumber;
            /**
             * 通道类型
             */
            private String channelTypes;
            /**
             * 是否是大华设备 -fales:否，true:是
             */
            private Boolean dhMachine;
            /**
             * 编码单元
             */
            private List<encoderUnit> encoderUnits;
            /**
             * 显示设备单元
             */
            private List<ledUnit> ledUnits;
            /**
             * 道闸通道单元
             */
            private List<sluiceUnit> sluiceUnits;

            @lombok.Data
            public static class encoderUnit {
                /**
                 * 通道类型
                 */
                private Integer channelType;
                /**
                 * 设备id
                 */
                private Long id;
                /**
                 * 设备编码
                 */
                private String deviceCode;
                /**
                 * 单元序号
                 */
                private Integer unitSeq;
                /**
                 * 通道数量
                 */
                private Integer channelNum;
                /**
                 * 辅码流
                 */
                private Integer assistantStream;
                /**
                 * 零通道编码
                 */
                private Integer zeroChnEncode;
                /**
                 * pts服务id
                 */
                private Long ptsServiceId;
                /**
                 * pts服务名称
                 */
                private String ptsServiceName;
                /**
                 * 编码通道
                 */
                private List<EncodeChannel> channels;

                @lombok.Data
                public static class EncodeChannel {
                    /**
                     * 设备id
                     */
                    private Long id;
                    /**
                     * 通道名称
                     */
                    private String channelName;
                    /**
                     * 相机类型
                     */
                    private Integer cameraType;
                    /**
                     * 通道类型
                     */
                    private String cameraFunctions;
                    /**
                     * 通道sn
                     */
                    private String channelSn;
                    /**
                     * 通道类型
                     */
                    private Integer channelType;
                    /**
                     * 设备类型
                     */
                    private String type;
                }
            }

            @lombok.Data
            public static class ledUnit {
                /**
                 * 通道类型
                 */
                private Integer channelType;
                /**
                 * 设备id
                 */
                private Long id;
                /**
                 * 设备编码
                 */
                private String deviceCode;
                /**
                 * 通道数量
                 */
                private Integer channelNum;
                /**
                 * 注册服务id
                 */
                private String registServiceId;
                /**
                 * 代理端口
                 */
                private Integer registProxyPort;
                /**
                 * 显示通道
                 */
                private List<LedChannel> channels;

                @lombok.Data
                public static class LedChannel {
                    /**
                     * 设备id
                     */
                    private Long id;
                    /**
                     * 通道名称
                     */
                    private String channelName;
                    /**
                     * LED设备类型
                     */
                    private Integer ledType;
                    /**
                     * 通道sn
                     */
                    private String channelSn;
                    /**
                     * 显示设备编码
                     */
                    private String deviceCode;
                    /**
                     * 单元序号
                     */
                    private Integer unitSeq;
                    /**
                     * 通道序号
                     */
                    private Integer channelSeq;
                    /**
                     * 设备类型
                     */
                    private String type;
                }
            }

            @lombok.Data
            public static class sluiceUnit {
                /**
                 * 通道类型
                 */
                private Integer channelType;
                /**
                 * 通道数量
                 */
                private Integer channelNum;
                /**
                 * 注册服务id
                 */
                private String registServiceId;
                /**
                 * 代理端口
                 */
                private Integer registProxyPort;
                /**
                 * 道闸通道
                 */
                private List<SluiceChannel> channels;

                @lombok.Data
                public static class SluiceChannel {
                    /**
                     * 设备id
                     */
                    private Long id;
                    /**
                     * 通道名称
                     */
                    private String channelName;
                    /**
                     * 道闸设备编码
                     */
                    private String deviceCode;
                    /**
                     * 通道sn
                     */
                    private String channelSn;
                    /**
                     * 单元序号
                     */
                    private Integer unitSeq;
                    /**
                     * 通道序号
                     */
                    private String channelSeq;
                }
            }
        }
    }
}
