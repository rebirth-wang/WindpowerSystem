import request from '@/utils/request';

// 查询系统授权列表
export function listSysclient(query) {
  return request({
    url: '/system/sysclient/list',
    method: 'get',
    params: query,
  });
}

// 查询系统授权详细
export function getSysclient(id) {
  return request({
    url: '/system/sysclient/' + id,
    method: 'get',
  });
}

// 新增系统授权
export function addSysclient(data) {
  return request({
    url: '/system/sysclient',
    method: 'post',
    data: data,
  });
}

// 修改系统授权
export function updateSysclient(data) {
  return request({
    url: '/system/sysclient',
    method: 'put',
    data: data,
  });
}

// 删除系统授权
export function delSysclient(id) {
  return request({
    url: '/system/sysclient/' + id,
    method: 'delete',
  });
}
