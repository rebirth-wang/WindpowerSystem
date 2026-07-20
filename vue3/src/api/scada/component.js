import request from '@/utils/request';

// 查询组件管理列表
export function listComponent(query) {
  return request({
    url: '/scada/component/list',
    method: 'get',
    params: query,
  });
}

// 查询组件管理详细
export function getComponent(id) {
  return request({
    url: '/scada/component/' + id,
    method: 'get',
  });
}

// 新增组件管理
export function addComponent(data) {
  return request({
    url: '/scada/component',
    method: 'post',
    data: data,
  });
}

// 修改组件管理
export function updateComponent(data) {
  return request({
    url: '/scada/component',
    method: 'put',
    data: data,
  });
}

// 删除组件管理
export function delComponent(id) {
  return request({
    url: '/scada/component/' + id,
    method: 'delete',
  });
}
