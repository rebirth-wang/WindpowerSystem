package com.fastbee.media.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.media.domain.BindingCommonChannel;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.vo.CommonChannelVO;

/**
 * 监控视频通道信息Service接口
 *
 * @author fastbee
 * @date 2026-01-30
 */
public interface ICommonChannelService extends IService<CommonChannel>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询监控视频通道信息列表
     *
     * @param commonChannel 监控视频通道信息
     * @return 监控视频通道信息分页集合
     */
    Page<CommonChannelVO> pageCommonChannelVO(CommonChannel commonChannel);

    /**
     * 查询监控视频通道信息列表
     *
     * @param commonChannel 监控视频通道信息
     * @return 监控视频通道信息集合
     */
    List<CommonChannelVO> listCommonChannelVO(CommonChannel commonChannel);

    /**
     * 查询监控视频通道信息
     *
     * @param id 主键
     * @return 监控视频通道信息
     */
    CommonChannel selectCommonChannelById(Long id);
    CommonChannel selectCommonChannelByDeviceChannelId(String deviceId, String channelId);
    CommonChannel selectCommonChannelByChannelId(String channelId);

    /**
     * 查询监控视频通道信息
     *
     * @param id 主键
     * @return 监控视频通道信息
     */
    CommonChannel queryByIdWithCache(Long id);

    /**
     * 新增监控视频通道信息
     *
     * @param commonChannel 监控视频通道信息
     * @return 是否新增成功
     */
    Boolean insertWithCache(CommonChannel commonChannel);

    /**
     * 修改监控视频通道信息
     *
     * @param commonChannel 监控视频通道信息
     * @return 是否修改成功
     */
    Boolean updateWithCache(CommonChannel commonChannel);

    /**
     * 新增或修改监控视频通道信息
     *
     * @param commonChannel 监控视频通道信息
     * @return 是否保存成功
     */
    Boolean upsertWithCache(CommonChannel commonChannel);

    /**
     * 校验并批量删除监控视频通道信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /**
     * 根据channelId获取绑定的设备或场景
     * @param channelId
     * @return
     */
    public BindingCommonChannel getBindingChannel(String channelId);

    /**
     * @description: 查询设备或场景关联通道
     * @param: serialNumber 设备编号
     * @param: sceneModelId  场景id
     * @return: java.util.List<com.fastbee.sip.domain.SipDeviceChannel>
     */
    List<CommonChannelVO> listRelDeviceOrScene(String serialNumber, Long sceneModelId);

    /** 自定义代码区域 END**/

}
