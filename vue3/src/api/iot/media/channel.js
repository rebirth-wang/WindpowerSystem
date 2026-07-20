import request from '@/utils/request';

// 查询监控设备通道信息列表
export function listComChannel(query) {
  return request({
    url: '/iot/channel/list',
    method: 'get',
    params: query,
  });
}

// 查询监控设备通道信息详细
export function getComChannel(channelId) {
  return request({
    url: '/iot/channel/' + channelId,
    method: 'get',
  });
}

// 新增监控设备通道信息
export function addComChannel(createNum, data) {
  return request({
    url: '/iot/channel/sip/' + createNum,
    method: 'post',
    data: data,
  });
}

// 修改监控设备通道信息
export function updateComChannel(data) {
  return request({
    url: '/iot/channel',
    method: 'put',
    data: data,
  });
}

// 删除监控设备通道信息
export function delComChannel(channelId) {
  return request({
    url: '/iot/channel/' + channelId,
    method: 'delete',
  });
}

// 监控设备绑定设备或场景
export function Combinding(data) {
  return request({
    url: '/iot/relation/addOrUp',
    method: 'post',
    data: data,
  });
}
// 通过设备或场景查询监控设备通道信息
export function listComRelDeviceOrScene(query) {
  return request({
    url: '/iot/channel/listRelDeviceOrScene',
    method: 'get',
    params: query,
  });
}
