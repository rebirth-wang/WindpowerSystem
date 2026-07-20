import request from '@/utils/request';

// 查询规则组件列表
export function listCmp(query) {
  return request({
    url: '/rule/cmp/list',
    method: 'get',
    params: query,
  });
}

// 查询规则组件详细
export function getCmp(id) {
  return request({
    url: '/rule/cmp/' + id,
    method: 'get',
  });
}

// 新增规则组件
export function addCmp(data) {
  return request({
    url: '/rule/cmp',
    method: 'post',
    data: data,
  });
}

// 修改规则组件
export function updateCmp(data) {
  return request({
    url: '/rule/cmp',
    method: 'put',
    data: data,
  });
}

// 删除规则组件
export function delCmp(id) {
  return request({
    url: '/rule/cmp/' + id,
    method: 'delete',
  });
}
