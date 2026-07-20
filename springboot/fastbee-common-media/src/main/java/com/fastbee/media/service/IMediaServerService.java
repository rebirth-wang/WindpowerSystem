package com.fastbee.media.service;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.domain.vo.MediaServerVO;

/**
 * 流媒体服务器配置Service接口
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
public interface IMediaServerService extends IService<MediaServer> {
    /**
     * 查询流媒体服务器配置
     *
     * @param id 流媒体服务器配置主键
     * @return 流媒体服务器配置
     */
    public MediaServer selectMediaServerById(Long id);
    /**
     * 查询流媒体服务器配置
     *
     * @return 流媒体服务器配置
     */
    List<MediaServer> selectMediaServer();
    MediaServer selectMediaServerBytenantId(Long tenantId);
    MediaServer selectMediaServerBydeviceSipId(String deviceSipId);
    MediaServer selectMediaServerByServerId(String mediaServerId);
    /**
     * 查询流媒体服务器配置列表
     *
     * @param mediaServer 流媒体服务器配置
     * @return 流媒体服务器配置集合
     */
    List<MediaServerVO> selectMediaServerList(MediaServer mediaServer);

    /**
     * 新增流媒体服务器配置
     *
     * @param mediaServer 流媒体服务器配置
     * @return 结果
     */
    int insertMediaServer(MediaServer mediaServer);

    /**
     * 修改流媒体服务器配置
     *
     * @param mediaServer 流媒体服务器配置
     * @return 结果
     */
    int updateMediaServer(MediaServer mediaServer);
    boolean syncMediaServer(MediaServer mediaServer,String secret);
    /**
     * 批量删除流媒体服务器配置
     *
     * @param ids 需要删除的流媒体服务器配置主键集合
     * @return 结果
     */
    int deleteMediaServerByIds(Long[] ids);

    /**
     * 删除流媒体服务器配置信息
     *
     * @param id 流媒体服务器配置主键
     * @return 结果
     */
    int deleteMediaServerById(Long id);

    JSONObject getMediaList(String schema, String stream);
    JSONObject listRtpServer();
    MediaServer checkMediaServer(String ip, Long port, String secret);

    Page<MediaServerVO> pageMediaServerVO(MediaServer mediaServer);

    MediaServer getMediaServerForMinimumLoad(Boolean hasAssist);

    String getHttps_fmp4(String deviceId, String channelId);
}
