import request from '@/utils/request';

// 查询规则执行器列表
export function listExecutor(query) {
  return request({
    url: '/rule/executor/list',
    method: 'get',
    params: query,
  });
}

// 查询规则执行器详细
export function getExecutor(id) {
  return request({
    url: '/rule/executor/' + id,
    method: 'get',
  });
}

// 新增规则执行器
export function addExecutor(data) {
  return request({
    url: '/rule/executor',
    method: 'post',
    data: data,
  });
}

// 修改规则执行器
export function updateExecutor(data) {
  return request({
    url: '/rule/executor',
    method: 'put',
    data: data,
  });
}

// 删除规则执行器
export function delExecutor(id) {
  return request({
    url: '/rule/executor/' + id,
    method: 'delete',
  });
}
