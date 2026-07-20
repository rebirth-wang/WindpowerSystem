import request from '@/utils/request';

// 查询报表记录列表
export function listRecords(query) {
  return request({
    url: '/data/center/report/records/list',
    method: 'get',
    params: query,
  });
}

// 查询报表记录详细
export function getRecords(id) {
  return request({
    url: '/iot/records/' + id,
    method: 'get',
  });
}

// 新增报表记录
export function addRecords(data) {
  return request({
    url: '/iot/records',
    method: 'post',
    data: data,
  });
}

// 修改报表记录
export function updateRecords(data) {
  return request({
    url: '/iot/records',
    method: 'put',
    data: data,
  });
}

// 删除报表记录
export function delRecords(id) {
  return request({
    url: '/data/center/report/records/' + id,
    method: 'delete',
  });
}
