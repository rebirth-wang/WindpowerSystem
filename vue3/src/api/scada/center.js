import request from '@/utils/request';

// 查询组态中心列表
export function listCenter(query) {
  return request({
    url: '/scada/center/list',
    method: 'get',
    params: query,
  });
}

// 查询组态中心详细
export function getCenter(id) {
  return request({
    url: '/scada/center/' + id,
    method: 'get',
  });
}

// 新增组态中心
export function addCenter(data) {
  return request({
    url: '/scada/center',
    method: 'post',
    data: data,
  });
}

// 修改组态中心
export function updateCenter(data) {
  return request({
    url: '/scada/center',
    method: 'put',
    data: data,
  });
}

// 删除组态中心
export function delCenter(id) {
  return request({
    url: '/scada/center/' + id,
    method: 'delete',
  });
}

// 组态查看分享信息
export function getShare(query) {
  return request({
    url: '/scada/center/getShare',
    method: 'get',
    params: query,
  });
}

// 组态编辑分享
export function editShare(data) {
  return request({
    url: '/scada/center/editShare',
    method: 'post',
    data: data,
  });
}
