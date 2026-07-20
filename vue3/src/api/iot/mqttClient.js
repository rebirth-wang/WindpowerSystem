import request from '@/utils/request';

// 查询mqtt桥接配置表列表
export function mqttClientList(query) {
  return request({
    url: '/iot/mqttClient/list',
    method: 'get',
    params: query,
  });
}

// 查询mqtt桥接配置表详细
export function getClient(id) {
  return request({
    url: '/iot/mqttClient/' + id,
    method: 'get',
  });
}

// 新增mqtt桥接配置表
export function addClient(data) {
  return request({
    url: '/iot/mqttClient',
    method: 'post',
    data: data,
  });
}

// 修改mqtt桥接配置表
export function updateClient(data) {
  return request({
    url: '/iot/mqttClient',
    method: 'put',
    data: data,
  });
}

// 删除mqtt桥接配置表
export function delClient(id) {
  return request({
    url: '/iot/mqttClient/' + id,
    method: 'delete',
  });
}
export function bridgeClient(data) {
  return request({
    url: '/iot/mqttClient/bridge',
    method: 'post',
    data: data,
  });
}
