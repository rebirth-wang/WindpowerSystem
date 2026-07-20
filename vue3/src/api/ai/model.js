import request from '@/utils/request';

// 查询模型列表
export function listAiModel(query) {
  return request({
    url: '/ai/model/list',
    method: 'get',
    params: query,
  });
}

// 查询聊天页模型分组
export function listAiModelGrouped() {
  return request({
    url: '/ai/model/grouped',
    method: 'get',
  });
}

// 查询模型详情
export function getAiModel(modelId) {
  return request({
    url: '/ai/model/' + modelId,
    method: 'get',
  });
}

// 查询运行时模型快照缓存统计
export function getAiModelCacheStats() {
  return request({
    url: '/ai/model/cacheStats',
    method: 'get',
  });
}

// 新增模型
export function addAiModel(data) {
  return request({
    url: '/ai/model',
    method: 'post',
    data,
  });
}

// 修改模型
export function updateAiModel(data) {
  return request({
    url: '/ai/model',
    method: 'put',
    data,
  });
}

// 修改模型状态
export function changeAiModelStatus(modelId, status) {
  return request({
    url: '/ai/model/changeStatus',
    method: 'put',
    data: {
      modelId,
      status,
    },
  });
}

// 删除模型
export function removeAiModel(modelId) {
  return request({
    url: '/ai/model/' + modelId,
    method: 'delete',
  });
}
