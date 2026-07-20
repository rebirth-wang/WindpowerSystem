import request from '@/utils/request';

// 查询物联卡平台列表
export function listCardPlatform(query) {
  return request({
    url: '/iot/cardPlatform/list',
    method: 'get',
    params: query,
  });
}

// 查询物联卡平台详细
export function getCardPlatform(id) {
  return request({
    url: '/iot/cardPlatform/' + id,
    method: 'get',
  });
}

// 新增物联卡平台
export function addCardPlatform(data) {
  return request({
    url: '/iot/cardPlatform',
    method: 'post',
    data: data,
  });
}

// 修改物联卡平台
export function updateCardPlatform(data) {
  return request({
    url: '/iot/cardPlatform',
    method: 'put',
    data: data,
  });
}

// 删除物联卡平台
export function delCardPlatform(id) {
  return request({
    url: '/iot/cardPlatform/' + id,
    method: 'delete',
  });
}
