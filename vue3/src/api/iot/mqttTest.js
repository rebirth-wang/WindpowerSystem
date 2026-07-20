import request from '@/utils/request';

// 指令编码
export function encode(query) {
  return request({
    url: '/iot/message/encode',
    method: 'get',
    params: query,
  });
}
//json编码
export function jsonEncode(data) {
  return request({
    url: '/iot/message/commandGenerate',
    method: 'post',
    data: data,
  });
}

// 指令解码
export function decode(query) {
  return request({
    url: '/iot/message/decode',
    method: 'get',
    params: query,
  });
}

// 平台下发指令
export function messagePost(data) {
  return request({
    url: '/iot/message/post',
    method: 'post',
    data: data,
  });
}

// 查询设备指令偏好设置列表
export function preferencesList(query) {
  return request({
    url: '/iot/preferences/list',
    method: 'get',
    params: query,
  });
}

// 新增设备指令偏好设置
export function addPreferences(data) {
  return request({
    url: '/iot/preferences',
    method: 'post',
    data: data,
  });
}

// 修改设备指令偏好设置
export function editPreferences(data) {
  return request({
    url: '/iot/preferences',
    method: 'put',
    data: data,
  });
}

// 删除设备指令偏好设置
export function delPreferences(data) {
  return request({
    url: `/iot/preferences/${data.id}`,
    method: 'DELETE',
  });
}
