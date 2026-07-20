package com.fastbee.icc.demo.video.videoReplay;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

import org.junit.Test;

/**
 * 录像下载示例
 */
public class PlayBackDownloadDemo {

    /**
     * 下载视频文件
     */
    @Test
    public void download() {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL("https://icc-dev.hibetatest.com:4077/evo-apigw/evo-httpnode/vod/cam/download.mp4?vcuid=1000131%240&subtype=1&starttime=2023_12_21_19_49_00&endtime=2023_12_21_19_49_52&videoType=2&token=1:Z0v6Ts2ykAoZYZWgvzsTP0etivAMBs5Z&recordType=1");
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            inputStream = connection.getInputStream();

            outputStream = new FileOutputStream("D:\\temp\\video.mp4");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

    /**
     * 信任所有主机
     */
    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
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
            e.printStackTrace();
        }
    }

    private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
