package com.fastbee.notify.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.domin.notify.NotifyConfigVO;
import com.fastbee.common.extend.enums.NotifyChannelProviderEnum;
import com.fastbee.notify.domain.NotifyTemplate;
import com.fastbee.notify.vo.NotifyTemplateVO;

/**
 * 通知模版Service接口
 *
 * @author kerwincui
 * @date 2023-12-01
 */
public interface INotifyTemplateService extends IService<NotifyTemplate>
{
    /**
     * 查询通知模版
     *
     * @param notifyTemplate 通知模版
     * @return 通知模版
     */
    public NotifyTemplate selectNotifyTemplateById(NotifyTemplate notifyTemplate);

    /**
     * 查询通知模版列表
     *
     * @param notifyTemplate 通知模版
     * @return 通知模版分页集合
     */
    Page<NotifyTemplateVO> pageNotifyTemplateVO(NotifyTemplate notifyTemplate);


    /**
     * 新增通知模版
     *
     * @param notifyTemplate 通知模版
     * @return 结果
     */
    public AjaxResult insertNotifyTemplate(NotifyTemplate notifyTemplate);

    /**
     * 修改通知模版
     *
     * @param notifyTemplate 通知模版
     * @return 结果
     */
    public AjaxResult updateNotifyTemplate(NotifyTemplate notifyTemplate);

    /**
     * 批量删除通知模版
     *
     * @param ids 需要删除的通知模版主键集合
     * @return 结果
     */
    public int deleteNotifyTemplateByIds(Long[] ids);

    /**
     * 删除通知模版信息
     *
     * @param id 通知模版主键
     * @return 结果
     */
    public int deleteNotifyTemplateById(Long id);

    /**
     * 查询某一业务通知通道是否有启动的（业务编码唯一启用一个模板）
     * @param notifyTemplate 通知模板
     */
    public Integer countNormalTemplate(NotifyTemplate notifyTemplate);

    /**
     * 更新某一类型为不可用状态，选中的为可用状态
     * @param notifyTemplate 通知模板
     */
    public void updateTemplateStatus(NotifyTemplate notifyTemplate);

    /**
     * @description: 查询启用通知模板
     * @param: serviceCode  业务编码
     * @return: com.fastbee.notify.domain.NotifyTemplate
     */
    NotifyTemplate selectOnlyEnable(NotifyTemplate notifyTemplate);

    /**
     * @description: 获取消息通知模版参数信息
     * @author fastb
     * @date 2023-12-22 11:01
     * @version 1.0
     */
    List<NotifyConfigVO> getNotifyMsgParams(Long channelId, String msgType, String method);

    /**
     * @description: 统一获取模板参数内容变量，调用这个方法
     * @param: channelId
     * @return: java.lang.String
     */
    List<String> listVariables(String content, NotifyChannelProviderEnum notifyChannelProviderEnum);

    /**
     * 根据ID查询通知模板（带Redis缓存）
     * @param id 模板ID
     * @return 通知模板
     */
    NotifyTemplate getByIdWithCache(Long id);

    /**
     * 根据ID清除通知模板缓存
     * @param id 模板ID
     */
    void clearCacheById(Long id);

    /**
     * 获取模板变量列表（带Redis缓存）
     * @param content 模板内容
     * @param notifyChannelProviderEnum 渠道提供商枚举
     * @return 变量列表
     */
    List<String> listVariablesWithCache(String content, NotifyChannelProviderEnum notifyChannelProviderEnum);

    /**
     * @description: 获取告警微信小程序模板id
     * @param:
     * @return: java.lang.String
     */
    String getAlertWechatMini();

    /**
     * 获取唯一启用模版查询条件
     * 短信、语音、邮箱以业务编码+渠道保证唯一启用，微信、钉钉以业务编码+渠道+服务商保证唯一启用
     * @param: serviceCode
     * @param: channelType
     * @param: provider
     * @return com.fastbee.notify.domain.NotifyTemplate
     */
    NotifyTemplate getEnableQueryCondition(String serviceCode, String channelType, String provider, Long tenantId);

}
