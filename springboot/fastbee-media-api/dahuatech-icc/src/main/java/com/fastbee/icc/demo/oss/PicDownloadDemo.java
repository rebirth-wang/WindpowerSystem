package com.fastbee.icc.demo.oss;

import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.*;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.HttpConfigInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.icc.config.OauthConfigUtil;

public class PicDownloadDemo {
    private static Logger logger = LoggerFactory.getLogger(PicDownloadDemo.class);



    /**
     * 获取https图片全路径
     * @param picUrl 图片相对地址
     * @return
     */
    public String getHttpsImageFullPath(String picUrl){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            logger.error(e.getErrMsg(),e);
        }

        HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
        String imageFullPath = httpConfigInfo.getPrefixUrl() +"/evo-apigw/evo-oss/"+picUrl+"?token="+ token;
        return imageFullPath;
    }

    /**
     * 获取http图片全路径
     * @param picUrl 图片相对地址
     * @return
     */
    public String getHttpImageFullPath(String picUrl){
        String token=null;
        try {
            token = HttpUtils.getToken(OauthConfigUtil.getOauthConfig()).getAccess_token();
        } catch (ClientException e) {
            e.printStackTrace();
            logger.error(e.getErrMsg(),e);
        }
        HttpConfigInfo httpConfigInfo = OauthConfigUtil.getOauthConfig().getHttpConfigInfo();
        String imageFullPath = httpConfigInfo.getPrefixUrl() +"/evo-apigw/evo-oss/"+picUrl+"?token="+token;
        return imageFullPath;
    }


    /**
     * 下载图片
     *
     * @param picUrl     图片地址
     * @param targetPath 保存路径
     */
    public void download(String picUrl, String targetPath) {
        try {
            // 构造URL
            URL url = new URL(picUrl);
            trustAllHosts();
            // 打开连接
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            String url1 = picUrl.substring(picUrl.lastIndexOf("/"));
            String url2 = url1.substring(1, url1.lastIndexOf("?"));
            OutputStream os = new FileOutputStream(targetPath + "/" + url2);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取图片字节流
     * @param picUrl 图片全路径
     * @return
     */
    public ByteArrayOutputStream download(String picUrl){
        ByteArrayOutputStream os = null;
        InputStream is = null;
        try {
            // 构造URL
            URL url = new URL(picUrl);
            trustAllHosts();
            // 打开连接
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            os = new ByteArrayOutputStream();
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            return os;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }finally {
            // 完毕，关闭所有链接
            try {
                os.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }

    /**
     * 信任所有主机
     */
    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 图片转换成base64流
     * @param imagePath 存放图片地址
     * @return
     */
    public static String getImageBase(String imagePath) {
        InputStream in;
        byte[] data = null;
        try {
            in = new FileInputStream(imagePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return "data:image/jpeg;base64," + encoder.encodeToString(data);

    }

}
