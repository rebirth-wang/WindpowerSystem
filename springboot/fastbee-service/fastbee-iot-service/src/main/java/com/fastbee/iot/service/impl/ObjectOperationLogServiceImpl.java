package com.fastbee.iot.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.utils.ObjectChangeDetector;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.ObjectOperationLog;
import com.fastbee.iot.mapper.ObjectOperationLogMapper;
import com.fastbee.iot.service.IObjectOperationLogService;

/**
 * 对象操作记录Service业务层处理
 *
 * @author fastbee
 * @date 2025-08-21
 */
@Service
public class ObjectOperationLogServiceImpl extends ServiceImpl<ObjectOperationLogMapper,ObjectOperationLog> implements IObjectOperationLogService {

    /**
     * 查询对象操作记录分页列表
     *
     * @param objectOperationLog 对象操作记录
     * @return 对象操作记录
     */
    @Override
    public Page<ObjectOperationLog> pageObjectOperationLog(ObjectOperationLog objectOperationLog) {
        LambdaQueryWrapper<ObjectOperationLog> lqw = buildQueryWrapper(objectOperationLog);
        lqw.orderByDesc(ObjectOperationLog::getCreateTime);
        return baseMapper.selectPage(new Page<>(objectOperationLog.getPageNum(), objectOperationLog.getPageSize()), lqw);
    }

    @Override
    public Boolean insert(Object oldObject, Object newObject, Long objectId, Integer type, String createBy) {
        ObjectOperationLog objectOperationLog = this.generateObjectOperationLog(oldObject, newObject, objectId, type, createBy);
        if (Objects.isNull(objectOperationLog)) {
            return false;
        }
        return save(objectOperationLog);
    }

    @Override
    public Boolean deleteByObjectIdAndType(List<Long> objectIdList, Integer type) {
        LambdaQueryWrapper<ObjectOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ObjectOperationLog::getObjectId, objectIdList);
        queryWrapper.eq(ObjectOperationLog::getType, type);
        return remove(queryWrapper);
    }

    @Override
    public ObjectOperationLog generateObjectOperationLog(Object oldObject, Object newObject, Long objectId, Integer type, String createBy) {
        String result = ObjectChangeDetector.detectChanges(oldObject, newObject);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        ObjectOperationLog objectOperationLog = new ObjectOperationLog();
        objectOperationLog.setObjectId(objectId);
        objectOperationLog.setType(type);
        objectOperationLog.setContent(result);
        objectOperationLog.setCreateBy(createBy);
        return objectOperationLog;
    }


    private LambdaQueryWrapper<ObjectOperationLog> buildQueryWrapper(ObjectOperationLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ObjectOperationLog> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, ObjectOperationLog::getId, query.getId());
                    lqw.eq(query.getObjectId() != null, ObjectOperationLog::getObjectId, query.getObjectId());
                    lqw.eq(query.getType() != null, ObjectOperationLog::getType, query.getType());
                    lqw.eq(StringUtils.isNotBlank(query.getContent()), ObjectOperationLog::getContent, query.getContent());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ObjectOperationLog::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, ObjectOperationLog::getCreateTime, query.getCreateTime());
                    lqw.eq(query.getDelFlag() != null, ObjectOperationLog::getDelFlag, query.getDelFlag());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(ObjectOperationLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

}
