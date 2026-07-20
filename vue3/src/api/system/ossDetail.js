import request from '@/utils/request';

// 查询文件记录列表
export function listDetail(query) {
  return request({
    url: '/oss/detail/list',
    method: 'get',
    params: query,
  });
}

// 查询文件记录详细
export function getDetail(id) {
  return request({
    url: '/oss/detail/' + id,
    method: 'get',
  });
}

// 新增文件记录
export function addDetail(data) {
  return request({
    url: '/oss/detail',
    method: 'post',
    data: data,
  });
}

// 修改文件记录
export function updateDetail(data) {
  return request({
    url: '/oss/detail',
    method: 'put',
    data: data,
  });
}

// 删除文件记录
export function delDetail(id) {
  return request({
    url: '/oss/detail/' + id,
    method: 'delete',
  });
}

export function download(ossId) {
  return request({
    url: '/oss/detail/download/' + ossId,
    method: 'get',
  });
}
