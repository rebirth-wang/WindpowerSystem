package com.fastbee.notify.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.extend.core.domin.notify.NotifyConfigVO;
import com.fastbee.notify.domain.NotifyChannel;
import com.fastbee.notify.vo.ChannelProviderVO;
import com.fastbee.notify.vo.NotifyChannelVO;

/**
 * 通知渠道Service接口
 *
 * @author kerwincui
 * @date 2023-12-01
 */
public interface INotifyChannelService extends IService<NotifyChannel> {
    /**
     * 查询通知渠道
     *
     * @param notifyChannel 通知渠道
     * @return 通知渠道
     */
    public NotifyChannel selectNotifyChannelById(NotifyChannel notifyChannel);

    /**
     * 新增通知渠道
     *
     * @param notifyChannel 通知渠道
     * @return 结果
     */
    public int insertNotifyChannel(NotifyChannel notifyChannel);

    /**
     * 修改通知渠道
     *
     * @param notifyChannel 通知渠道
     * @return 结果
     */
    public int updateNotifyChannel(NotifyChannel notifyChannel);

    /**
     * 批量删除通知渠道
     *
     * @param ids 需要删除的通知渠道主键集合
     * @return 结果
     */
    public int deleteNotifyChannelByIds(Long[] ids);

    /**
     * 删除通知渠道信息
     *
     * @param id 通知渠道主键
     * @return 结果
     */
    public int deleteNotifyChannelById(Long id);

    /**
     * 查询通知渠道和服务商
     * @return
     */
    List<ChannelProviderVO> listChannel();

    /**
     * 获取消息通知渠道参数信息
     * @param channelType 渠道类型
     * @param: provider 服务商
     * @return 结果集
     */
    List<NotifyConfigVO> getConfigContent(String channelType, String provider);

    /**
     * 查询通知渠道列表
     *
     * @param notifyChannel 通知渠道
     * @return 通知渠道分页集合
     */
    Page<NotifyChannelVO> pageNotifyChannelVO(NotifyChannel notifyChannel);

    /**
     * 根据ID查询通知渠道（带Redis缓存）
     * @param id 渠道ID
     * @return 通知渠道
     */
    NotifyChannel getByIdWithCache(Long id);

    /**
     * 根据ID清除通知渠道缓存
     * @param id 渠道ID
     */
    void clearCacheById(Long id);
}
