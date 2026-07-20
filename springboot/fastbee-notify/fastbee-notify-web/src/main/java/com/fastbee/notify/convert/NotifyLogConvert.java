package com.fastbee.notify.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.notify.domain.NotifyLog;
import com.fastbee.notify.vo.NotifyLogVO;

/**
 * 通知日志Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface NotifyLogConvert
{

    NotifyLogConvert INSTANCE = Mappers.getMapper(NotifyLogConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param notifyLog
     * @return 通知日志集合
     */
    NotifyLogVO convertNotifyLogVO(NotifyLog notifyLog);

    /**
     * VO类转换为实体类集合
     *
     * @param notifyLogVO
     * @return 通知日志集合
     */
    NotifyLog convertNotifyLog(NotifyLogVO notifyLogVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param notifyLogList
     * @return 通知日志集合
     */
    List<NotifyLogVO> convertNotifyLogVOList(List<NotifyLog> notifyLogList);

    /**
     * VO类转换为实体类
     *
     * @param notifyLogVOList
     * @return 通知日志集合
     */
    List<NotifyLog> convertNotifyLogList(List<NotifyLogVO> notifyLogVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param notifyLogPage
     * @return 通知日志分页
     */
    Page<NotifyLogVO> convertNotifyLogVOPage(Page<NotifyLog> notifyLogPage);

    /**
     * VO类转换为实体类
     *
     * @param notifyLogVOPage
     * @return 通知日志分页
     */
    Page<NotifyLog> convertNotifyLogPage(Page<NotifyLogVO> notifyLogVOPage);
}
