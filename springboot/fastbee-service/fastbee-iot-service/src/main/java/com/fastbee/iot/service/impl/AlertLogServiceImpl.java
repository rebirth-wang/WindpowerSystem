package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.enums.AlertLogStatusEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.AlertLogConvert;
import com.fastbee.iot.domain.AlertLog;
import com.fastbee.iot.mapper.AlertLogMapper;
import com.fastbee.iot.model.DeviceAlertCount;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.AlertLogVO;
import com.fastbee.iot.service.IAlertLogService;

/**
 * 设备告警Service业务层处理
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Service
public class AlertLogServiceImpl extends ServiceImpl<AlertLogMapper, AlertLog> implements IAlertLogService {

    @Resource
    private AlertLogMapper alertLogMapper;

    /**
     * 查询设备告警
     *
     * @param alertLog 设备告警
     * @return 设备告警
     */
    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public AlertLog selectAlertLogByAlertLogId(AlertLog alertLog) {
        LambdaQueryWrapper<AlertLog> wrapper = this.buildQueryWrapper(alertLog);
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            // 数据范围过滤
            if (ObjectUtil.isNotEmpty(alertLog.getParams().get(DataScopeAspect.DATA_SCOPE))) {
                wrapper.apply((String) alertLog.getParams().get(DataScopeAspect.DATA_SCOPE));
            }
        } else {
            wrapper.eq(AlertLog::getUserId, user.getUserId());
        }
        return alertLogMapper.selectOne(wrapper);
    }

    /**
     * 查询设备告警日志列表
     *
     * @param alertLogVO 设备告警日志
     * @return 设备告警日志
     */
    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public Page<AlertLogVO> pageAlertLogVO(AlertLogVO alertLogVO) {
        AlertLog alertLog = AlertLogConvert.INSTANCE.convertAlertLog(alertLogVO);
        SysUser user = getLoginUser().getUser();
        LambdaQueryWrapper<AlertLog> lqw = buildQueryWrapper(alertLog);
        // 查询所属机构
        // 兼容组态多租户分享
        if (null != alertLogVO.getDeptUserId()) {
            lqw.eq(AlertLog::getUserId, alertLogVO.getDeptUserId());
        } else {
            if (null != user.getDeptId()) {
                // 数据范围过滤
                if (ObjectUtil.isNotEmpty(alertLogVO.getParams().get(DataScopeAspect.DATA_SCOPE))) {
                    lqw.apply((String) alertLogVO.getParams().get(DataScopeAspect.DATA_SCOPE));
                }
            } else {
                lqw.eq(AlertLog::getUserId, user.getUserId());
            }
        }
        lqw.orderByDesc(AlertLog::getCreateTime);
        Page<AlertLog> alertLogPage = baseMapper.selectPage(new Page<>(alertLogVO.getPageNum(), alertLogVO.getPageSize()), lqw);
        return AlertLogConvert.INSTANCE.convertAlertLogVOPage(alertLogPage);
    }

    @Override
    public List<AlertLog> selectAlertLogListByCreateBy(Long sceneId, String remark, Integer status) {
        AlertLog alertLog = new AlertLog();
        alertLog.setSceneId(sceneId);
        alertLog.setStatus(status);
        alertLog.setRemark(remark);
        LambdaQueryWrapper<AlertLog> queryWrapper = this.buildQueryWrapper(alertLog);
        queryWrapper.orderByDesc(AlertLog::getCreateTime);
        return alertLogMapper.selectList(queryWrapper);
    }

    /**
     * 查询设备告警列表
     *
     * @param alertLog 设备告警
     * @return 设备告警
     */
    @Override
    public Long selectAlertLogListCount(AlertLog alertLog) {
        return alertLogMapper.selectAlertLogListCount(alertLog);
    }

    @Override
    public List<DeviceAlertCount> selectDeviceAlertCount(List<String> serialNumberList) {
        return alertLogMapper.selectDeviceAlertCount(serialNumberList);
    }

    @Override
    public DeviceAlertCount selectDeviceAlertCountBySN(String serialNumber) {
        return alertLogMapper.selectDeviceAlertCountBySN(serialNumber);
    }

    @Override
    public List<DeviceAlertCount> selectSceneAlertCount() {
        return alertLogMapper.selectSceneAlertCount();
    }

    @Override
    public DeviceAlertCount selectSceneAlertCountBySceneId(String sceneId) {
        return alertLogMapper.selectSceneAlertCountBySceneId(sceneId);
    }

    /**
     * 新增设备告警
     *
     * @param alertLog 设备告警
     * @return 结果
     */
    @Override
    public int insertAlertLog(AlertLog alertLog) {
        alertLog.setCreateTime(DateUtils.getNowDate());
        return alertLogMapper.insert(alertLog);
    }

    @Override
    public int insertAlertLogBatch(List<AlertLog> alertLogList) {
        return alertLogMapper.insertBatch(alertLogList) ? 1 : 0;
    }

    /**
     * 修改设备告警
     *
     * @param alertLog 设备告警
     * @return 结果
     */
    @Override
    public int updateAlertLog(AlertLog alertLog) {
        alertLog.setUpdateTime(DateUtils.getNowDate());
        if (alertLog.getStatus() != null && !AlertLogStatusEnum.PROCESSING.getStatus().equals(alertLog.getStatus())) {
            if (alertLog.getRemark() == null) {
                if (AlertLogStatusEnum.PENDING.getStatus().equals(alertLog.getStatus())) {
                    alertLog.setRemark("无需处理");
                } else if (AlertLogStatusEnum.PROCESSED.getStatus().equals(alertLog.getStatus())) {
                    alertLog.setRemark("已处理");
                } else {
                    alertLog.setRemark("无");
                }
            }
        }
        return alertLogMapper.updateById(alertLog);
    }

    @Override
    public int updateAlertLogStatus(AlertLog alertLog) {
        LambdaUpdateWrapper<AlertLog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AlertLog::getStatus, alertLog.getStatus());
        updateWrapper.set(AlertLog::getUpdateTime, DateUtils.getNowDate());
        updateWrapper.set(AlertLog::getRemark, alertLog.getRemark());
        updateWrapper.eq(StringUtils.isNotBlank(alertLog.getSerialNumber()), AlertLog::getSerialNumber, alertLog.getSerialNumber());
        updateWrapper.eq(AlertLog::getSceneId, alertLog.getSceneId());
        return this.update(updateWrapper) ? 1 : 0;
    }

    /**
     * 通过设备编号删除设备告警信息
     *
     * @param SerialNumber 设备告警主键
     * @return 结果
     */
    @Override
    public int deleteAlertLogBySerialNumber(String SerialNumber) {
        LambdaQueryWrapper<AlertLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlertLog::getSerialNumber, SerialNumber);
        return alertLogMapper.delete(queryWrapper);
    }

    @Override
    public List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam) {
        Date beginTime = null;
        Date endTime = null;
        if (dataCenterParam.getBeginTime() != null && dataCenterParam.getBeginTime() != "" && dataCenterParam.getEndTime() != null && dataCenterParam.getEndTime() != "") {
            beginTime = parseTime(dataCenterParam.getBeginTime());
            endTime = parseTime(dataCenterParam.getEndTime());
        }
        return alertLogMapper.countAlertProcess(dataCenterParam, beginTime, endTime);
    }

    private Date parseTime(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(time);
        } catch (ParseException e) {
            throw new IllegalArgumentException("时间格式错误: " + time, e);
        }
    }

    @Override
    public List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam) {
        Date beginTime = null;
        Date endTime = null;
        if (dataCenterParam.getBeginTime() != null && dataCenterParam.getBeginTime() != "" && dataCenterParam.getEndTime() != null && dataCenterParam.getEndTime() != "") {
            beginTime = parseTime(dataCenterParam.getBeginTime());
            endTime = parseTime(dataCenterParam.getEndTime());
        }
        return alertLogMapper.countAlertLevel(dataCenterParam, beginTime, endTime);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AlertLog entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    private LambdaQueryWrapper<AlertLog> buildQueryWrapper(AlertLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<AlertLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != query.getAlertLogId(), AlertLog::getAlertLogId, query.getAlertLogId());
        lqw.like(StringUtils.isNotBlank(query.getAlertName()), AlertLog::getAlertName, query.getAlertName());
        lqw.eq(query.getAlertLevel() != null, AlertLog::getAlertLevel, query.getAlertLevel());
        lqw.eq(query.getStatus() != null, AlertLog::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), AlertLog::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getProductId() != null, AlertLog::getProductId, query.getProductId());
        lqw.eq(StringUtils.isNotBlank(query.getDetail()), AlertLog::getDetail, query.getDetail());
        lqw.eq(query.getUserId() != null, AlertLog::getUserId, query.getUserId());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), AlertLog::getDeviceName, query.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), AlertLog::getRemark, query.getRemark());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), AlertLog::getCreateBy, query.getCreateBy());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(AlertLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
