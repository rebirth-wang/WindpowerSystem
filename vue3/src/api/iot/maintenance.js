import request from '@/utils/request';

// 查询设备维保列表
export function listMaintenance(query) {
  return request({
    url: '/iot/maintenance/list',
    method: 'get',
    params: query,
  });
}

// 查询设备维保详细
export function getMaintenance(id) {
  return request({
    url: '/iot/maintenance/' + id,
    method: 'get',
  });
}

// 新增设备维保
export function addMaintenance(data) {
  return request({
    url: '/iot/maintenance',
    method: 'post',
    data: data,
  });
}

// 修改设备维保
export function updateMaintenance(data) {
  return request({
    url: '/iot/maintenance',
    method: 'put',
    data: data,
  });
}

// 删除设备维保
export function delMaintenance(id) {
  return request({
    url: '/iot/maintenance/' + id,
    method: 'delete',
  });
}
