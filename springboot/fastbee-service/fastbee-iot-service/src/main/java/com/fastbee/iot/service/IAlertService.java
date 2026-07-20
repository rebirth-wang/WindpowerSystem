package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.AlertNotifyTemplate;
import com.fastbee.iot.domain.AlertScene;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.AlertVO;
import com.fastbee.iot.model.vo.SceneVO;
import com.fastbee.notify.domain.NotifyTemplate;

/**
 * 设备告警Service接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
public interface IAlertService extends IService<Alert>
{

    /**
     * 查询设备告警列表
     *
     * @param alert 设备告警
     * @return 设备告警分页集合
     */
    Page<AlertVO> pageAlertVO(Alert alert);

    /**
     * 查询设备告警
     *
     * @param alert 设备告警
     * @return 设备告警
     */
    public AlertVO selectAlertByAlertId(Alert alert);


    /**
     * 获取告警关联的场景列表
     * @param alertId
     * @return
     */
    public List<Scene> selectScenesByAlertId(Long alertId);

    /**
     * 新增设备告警
     *
     * @param alertVO 设备告警
     * @return 结果
     */
    public int insertAlert(AlertVO alertVO);

    /**
     * 修改设备告警
     *
     * @param alertVO 设备告警
     * @return 结果
     */
    public int updateAlert(AlertVO alertVO);

    /**
     * 批量删除设备告警
     *
     * @param alertIds 需要删除的设备告警主键集合
     * @return 结果
     */
    public int deleteAlertByAlertIds(Long[] alertIds);

    /**
     * 删除设备告警信息
     *
     * @param alertId 设备告警主键
     * @return 结果
     */
    public int deleteAlertByAlertId(Long alertId);

    /**
     * 修改设备告警状态
     * @param alertId 告警id
     * @param status 状态
     * @return 结果
     */
    int editStatus(Long alertId, Integer status);

    /**
     * 查询告警通知模版列表
     */
    List<NotifyTemplate> listNotifyTemplateByAlertId(Long alertId);

    /**
     * 查询告警关联的通知模版
     * @param alertId 告警id
     * @return java.util.List<com.fastbee.iot.domain.AlertNotifyTemplate>
     */
    List<AlertNotifyTemplate> listAlertNotifyTemplate(Long alertId);

    /**
     * 查询场景关联的告警
     * @param sceneId 场景id
     * @return java.util.List<com.fastbee.iot.domain.AlertScene>
     */
    List<AlertScene> listAlertScene(Long sceneId);

    /**
     * 获取场景告警通知参数
     *
     * @param sceneId 场景id
     * @return 设备告警集合
     */
    public List<AlertSceneSendVO> listByAlertIds(Long sceneId);

    /**
     * 通过场景id删除告警绑定关系
     * @param sceneIds
     * @return
     */
    int deleteAlertSceneBySceneIds(Long[] sceneIds);

    Page<Alert> listAlertByScene(SceneVO scene);
}
