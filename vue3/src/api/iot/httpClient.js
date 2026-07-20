import request from '@/utils/request';

// 查询mqtt桥接http配置列表
export function httpClientList(query) {
  return request({
    url: '/iot/httpClient/list',
    method: 'get',
    params: query,
  });
}

// 查询mqtt桥接http配置详细
export function getHttpClient(id) {
  return request({
    url: '/iot/httpClient/' + id,
    method: 'get',
  });
}

// 新增mqtt桥接http配置
export function addHttpClient(data) {
  return request({
    url: '/iot/httpClient',
    method: 'post',
    data: data,
  });
}

// 修改mqtt桥接http配置
export function updateHttpClient(data) {
  return request({
    url: '/iot/httpClient',
    method: 'put',
    data: data,
  });
}

// 删除mqtt桥接http配置
export function delHttpClient(id) {
  return request({
    url: '/iot/httpClient/' + id,
    method: 'delete',
  });
}
export function bridgeClient(data) {
  return request({
    url: '/iot/httpClient/bridge',
    method: 'post',
    data: data,
  });
}
