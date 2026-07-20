import request from '@/utils/request';

// 查询工单管理列表
export function listWorkOrder(query) {
  return request({
    url: '/iot/workOrder/list',
    method: 'get',
    params: query,
  });
}

// 查询工单管理详细
export function getWorkOrder(id) {
  return request({
    url: '/iot/workOrder/' + id,
    method: 'get',
  });
}

// 新增工单管理
export function addWorkOrder(data) {
  return request({
    url: '/iot/workOrder',
    method: 'post',
    data: data,
  });
}

// 修改工单管理
export function updateWorkOrder(data) {
  return request({
    url: '/iot/workOrder',
    method: 'put',
    data: data,
  });
}

// 删除工单管理
export function delWorkOrder(id) {
  return request({
    url: '/iot/workOrder/' + id,
    method: 'delete',
  });
}

// 接单，派单，结单操作
export function optionWorkOrder(data) {
  return request({
    url: '/iot/workOrder/changeStatus',
    method: 'post',
    data: data,
  });
}

// 查询工单记录
export function workOrderRecordList(id) {
  return request({
    url: '/iot/workOrder/listLog?workOrderId=' + id,
    method: 'get',
  });
}

// 查询我的工单列表
export function myWorkOrderList(query) {
  return request({
    url: '/iot/workOrder/listMyself',
    method: 'get',
    params: query,
  });
}
