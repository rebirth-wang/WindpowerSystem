package com.fastbee.system.service.impl;

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
import com.fastbee.system.convert.SysOperLogConvert;
import com.fastbee.system.domain.SysOperLog;
import com.fastbee.system.domain.vo.SysOperLogVO;
import com.fastbee.system.mapper.SysOperLogMapper;
import com.fastbee.system.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper,SysOperLog> implements ISysOperLogService
{
    @Resource
    private SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog)
    {
        operLog.setOperTime(DateUtils.getNowDate());
        operLogMapper.insert(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLogVO> selectOperLogList(SysOperLogVO operLog)
    {
        operLog.setOperTime(DateUtils.getNowDate());
        LambdaQueryWrapper<SysOperLog> lqw = buildQueryWrapper(operLog);
        List<SysOperLog> operLogList = operLogMapper.selectList(lqw);
        return SysOperLogConvert.INSTANCE.convertSysOperLogVOList(operLogList);
    }

    /**
     * 查询操作日志记录分页列表
     *
     * @param sysOperLog 操作日志记录
     * @return 操作日志记录
     */
    @Override
    public Page<SysOperLogVO> pageSysOperLogVO(SysOperLogVO sysOperLog) {
        LambdaQueryWrapper<SysOperLog> lqw = buildQueryWrapper(sysOperLog);
        lqw.orderByDesc(SysOperLog::getOperTime);
        Page<SysOperLog> sysOperLogPage = baseMapper.selectPage(new Page<>(sysOperLog.getPageNum(), sysOperLog.getPageSize()), lqw);
        return SysOperLogConvert.INSTANCE.convertSysOperLogVOPage(sysOperLogPage);
    }


    private LambdaQueryWrapper<SysOperLog> buildQueryWrapper(SysOperLogVO query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysOperLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getOperId() != null, SysOperLog::getOperId, query.getOperId());
        lqw.like(StringUtils.isNotBlank(query.getTitle()), SysOperLog::getTitle, query.getTitle());
        lqw.eq(query.getBusinessType() != null, SysOperLog::getBusinessType, query.getBusinessType());
        lqw.in(query.getBusinessTypes() != null && query.getBusinessTypes().length != 0, SysOperLog::getBusinessType, query.getBusinessTypes());
        lqw.eq(StringUtils.isNotBlank(query.getMethod()), SysOperLog::getMethod, query.getMethod());
        lqw.eq(StringUtils.isNotBlank(query.getRequestMethod()), SysOperLog::getRequestMethod, query.getRequestMethod());
        lqw.eq(query.getOperatorType() != null, SysOperLog::getOperatorType, query.getOperatorType());
        lqw.like(StringUtils.isNotBlank(query.getOperName()), SysOperLog::getOperName, query.getOperName());
        lqw.like(StringUtils.isNotBlank(query.getDeptName()), SysOperLog::getDeptName, query.getDeptName());
        lqw.eq(StringUtils.isNotBlank(query.getOperUrl()), SysOperLog::getOperUrl, query.getOperUrl());
        lqw.eq(StringUtils.isNotBlank(query.getOperIp()), SysOperLog::getOperIp, query.getOperIp());
        lqw.eq(StringUtils.isNotBlank(query.getOperLocation()), SysOperLog::getOperLocation, query.getOperLocation());
        lqw.eq(StringUtils.isNotBlank(query.getOperParam()), SysOperLog::getOperParam, query.getOperParam());
        lqw.eq(StringUtils.isNotBlank(query.getJsonResult()), SysOperLog::getJsonResult, query.getJsonResult());
        lqw.eq(query.getStatus() != null, SysOperLog::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getErrorMsg()), SysOperLog::getErrorMsg, query.getErrorMsg());
        lqw.eq(query.getOperTime() != null, SysOperLog::getOperTime, query.getOperTime());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysOperLog::getOperTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }


    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds)
    {
        return operLogMapper.deleteBatchIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId)
    {
        return operLogMapper.selectById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
        operLogMapper.cleanOperLog();
    }
}
