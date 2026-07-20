package com.fastbee.iot.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.fastbee.common.enums.DataEnum;
import com.fastbee.common.mybatis.LambdaQueryWrapperX;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.FunctionLog;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.FunctionLogMapper;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.ThingsModelItem.EnumItem;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.service.IFunctionLogService;

/**
 * 设备服务下发日志Service业务层处理
 *
 * @author kerwincui
 * @date 2022-10-22
 */
@Service
public class FunctionLogServiceImpl extends ServiceImpl<FunctionLogMapper, FunctionLog> implements IFunctionLogService {
    @Resource
    private FunctionLogMapper functionLogMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private ITSLCache itslCache;

    /**
     * 查询设备服务下发日志
     *
     * @param id 设备服务下发日志主键
     * @return 设备服务下发日志
     */
    @Override
    public FunctionLog selectFunctionLogById(Long id) {
        return functionLogMapper.selectById(id);
    }

    /**
     * 查询设备服务下发日志列表
     *
     * @param functionLogVO 设备服务下发日志
     * @return 设备服务下发日志
     */
    @Override
    public Page<FunctionLogVO> selectFunctionLogList(FunctionLogVO functionLogVO) {
        Page<FunctionLogVO> functionLogVOPage = functionLogMapper.selectFunctionLogList(new Page<>(functionLogVO.getPageNum(), functionLogVO.getPageSize()), functionLogVO);
        if (0 == functionLogVOPage.getTotal()) {
            return new Page<>();
        }
        List<String> serialNumberList = functionLogVOPage.getRecords().stream().map(FunctionLogVO::getSerialNumber).collect(Collectors.toList());
        LambdaQueryWrapperX<Device> queryWrapperX = new LambdaQueryWrapperX<>();
        queryWrapperX.inIfPresent(Device::getSerialNumber, serialNumberList);
        List<Device> deviceList = deviceMapper.selectList(queryWrapperX);
        if (CollectionUtils.isEmpty(deviceList)) {
            return functionLogVOPage;
        }
        Map<String, Device> deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getSerialNumber, Function.identity(), (o1, o2) -> o2));
        for (FunctionLogVO logVO : functionLogVOPage.getRecords()) {
            Device device = deviceMap.get(logVO.getSerialNumber());
            if (Objects.isNull(device)) {
                continue;
            }
            logVO.setDeviceName(device.getDeviceName());
            ThingsModelValueItem valueItem = itslCache.getSingleThingModels(device.getProductId(), logVO.getIdentify());
            if (Objects.isNull(valueItem) || valueItem.getDatatype() == null) {
                continue;
            }
            logVO.setDataType(valueItem.getDatatype().getType());
            if (DataEnum.ENUM.getType().equals(valueItem.getDatatype().getType())) {
                List<EnumItem> enumList = valueItem.getDatatype().getEnumList();
                if (CollectionUtils.isNotEmpty(enumList)) {
                    String text = enumList.stream().filter(e -> e.getValue().equals(logVO.getFunValue())).map(EnumItem::getText).findAny().orElse(null);
                    if (StringUtils.isNotBlank(text)) {
                        logVO.setFunValue(text);
                    }
                }
            }
        }
        return functionLogVOPage;
    }

    /**
     * 新增设备服务下发日志
     *
     * @param functionLog 设备服务下发日志
     * @return 结果
     */
    @Override
    public int insertFunctionLog(FunctionLog functionLog) {
        functionLog.setCreateTime(DateUtils.getNowDate());
        return functionLogMapper.insert(functionLog);
    }

    /**
     * 批量插入数据
     *
     * @param list
     */
    @Override
    public void insertBatch(List<FunctionLog> list) {
        functionLogMapper.insertBatch(list);
    }

    /**
     * 修改设备服务下发日志
     *
     * @param functionLog 设备服务下发日志
     * @return 结果
     */
    @Override
    public int updateFunctionLog(FunctionLog functionLog) {
        return functionLogMapper.updateById(functionLog);
    }

    /**
     * 批量删除设备服务下发日志
     *
     * @param ids 需要删除的设备服务下发日志主键
     * @return 结果
     */
    @Override
    public int deleteFunctionLogByIds(Long[] ids) {
        return functionLogMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除设备服务下发日志信息
     *
     * @param id 设备服务下发日志主键
     * @return 结果
     */
    @Override
    public int deleteFunctionLogById(Long id) {
        return functionLogMapper.deleteById(id);
    }

    /**
     * 根据设备编号删除设备服务下发日志信息
     *
     * @param serialNumber 设备编号
     * @return 结果
     */
    @Override
    public int deleteFunctionLogByDeviceNumber(String serialNumber) {
        LambdaQueryWrapper<FunctionLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FunctionLog::getSerialNumber, serialNumber);
        return functionLogMapper.delete(queryWrapper);
    }

    /**
     * 批量更新日志状态值
     *
     * @param log 参数
     */
    @Override
    public void updateFuncLogBatch(FunctionLog log) {
        functionLogMapper.updateFuncLogBatch(log);
    }

    /**
     * 根据消息id更新指令下发状态
     *
     * @param log
     */
    @Override
    public void updateByMessageId(FunctionLog log) {
        LambdaUpdateWrapper<FunctionLog> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FunctionLog::getMessageId, log.getMessageId());
        functionLogMapper.update(log, updateWrapper);
    }

    @Override
    public List<HistoryModel> listHistory(FunctionLogVO functionLogVO) {
        return functionLogMapper.listHistory(functionLogVO);
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        Date beginTime = null;
        Date endTime = null;
        if (dataCenterParam.getBeginTime() != null && dataCenterParam.getBeginTime() != "" && dataCenterParam.getEndTime() != null && dataCenterParam.getEndTime() != "") {
            beginTime = parseTime(dataCenterParam.getBeginTime());
            endTime = parseTime(dataCenterParam.getEndTime());
        }
        return functionLogMapper.countThingsModelInvoke(dataCenterParam, beginTime, endTime);
    }
    private Date parseTime(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(time);
        } catch (ParseException e) {
            throw new IllegalArgumentException("时间格式错误: " + time, e);
        }
    }

    /**
     * 根据消息id获取指令日志
     *
     * @param messageId
     * @return
     */
    @Override
    public FunctionLog selectLogByMessageId(String messageId) {
        LambdaQueryWrapper<FunctionLog> queryWrapper = new LambdaQueryWrapper<FunctionLog>().eq(FunctionLog::getMessageId, messageId);
        return functionLogMapper.selectOne(queryWrapper);
    }

    private LambdaQueryWrapper<FunctionLog> buildQueryWrapper(FunctionLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<FunctionLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(query.getIdentify()), FunctionLog::getIdentify, query.getIdentify());
        lqw.eq(query.getFunType() != null, FunctionLog::getFunType, query.getFunType());
        lqw.eq(StringUtils.isNotBlank(query.getFunValue()), FunctionLog::getFunValue, query.getFunValue());
        lqw.eq(StringUtils.isNotBlank(query.getMessageId()), FunctionLog::getMessageId, query.getMessageId());
        lqw.like(StringUtils.isNotBlank(query.getDeviceName()), FunctionLog::getDeviceName, query.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), FunctionLog::getSerialNumber, query.getSerialNumber());
        lqw.eq(query.getMode() != null, FunctionLog::getMode, query.getMode());
        lqw.eq(query.getUserId() != null, FunctionLog::getUserId, query.getUserId());
        lqw.eq(StringUtils.isNotBlank(query.getResultMsg()), FunctionLog::getResultMsg, query.getResultMsg());
        lqw.eq(query.getResultCode() != null, FunctionLog::getResultCode, query.getResultCode());
        lqw.eq(StringUtils.isNotBlank(query.getShowValue()), FunctionLog::getShowValue, query.getShowValue());
        lqw.like(StringUtils.isNotBlank(query.getModelName()), FunctionLog::getModelName, query.getModelName());
        lqw.eq(query.getReplyTime() != null, FunctionLog::getReplyTime, query.getReplyTime());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(FunctionLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(FunctionLog entity) {
        //TODO 做一些数据校验,如唯一约束
    }
}
