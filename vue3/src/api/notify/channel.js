import request from '@/utils/request';

// 查询通知渠道列表
export function listChannel(query) {
  return request({
    url: '/notify/channel/list',
    method: 'get',
    params: query,
  });
}

// 查询通知渠道详细
export function getChannel(id) {
  return request({
    url: '/notify/channel/' + id,
    method: 'get',
  });
}

// 查询通知渠道和服务商
export function getChannelMessage(query) {
  return request({
    url: '/notify/channel/listChannel',
    method: 'get',
    params: query,
  });
}
//查询配置信息
export function getConfigContent(provider, channelType) {
  return request({
    url: '/notify/channel/getConfigContent?channelType=' + channelType + '&provider=' + provider,
    method: 'get',
  });
}
// 新增通知渠道
export function addChannel(data) {
  return request({
    url: '/notify/channel',
    method: 'post',
    data: data,
  });
}

// 修改通知渠道
export function updateChannel(data) {
  return request({
    url: '/notify/channel',
    method: 'put',
    data: data,
  });
}

// 删除通知渠道
export function delChannel(id) {
  return request({
    url: '/notify/channel/' + id,
    method: 'delete',
  });
}
