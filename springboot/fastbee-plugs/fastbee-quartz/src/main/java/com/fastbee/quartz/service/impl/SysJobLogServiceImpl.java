package com.fastbee.quartz.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.quartz.convert.SysJobLogConvert;
import com.fastbee.quartz.domain.SysJobLog;
import com.fastbee.quartz.mapper.SysJobLogMapper;
import com.fastbee.quartz.service.ISysJobLogService;
import com.fastbee.quartz.vo.SysJobLogVO;

/**
 * 定时任务调度日志信息 服务层
 *
 * @author ruoyi
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper,SysJobLog> implements ISysJobLogService
{
    @Resource
    private SysJobLogMapper jobLogMapper;

    /**
     * 获取quartz调度器日志的计划任务
     *
     * @param jobLog 调度日志信息
     * @return 调度任务日志集合
     */
    @Override
    public List<SysJobLogVO> selectJobLogList(SysJobLog jobLog)
    {
        LambdaQueryWrapper<SysJobLog> lqw = buildQueryWrapper(jobLog);
        List<SysJobLog> sysJobLogList = baseMapper.selectList(lqw);
        return SysJobLogConvert.INSTANCE.convertSysJobLogVOList(sysJobLogList);
    }

    /**
     * 查询定时任务调度日志分页列表
     *
     * @param sysJobLog 定时任务调度日志
     * @return 定时任务调度日志
     */
    @Override
    public Page<SysJobLogVO> pageSysJobLogVO(SysJobLog sysJobLog) {
        LambdaQueryWrapper<SysJobLog> lqw = buildQueryWrapper(sysJobLog);
        Page<SysJobLog> sysJobLogPage = baseMapper.selectPage(new Page<>(sysJobLog.getPageNum(), sysJobLog.getPageSize()), lqw);
        return SysJobLogConvert.INSTANCE.convertSysJobLogVOPage(sysJobLogPage);
    }

    private LambdaQueryWrapper<SysJobLog> buildQueryWrapper(SysJobLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysJobLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getJobLogId() != null, SysJobLog::getJobLogId, query.getJobLogId());
        lqw.like(StringUtils.isNotBlank(query.getJobName()), SysJobLog::getJobName, query.getJobName());
        lqw.eq(StringUtils.isNotBlank(query.getJobGroup()), SysJobLog::getJobGroup, query.getJobGroup());
        lqw.like(StringUtils.isNotBlank(query.getInvokeTarget()), SysJobLog::getInvokeTarget, query.getInvokeTarget());
        lqw.eq(StringUtils.isNotBlank(query.getJobMessage()), SysJobLog::getJobMessage, query.getJobMessage());
        lqw.eq(query.getStatus() != null, SysJobLog::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getExceptionInfo()), SysJobLog::getExceptionInfo, query.getExceptionInfo());
        lqw.eq(query.getCreateTime() != null, SysJobLog::getCreateTime, query.getCreateTime());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysJobLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 通过调度任务日志ID查询调度信息
     *
     * @param jobLogId 调度任务日志ID
     * @return 调度任务日志对象信息
     */
    @Override
    public SysJobLog selectJobLogById(Long jobLogId)
    {
        return jobLogMapper.selectById(jobLogId);
    }

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    @Override
    public void addJobLog(SysJobLogVO jobLog)
    {
        jobLog.setCreateTime(DateUtils.getNowDate());
        jobLogMapper.insert(SysJobLogConvert.INSTANCE.convertSysJobLog(jobLog));
    }

    /**
     * 批量删除调度日志信息
     *
     * @param logIds 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteJobLogByIds(Long[] logIds)
    {
        return jobLogMapper.deleteBatchIds(Arrays.asList(logIds));
    }

    /**
     * 删除任务日志
     *
     * @param jobId 调度日志ID
     */
    @Override
    public int deleteJobLogById(Long jobId)
    {
        return jobLogMapper.deleteById(jobId);
    }

    /**
     * 清空任务日志
     */
    @Override
    public void cleanJobLog()
    {
        jobLogMapper.cleanJobLog();
    }
}
