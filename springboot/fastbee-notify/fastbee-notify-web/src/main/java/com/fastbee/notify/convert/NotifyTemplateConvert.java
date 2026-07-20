package com.fastbee.notify.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.notify.domain.NotifyTemplate;
import com.fastbee.notify.vo.NotifyTemplateVO;

/**
 * 通知模版Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface NotifyTemplateConvert
{

    NotifyTemplateConvert INSTANCE = Mappers.getMapper(NotifyTemplateConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param notifyTemplate
     * @return 通知模版集合
     */
    NotifyTemplateVO convertNotifyTemplateVO(NotifyTemplate notifyTemplate);

    /**
     * VO类转换为实体类集合
     *
     * @param notifyTemplateVO
     * @return 通知模版集合
     */
    NotifyTemplate convertNotifyTemplate(NotifyTemplateVO notifyTemplateVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param notifyTemplateList
     * @return 通知模版集合
     */
    List<NotifyTemplateVO> convertNotifyTemplateVOList(List<NotifyTemplate> notifyTemplateList);

    /**
     * VO类转换为实体类
     *
     * @param notifyTemplateVOList
     * @return 通知模版集合
     */
    List<NotifyTemplate> convertNotifyTemplateList(List<NotifyTemplateVO> notifyTemplateVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param notifyTemplatePage
     * @return 通知模版分页
     */
    Page<NotifyTemplateVO> convertNotifyTemplateVOPage(Page<NotifyTemplate> notifyTemplatePage);

    /**
     * VO类转换为实体类
     *
     * @param notifyTemplateVOPage
     * @return 通知模版分页
     */
    Page<NotifyTemplate> convertNotifyTemplatePage(Page<NotifyTemplateVO> notifyTemplateVOPage);
}
