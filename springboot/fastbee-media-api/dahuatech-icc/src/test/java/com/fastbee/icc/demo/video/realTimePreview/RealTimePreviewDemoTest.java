package com.fastbee.icc.demo.video.realTimePreview;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.HttpConfigInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.video.realTimePreview.HlsUrlRequest;
import com.fastbee.icc.model.video.realTimePreview.RtspUrlRequest;
import com.fastbee.icc.model.video.realTimePreview.RtspUrlResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-02 09:35
 * @Description: 实时预览测试类
 */
@Slf4j
public class RealTimePreviewDemoTest {

    private RealTimePreviewDemo realTimePreviewDemo;

    public RealTimePreviewDemoTest() {
        realTimePreviewDemo = new RealTimePreviewDemo();
    }

    /**
     * 测试获取RTSP流地址
     */
    @Test
    public void testGetRtspUrl(){
        RtspUrlRequest rtspUrlRequest = new RtspUrlRequest();
        RtspUrlRequest.Data data = new RtspUrlRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setDataType("1");
        data.setStreamType("1");
        rtspUrlRequest.setData(data);
        RtspUrlResponse response = realTimePreviewDemo.getRtspUrl(rtspUrlRequest);
        if(response.getData()!=null){
            //获取完整的rtsp流地址
            String rtspUrl = response.getData().getUrl()+"?token="+response.getData().getToken();
            log.info("完整的rtsp流地址：{}",rtspUrl);
        }
    }

    /**
     * 测试获取HLS、FLV、RTMP流地址
     */
    @Test
    public void testGetHlsUrl(){
        HlsUrlRequest hlsUrlRequest = new HlsUrlRequest();
        HlsUrlRequest.Data data = new HlsUrlRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setStreamType("1");
        data.setType("hls");
        hlsUrlRequest.setData(data);
        realTimePreviewDemo.getHlsUrl(hlsUrlRequest);
    }

    /**
     * 测试获取hls拼接地址
     */
    @Test
    public void testGetJointHlsUrl(){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getErrMsg(),e);
        }
        HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
        realTimePreviewDemo.getJointHlsUrl("http",httpConfigInfo.getHost(),"4091",false,"1002539$1$0$0","1",token);
    }

    /**
     * 测试获取flv拼接地址
     */
    @Test
    public void testGetJointFlvUrl(){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getErrMsg(),e);
        }
        HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
        realTimePreviewDemo.getJointFlvUrl("http",httpConfigInfo.getHost(),"4093",false,"1002539$1$0$0","1",token);
    }

    /**
     * 测试获取rtmp拼接地址
     */
    @Test
    public void testGetJointRtmpUrl(){
        String token=null;
        try {
           token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getErrMsg(),e);
        }
        HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
        realTimePreviewDemo.getJointRtmpUrl(httpConfigInfo.getHost(),"4096",true,"1000004@001$1$0$3","1",token);
    }

}
