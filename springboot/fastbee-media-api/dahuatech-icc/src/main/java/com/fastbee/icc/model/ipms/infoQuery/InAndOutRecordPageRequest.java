package com.fastbee.icc.model.ipms.infoQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-06 17:20
 * @Description: 车辆进出数据请求参数
 */
@Data
public class InAndOutRecordPageRequest {
    /** 页码 */
    private Integer pageNum=1;
    /** 每页记录数 */
    private Integer pageSize=10;
    /** 入口通道id，多个时用半角逗号隔开 */
    private String entranceChannelIds;
    /** 出口通道id，多个时用半角逗号隔开 */
    private String exitusChannelIds;
    /** 车辆类型 （1-内部车，0-外部车，字段不送则查询全部） */
    private Integer carType;
    /** 部门ID */
    private Long departmentId;
    /** 是否ETC用户(0:否 1：是) */
    private Integer etcType;
    /** 车主编码 */
    private String ownerCode;
    /** 出口总数 */
    private Integer exitusNum;
    /** 车主名称模糊查询 */
    private String ownerLikeStr;
    /** 所属场区编码(多个时半角逗号分隔) */
    private String parkingLotCodes;
    /** 进场车牌 */
    private String enterCarNumLikeStr;
    /** 出场车牌 */
    private String exitCarNumLikeStr;
    /** 进场时间起，格式：yyyy-MM-dd HH:mm:ss */
    private String enterTimeStrLeft;
    /** 进场时间止，格式：yyyy-MM-dd HH:mm:ss */
    private String enterTimeStrRight;
    /** 出场时间起，格式：yyyy-MM-dd HH:mm:ss */
    private String exitTimeStrLeft;
    /** 出场时间止，格式：yyyy-MM-dd HH:mm:ss */
    private String exitTimeStrRight;

    /**
     * 将参数连接到url末尾
     * @return
     */
    public String getUrlSuffix()throws UnsupportedEncodingException{
        StringBuilder urlSuffix = new StringBuilder();
        urlSuffix.append("?");
        urlSuffix.append("pageNum="+pageNum);
        urlSuffix.append("&pageSize="+pageSize);
        if(!StringUtils.isEmpty(entranceChannelIds)){
            urlSuffix.append("&entranceChannelIds="+entranceChannelIds);
        }
        if(!StringUtils.isEmpty(exitusChannelIds)){
            urlSuffix.append("&exitusChannelIds="+exitusChannelIds);
        }
        if(carType != null ){
            urlSuffix.append("&carType="+carType);
        }
        if(departmentId != null ){
            urlSuffix.append("&departmentId="+departmentId);
        }
        if(etcType != null ){
            urlSuffix.append("&etcType="+etcType);
        }
        if(!StringUtils.isEmpty(ownerCode)){
            urlSuffix.append("&ownerCode="+ownerCode);
        }
        if(exitusNum != null ){
            urlSuffix.append("&exitusNum="+exitusNum);
        }
        if(!StringUtils.isEmpty(ownerLikeStr)){
            urlSuffix.append("&ownerLikeStr="+URLEncoder.encode(ownerLikeStr, "UTF-8"));
        }
        if(!StringUtils.isEmpty(parkingLotCodes)){
            urlSuffix.append("&parkingLotCodes="+parkingLotCodes);
        }
        if(!StringUtils.isEmpty(enterCarNumLikeStr)){
            urlSuffix.append("&enterCarNumLikeStr="+URLEncoder.encode(enterCarNumLikeStr, "UTF-8"));
        }
        if(!StringUtils.isEmpty(exitCarNumLikeStr)){
            urlSuffix.append("&exitCarNumLikeStr="+URLEncoder.encode(exitCarNumLikeStr, "UTF-8"));
        }
        if(!StringUtils.isEmpty(enterTimeStrLeft)){
            urlSuffix.append("&enterTimeStrLeft="+URLEncoder.encode(enterTimeStrLeft, "UTF-8"));
        }
        if(!StringUtils.isEmpty(enterTimeStrRight)){
            urlSuffix.append("&enterTimeStrRight="+URLEncoder.encode(enterTimeStrRight, "UTF-8"));
        }
        if(!StringUtils.isEmpty(exitTimeStrLeft)){
            urlSuffix.append("&exitTimeStrLeft="+URLEncoder.encode(exitTimeStrLeft, "UTF-8"));
        }
        if(!StringUtils.isEmpty(exitTimeStrRight)){
            urlSuffix.append("&exitTimeStrRight="+URLEncoder.encode(exitTimeStrRight, "UTF-8"));
        }
        return urlSuffix.toString();
    }
}
