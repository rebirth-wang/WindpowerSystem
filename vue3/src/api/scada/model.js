import request from '@/utils/request';

// 查询模型管理列表
export function listModel(query) {
  return request({
    url: '/scada/model/list',
    method: 'get',
    params: query,
  });
}

// 查询模型管理详细
export function getModel(id) {
  return request({
    url: '/scada/model/' + id,
    method: 'get',
  });
}

// 新增模型管理
export function addModel(data) {
  return request({
    url: '/scada/model',
    method: 'post',
    data: data,
  });
}

// 修改模型管理
export function updateModel(data) {
  return request({
    url: '/scada/model',
    method: 'put',
    data: data,
  });
}

// 删除模型管理
export function delModel(id) {
  return request({
    url: '/scada/model/' + id,
    method: 'delete',
  });
}

// 文件下载
export function downloadModel(url) {
  return request({
    url: url,
    method: 'get',
    responseType: 'blob',
  });
}
