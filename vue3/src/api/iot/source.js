import request from '@/utils/request';

// 查询数据源配置列表
export function listSource(query) {
  return request({
    url: '/iot/source/list',
    method: 'get',
    params: query,
  });
}

// 查询数据源配置详细
export function getSource(id) {
  return request({
    url: '/iot/source/' + id,
    method: 'get',
  });
}

// 新增数据源配置
export function addSource(data) {
  return request({
    url: '/iot/source',
    method: 'post',
    data: data,
  });
}

// 修改数据源配置
export function updateSource(data) {
  return request({
    url: '/iot/source',
    method: 'put',
    data: data,
  });
}

// 删除数据源配置
export function delSource(id) {
  return request({
    url: '/iot/source/' + id,
    method: 'delete',
  });
}
