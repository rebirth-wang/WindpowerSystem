package com.fastbee.icc.demo.video.videoReplay;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.HttpConfigInfo;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.video.videoReplay.*;
import com.fastbee.icc.util.SSLUtil;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-07 10:17
 * @Description: 录像回放
 */
@Slf4j
public class VideoReplayDemo {

    /**
     * 查询普通录像信息列表
     * @param regularVideoRecordRequest
     * @return
     */
    public RegularVideoRecordResponse getRegularVideoRecords(RegularVideoRecordRequest regularVideoRecordRequest){
        RegularVideoRecordResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("videoReplayDemo,getRegularVideoRecords,request:{}", JSONUtil.toJsonStr(regularVideoRecordRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/SS/Record/QueryRecords", regularVideoRecordRequest,null, Method.POST , config, RegularVideoRecordResponse.class);
            log.info("videoReplayDemo,getRegularVideoRecords,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("查询普通录像信息列表失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * rtsp以时间形式回放录像
     * @param playbackByTimeRequest
     * @return
     */
    public PlayBackByTimeResponse getPlaybackByTimeRtspUrl(PlaybackByTimeRequest playbackByTimeRequest){
        PlayBackByTimeResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("videoReplayDemo,getPlaybackByTimeRtspUrl,request:{}", JSONUtil.toJsonStr(playbackByTimeRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/SS/Playback/StartPlaybackByTime", playbackByTimeRequest,null, Method.POST , config, PlayBackByTimeResponse.class);
            log.info("videoReplayDemo,getPlaybackByTimeRtspUrl,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("获取以时间形式回放录像rtsp流地址失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * HLS、RTMP录像回放（FLV不支持）
     * @param hlsPlaybackRequest
     * @return
     */
    public HlsPlaybackResponse getPlayBackHlsUrl(HlsPlaybackRequest hlsPlaybackRequest){
        HlsPlaybackResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("videoReplayDemo,getPlayBackHlsUrl,request:{}", JSONUtil.toJsonStr(hlsPlaybackRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/video/stream/record", hlsPlaybackRequest,null, Method.POST , config, HlsPlaybackResponse.class);
            log.info("videoReplayDemo,getPlayBackHlsUrl,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("获取HLS、RTMP录像回放流地址失败:{}",response.getErrMsg());
        }
        return response;
    }


    /**
     * 获取录像回放hls拼接流地址
     * @param protocol  协议类型，http、https
     * @param ip 平台ip
     * @param port hls录像回放端口
     * @param channelId 通道编码
     * @param streamType 码流类型：1-主码流，2-辅码流
     * @param recordType 录像类型:0-设备录像，1-普通录像(属于中心录像)，2-报警录像(属于中心录像)，81-补录录像(属于中心录像)，82-报警预录录像(属于中心录像)
     * @param beginTime 录像开始时间
     * @param endTime 录像结束时间
     * @param token 认证接口返回的access_token
     * @return
     */
    public String getCallbackJointHlsUrl(String protocol,String ip,String port, String channelId,String streamType,String recordType,String beginTime,String endTime, String token){
        StringBuilder hlsUrl = new StringBuilder();
        hlsUrl.append(protocol + "://" + ip + ":" + port );
        if(recordType.equals("0")) {
            //录像为设备录像
            hlsUrl.append("/vod/device");
        }else{
            //录像为中心录像
            hlsUrl.append( "/vod/center");
        }
        String deviceCode = channelId.substring(0,channelId.indexOf("$"));
        String channelSeq = channelId.substring(channelId.lastIndexOf("$")+1);
        hlsUrl.append("/cameraid/"+deviceCode+"%24"+channelSeq);
        hlsUrl.append("/substream/"+streamType);
        hlsUrl.append("/recordtype/"+recordType);
        long totallength = Long.parseLong(endTime)-Long.parseLong(beginTime);
        hlsUrl.append("/totallength/"+totallength+"/begintime/"+beginTime+"/endtime/"+endTime+".m3u8");
        if(!StringUtils.isEmpty(token)){
            hlsUrl.append("?token="+token);
        }
        log.info("拼接的录像回放hls流地址:{}",hlsUrl);
        return hlsUrl.toString();
    }

    /**
     * 获取录像回放rtmp拼接流地址
     * @param ip 平台ip
     * @param port rtmp录像回放端口
     * @param channelId 通道编码
     * @param streamType 码流类型：1-主码流，2-辅码流
     * @param recordType 录像类型:0-设备录像，1-普通录像(属于中心录像)，2-报警录像(属于中心录像)，81-补录录像(属于中心录像)，82-报警预录录像(属于中心录像)
     * @param beginTime 录像开始时间
     * @param endTime 录像结束时间
     * @param token 认证接口返回的access_token
     * @return
     */
    public String getCallbackJointRtmpUrl(String ip,String port, String channelId,String streamType,String recordType,String beginTime,String endTime, String token){
        StringBuilder rtmpUrl = new StringBuilder();
        rtmpUrl.append( "rtmp://" + ip + ":" + port );
        if(recordType.equals("0")) {
            //录像为设备录像
            rtmpUrl.append("/vod/device");
        }else{
            //录像为中心录像
            rtmpUrl.append( "/vod/center");
        }
        String deviceCode = channelId.substring(0,channelId.indexOf("$"));
        String channelSeq = channelId.substring(channelId.lastIndexOf("$")+1);
        rtmpUrl.append("/cameraid="+deviceCode+"%24"+channelSeq);
        rtmpUrl.append(";substream="+streamType+";recordtype="+recordType);
        rtmpUrl.append(";begintime="+beginTime+";endtime="+endTime);
        if(!StringUtils.isEmpty(token)){
            rtmpUrl.append(";token="+token);
        }
        log.info("拼接的录像回放rtmp流地址:{}",rtmpUrl);
        return rtmpUrl.toString();
    }

    /**
     * 获取录像下载地址
     * @param videoDownloadRequest
     * @return
     */
    public String getVideoDownloadUrl(VideoDownloadRequest videoDownloadRequest){
        String channelId = videoDownloadRequest.getChannelId();
        String deviceCode = channelId.substring(0,channelId.indexOf("$"));
        String channelSeq = channelId.substring(channelId.lastIndexOf("$")+1);
        StringBuilder videoDownloadUrl=new StringBuilder();
        HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
        videoDownloadUrl.append(httpConfigInfo.getPrefixUrl());
        videoDownloadUrl.append("/evo-apigw/evo-httpnode/vod/cam/download.mp4");
        videoDownloadUrl.append("?vcuid="+deviceCode+"%24"+channelSeq);
        videoDownloadUrl.append("&subtype="+videoDownloadRequest.getSubtype());
        videoDownloadUrl.append("&starttime="+videoDownloadRequest.getStarttime());
        videoDownloadUrl.append("&endtime="+videoDownloadRequest.getEndtime());
        videoDownloadUrl.append("&videoType="+videoDownloadRequest.getVideoType());
        videoDownloadUrl.append("&token="+videoDownloadRequest.getToken());
        videoDownloadUrl.append("&recordType="+videoDownloadRequest.getVideoRecordType());
        log.info("录像下载的地址:{}",videoDownloadUrl.toString());
        return videoDownloadUrl.toString();
    }

    /**
     * 下载录像
     * @param videoDownloadRequest 录像下载请求参数
     * @param filePath 录像文件存放地址
     */
    public void downloadVideo(VideoDownloadRequest videoDownloadRequest,String filePath){
        String videoDownloadUrl=getVideoDownloadUrl(videoDownloadRequest);
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(videoDownloadUrl);
            URLConnection connection = url.openConnection();

            //如果是https，忽略ssl校验
            if(videoDownloadUrl.startsWith("https")){
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
                SSLUtil.trustAllHosts(httpsURLConnection);
                httpsURLConnection.setHostnameVerifier(SSLUtil.DO_NOT_VERIFY);
            }

            inputStream = connection.getInputStream();

            outputStream = new FileOutputStream(filePath + videoDownloadRequest.getStarttime() + "-" + videoDownloadRequest.getEndtime() + ".mp4");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
