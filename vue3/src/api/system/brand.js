import request from '@/utils/request';

// 查询APP启动信息列表
export function listBrand(query) {
  return request({
    url: '/app/brand/list',
    method: 'get',
    params: query,
  });
}

// 查询APP启动信息详细
export function getBrand(id) {
  return request({
    url: '/app/brand/' + id,
    method: 'get',
  });
}

// 新增APP启动信息
export function addBrand(data) {
  return request({
    url: '/app/brand',
    method: 'post',
    data: data,
  });
}

// 修改APP启动信息
export function updateBrand(data) {
  return request({
    url: '/app/brand',
    method: 'put',
    data: data,
  });
}

// 删除APP启动信息
export function delBrand(id) {
  return request({
    url: '/app/brand/' + id,
    method: 'delete',
  });
}
