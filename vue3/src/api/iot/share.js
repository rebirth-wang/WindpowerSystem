import request from '@/utils/request';

// 查询设备分享列表
export function listShare(query) {
  return request({
    url: '/iot/share/list',
    method: 'get',
    params: query,
  });
}

// 查询用户
export function shareUser(query) {
  return request({
    url: '/iot/share/shareUser',
    method: 'get',
    params: query,
  });
}

// 查询设备分享详细
export function getShare(deviceId, userId) {
  return request({
    url: '/iot/share/detail?deviceId=' + deviceId + '&userId=' + userId,
    method: 'get',
  });
}

// 新增设备分享
export function addShare(data) {
  return request({
    url: '/iot/share',
    method: 'post',
    data: data,
  });
}

// 修改设备分享
export function updateShare(data) {
  return request({
    url: '/iot/share',
    method: 'put',
    data: data,
  });
}

// 删除设备分享
export function delShare(data) {
  return request({
    url: '/iot/share',
    method: 'delete',
    data: data,
  });
}

//删除绑定设备分享
export function delBind(data) {
  return request({
    url: '/iot/deviceUser',
    method: 'delete',
    data: data,
  });
}
