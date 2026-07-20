package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.Card;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.dto.TrafficInfoDTO;
import com.fastbee.iot.model.vo.CardStatisticsVO;
import com.fastbee.iot.model.vo.CardVO;

/**
 * 物联网卡Service接口
 *
 * @author fastbee
 * @date 2025-11-12
 */
public interface ICardService extends IService<Card>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询物联网卡列表
     *
     * @param card 物联网卡
     * @return 物联网卡分页集合
     */
    Page<CardVO> pageCardVO(Card card);

    /**
     * 查询物联网卡列表
     *
     * @param card 物联网卡
     * @return 物联网卡集合
     */
    List<CardVO> listCardVO(Card card);

    /**
     * 查询物联网卡
     *
     * @param id 主键
     * @return 物联网卡
     */
     Card selectCardById(Long id);

    /**
     * 查询物联网卡
     *
     * @param card 物联网卡
     * @return 物联网卡
     */
    CardVO queryByIdWithCache(Card card);

    /**
     * 新增物联网卡
     *
     * @param card 物联网卡
     * @return 是否新增成功
     */
    Boolean insertWithCache(Card card);

    /**
     * 修改物联网卡
     *
     * @param card 物联网卡
     * @return 是否修改成功
     */
    Boolean updateWithCache(Card card);

    /**
     * 校验并批量删除物联网卡信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * 同步卡信息
     *
     * @param cardVO
     * @return {@link Card }
     */
    Card syncCardInfo(CardVO cardVO);


    /**
     * 获取流量信息
     *
     * @param iccid
     * @return {@link TrafficInfoDTO }
     */
    Card syncTrafficInfo(String iccid, Long cardPlatformId);


    /**
     * 激活
     *
     * @param iccid
     * @return boolean
     */
    boolean activate(String iccid);


    /**
     * 停用卡片
     *
     * @param iccid
     * @return boolean
     */
    boolean suspend(String iccid);


    /**
     * 恢复卡片
     *
     * @param iccid
     * @return boolean
     */
    boolean resume(String iccid);

    /**
     * 检查流量告警
     */
    void checkFlowWarning();

    CardStatisticsVO statistics();

    /**
     * 设备上报iccid
     *
     * @param device       设备
     * @param iccid
     * @param cardPlatformId 卡平台ID
     */
    void deviceReportIccId(Device device, String iccid, Long cardPlatformId);

    String selectIccIdByDeviceId(Long deviceId);

    CardVO syncStatus(String iccid, Long cardPlatformId) throws Exception;

    List<CardVO> selectCardAndPlatformList(CardVO cardVO);
}
