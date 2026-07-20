import request from '@/utils/request';

// 查询指令权限控制列表
export function listControl(query) {
  return request({
    url: '/order/control/list',
    method: 'get',
    params: query,
  });
}

// 查询指令权限控制详细
export function getControl(id) {
  return request({
    url: '/order/control/' + id,
    method: 'get',
  });
}

// 新增指令权限控制
export function addControl(data) {
  return request({
    url: '/order/control',
    method: 'post',
    data: data,
  });
}

// 修改指令权限控制
export function updateControl(data) {
  return request({
    url: '/order/control',
    method: 'put',
    data: data,
  });
}

// 删除指令权限控制
export function delControl(id) {
  return request({
    url: '/order/control/' + id,
    method: 'delete',
  });
}
// 查询指令权限
export function getOrderControl(params) {
  return request({
    url: '/order/control/get',
    method: 'get',
    params: params,
  });
}
