package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.model.vo.AlertVO;

/**
 * 设备告警Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-03
 */
@Mapper
public interface AlertConvert
{

    AlertConvert INSTANCE = Mappers.getMapper(AlertConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param alert
     * @return 设备告警集合
     */
    AlertVO convertAlertVO(Alert alert);

    /**
     * VO类转换为实体类集合
     *
     * @param alertVO
     * @return 设备告警集合
     */
    Alert convertAlert(AlertVO alertVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param alertList
     * @return 设备告警集合
     */
    List<AlertVO> convertAlertVOList(List<Alert> alertList);

    /**
     * VO类转换为实体类
     *
     * @param alertVOList
     * @return 设备告警集合
     */
    List<Alert> convertAlertList(List<AlertVO> alertVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param alertPage
     * @return 设备告警分页
     */
    Page<AlertVO> convertAlertVOPage(Page<Alert> alertPage);

    /**
     * VO类转换为实体类
     *
     * @param alertVOPage
     * @return 设备告警分页
     */
    Page<Alert> convertAlertPage(Page<AlertVO> alertVOPage);
}
