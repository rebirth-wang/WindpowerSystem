import request from '@/utils/request';

// 查询用户列表
export function alertUserList(query) {
  return request({
    url: '/iot/deviceAlertUser/query',
    method: 'get',
    params: query,
  });
}

// 查询告警用户列表
export function listUser(data) {
  return request({
    url: '/iot/deviceAlertUser/list',
    method: 'get',
    params: data,
  });
}

// 新增告警用户
export function addAlertUser(data) {
  return request({
    url: '/iot/deviceAlertUser',
    method: 'post',
    data: data,
  });
}

// 删除告警用户
export function delAlertUser(deviceId, userId) {
  return request({
    url: '/iot/deviceAlertUser?deviceId=' + deviceId + '&userId=' + userId,
    method: 'delete',
  });
}
