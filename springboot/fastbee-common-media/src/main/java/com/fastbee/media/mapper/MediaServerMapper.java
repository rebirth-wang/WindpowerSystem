package com.fastbee.media.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.media.domain.MediaServer;

/**
 * 流媒体服务器配置Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Repository
public interface MediaServerMapper extends BaseMapperX<MediaServer> {
    /**
     * 查询流媒体服务器配置
     *
     * @return 流媒体服务器配置
     */
    public List<MediaServer> selectMediaServer(MediaServer mediaServer);
    public List<MediaServer> selectMediaServerBytenantId(Long tenantId);
    /**
     * 查询流媒体服务器配置列表
     *
     * @param mediaServer 流媒体服务器配置
     * @return 流媒体服务器配置集合
     */
    public List<MediaServer> selectMediaServerList(MediaServer mediaServer);

}
