package com.fastbee.media.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.media.domain.BindingCommonChannel;
import com.fastbee.media.domain.CommonChannel;

/**
 * 监控视频通道信息Mapper接口
 *
 * @author fastbee
 * @date 2026-01-30
 */
public interface CommonChannelMapper extends BaseMapperX<CommonChannel>
{
    /** 自定义代码区域 **/
    List<CommonChannel> selectChannelWithCivilCodeAndLength(@Param("deviceId") String deviceId, @Param("parentId") String parentId, @Param("length")Integer length);
    public List<CommonChannel> selectChannelByCivilCode(@Param("deviceId") String deviceId, @Param("parentId") String parentId);
    List<CommonChannel> selectChannelWithoutCiviCode(String deviceId);
    Integer getChannelMinLength(String deviceId);
    /**
     * 根据channelId获取绑定的设备或场景
     * @param channelId
     * @return
     */
    public BindingCommonChannel getBindingChannel(String channelId);

    /**
     * @description: 查询设备关联通道
     * @param: serialNumber 设备编号
     * @return: java.util.List<com.fastbee.media.domain.CommonChannel>
     */
    List<CommonChannel> selectDeviceRelSipDeviceChannelList(String serialNumber);

    /**
     * @description: 查询场景关联通道
     * @param: sceneModelId  场景id
     * @return: java.util.List<com.fastbee.media.domain.CommonChannel>
     */
    List<CommonChannel> selectSceneRelSipDeviceChannelList(Long sceneModelId);

    /** 自定义代码区域 END**/
}
