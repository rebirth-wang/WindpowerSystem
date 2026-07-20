package com.fastbee.isup.sdk.service.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.isup.sdk.service.PLAYBACK_DATA_CB;
import com.fastbee.isup.sdk.structure.NET_EHOME_PLAYBACK_DATA_CB_INFO;
import com.fastbee.media.model.VideoSessionInfo;

@Slf4j
public class PlaybackDataCallback implements PLAYBACK_DATA_CB {

    private VideoSessionInfo info;

    // 存储每个预览句柄对应的连接信息
    private static class RtpConnection {
        java.net.Socket tcpSocket;
        java.net.DatagramSocket udpSocket;
        java.io.OutputStream tcpOutputStream;
        InetAddress targetAddress;
        int ssrc = 0;
        int targetPort;
        int seqNum = 0;
        int timestamp = 0;
    }

    public PlaybackDataCallback(VideoSessionInfo info) {
        this.info = info;
    }

    // 使用线程安全的 Map 存储每个句柄对应的连接
    private final Map<Integer, PlaybackDataCallback.RtpConnection> connectionMap = new ConcurrentHashMap<>();

    //实时流回调函数
    public boolean invoke(int iPlayBackLinkHandle, NET_EHOME_PLAYBACK_DATA_CB_INFO pDataCBInfo, Pointer pUserData) {
        Integer rtpPort = info.getPort();
        if (rtpPort == null) {
            log.error("未找到 sessionID {} 对应的 RTP 端口", info.getSessionId());
            return false;
        }
        String mediaServerIp = info.getMediaServerIp();
        int sessionId = info.getSessionId();

        PlaybackDataCallback.RtpConnection connection = connectionMap.computeIfAbsent(iPlayBackLinkHandle, handle -> createRtpConnection(handle, mediaServerIp, rtpPort, sessionId));
        if (connection == null || connection.tcpOutputStream == null) {
            log.error("RTP连接不可用，句柄: {}", iPlayBackLinkHandle);
            return false;
        }
        byte[] dataStream = pDataCBInfo.pData.getByteArray(0, pDataCBInfo.dwDataLen);
        if (dataStream != null && dataStream.length > 0) {
            if (pDataCBInfo.dwType == 2) {
                processRtpData(iPlayBackLinkHandle, pDataCBInfo, connection);
            }
        }
        return true;
    }

    private RtpConnection createRtpConnection(int handle, String ip, int port, int sessionId) {
        RtpConnection conn = new RtpConnection();
        try {
            conn.tcpSocket = new java.net.Socket(ip, port);
            conn.tcpOutputStream = conn.tcpSocket.getOutputStream();
            conn.udpSocket = new java.net.DatagramSocket(); // 动态分配本地端口
            conn.targetAddress = InetAddress.getByName(ip);
            conn.targetPort = port;
            conn.ssrc = Integer.parseUnsignedInt(info.getSsrc());
            log.info("预览句柄: {} ==== RTP Socket创建成功，Ip: {} 端口: {}, sessionID: {}", handle, ip, port, sessionId);
        } catch (Exception e) {
            log.error("创建RTP Socket失败，句柄: {}, 端口: {}", handle, port, e);
            return null;
        }
        return conn;
    }

    private void processRtpData(int handle, NET_EHOME_PLAYBACK_DATA_CB_INFO msg, PlaybackDataCallback.RtpConnection connection) {
        int dwBufSize = msg.dwDataLen;
        Pointer pBuffer = msg.pData;
        byte[] rtpPacket = new byte[1456];
        byte pt = 96;
        connection.timestamp += 3600;

        byte[] timestampBytes = intToBytes(connection.timestamp);
        byte[] ssrc = intToBytes(connection.ssrc);

        byte[] rtpHeader = new byte[]{
                0x00, 0x00,
                (byte) 0x80, (byte) (pt & 0xFF),
                0x00, 0x00,
                timestampBytes[0], timestampBytes[1], timestampBytes[2], timestampBytes[3],
                ssrc[0], ssrc[1], ssrc[2], ssrc[3]
        };
        System.arraycopy(rtpHeader, 0, rtpPacket, 0, rtpHeader.length);
        int datasize = dwBufSize;
        int offset = 0;
        int useDataSize = rtpPacket.length - rtpHeader.length;
        //DatagramPacket packet = new DatagramPacket(rtpPacket, rtpPacket.length, connection.targetAddress, connection.targetPort);
        while (datasize > 0) {
            byte[] seqNumBytes = shortToBytes(++connection.seqNum);
            try {
                if (datasize <= useDataSize) {
                    pBuffer.read(offset, rtpPacket, rtpHeader.length, datasize);
                    byte[] dataLengthBytes = shortToBytes(rtpHeader.length + datasize - 2);
                    rtpPacket[0] = dataLengthBytes[0];
                    rtpPacket[1] = dataLengthBytes[1];
                    rtpPacket[3] = (byte) (rtpPacket[3] | 0x80);
                    rtpPacket[4] = seqNumBytes[0];
                    rtpPacket[5] = seqNumBytes[1];
                    //packet.setData(rtpPacket, 0, rtpHeader.length + datasize);
                    //connection.udpSocket.send(packet);
                    connection.tcpOutputStream.write(rtpPacket, 0, rtpHeader.length + datasize);
                    connection.tcpOutputStream.flush();
                    break;
                } else {
                    pBuffer.read(offset, rtpPacket, rtpHeader.length, useDataSize);
                    byte[] dataLengthBytes = shortToBytes(rtpPacket.length - 2);
                    rtpPacket[0] = dataLengthBytes[0];
                    rtpPacket[1] = dataLengthBytes[1];
                    rtpPacket[4] = seqNumBytes[0];
                    rtpPacket[5] = seqNumBytes[1];
                    // packet.setData(rtpPacket);
                    // connection.udpSocket.send(packet);
                    connection.tcpOutputStream.write(rtpPacket);
                    connection.tcpOutputStream.flush();
                    offset += useDataSize;
                    datasize -= useDataSize;
                }
            } catch (IOException e) {
                log.error("写入RTP数据失败，句柄: {}", handle, e);
                closeConnection(handle);
                break;
            }
        }
    }

    /**
     * 关闭指定句柄的RTP连接
     *
     * @param iPreviewHandle 预览句柄
     */
    public void closeConnection(int iPreviewHandle) {
        PlaybackDataCallback.RtpConnection connection = connectionMap.remove(iPreviewHandle);
        if (connection != null) {
            try {
                if (connection.tcpOutputStream != null) {
                    connection.tcpOutputStream.close();
                }
                if (connection.tcpSocket != null) {
                    connection.tcpSocket.close();
                }
                if (connection.udpSocket != null && !connection.udpSocket.isClosed()) {
                    connection.udpSocket.close();
                }
                log.info("关闭RTP连接成功，句柄: {}", iPreviewHandle);
            } catch (IOException e) {
                log.error("关闭RTP连接失败，句柄: {}", iPreviewHandle, e);
            }
        }
    }

    /**
     * 关闭所有RTP连接
     */
    public void closeAllConnections() {
        connectionMap.keySet().forEach(this::closeConnection);
        log.info("已关闭所有RTP连接");
    }

    /**
     * 将int值转换为4字节的字节数组 大端序
     *
     * @param value 要转换的int值
     * @return 包含该int值的4字节表示形式的字节数组
     */
    private byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    /**
     * 将short值转换为2字节的字节数组 大端序
     *
     * @param value 要转换的int值
     * @return 包含该int值的2字节表示形式的字节数组
     */
    private byte[] shortToBytes(int value) {
        return new byte[]{
                (byte) (value >>> 8),
                (byte) value
        };
    }
}
