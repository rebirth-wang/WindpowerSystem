package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

import com.fastbee.iot.domain.ModbusJob;
import com.fastbee.iot.model.vo.ModbusJobVO;

/**
 * 轮训任务列Service接口
 *
 * @author kerwincui
 * @date 2024-07-05
 */
public interface IModbusJobService extends IService<ModbusJob>
{
    /**
     * 查询轮训任务列
     *
     * @param taskId 轮训任务列主键
     * @return 轮训任务列
     */
    public ModbusJob selectModbusJobByTaskId(Long taskId);

    /**
     * 分页查询
     * @param modbusJobVO VO类
     * @return
     */
    Page<ModbusJobVO> pageModbusJobVO(ModbusJobVO modbusJobVO);

    /**
     * 查询轮训任务列列表
     *
     * @param modbusJobVO 轮训任务列
     * @return 轮训任务列集合
     */
    public List<ModbusJobVO> selectModbusJobList(ModbusJobVO modbusJobVO);

    /**
     * 新增轮训任务列
     *
     * @param modbusJobVO 轮训任务列
     * @return 结果
     */
    public int insertModbusJob(ModbusJobVO modbusJobVO);

    /**
     * 修改轮训任务列
     *
     * @param modbusJob 轮训任务列
     * @return 结果
     */
    public Boolean updateModbusJob(ModbusJob modbusJob);

    /**
     * 批量删除轮训任务列
     *
     * @param taskIds 需要删除的轮训任务列主键集合
     * @return 结果
     */
    public int deleteModbusJobByTaskIds(Long[] taskIds);

    /**
     * 删除轮训任务列信息
     *
     * @param modbusJobVO 轮训任务列主键
     * @return 结果
     */
    public int deleteModbusJobByTaskId(ModbusJobVO modbusJobVO) throws SchedulerException;

    /**
     * 根据设备类型获取所有轮询任务
     * @param deviceType
     * @param serialNumber
     * @return
     */
    List<ModbusJobVO> selectDevicesJobByDeviceType(String serialNumber);
}
