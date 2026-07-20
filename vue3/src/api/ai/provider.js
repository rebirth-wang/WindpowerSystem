import request from '@/utils/request';

// 查询厂商列表
export function listAiProvider(query) {
  return request({
    url: '/ai/provider/list',
    method: 'get',
    params: query,
  });
}

// 查询已启用厂商
export function listEnabledAiProvider() {
  return request({
    url: '/ai/provider/enabled',
    method: 'get',
  });
}

// 查询厂商详情
export function getAiProvider(providerId) {
  return request({
    url: '/ai/provider/' + providerId,
    method: 'get',
  });
}

// 新增厂商
export function addAiProvider(data) {
  return request({
    url: '/ai/provider',
    method: 'post',
    data,
  });
}

// 修改厂商
export function updateAiProvider(data) {
  return request({
    url: '/ai/provider',
    method: 'put',
    data,
  });
}

// 修改厂商状态
export function changeAiProviderStatus(providerId, status) {
  return request({
    url: '/ai/provider/changeStatus',
    method: 'put',
    data: {
      providerId,
      status,
    },
  });
}

// 删除厂商
export function removeAiProvider(providerId) {
  return request({
    url: '/ai/provider/' + providerId,
    method: 'delete',
  });
}
