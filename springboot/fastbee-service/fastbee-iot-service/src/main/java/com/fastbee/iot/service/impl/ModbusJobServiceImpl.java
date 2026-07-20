package com.fastbee.iot.service.impl;

import java.util.*;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.extend.enums.ModbusJobCommantEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.ModbusJobConvert;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.ModbusJob;
import com.fastbee.iot.mapper.ModbusJobMapper;
import com.fastbee.iot.model.scenemodel.JobCronCycleVO;
import com.fastbee.iot.model.vo.ModbusJobVO;
import com.fastbee.iot.service.IDeviceJobService;
import com.fastbee.iot.service.IModbusJobService;
import com.fastbee.iot.util.JobCronUtils;

/**
 * 轮训任务列Service业务层处理
 *
 * @author kerwincui
 * @date 2024-07-05
 */
@Service
public class ModbusJobServiceImpl extends ServiceImpl<ModbusJobMapper,ModbusJob> implements IModbusJobService {
    @Resource
    private ModbusJobMapper modbusJobMapper;
    @Resource
    private IDeviceJobService deviceJobService;

    /**
     * 查询轮训任务列
     *
     * @param taskId 轮训任务列主键
     * @return 轮训任务列
     */
    @Override
    public ModbusJob selectModbusJobByTaskId(Long taskId) {
        return modbusJobMapper.selectById(taskId);
    }

    @Override
    public Page<ModbusJobVO> pageModbusJobVO(ModbusJobVO modbusJobVO) {
        LambdaQueryWrapper<ModbusJob> queryWrapper = this.buildQueryWrapper(ModbusJobConvert.INSTANCE.convertModbusJob(modbusJobVO));
        Page<ModbusJob> modbusJobPage = baseMapper.selectPage(new Page<>(modbusJobVO.getPageNum(), modbusJobVO.getPageSize()), queryWrapper);
        if (0 == modbusJobPage.getTotal()) {
            return new Page<>();
        }
        Page<ModbusJobVO> modbusJobVoPage = ModbusJobConvert.INSTANCE.convertModbusJobVOPage(modbusJobPage);
        for (ModbusJobVO jobVO : modbusJobVoPage.getRecords()) {
            if (StringUtils.isNotEmpty(jobVO.getRemark())) {
                jobVO.setRemarkStr(JobCronUtils.handleCronCycle(1, jobVO.getRemark()).getDesc());
            }
        }
        return modbusJobVoPage;
    }

    /**
     * 查询轮训任务列列表
     *
     * @param modbusJobVO 轮训任务列
     * @return 轮训任务列
     */
    @Override
    public List<ModbusJobVO> selectModbusJobList(ModbusJobVO modbusJobVO) {
        ModbusJob modbusJob = ModbusJobConvert.INSTANCE.convertModbusJob(modbusJobVO);
        LambdaQueryWrapper<ModbusJob> queryWrapper = this.buildQueryWrapper(modbusJob);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(modbusJobVO.getSubDeviceList())) {
            queryWrapper.in(ModbusJob::getSerialNumber, modbusJobVO.getSubDeviceList());
        }
        List<ModbusJob> modbusJobList = modbusJobMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(modbusJobList)) {
            return new ArrayList<>();
        }
        List<ModbusJobVO> modbusJobVOList = ModbusJobConvert.INSTANCE.convertModbusJobVOList(modbusJobList);
        for (ModbusJobVO jobVO : modbusJobVOList) {
            if (StringUtils.isNotEmpty(jobVO.getRemark())) {
                jobVO.setRemarkStr(JobCronUtils.handleCronCycle(1, jobVO.getRemark()).getDesc());
            }
        }
        return modbusJobVOList;
    }

    /**
     * 新增轮训任务列
     *
     * @param modbusJobVO 轮训任务列
     * @return 结果
     */
    @Override
    public int insertModbusJob(ModbusJobVO modbusJobVO) {
        try {
                //直连设备/网关设备
                ModbusJob modbusJob = ModbusJobConvert.INSTANCE.convertModbusJob(modbusJobVO);
                if (StringUtils.isNotEmpty(modbusJob.getRemark()) && ModbusJobCommantEnum.POLLING.getType().equals(modbusJob.getCommandType())) {
                    DeviceJob deviceJob = new DeviceJob();
                    deviceJob.setDeviceId(modbusJob.getDeviceId());
                    deviceJob.setRemark(modbusJob.getRemark());
                    deviceJob.setJobType(DeviceJobTypeEnum.MODBUS_JOB.getType());
                    List<DeviceJob> list = deviceJobService.selectJobList(deviceJob);
                    if (CollectionUtils.isEmpty(list)) {
                        //创建定时任务
                        //如果不存在定时任务则新建
                        deviceJob.setJobName("modbus poll");
                        deviceJob.setJobGroup("MODBUS");
                        deviceJob.setMisfirePolicy("2");
                        deviceJob.setConcurrent("1");
                        deviceJob.setIsAdvance(0);
                        deviceJob.setStatus(0);
                        deviceJob.setSerialNumber(modbusJob.getSerialNumber());
                        deviceJob.setProductId(modbusJobVO.getProductId());
                        deviceJob.setCreateTime(DateUtils.getNowDate());
                        deviceJob.setCreateBy(modbusJob.getCreateBy());
                        //处理时间转换,这里原始的表达式放在remark
                        JobCronCycleVO jobCronCycleVO = JobCronUtils.handleCronCycle(1, deviceJob.getRemark());
                        deviceJob.setCronExpression(jobCronCycleVO.getCron());
                        deviceJobService.insertJob(deviceJob);
                        modbusJob.setJobId(deviceJob.getJobId());
                    } else {
                        DeviceJob job = list.get(0);
                        modbusJob.setJobId(job.getJobId());
                    }
                }
                modbusJob.setCreateTime(DateUtils.getNowDate());
                return modbusJobMapper.insert(modbusJob);
//            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 修改轮训任务列
     *
     * @param modbusJob 轮训任务列
     * @return 结果
     */
    @Override
    public Boolean updateModbusJob(ModbusJob modbusJob) {
        if (null != modbusJob.getTaskId()) {
            if ("[]".equals(modbusJob.getCommand())) {
                return this.removeById(modbusJob.getTaskId());
            }
            return this.updateById(modbusJob);
        } else {
            return this.save(modbusJob);
        }
    }

    /**
     * 批量删除轮训任务列
     *
     * @param taskIds 需要删除的轮训任务列主键
     * @return 结果
     */
    @Override
    public int deleteModbusJobByTaskIds(Long[] taskIds) {
        return modbusJobMapper.deleteBatchIds(Arrays.asList(taskIds));
    }

    /**
     * 删除轮训任务列信息
     *
     * @param modbusJobVO 轮训任务列主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteModbusJobByTaskId(ModbusJobVO modbusJobVO) {
        try {
            //先删除轮询任务
            int result = modbusJobMapper.deleteById(modbusJobVO.getTaskId());
            //查询是否有轮询任务
            if (Objects.nonNull(modbusJobVO.getJobId())) {
                LambdaQueryWrapper<ModbusJob> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ModbusJob::getJobId, modbusJobVO.getJobId());
                List<ModbusJob> modbusJobList = baseMapper.selectList(queryWrapper);
                if (CollectionUtils.isEmpty(modbusJobList)) {
                    //删除任务
                    deviceJobService.deleteJobByIds(new Long[]{modbusJobVO.getJobId()});
                }
            }
            return result;
        } catch (SchedulerException e) {
            return 0;
        }
    }


    /**
     * 根据设备类型获取所有轮询任务
     * @param serialNumber
     * @return
     */
    @Override
    public List<ModbusJobVO> selectDevicesJobByDeviceType(String serialNumber){
        ModbusJobVO modbusJobVO = new ModbusJobVO();
        modbusJobVO.setStatus(0);
        //直连设备直接查询
        modbusJobVO.setSerialNumber(serialNumber);
        modbusJobVO.setCommandType(ModbusJobCommantEnum.POLLING.getType());
        return this.selectModbusJobList(modbusJobVO);
    }

    private LambdaQueryWrapper<ModbusJob> buildQueryWrapper(ModbusJob query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ModbusJob> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getJobName()), ModbusJob::getJobName, query.getJobName());
        lqw.eq(query.getDeviceId() != null, ModbusJob::getDeviceId, query.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), ModbusJob::getSerialNumber, query.getSerialNumber());
        lqw.eq(StringUtils.isNotBlank(query.getCommand()), ModbusJob::getCommand, query.getCommand());
        lqw.eq(query.getJobId() != null, ModbusJob::getJobId, query.getJobId());
        lqw.eq(query.getStatus() != null, ModbusJob::getStatus, query.getStatus());
        lqw.eq(query.getCommandType() != null, ModbusJob::getCommandType, query.getCommandType());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ModbusJob::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModbusJob entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
