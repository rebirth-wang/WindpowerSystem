import request from '@/utils/request';

// 查询规则脚本节点列表
export function listNode(query) {
  return request({
    url: '/rule/node/list',
    method: 'get',
    params: query,
  });
}

// 查询规则脚本节点详细
export function getNode(id) {
  return request({
    url: '/rule/node/' + id,
    method: 'get',
  });
}

// 新增规则脚本节点
export function addNode(data) {
  return request({
    url: '/rule/node',
    method: 'post',
    data: data,
  });
}

// 修改规则脚本节点
export function updateNode(data) {
  return request({
    url: '/rule/node',
    method: 'put',
    data: data,
  });
}

// 删除规则脚本节点
export function delNode(id) {
  return request({
    url: '/rule/node/' + id,
    method: 'delete',
  });
}
