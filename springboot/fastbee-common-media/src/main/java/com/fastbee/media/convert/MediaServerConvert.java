package com.fastbee.media.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.domain.vo.MediaServerVO;

/**
 * 流媒体服务器配置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@Mapper
public interface MediaServerConvert
{

    MediaServerConvert INSTANCE = Mappers.getMapper(MediaServerConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param mediaServer
     * @return 流媒体服务器配置集合
     */
    MediaServerVO convertMediaServerVO(MediaServer mediaServer);

    /**
     * VO类转换为实体类集合
     *
     * @param mediaServerVO
     * @return 流媒体服务器配置集合
     */
    MediaServer convertMediaServer(MediaServerVO mediaServerVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param mediaServerList
     * @return 流媒体服务器配置集合
     */
    List<MediaServerVO> convertMediaServerVOList(List<MediaServer> mediaServerList);

    /**
     * VO类转换为实体类
     *
     * @param mediaServerVOList
     * @return 流媒体服务器配置集合
     */
    List<MediaServer> convertMediaServerList(List<MediaServerVO> mediaServerVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param mediaServerPage
     * @return 流媒体服务器配置分页
     */
    Page<MediaServerVO> convertMediaServerVOPage(Page<MediaServer> mediaServerPage);

    /**
     * VO类转换为实体类
     *
     * @param mediaServerVOPage
     * @return 流媒体服务器配置分页
     */
    Page<MediaServer> convertMediaServerPage(Page<MediaServerVO> mediaServerVOPage);
}
