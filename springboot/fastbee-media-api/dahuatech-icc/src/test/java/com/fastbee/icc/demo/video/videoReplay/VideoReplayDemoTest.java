package com.fastbee.icc.demo.video.videoReplay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.HttpConfigInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import com.dahuatech.icc.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.video.videoReplay.*;
import com.fastbee.icc.util.DateUtil;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-07 13:59
 * @Description: 录像回放
 */
@Slf4j
public class VideoReplayDemoTest {
    VideoReplayDemo videoReplayDemo;

    public VideoReplayDemoTest() {
        videoReplayDemo = new VideoReplayDemo();
    }

    /**
     * 测试查询录像，并获取以时间形式回放录像的rtsp流地址
     */
    @Test
    public void testGetPlaybackByTimeRtspUrl(){
        //查询普通录像信息
        RegularVideoRecordRequest regularVideoRecordRequest = new RegularVideoRecordRequest();
        RegularVideoRecordRequest.Data data = new RegularVideoRecordRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setRecordSource("2");
        data.setStartTime(DateUtil.convertDateToTimestamp("2024-04-07 00:00:00").toString());
        data.setEndTime(DateUtil.convertDateToTimestamp("2024-04-07 23:59:59").toString());
        data.setStreamType("1");
        data.setRecordType("0");
        regularVideoRecordRequest.setData(data);
        RegularVideoRecordResponse regularVideoRecordResponse = videoReplayDemo.getRegularVideoRecords(regularVideoRecordRequest);
        if(regularVideoRecordResponse.getData()==null|| CollectionUtil.isEmpty(regularVideoRecordResponse.getData().getRecords())){
            log.error("未查到录像信息!");
        }else{
            //获取录像回放rtsp流地址
            //获取录像列表数量
            int size = regularVideoRecordResponse.getData().getRecords().size();
            //获取第一段录像信息
            RegularVideoRecordResponse.Data.RecordInfo firstRecordInfo = regularVideoRecordResponse.getData().getRecords().get(0);
            //获取最后一段录像信息
            RegularVideoRecordResponse.Data.RecordInfo lastRecordInfo = regularVideoRecordResponse.getData().getRecords().get(size-1);
            PlaybackByTimeRequest playbackByTimeRequest = new PlaybackByTimeRequest();
            PlaybackByTimeRequest.Data data1 = new PlaybackByTimeRequest.Data();
            data1.setChannelId(firstRecordInfo.getChannelId());
            data1.setRecordSource(firstRecordInfo.getRecordSource());
            data1.setStartTime(firstRecordInfo.getStartTime());
            data1.setEndTime(lastRecordInfo.getEndTime());
            data1.setStreamType(firstRecordInfo.getStreamType());
            data1.setRecordType(firstRecordInfo.getRecordType());
            playbackByTimeRequest.setData(data1);
            PlayBackByTimeResponse playBackByTimeResponse = videoReplayDemo.getPlaybackByTimeRtspUrl(playbackByTimeRequest);
        }
    }

    /**
     * 测试查询录像，并获取录像回放hls流地址
     */
    @Test
    public void testGetPlayBackHlsUrl(){
        //查询普通录像信息
        RegularVideoRecordRequest regularVideoRecordRequest = new RegularVideoRecordRequest();
        RegularVideoRecordRequest.Data data = new RegularVideoRecordRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setRecordSource("2");
        data.setStartTime(DateUtil.convertDateToTimestamp("2024-04-07 00:00:00").toString());
        data.setEndTime(DateUtil.convertDateToTimestamp("2024-04-07 23:59:59").toString());
        data.setStreamType("1");
        data.setRecordType("0");
        regularVideoRecordRequest.setData(data);
        RegularVideoRecordResponse regularVideoRecordResponse = videoReplayDemo.getRegularVideoRecords(regularVideoRecordRequest);
        if(regularVideoRecordResponse.getData()==null|| CollectionUtil.isEmpty(regularVideoRecordResponse.getData().getRecords())){
            log.error("未查到录像信息!");
        }else{
            //获取录像回放流地址
            //获取第一段录像信息
            RegularVideoRecordResponse.Data.RecordInfo firstRecordInfo = regularVideoRecordResponse.getData().getRecords().get(0);
            //取第一段录像信息，获取录像回放hls或rtmp流地址
            HlsPlaybackRequest hlsPlaybackRequest = new HlsPlaybackRequest();
            HlsPlaybackRequest.Data data1 = new HlsPlaybackRequest.Data();
            data1.setChannelId(firstRecordInfo.getChannelId());
            data1.setRecordSource(firstRecordInfo.getRecordSource());
            data1.setBeginTime(DateUtil.convertTimestampToDate(Long.parseLong(firstRecordInfo.getStartTime())));
            data1.setEndTime(DateUtil.convertTimestampToDate(Long.parseLong(firstRecordInfo.getEndTime())));
            data1.setStreamType(firstRecordInfo.getStreamType());
            data1.setRecordType(firstRecordInfo.getRecordType());
            hlsPlaybackRequest.setData(data1);
            HlsPlaybackResponse playBackByTimeResponse = videoReplayDemo.getPlayBackHlsUrl(hlsPlaybackRequest);
        }
    }

    /**
     * 测试获取拼接方式的录像回放hls流地址
     */
    @Test
    public void testGetCallbackJointHlsUrl(){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getErrMsg(),e);
        }
        //查询普通录像信息
        RegularVideoRecordRequest regularVideoRecordRequest = new RegularVideoRecordRequest();
        RegularVideoRecordRequest.Data data = new RegularVideoRecordRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setRecordSource("2");
        data.setStartTime(DateUtil.convertDateToTimestamp("2024-04-07 00:00:00").toString());
        data.setEndTime(DateUtil.convertDateToTimestamp("2024-04-07 23:59:59").toString());
        data.setStreamType("1");
        data.setRecordType("0");
        regularVideoRecordRequest.setData(data);
        RegularVideoRecordResponse regularVideoRecordResponse = videoReplayDemo.getRegularVideoRecords(regularVideoRecordRequest);
        if(regularVideoRecordResponse.getData()==null|| CollectionUtil.isEmpty(regularVideoRecordResponse.getData().getRecords())){
            log.error("未查到录像信息!");
        }else{
            //获取第一段录像信息
            RegularVideoRecordResponse.Data.RecordInfo firstRecordInfo = regularVideoRecordResponse.getData().getRecords().get(0);
            String recordType;
            //判断是否是设备录像
            if(firstRecordInfo.getRecordSource().equals("2")){
                recordType="0";
            }else{
                recordType = firstRecordInfo.getRecordType();
            }
            HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
            String url = videoReplayDemo.getCallbackJointHlsUrl("https",httpConfigInfo.getHost(),"4091",firstRecordInfo.getChannelId(),
                    firstRecordInfo.getStreamType(),recordType ,firstRecordInfo.getStartTime(),firstRecordInfo.getEndTime(),token);
        }
    }

    /**
     * 测试获取拼接方式的录像回放rtmp流地址
     */
    @Test
    public void testGetCallbackJointRtmpUrl(){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getErrMsg(),e);
        }
        //查询普通录像信息
        RegularVideoRecordRequest regularVideoRecordRequest = new RegularVideoRecordRequest();
        RegularVideoRecordRequest.Data data = new RegularVideoRecordRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setRecordSource("2");
        data.setStartTime(DateUtil.convertDateToTimestamp("2024-04-07 00:00:00").toString());
        data.setEndTime(DateUtil.convertDateToTimestamp("2024-04-07 23:59:59").toString());
        data.setStreamType("1");
        data.setRecordType("0");
        regularVideoRecordRequest.setData(data);
        RegularVideoRecordResponse regularVideoRecordResponse = videoReplayDemo.getRegularVideoRecords(regularVideoRecordRequest);
        if(regularVideoRecordResponse.getData()==null|| CollectionUtil.isEmpty(regularVideoRecordResponse.getData().getRecords())){
            log.error("未查到录像信息!");
        }else{
            //获取第一段录像信息
            RegularVideoRecordResponse.Data.RecordInfo firstRecordInfo = regularVideoRecordResponse.getData().getRecords().get(0);
            String recordType;
            //判断是否是设备录像
            if(firstRecordInfo.getRecordSource().equals("2")){
                recordType="0";
            }else{
                recordType = firstRecordInfo.getRecordType();
            }
            HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
            String url = videoReplayDemo.getCallbackJointRtmpUrl(httpConfigInfo.getHost(),"4096",firstRecordInfo.getChannelId(),
                    firstRecordInfo.getStreamType(),recordType ,firstRecordInfo.getStartTime(),firstRecordInfo.getEndTime(),token);
        }
    }

    /**
     * 测试下载录像
     */
    @Test
    public void testDownloadVideo(){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getErrMsg(),e);
        }
        VideoDownloadRequest videoDownloadRequest = new VideoDownloadRequest();
        VideoRecordInfo videoRecordInfo = getRecordInfo();
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        videoDownloadRequest.setToken(token);
        videoDownloadRequest.setVideoRecordType(videoRecordInfo.getVideoRecordType());
        videoDownloadRequest.setVideoType(videoRecordInfo.getRecordSource());
        videoDownloadRequest.setSubtype(videoRecordInfo.getStreamType());
        videoDownloadRequest.setChannelId(videoRecordInfo.getChannelId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(videoRecordInfo.getStartTime() * 1000l);
        videoDownloadRequest.setStarttime(dateFormat.format(calendar.getTime()));
        calendar.setTimeInMillis(videoRecordInfo.getEndTime() * 1000l);
        videoDownloadRequest.setEndtime(dateFormat.format(calendar.getTime()));
        videoReplayDemo.downloadVideo(videoDownloadRequest,"d:/");
    }

    /**
     * 获取录像信息列表
     *
     * @return 录像信息列表
     */
    private VideoRecordInfo getRecordInfo() {
        //videoReplayDemo.getRegularVideoRecords();//获取录像信息列表
        String str = "{\n" +
                "    \"code\": 1000,\n" +
                "    \"desc\": \"Success\",\n" +
                "    \"data\": {\n" +
                "        \"records\": [\n" +
                "            {\n" +
                "                \"channelId\": \"1002636$1$0$0\",\n" +
                "                \"recordSource\": \"3\",\n" +
                "                \"recordType\": \"6\",\n" +
                "                \"startTime\": \"1725552000\",\n" +
                "                \"endTime\": \"1725553800\",\n" +
                "                \"recordName\": \"{5e464f2324945c86-f5a4-e6ee-4e3b-30fe00000000}\",\n" +
                "                \"fileLength\": \"966656\",\n" +
                "                \"planId\": \"0\",\n" +
                "                \"ssId\": \"1025\",\n" +
                "                \"diskId\": \"1725552000-1725553800\",\n" +
                "                \"streamId\": \"3163\",\n" +
                "                \"forgotten\": \"0\",\n" +
                "                \"streamType\": \"1\",\n" +
                "                \"videoRecordType\": \"1\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"channelId\": \"1002636$1$0$0\",\n" +
                "                \"recordSource\": \"3\",\n" +
                "                \"recordType\": \"6\",\n" +
                "                \"startTime\": \"1725553800\",\n" +
                "                \"endTime\": \"1725555600\",\n" +
                "                \"recordName\": \"{5e464f2324945c86-f5a4-e6ee-4e3b-30fe00000000}\",\n" +
                "                \"fileLength\": \"950272\",\n" +
                "                \"planId\": \"0\",\n" +
                "                \"ssId\": \"1025\",\n" +
                "                \"diskId\": \"1725553800-1725555600\",\n" +
                "                \"streamId\": \"3163\",\n" +
                "                \"forgotten\": \"0\",\n" +
                "                \"streamType\": \"1\",\n" +
                "                \"videoRecordType\": \"1\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"success\": true\n" +
                "}";
        VideoRecordInfo request = JSONUtil.parseObj(str).getJSONObject("data").getJSONArray("records").get(0, VideoRecordInfo.class);
        return request;
    }

}
