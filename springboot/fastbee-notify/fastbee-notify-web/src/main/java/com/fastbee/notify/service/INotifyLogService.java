package com.fastbee.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.notify.domain.NotifyLog;
import com.fastbee.notify.vo.NotifyLogVO;

/**
 * 通知日志Service接口
 *
 * @author kerwincui
 * @date 2023-12-16
 */
public interface INotifyLogService extends IService<NotifyLog> {
    /**
     * 查询通知日志
     *
     * @param notifyLog 通知日志
     * @return 通知日志
     */
    public NotifyLog selectNotifyLogById(NotifyLog notifyLog);

    /**
     * 查询通知日志列表
     *
     * @param notifyLog 通知日志
     * @return 通知日志分页集合
     */
    Page<NotifyLogVO> pageNotifyLogVO(NotifyLog notifyLog);

    /**
     * 新增通知日志
     *
     * @param notifyLog 通知日志
     * @return 结果
     */
    public int insertNotifyLog(NotifyLog notifyLog);

    /**
     * 修改通知日志
     *
     * @param notifyLog 通知日志
     * @return 结果
     */
    public int updateNotifyLog(NotifyLog notifyLog);

    /**
     * 批量删除通知日志
     *
     * @param ids 需要删除的通知日志主键集合
     * @return 结果
     */
    public int deleteNotifyLogByIds(Long[] ids);

    /**
     * 删除通知日志信息
     *
     * @param id 通知日志主键
     * @return 结果
     */
    public int deleteNotifyLogById(Long id);
}
