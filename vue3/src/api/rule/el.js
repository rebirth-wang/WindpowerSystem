import request from '@/utils/request';

// 查询规则列表
export function getRuleElList(query) {
  return request({
    url: '/rule/el/list',
    method: 'get',
    params: query,
  });
}

// 新增规则列表
export function addRuleEl(data) {
  return request({
    url: '/rule/el',
    method: 'post',
    data: data,
  });
}

// 修改规则列表
export function updateRuleEl(data) {
  return request({
    url: '/rule/el',
    method: 'put',
    data: data,
  });
}

// 删除规则列表
export function deleteRuleEl(id) {
  return request({
    url: '/rule/el/' + id,
    method: 'delete',
  });
}

// 获取规则列表详细信息
export function getRuleElDetail(id) {
  return request({
    url: '/rule/el/' + id,
    method: 'get',
  });
}

// 发布规则引擎
export function publishRuleEl(data) {
  return request({
    url: '/rule/el/publish',
    method: 'post',
    data: data,
  });
}

// 执行一次
export function executeRuleEl(data) {
  return request({
    url: '/rule/el/exec',
    method: 'post',
    data: data,
  });
}

// 查看日志
export function getRuleElLog(id) {
  return request({
    url: '/iot/rulelog/' + id,
    method: 'get',
  });
}

// 查看组件日志
export function getRuleElComponentLog(id, curNodeId) {
  return request({
    url: '/iot/rulelog/' + id + '/' + curNodeId,
    method: 'get',
  });
}
