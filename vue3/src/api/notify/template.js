import request from '@/utils/request';

// 查询通知模版列表
export function listTemplate(query) {
  return request({
    url: '/notify/template/list',
    method: 'get',
    params: query,
  });
}

// 查询通知模版详细
export function getTemplate(id) {
  return request({
    url: '/notify/template/' + id,
    method: 'get',
  });
}

// 新增通知模版
export function addTemplate(data) {
  return request({
    url: '/notify/template',
    method: 'post',
    data: data,
  });
}

// 修改通知模版
export function updateTemplate(data) {
  return request({
    url: '/notify/template',
    method: 'put',
    data: data,
  });
}

// 删除通知模版
export function delTemplate(id) {
  return request({
    url: '/notify/template/' + id,
    method: 'delete',
  });
}

// 查询通知模版示例
export function getTemplateExample(params) {
  return request({
    url: '/notify/template/example',
    method: 'get',
    params: params,
  });
}

// 获取通知模版详细信息
export function getUsableTempate(params) {
  return request({
    url: '/notify/template/getUsable',
    method: 'get',
    params: params,
  });
}

// 修改通知模版-更新状态
export function updateState(data) {
  return request({
    url: '/notify/template/updateState',
    method: 'post',
    data: data,
  });
}

// 测试发送
export function notifyTestTemplate(data) {
  return request({
    url: '/notify/send',
    method: 'post',
    data: data,
  });
}

//获取模板参数
export function templateParams(data) {
  return request({
    url: '/notify/template/msgParams',
    method: 'get',
    params: data,
  });
}

//获取消息通知模版参数变量
export function getVariablesList(id, channelType, provider) {
  return request({
    url: '/notify/template/listVariables?id=' + id + '&channelType=' + channelType + '&provider=' + provider,
    method: 'get',
  });
}
