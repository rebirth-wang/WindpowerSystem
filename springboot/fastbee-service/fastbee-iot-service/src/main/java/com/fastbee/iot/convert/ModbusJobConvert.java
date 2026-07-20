package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ModbusJob;
import com.fastbee.iot.model.vo.ModbusJobVO;

/**
 * 轮训任务列Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface ModbusJobConvert
{

    ModbusJobConvert INSTANCE = Mappers.getMapper(ModbusJobConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param modbusJob
     * @return 轮训任务列集合
     */
    ModbusJobVO convertModbusJobVO(ModbusJob modbusJob);

    /**
     * VO类转换为实体类集合
     *
     * @param modbusJobVO
     * @return 轮训任务列集合
     */
    ModbusJob convertModbusJob(ModbusJobVO modbusJobVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param modbusJobList
     * @return 轮训任务列集合
     */
    List<ModbusJobVO> convertModbusJobVOList(List<ModbusJob> modbusJobList);

    /**
     * VO类转换为实体类
     *
     * @param modbusJobVOList
     * @return 轮训任务列集合
     */
    List<ModbusJob> convertModbusJobList(List<ModbusJobVO> modbusJobVOList);

    /**
     * 分页转换
     * @param modbusJobPage 实体类分页
     * @return ModbusJobVO
     */
    Page<ModbusJobVO> convertModbusJobVOPage(Page<ModbusJob> modbusJobPage);
}
