import request from '@/utils/request';

// 查询图库管理列表
export function listGallery(query) {
  return request({
    url: '/scada/gallery/list',
    method: 'get',
    params: query,
  });
}

// 查询图库管理详细
export function getGallery(id) {
  return request({
    url: '/scada/gallery/' + id,
    method: 'get',
  });
}

// 新增图库管理
export function addGallery(data) {
  return request({
    url: '/scada/gallery',
    method: 'post',
    data: data,
  });
}

// 上传文件管理
export function uploadFile(data) {
  return request({
    url: '/scada/gallery/uploadFile',
    method: 'post',
    data: data,
  });
}

// 修改图库管理
export function updateGallery(data) {
  return request({
    url: '/scada/gallery',
    method: 'put',
    data: data,
  });
}

// 删除图库管理
export function delGallery(id) {
  return request({
    url: '/scada/gallery/' + id,
    method: 'delete',
  });
}
