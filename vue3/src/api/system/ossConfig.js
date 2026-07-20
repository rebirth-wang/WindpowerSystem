import request from '@/utils/request';

// 查询文件存储配置列表
export function listConfig(query) {
  return request({
    url: '/oss/config/list',
    method: 'get',
    params: query,
  });
}

// 查询文件存储配置详细
export function getConfig(id) {
  return request({
    url: '/oss/config/' + id,
    method: 'get',
  });
}

// 新增文件存储配置
export function addConfig(data) {
  return request({
    url: '/oss/config',
    method: 'post',
    data: data,
  });
}

// 修改文件存储配置
export function updateConfig(data) {
  return request({
    url: '/oss/config',
    method: 'put',
    data: data,
  });
}

// 删除文件存储配置
export function delConfig(id) {
  return request({
    url: '/oss/config/' + id,
    method: 'delete',
  });
}

export function changeOssConfigStatus(data) {
  return request({
    url: '/oss/config/changeStatus',
    method: 'put',
    data: data,
  });
}
