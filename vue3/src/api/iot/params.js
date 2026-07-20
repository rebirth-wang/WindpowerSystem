import request from '@/utils/request';

// 查询产品modbus配置参数列表
export function listParams(query) {
  return request({
    url: '/modbus/params/list',
    method: 'get',
    params: query,
  });
}

// 查询产品modbus配置参数详细
export function getParams(id) {
  return request({
    url: '/modbus/params/' + id,
    method: 'get',
  });
}

// 新增产品modbus配置参数
export function addOrUpdate(data) {
  return request({
    url: '/modbus/params/addOrUpdate',
    method: 'post',
    data: data,
  });
}

// 删除产品modbus配置参数
export function delParams(id) {
  return request({
    url: '/modbus/params/' + id,
    method: 'delete',
  });
}

// 根据产品io获取modbus配置
export function getByProductId(params) {
  return request({
    url: '/modbus/params/getByProductId',
    method: 'get',
    params: params,
  });
}
