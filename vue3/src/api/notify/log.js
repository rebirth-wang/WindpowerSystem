import request from '@/utils/request';

// 查询通知日志列表
export function listLog(query) {
  return request({
    url: '/notify/log/list',
    method: 'get',
    params: query,
  });
}

// 查询通知日志详细
export function getLog(id) {
  return request({
    url: '/notify/log/' + id,
    method: 'get',
  });
}

// 新增通知日志
export function addLog(data) {
  return request({
    url: '/notify/log',
    method: 'post',
    data: data,
  });
}

// 修改通知日志
export function updateLog(data) {
  return request({
    url: '/notify/log',
    method: 'put',
    data: data,
  });
}

// 删除通知日志
export function delLog(id) {
  return request({
    url: '/notify/log/' + id,
    method: 'delete',
  });
}
