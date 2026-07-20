import request from '@/utils/request';

// 查询物联网卡列表
export function listCard(query) {
  return request({
    url: '/iot/card/list',
    method: 'get',
    params: query,
  });
}

// 查询物联网卡详细
export function getCard(id) {
  return request({
    url: '/iot/card/' + id,
    method: 'get',
  });
}

// 新增物联网卡
export function addCard(data) {
  return request({
    url: '/iot/card/syncInfo',
    method: 'post',
    data: data,
  });
}

// 修改物联网卡
export function updateCard(data) {
  return request({
    url: '/iot/card',
    method: 'put',
    data: data,
  });
}

// 删除物联网卡
export function delCard(id) {
  return request({
    url: '/iot/card/' + id,
    method: 'delete',
  });
}

//更新流量使用情况
export function updateUsage(data) {
  return request({
    url: '/iot/card/syncTrafficInfo',
    method: 'post',
    data: data,
  });
}

// 获取物联网卡概况数据
export function getCardOverview() {
  return request({
    url: '/iot/card/statistics',
    method: 'get',
  });
}

// 同步物联网卡信息
export function syncCardInfo(data) {
  return request({
    url: '/iot/card/syncStatus',
    method: 'post',
    data: data,
  });
}
