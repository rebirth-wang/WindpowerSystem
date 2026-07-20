package com.fastbee.notify.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.notify.domain.NotifyChannel;
import com.fastbee.notify.vo.NotifyChannelVO;

/**
 * 通知渠道Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@Mapper
public interface NotifyChannelConvert
{

    NotifyChannelConvert INSTANCE = Mappers.getMapper(NotifyChannelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param notifyChannel
     * @return 通知渠道集合
     */
    NotifyChannelVO convertNotifyChannelVO(NotifyChannel notifyChannel);

    /**
     * VO类转换为实体类集合
     *
     * @param notifyChannelVO
     * @return 通知渠道集合
     */
    NotifyChannel convertNotifyChannel(NotifyChannelVO notifyChannelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param notifyChannelList
     * @return 通知渠道集合
     */
    List<NotifyChannelVO> convertNotifyChannelVOList(List<NotifyChannel> notifyChannelList);

    /**
     * VO类转换为实体类
     *
     * @param notifyChannelVOList
     * @return 通知渠道集合
     */
    List<NotifyChannel> convertNotifyChannelList(List<NotifyChannelVO> notifyChannelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param notifyChannelPage
     * @return 通知渠道分页
     */
    Page<NotifyChannelVO> convertNotifyChannelVOPage(Page<NotifyChannel> notifyChannelPage);

    /**
     * VO类转换为实体类
     *
     * @param notifyChannelVOPage
     * @return 通知渠道分页
     */
    Page<NotifyChannel> convertNotifyChannelPage(Page<NotifyChannelVO> notifyChannelVOPage);
}
