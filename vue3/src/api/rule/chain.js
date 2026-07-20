import request from '@/utils/request';

// 查询规则链列表
export function listChain(query) {
  return request({
    url: '/rule/chain/list',
    method: 'get',
    params: query,
  });
}

// 查询规则链详细
export function getChain(id) {
  return request({
    url: '/rule/chain/' + id,
    method: 'get',
  });
}

// 新增规则链
export function addChain(data) {
  return request({
    url: '/rule/chain',
    method: 'post',
    data: data,
  });
}

// 修改规则链
export function updateChain(data) {
  return request({
    url: '/rule/chain',
    method: 'put',
    data: data,
  });
}

// 删除规则链
export function delChain(id) {
  return request({
    url: '/rule/chain/' + id,
    method: 'delete',
  });
}
