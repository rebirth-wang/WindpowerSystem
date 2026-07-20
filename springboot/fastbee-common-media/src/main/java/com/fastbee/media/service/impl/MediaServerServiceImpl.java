package com.fastbee.media.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.*;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.media.convert.MediaServerConvert;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.domain.vo.MediaServerVO;
import com.fastbee.media.mapper.MediaServerMapper;
import com.fastbee.media.model.ZlmMediaServer;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.util.ZlmApiUtils;

/**
 * 流媒体服务器配置Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Slf4j
@Service
public class MediaServerServiceImpl extends ServiceImpl<MediaServerMapper, MediaServer> implements IMediaServerService {
    @Autowired
    private MediaServerMapper mediaServerMapper;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private IMediaCacheService mediaCacheService;

    @Cacheable(cacheNames = "mediaServer", key = "#root.methodName + '_' + #id", unless = "#result == null")
    @Override
    public MediaServer selectMediaServerById(Long id) {
        return mediaServerMapper.selectById(id);
    }

    /**
     * 查询流媒体服务器配置
     *
     * @return 流媒体服务器配置
     */
    @Override
    public List<MediaServer> selectMediaServer() {
        MediaServer mediaServer = new MediaServer();
        SysUser user = getLoginUser().getUser();
        List<SysRole> roles = user.getRoles();
        for (SysRole role : roles) {
            if (role.getRoleKey().equals("tenant")) {
                // 租户查看第一条流媒体服务器配置
                mediaServer.setTenantId(user.getUserId());
            }
        }
        return mediaServerMapper.selectMediaServer(mediaServer);
    }

    @Cacheable(cacheNames = "mediaServer", key = "#root.methodName + '_' + #tenantId", unless = "#result == null")
    @Override
    public MediaServer selectMediaServerBytenantId(Long tenantId) {
        List<MediaServer> list = mediaServerMapper.selectMediaServerBytenantId(tenantId);
        if (list.isEmpty()) {
            if (tenantId != 1L) {
                return selectMediaServerBytenantId(1L);
            } else {
                return null;
            }
        } else {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                //随机选择一个流媒体节点
                Random random = new Random();
                int index = random.nextInt(list.size());
                return list.get(index);
            }
        }
    }

    @Override
    public MediaServer selectMediaServerBydeviceSipId(String deviceSipId) {
        Device device = deviceService.selectDeviceBySerialNumber(deviceSipId);
        if (device != null) {
            List<MediaServer> list = mediaServerMapper.selectMediaServerBytenantId(device.getTenantId());
            if (list.isEmpty()) {
                // 使用admin账号的流媒体节点
                return selectMediaServerBytenantId(1L);
            } else if (list.size() == 1) {
                return list.get(0);
            } else {
                //随机选择一个流媒体节点
                Random random = new Random();
                int index = random.nextInt(list.size());
                return list.get(index);
            }
        } else {
            // 使用admin账号的流媒体节点
            return selectMediaServerBytenantId(1L);
        }
    }

    @Override
    public MediaServer selectMediaServerByServerId(String mediaServerId) {
        MediaServer mediaServer = new MediaServer();
        mediaServer.setServerId(mediaServerId);
        return mediaServerMapper.selectOne(buildQueryWrapper(mediaServer));
    }

    /**
     * 查询流媒体服务器配置列表
     *
     * @param mediaServer 流媒体服务器配置
     * @return 流媒体服务器配置
     */
    @Override
    public List<MediaServerVO> selectMediaServerList(MediaServer mediaServer) {
        try {
            SysUser user = getLoginUser().getUser();
//        List<SysRole> roles=user.getRoles();
//        // 租户
//        if(roles.stream().anyMatch(a->a.getRoleKey().equals("tenant"))){
//            mediaServer.setTenantId(user.getUserId());
//        }
            mediaServer.setTenantId(user.getDept().getDeptUserId());
        } catch (Exception e) {
            log.error("获取登录用户信息失败", e);
        }
        LambdaQueryWrapper<MediaServer> lqw = buildQueryWrapper(mediaServer);
        List<MediaServer> mediaServerList = baseMapper.selectList(lqw);
        return MediaServerConvert.INSTANCE.convertMediaServerVOList(mediaServerList);
    }

    /**
     * 查询数据桥接分页列表
     *
     * @param mediaServer 数据桥接
     * @return 数据桥接
     */
    @Override
    public Page<MediaServerVO> pageMediaServerVO(MediaServer mediaServer) {
        try {
            SysUser user = getLoginUser().getUser();
//        List<SysRole> roles=user.getRoles();
//        // 租户
//        if(roles.stream().anyMatch(a->a.getRoleKey().equals("tenant"))){
//            mediaServer.setTenantId(user.getUserId());
//        }
            mediaServer.setTenantId(user.getDept().getDeptUserId());
        } catch (Exception e) {
            log.error("获取登录用户信息失败", e);
        }
        LambdaQueryWrapper<MediaServer> lqw = buildQueryWrapper(mediaServer);
        Page<MediaServer> mediaServerPage = baseMapper.selectPage(new Page<>(mediaServer.getPageNum(), mediaServer.getPageSize()), lqw);
        return MediaServerConvert.INSTANCE.convertMediaServerVOPage(mediaServerPage);
    }

    private LambdaQueryWrapper<MediaServer> buildQueryWrapper(MediaServer query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<MediaServer> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getServerId()), MediaServer::getServerId, query.getServerId());
//        lqw.eq(query.getTenantId() != null, MediaServer::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), MediaServer::getTenantName, query.getTenantName());
        lqw.eq(query.getEnabled() != null, MediaServer::getEnabled, query.getEnabled());
        lqw.eq(StringUtils.isNotBlank(query.getProtocol()), MediaServer::getProtocol, query.getProtocol());
        lqw.eq(StringUtils.isNotBlank(query.getIp()), MediaServer::getIp, query.getIp());
        lqw.eq(StringUtils.isNotBlank(query.getDomainAlias()), MediaServer::getDomainAlias, query.getDomainAlias());
        lqw.eq(StringUtils.isNotBlank(query.getHookurl()), MediaServer::getHookurl, query.getHookurl());
        lqw.eq(StringUtils.isNotBlank(query.getSecret()), MediaServer::getSecret, query.getSecret());
        lqw.eq(query.getPortHttp() != null, MediaServer::getPortHttp, query.getPortHttp());
        lqw.eq(query.getPortHttps() != null, MediaServer::getPortHttps, query.getPortHttps());
        lqw.eq(query.getPortRtmp() != null, MediaServer::getPortRtmp, query.getPortRtmp());
        lqw.eq(query.getPortRtsp() != null, MediaServer::getPortRtsp, query.getPortRtsp());
        lqw.eq(query.getRtpProxyPort() != null, MediaServer::getRtpProxyPort, query.getRtpProxyPort());
        lqw.eq(query.getRtpEnable() != null, MediaServer::getRtpEnable, query.getRtpEnable());
        lqw.eq(StringUtils.isNotBlank(query.getRtpPortRange()), MediaServer::getRtpPortRange, query.getRtpPortRange());
        lqw.eq(query.getRecordPort() != null, MediaServer::getRecordPort, query.getRecordPort());
        lqw.eq(query.getAutoConfig() != null, MediaServer::getAutoConfig, query.getAutoConfig());
        lqw.eq(query.getStatus() != null, MediaServer::getStatus, query.getStatus());
        lqw.eq(query.getPortWs() != null, MediaServer::getPortWs, query.getPortWs());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(MediaServer::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增流媒体服务器配置
     *
     * @param mediaServer 流媒体服务器配置
     * @return 结果
     */
    @Override
    public int insertMediaServer(MediaServer mediaServer) {
        // 判断是否为管理员
        SysUser user = getLoginUser().getUser();
//        List<SysRole> roles=user.getRoles();
//        for(int i=0;i<roles.size();i++){
//            if(roles.get(i).getRoleKey().equals("tenant") || roles.get(i).getRoleKey().equals("general")){
//                mediaServer.setIsSys(0);
//                break;
//            }
//        }
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        mediaServer.setTenantId(user.getDept().getDeptUserId());
        mediaServer.setTenantName(user.getDept().getDeptName());
        mediaServer.setCreateBy(user.getUserName());
        //判断相同ip服务器是否存在
        MediaServer temp = new MediaServer();
        temp.setIp(mediaServer.getIp());
        List<MediaServer> list = mediaServerMapper.selectMediaServerList(temp);
        if (list.size() > 0) {
            return 0;
        }
        mediaServer.setCreateTime(DateUtils.getNowDate());
        boolean ret = syncMediaServer(mediaServer, mediaServer.getSecret());
        if (ret) {
            return mediaServerMapper.insert(mediaServer);
        } else {
            return 0;
        }
    }

    /**
     * 修改流媒体服务器配置
     *
     * @param mediaServer 流媒体服务器配置
     * @return 结果
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "mediaServer", key = "'selectMediaServerById' + '_' + #mediaServer.id"),
            @CacheEvict(cacheNames = "mediaServer", key = "'selectMediaServerBytenantId' + '_' + #mediaServer.tenantId"),
    })
    @Override
    public int updateMediaServer(MediaServer mediaServer) {
        if (mediaServer.getId() != null) {
            String newSecret = mediaServer.getSecret();
            mediaServer.setSecret(mediaServerMapper.selectById(mediaServer.getId()).getSecret());
            boolean ret = syncMediaServer(mediaServer, newSecret);
            if (ret) {
                mediaServer.setSecret(newSecret);
                mediaServer.setUpdateTime(DateUtils.getNowDate());
                mediaServer.setUpdateBy(getUsername());
                return mediaServerMapper.updateById(mediaServer);
            }
        }
        return 0;
    }

    @Override
    public boolean syncMediaServer(MediaServer mediaServer, String secret) {
        String hookPrex = String.format("http://%s/zlmhook", mediaServer.getHookurl());
        Map<String, Object> param = new HashMap<>();
        param.put("api.secret", secret);

        param.put("hook.enable", "1");
        param.put("general.mediaServerId", mediaServer.getServerId());
        param.put("general.flowThreshold", "64");
        param.put("hook.on_play", String.format("%s/on_play", hookPrex));
        param.put("hook.on_http_access", String.format("%s/on_http_access", hookPrex));
        param.put("hook.on_publish", String.format("%s/on_publish", hookPrex));
        param.put("hook.on_server_started", String.format("%s/on_server_started", hookPrex));
        param.put("hook.on_stream_changed", String.format("%s/on_stream_changed", hookPrex));
        param.put("hook.on_stream_none_reader", String.format("%s/on_stream_none_reader", hookPrex));
        param.put("hook.on_stream_not_found", String.format("%s/on_stream_not_found", hookPrex));
        param.put("hook.on_send_rtp_stopped", String.format("%s/on_send_rtp_stopped", hookPrex));
        param.put("hook.on_rtp_server_timeout", String.format("%s/on_rtp_server_timeout", hookPrex));
        param.put("hook.on_flow_report", String.format("%s/on_flow_report", hookPrex));
        param.put("hook.on_server_keepalive", String.format("%s/on_server_keepalive", hookPrex));
        param.put("hook.on_server_exited", String.format("%s/on_server_exited", hookPrex));
        if (Objects.equals(mediaServer.getProtocol(), "http")) {
            param.put("hook.on_record_mp4", String.format("http://127.0.0.1:%s/zlmhook/on_record_mp4", mediaServer.getRecordPort()));
        } else if (Objects.equals(mediaServer.getProtocol(), "https")) {
            param.put("hook.on_record_mp4", String.format("https://%s:%s/zlmhook/on_record_mp4", mediaServer.getDomainAlias(), mediaServer.getRecordPort()));
        }
        param.put("hook.on_record_ts", "");
        param.put("hook.on_rtsp_auth", "");
        param.put("hook.on_rtsp_realm", "");
        param.put("hook.on_shell_login", "");
        param.put("hook.timeoutSec", "20");
        param.put("hook.alive_interval", "60.0");

        param.put("rtsp.port", mediaServer.getPortRtsp().toString());
        param.put("rtmp.port", mediaServer.getPortRtmp().toString());

        param.put("rtc.externIP", mediaServer.getIp());
        param.put("record.appName", "record");
        param.put("record.filePath", "./record");
        if (!ObjectUtils.isEmpty(mediaServer.getRtpPortRange())) {
            param.put("rtp_proxy.port_range", mediaServer.getRtpPortRange().replace(",", "-"));
            log.warn("[ZLM] 修改RTP推流端口时，请同步Docker端口映射");
        }
        JSONObject responseJSON = zlmApiUtils.setServerConfig(mediaServer, param);
        if (responseJSON != null && responseJSON.getInteger("code") == 0) {
            log.info("[ZLM] 设置成功,开始重启以保证配置生效");
            zlmApiUtils.restartServer(mediaServer);
            return true;
        } else {
            log.info("[ZLM] 设置zlm失败 {}", responseJSON);
            return false;
        }
    }

    /**
     * 批量删除流媒体服务器配置
     *
     * @param ids 需要删除的流媒体服务器配置主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "mediaServer", allEntries = true)
    @Override
    public int deleteMediaServerByIds(Long[] ids) {
        return mediaServerMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除流媒体服务器配置信息
     *
     * @param id 流媒体服务器配置主键
     * @return 结果
     */
    @CacheEvict(cacheNames = "mediaServer", allEntries = true)
    @Override
    public int deleteMediaServerById(Long id) {
        return mediaServerMapper.deleteById(id);
    }

    @Override
    public JSONObject getMediaList(String schema, String stream) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = selectMediaServerBytenantId(user.getDept().getDeptUserId());
        if (media != null) {
            return zlmApiUtils.getMediaList(media, "live", schema, stream).getJSONObject("data");
        }
        return null;
    }

    @Override
    public JSONObject listRtpServer() {
        SysUser user = getLoginUser().getUser();
        MediaServer media = selectMediaServerBytenantId(user.getDept().getDeptUserId());
        if (media != null) {
            return zlmApiUtils.listRtpServer(media).getJSONObject("data");
        }
        return null;
    }

    @Override
    public MediaServer checkMediaServer(String ip, Long port, String secret) {
        MediaServer mediaServerItem = new MediaServer();
        mediaServerItem.setIp(ip);
        mediaServerItem.setPortHttp(port);
        mediaServerItem.setSecret(secret);
        ZlmMediaServer zlmServerConfig = zlmApiUtils.getMediaServerConfig(mediaServerItem);
        if (zlmServerConfig == null) {
            return null;
        }
        mediaServerItem.setServerId(zlmServerConfig.getMediaServerId());
        mediaServerItem.setPortRtmp(Long.valueOf(zlmServerConfig.getRtmpPort()));
        mediaServerItem.setPortRtsp(Long.valueOf(zlmServerConfig.getRtspPort()));
        mediaServerItem.setRtpProxyPort(Long.valueOf(zlmServerConfig.getRtpProxyPort()));
        mediaServerItem.setHookurl("java:8080");
        return mediaServerItem;
    }

    @Override
    public MediaServer getMediaServerForMinimumLoad(Boolean hasAssist) {
        Set<Object> objects = mediaCacheService.getMediaServerLoadCount();
        ArrayList<Object> list = new ArrayList<>(objects);
        MediaServer mediaServerItem = null;
        if (hasAssist == null) {
            String mediaServerId = (String) list.get(0);
            mediaServerItem = selectMediaServerByServerId(mediaServerId);
        } else if (hasAssist.booleanValue()) {
            Iterator<Object> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Object obj = it.next();
                String mediaServerId2 = (String) obj;
                MediaServer serverItem = selectMediaServerByServerId(mediaServerId2);
                if (serverItem.getRecordPort() > 0) {
                    mediaServerItem = serverItem;
                    break;
                }
            }
        } else if (!hasAssist.booleanValue()) {
            Iterator<Object> it2 = list.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                Object obj = it2.next();
                String mediaServerId3 = (String) obj;
                MediaServer serverItem2 = selectMediaServerByServerId(mediaServerId3);
                if (serverItem2.getRecordPort() == 0) {
                    mediaServerItem = serverItem2;
                    break;
                }
            }
        }
        return mediaServerItem;
    }

    /**
     * 大屏参数配置返回写死的mp4播放链接，实际需要设备接入平台才能使用
     *
     * @param deviceId 设备ID
     * @param channelId 通道ID
     * @return 流媒体服务器地址
     */
    @Override
    public String getHttps_fmp4(String deviceId, String channelId) {
        MediaServerVO mediaServerVO = this.pageMediaServerVO(new MediaServer()).getRecords().get(0);
        String streamPath = "gb_play_" + deviceId + "_" + channelId;
        String secretParam = "?secret=" + mediaServerVO.getSecret();

        if ("https".equals(mediaServerVO.getProtocol())) {
            return "https://" + mediaServerVO.getDomainAlias() + ":" + mediaServerVO.getPortHttps()
                    + "/rtp/" + streamPath + ".live.mp4" + secretParam;
        } else {
            return "http://" + mediaServerVO.getIp() + ":" + mediaServerVO.getPortHttp()
                    + "/rtp/" + streamPath + ".live.mp4" + secretParam;
        }

    }


}
