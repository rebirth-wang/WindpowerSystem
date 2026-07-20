import request from '@/utils/request';

// 查询轮训任务列列表
export function listJob(query) {
  return request({
    url: '/modbus/job/list',
    method: 'get',
    params: query,
  });
}

// 查询轮训任务列详细
export function getJob(taskId) {
  return request({
    url: '/modbus/job/' + taskId,
    method: 'get',
  });
}

// 新增轮训任务列
export function addJob(data) {
  return request({
    url: '/modbus/job',
    method: 'post',
    data: data,
  });
}

// 修改轮训任务列
export function updateJob(data) {
  return request({
    url: '/modbus/job',
    method: 'put',
    data: data,
  });
}

// 删除轮训任务列
export function delJob(data) {
  return request({
    url: '/modbus/job/del',
    method: 'post',
    data: data,
  });
}

///产品///
// 查询产品轮训任务列列表
export function listProductJob(query) {
  return request({
    url: '/productModbus/job/list',
    method: 'get',
    params: query,
  });
}

// 新增产品轮训任务列
export function addProductJob(data) {
  return request({
    url: '/productModbus/job',
    method: 'post',
    data: data,
  });
}

// 修改产品轮训任务列
export function updateProductJob(data) {
  return request({
    url: '/productModbus/job',
    method: 'put',
    data: data,
  });
}

// 删除产品轮训任务列
export function delProductJob(ids) {
  return request({
    url: '/productModbus/job/' + ids,
    method: 'delete',
  });
}

// 获取产品轮训任务列详细信息
export function getProductJob(taskId) {
  return request({
    url: '/productModbus/job/' + taskId,
    method: 'get',
  });
}

//获取从机地址
export function getAddress(productId, serialNumber, deviceType) {
  return request({
    url:
      '/productModbus/job/getAddress?productId=' +
      productId +
      '&serialNumber=' +
      serialNumber +
      '&deviceType=' +
      deviceType,
    method: 'get',
  });
}

///指令下发
export function batchIntruction(data) {
  return request({
    url: '/api/modbus/batch-command',
    method: 'post',
    data: data,
  });
}
