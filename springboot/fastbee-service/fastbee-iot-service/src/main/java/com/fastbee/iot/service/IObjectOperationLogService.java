package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ObjectOperationLog;

/**
 * 对象操作记录Service接口
 *
 * @author fastbee
 * @date 2025-08-21
 */
public interface IObjectOperationLogService extends IService<ObjectOperationLog>
{

    /**
     * 查询对象操作记录列表
     *
     * @param objectOperationLog 对象操作记录
     * @return 对象操作记录分页集合
     */
    Page<ObjectOperationLog> pageObjectOperationLog(ObjectOperationLog objectOperationLog);

    /**
     * @description: 新增对象操作记录
     * @param: oldObject 原对象
     * @param: newObject 新对象
     * @param: type 对象表
     * @return: java.lang.Boolean
     * @author zzy
     * @date: 2025-08-21 17:07
     */
    Boolean insert(Object oldObject, Object newObject, Long objectId, Integer type, String createBy);

    /**
     * 校验并批量删除对象操作记录信息
     *
     * @param objectIdList  对象id
     * @param type 类型
     * @return 是否删除成功
     */
    Boolean deleteByObjectIdAndType(List<Long> objectIdList, Integer type);

    /**
     * 生成对象操作日志
     *
     * @param oldObject 旧对象
     * @param newObject 新对象
     * @param objectId  对象id
     * @param type      类型
     * @param createBy  所产生
     * @return {@link ObjectOperationLog }
     */
    ObjectOperationLog generateObjectOperationLog(Object oldObject, Object newObject, Long objectId, Integer type, String createBy);

}
