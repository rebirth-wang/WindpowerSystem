package com.fastbee.media.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.vo.CommonChannelVO;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.model.StreamPlayUrl;

/**
 * 监控视频通道信息Convert转换类
 *
 * @author fastbee
 * @date 2026-01-30
 */
@Mapper
public interface CommonChannelConvert
{
    /** 代码生成区域 可直接覆盖**/
    CommonChannelConvert INSTANCE = Mappers.getMapper(CommonChannelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param commonChannel
     * @return 监控视频通道信息集合
     */
    CommonChannelVO convertCommonChannelVO(CommonChannel commonChannel);

    StreamPlayUrl convertStream(StreamInfo steam);

    /**
     * VO类转换为实体类集合
     *
     * @param commonChannelVO
     * @return 监控视频通道信息集合
     */
    CommonChannel convertCommonChannel(CommonChannelVO commonChannelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param commonChannelList
     * @return 监控视频通道信息集合
     */
    List<CommonChannelVO> convertCommonChannelVOList(List<CommonChannel> commonChannelList);

    /**
     * VO类转换为实体类
     *
     * @param commonChannelVOList
     * @return 监控视频通道信息集合
     */
    List<CommonChannel> convertCommonChannelList(List<CommonChannelVO> commonChannelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param commonChannelPage
     * @return 监控视频通道信息分页
     */
    Page<CommonChannelVO> convertCommonChannelVOPage(Page<CommonChannel> commonChannelPage);

    /**
     * VO类转换为实体类
     *
     * @param commonChannelVOPage
     * @return 监控视频通道信息分页
     */
    Page<CommonChannel> convertCommonChannelPage(Page<CommonChannelVO> commonChannelVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
