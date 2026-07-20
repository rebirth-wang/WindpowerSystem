package com.fastbee.icc.model.ipms.blackWhiteListManage;

import java.util.List;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className QueryTaskStatusResponse
 * @Author 355079
 * @Date 2024/12/9
 * @Description 设备白名单状态查询返回结果
 */
@Data
public class QueryTaskStatusResponse extends IccResponse {
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
         * 白名单任务id
         */
        private Long id;
        /**
         * 相机设备编码
         */
        private String deviceCode;
        /**
         * 相机设备名称
         */
        private String deviceName;
        /**
         * 车牌号
         */
        private String carNum;
        /**
         * 有效开始时间
         */
        private String validStartDay;
        /**
         * 有效结束时间
         */
        private String validEndDay;
        /**
         * 任务状态（0-正在下发；1-下发成功；2-下发失败，3-超时，4-服务离线，5-设备离线）
         */
        private Integer stat;
        /**
         * 任务状态描述
         */
        private String statStr;
        /**
         * 操作类型（0-删除，1-增加，2-修改）
         */
        private Integer operType;
        /**
         * 操作类型描述
         */
        private String operTypeStr;
        /**
         * 操作时间
         */
        private String operTime;
        /**
         * 操作时间（格式：yyyy-MM-dd HH:mm:ss）
         */
        private String operTimeStr;

    }
}
