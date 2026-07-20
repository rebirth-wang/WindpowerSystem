import request from '@/utils/request';

// 查询数据桥接列表
export function listBridge(query) {
  return request({
    url: '/iot/bridge/list',
    method: 'get',
    params: query,
  });
}

// 查询数据桥接详细
export function getBridge(id) {
  return request({
    url: '/iot/bridge/' + id,
    method: 'get',
  });
}

// 新增数据桥接
export function addBridge(data) {
  return request({
    url: '/iot/bridge',
    method: 'post',
    data: data,
  });
}

// 修改数据桥接
export function updateBridge(data) {
  return request({
    url: '/iot/bridge',
    method: 'put',
    data: data,
  });
}

export function connectBridge(data) {
  return request({
    url: '/iot/bridge/connect',
    method: 'post',
    data: data,
  });
}

// 删除数据桥接
export function delBridge(id) {
  return request({
    url: '/iot/bridge/' + id,
    method: 'delete',
  });
}
