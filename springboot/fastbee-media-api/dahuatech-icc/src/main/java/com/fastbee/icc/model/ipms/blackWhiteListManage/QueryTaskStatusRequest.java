package com.fastbee.icc.model.ipms.blackWhiteListManage;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @className QueryTaskStausRequest
 * @Author 355079
 * @Date 2024/12/9
 * @Description 设备白名单状态查询请求参数
 */
@Data
public class QueryTaskStatusRequest {
    /**
     * 页码
     */
    private Integer pageNum=1;
    /**
     * 每页条数
     */
    private Integer pageSize=10;
    /**
     * 车牌号（模糊查询）
     */
    private String carNum;
    /**
     * 任务状态（0-正在下发；1-下发成功；2-下发失败，3-超时，4-服务离线，5-设备离线）
     */
    private Integer stat;
    /**
     * 操作状态（0-删除，1-增加，2-修改）
     */
    private Integer operType;
    /**
     * 相机设备编码
     */
    private String deviceCode;

    /**
     * 获取URL后缀
     *
     * @return URL后缀
     */
    public String getUrlSuffix() {
        StringBuilder urlSuffix = new StringBuilder();
        urlSuffix.append("?");
        urlSuffix.append("pageNum=" + pageNum);
        urlSuffix.append("&pageSize=" + pageSize);
        if (!StringUtils.isEmpty(carNum)) {
            urlSuffix.append("&carNum=" + carNum);
        }
        if(stat!=null){
            urlSuffix.append("&stat=" + stat);
        }
        if(!StringUtils.isEmpty(operType)){
            urlSuffix.append("&operType=" + operType);
        }
        if(!StringUtils.isEmpty(deviceCode)){
            urlSuffix.append("&deviceCode=" + deviceCode);
        }
        return urlSuffix.toString();
    }
}
