import request from '@/utils/request';

// 查询modbus配置列表
export function listConfig(query) {
  return request({
    url: '/modbus/config/list',
    method: 'get',
    params: query,
  });
}

// 查询modbus配置详细
export function getConfig(id) {
  return request({
    url: '/modbus/config/' + id,
    method: 'get',
  });
}

// 新增modbus配置
export function addConfig(data) {
  return request({
    url: '/modbus/config',
    method: 'post',
    data: data,
  });
}

// 修改modbus配置
export function updateConfig(data) {
  return request({
    url: '/modbus/config',
    method: 'put',
    data: data,
  });
}
//批量新增modbus1配置
export function addBatch(data) {
  return request({
    url: '/modbus/config/addBatch',
    method: 'post',
    data: data,
  });
}

//批量更新modbus1配置
export function editBatch(data) {
  return request({
    url: '/modbus/config/editBatch',
    method: 'post',
    data: data,
  });
}

// 删除modbus配置
export function delConfig(id) {
  return request({
    url: '/modbus/config/' + id,
    method: 'delete',
  });
}
