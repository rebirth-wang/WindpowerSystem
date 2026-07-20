import request from '@/utils/request';

// 查询报表管理列表
export function listReport(query) {
  return request({
    url: '/data/center/report/list',
    method: 'get',
    params: query,
  });
}

// 查询报表管理详细
export function getReport(id) {
  return request({
    url: '/data/center/report/' + id,
    method: 'get',
  });
}

// 新增报表管理
export function addReport(data) {
  return request({
    url: '/data/center/report',
    method: 'post',
    data: data,
  });
}

// 修改报表管理
export function updateReport(data) {
  return request({
    url: '/data/center/report',
    method: 'put',
    data: data,
  });
}

// 删除报表管理
export function delReport(id) {
  return request({
    url: '/data/center/report/' + id,
    method: 'delete',
  });
}
