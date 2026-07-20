//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.utils.StringUtils;

public class HttpUtils {
    private static final Logger bX = LoggerFactory.getLogger(HttpUtils.class);

    public static String sendGet(String url) {
        return sendGet(url, "");
    }

    public static String sendGet(String url, String param) {
        return sendGet(url, param, "UTF-8");
    }

    public static String sendGet(String url, String param, String contentType) {
        StringBuilder var3 = new StringBuilder();
        BufferedReader var4 = null;

        try {
            String var5 = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            bX.info("sendGet - {}", var5);
            URL var6 = new URL(var5);
            URLConnection var7 = var6.openConnection();
            var7.setRequestProperty("accept", "*/*");
            var7.setRequestProperty("connection", "Keep-Alive");
            var7.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            var7.setConnectTimeout(30000);
            var7.setReadTimeout(30000);
            var7.connect();
            var4 = new BufferedReader(new InputStreamReader(var7.getInputStream(), contentType));

            String var8;
            while((var8 = var4.readLine()) != null) {
                var3.append(var8);
            }

            bX.info("recv - {}", var3);
        } catch (ConnectException var23) {
            bX.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, var23);
        } catch (SocketTimeoutException var24) {
            bX.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, var24);
        } catch (IOException var25) {
            bX.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, var25);
        } catch (Exception var26) {
            bX.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, var26);
        } finally {
            try {
                if (var4 != null) {
                    var4.close();
                }
            } catch (Exception var22) {
                bX.error("调用in.close Exception, url=" + url + ",param=" + param, var22);
            }

        }

        return var3.toString();
    }

    public static String sendPost(String url, String param) {
        PrintWriter var2 = null;
        BufferedReader var3 = null;
        StringBuilder var4 = new StringBuilder();

        try {
            bX.info("sendPost - {}", url);
            URL var5 = new URL(url);
            URLConnection var6 = var5.openConnection();
            var6.setRequestProperty("accept", "*/*");
            var6.setRequestProperty("connection", "Keep-Alive");
            var6.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            var6.setRequestProperty("Accept-Charset", "utf-8");
            var6.setRequestProperty("contentType", "utf-8");
            var6.setDoOutput(true);
            var6.setDoInput(true);
            var2 = new PrintWriter(var6.getOutputStream());
            var2.print(param);
            var2.flush();
            var3 = new BufferedReader(new InputStreamReader(var6.getInputStream(), StandardCharsets.UTF_8));

            String var7;
            while((var7 = var3.readLine()) != null) {
                var4.append(var7);
            }

            bX.info("recv - {}", var4);
        } catch (ConnectException var22) {
            bX.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, var22);
        } catch (SocketTimeoutException var23) {
            bX.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, var23);
        } catch (IOException var24) {
            bX.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, var24);
        } catch (Exception var25) {
            bX.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, var25);
        } finally {
            try {
                if (var2 != null) {
                    var2.close();
                }

                if (var3 != null) {
                    var3.close();
                }
            } catch (IOException var21) {
                bX.error("调用in.close Exception, url=" + url + ",param=" + param, var21);
            }

        }

        return var4.toString();
    }

    public static String sendSSLPost(String url, String param) {
        StringBuilder var2 = new StringBuilder();
        String var3 = url + "?" + param;

        try {
            bX.info("sendSSLPost - {}", var3);
            SSLContext var4 = SSLContext.getInstance("SSL");
            var4.init((KeyManager[])null, new TrustManager[]{new b()}, new SecureRandom());
            URL var5 = new URL(var3);
            HttpsURLConnection var6 = (HttpsURLConnection)var5.openConnection();
            var6.setRequestProperty("accept", "*/*");
            var6.setRequestProperty("connection", "Keep-Alive");
            var6.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            var6.setRequestProperty("Accept-Charset", "utf-8");
            var6.setRequestProperty("contentType", "utf-8");
            var6.setDoOutput(true);
            var6.setDoInput(true);
            var6.setSSLSocketFactory(var4.getSocketFactory());
            var6.setHostnameVerifier(new a());
            var6.connect();
            InputStream var7 = var6.getInputStream();
            BufferedReader var8 = new BufferedReader(new InputStreamReader(var7));
            String var9 = "";

            while((var9 = var8.readLine()) != null) {
                if (var9 != null && !"".equals(var9.trim())) {
                    var2.append(new String(var9.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }

            bX.info("recv - {}", var2);
            var6.disconnect();
            var8.close();
        } catch (ConnectException var10) {
            bX.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, var10);
        } catch (SocketTimeoutException var11) {
            bX.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, var11);
        } catch (IOException var12) {
            bX.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, var12);
        } catch (Exception var13) {
            bX.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, var13);
        }

        return var2.toString();
    }

    public static String sendJsonPost(String url, String json) throws IOException {
        PrintWriter var2 = null;
        BufferedReader var3 = null;
        StringBuilder var4 = new StringBuilder();

        try {
            bX.info("sendPost - {}", url);
            URL var5 = new URL(url);
            URLConnection var6 = var5.openConnection();
            var6.setRequestProperty("accept", "*/*");
            var6.setRequestProperty("connection", "Keep-Alive");
            var6.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            var6.setRequestProperty("Accept-Charset", "utf-8");
            var6.setRequestProperty("Content-Type", "application/json");
            var6.setDoOutput(true);
            var6.setDoInput(true);
            var2 = new PrintWriter(var6.getOutputStream());
            var2.print(json);
            var2.flush();
            var3 = new BufferedReader(new InputStreamReader(var6.getInputStream(), StandardCharsets.UTF_8));

            String var7;
            while((var7 = var3.readLine()) != null) {
                var4.append(var7);
            }

            bX.info("recv - {}", var4);
        } catch (ConnectException var22) {
            bX.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + json, var22);
        } catch (SocketTimeoutException var23) {
            bX.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + json, var23);
        } catch (IOException var24) {
            bX.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + json, var24);
        } catch (Exception var25) {
            bX.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + json, var25);
        } finally {
            try {
                if (var2 != null) {
                    var2.close();
                }

                if (var3 != null) {
                    var3.close();
                }
            } catch (IOException var21) {
                bX.error("调用in.close Exception, url=" + url + ",param=" + json, var21);
            }

        }

        return var4.toString();
    }

    private static class b implements X509TrustManager {
        private b() {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class a implements HostnameVerifier {
        private a() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
