package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.AlertNotifyTemplate;
import com.fastbee.iot.domain.AlertScene;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.AlertSceneSendVO;
import com.fastbee.iot.model.vo.SceneVO;
import com.fastbee.notify.domain.NotifyTemplate;

/**
 * 设备告警Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface AlertMapper extends BaseMapperX<Alert>
{

    /**
     * 获取告警关联的场景列表
     * @param alertId
     * @return
     */
    public List<Scene> selectScenesByAlertId(Long alertId);

    /**
     * 批量插入告警场景
     * @param alerts
     * @return
     */
    public int insertAlertSceneList(List<AlertScene> alerts);

    /**
     * 根据告警ID批量删除告警场景
     * @param alertIds
     * @return
     */
    public int deleteAlertSceneByAlertIds(Long[]  alertIds);

    /**
     * 根据场景ID批量删除告警场景
     * @param sceneIds
     * @return
     */
    public int deleteAlertSceneBySceneIds(Long[] sceneIds);

    /**
     * 批量插入告警通知模版
     * @param alertNotifyTemplateList
     * @return
     */
    public int insertAlertNotifyTemplateList(List<AlertNotifyTemplate> alertNotifyTemplateList);

    /**
     * 根据告警ID批量删除告警场景
     * @param alertIds
     * @return
     */
    public int deleteAlertNotifyTemplateByAlertIds(Long[]  alertIds);

    /**
     * 查询告警通知模版
     * @param alertId 告警id
     * @return java.util.List<com.fastbee.iot.domain.AlertNotifyTemplate>
     */
    List<AlertNotifyTemplate> selectAlertNotifyTemplateList(Long alertId);

    /**
     * 获取告警关联的通知模版列表
     * @param alertId 告警id
     * @return
     */
    public List<NotifyTemplate> selectNotifyTemplateListByAlertId(Long alertId);

    /**
     * 获取场景关联的告警列表
     * @param sceneId 场景id
     * @return java.util.List<com.fastbee.iot.domain.AlertScene>
     */
    List<AlertScene> selectAlertSceneListBySceneId(Long sceneId);

    /**
     * 获取场景告警通知参数
     *
     * @param sceneId 场景id
     * @return 设备告警集合
     */
    List<AlertSceneSendVO> listByAlertIds(Long sceneId);

    Page<Alert> selectAlertByScene(Page<Alert> page, @Param("scene") SceneVO scene);

    int deleteAlertSceneBySceneAndAlertIds(@Param("sceneId") Long sceneId, @Param("alertIds") List<Long> alertIds);
}
