package com.fastbee.isup.sdk.service;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.springframework.stereotype.Service;

import com.fastbee.isup.sdk.structure.NET_EHOME_SS_CLIENT_PARAM;
import com.fastbee.isup.sdk.structure.NET_EHOME_SS_LISTEN_HTTPS_PARAM;
import com.fastbee.isup.sdk.structure.NET_EHOME_SS_LISTEN_PARAM;

@Service("hikIsupSs")
public interface IHikISUPStorage extends Library {

    /***宏定义***/
    //常量

    public static final int MAX_URL_LEN_SS = 4096; //图片服务器回调URL长度
    public static final int MAX_KMS_USER_LEN = 512; //KMS用户名最大长度
    public static final int MAX_KMS_PWD_LEN = 512; //KMS密码最大长度
    public static final int MAX_CLOUD_AK_SK_LEN = 64; //EHome5.0存储协议AK SK最大长度
    public static final int MAX_PATH = 260; //设备ID长度
    public static final int NET_EHOME_SERIAL_LEN = 12; //设备序列号长度

    //NET_EHOME_SS_MSG_TYPE
    public static final int NET_EHOME_SS_MSG_TOMCAT = 1;//Tomcat回调函数
    public static final int NET_EHOME_SS_MSG_KMS_USER_PWD = 2;//KMS用户名密码校验
    public static final int NET_EHOME_SS_MSG_CLOUD_AK = 3;//EHome5.0存储协议AK回调

    //NET_EHOME_SS_CLIENT_TYPE
    public static final int NET_EHOME_SS_CLIENT_TYPE_TOMCAT = 1; //Tomcat图片上传客户端
    public static final int NET_EHOME_SS_CLIENT_TYPE_VRB = 2;//VRB图片上传客户端
    public static final int NET_EHOME_SS_CLIENT_TYPE_KMS = 3;//KMS图片上传客户端
    public static final int NET_EHOME_SS_CLIENT_TYPE_CLOUD = 4;//EHome5.0存储协议客户端

    //NET_EHOME_SS_INIT_CFG_TYPE
    public static final int NET_EHOME_SS_INIT_CFG_SDK_PATH = 1;//设置SS组件加载路径（仅Linux版本支持）
    public static final int NET_EHOME_SS_INIT_CFG_CLOUD_TIME_DIFF = 2;//设置运存储的请求时间差值,默认15分钟,最小5分钟,最大60分钟

    /**
     * 获取错误码
     */
    int NET_ESS_GetLastError();

    /**
     * 日志
     *
     * @param iLogLevel
     * @param strLogDir
     * @param bAutoDel
     * @return
     */
    boolean NET_ESS_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    boolean NET_ESS_SetSDKInitCfg(int enumType, Pointer lpInBuff);

    /**
     * 获取版本号
     *
     * @return
     */
    int NET_ESS_GetBuildVersion();

    /**
     * 设置HTTP监听的Https参数
     *
     * @param pSSHttpsParam
     * @return
     */
    boolean NET_ESS_SetListenHttpsParam(NET_EHOME_SS_LISTEN_HTTPS_PARAM pSSHttpsParam);

    /**
     * 开启监听
     *
     * @param pSSListenParam
     * @return
     */
    int NET_ESS_StartListen(NET_EHOME_SS_LISTEN_PARAM pSSListenParam);

    /**
     * 关闭监听
     *
     * @param lListenHandle
     * @return
     */
    boolean NET_ESS_StopListen(int lListenHandle);

    /**
     * 设置初始化参数
     * @param enumType NET_EHOME_SS_INIT_CFG_TYPE enumType
     * @param lpInBuff
     * @return
     */


    /**
     * 创建图片上传/下载客户端
     *
     * @param pClientParam
     * @return
     */
    int NET_ESS_CreateClient(NET_EHOME_SS_CLIENT_PARAM pClientParam);

    /**
     * 设置图片上传/下载客户端超时时间,单位ms,默认为5s
     *
     * @param lHandle
     * @param dwSendTimeout
     * @param dwRecvTimeout
     * @return
     */
    boolean NET_ESS_ClientSetTimeout(int lHandle, int dwSendTimeout, int dwRecvTimeout);

    /**
     * 设置图片上传/下载客户端参数
     *
     * @param lHandle
     * @param strParamName
     * @param strParamVal
     * @return
     */
    boolean NET_ESS_ClientSetParam(int lHandle, String strParamName, String strParamVal);

    /**
     * 图片上传/下载客户端执行上传
     *
     * @param lHandle
     * @param strUrl
     * @param dwUrlLen
     * @return
     */
    boolean NET_ESS_ClientDoUpload(int lHandle, byte[] strUrl, int dwUrlLen);

    /**
     * 图片上传/下载客户端执行下载
     *
     * @param lHandle
     * @param strUrl
     * @param pFileContent
     * @param dwContentLen
     * @return
     */
    boolean NET_ESS_ClientDoDownload(int lHandle, String strUrl, PointerByReference pFileContent, IntByReference dwContentLen);

    /**
     * 销毁客户端
     *
     * @param lHandle
     * @return
     */
    boolean NET_ESS_DestroyClient(int lHandle);

    //计算HMAC-SHA256
    boolean NET_ESS_HAMSHA256(String pSrc, String pSecretKey, String pSingatureOut, int dwSingatureLen);

    //初始化，反初始化
    boolean NET_ESS_Init();

    boolean NET_ESS_Fini();

}
