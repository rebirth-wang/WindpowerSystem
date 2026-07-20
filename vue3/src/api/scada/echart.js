import request from '@/utils/request';

// 查询图表管理列表
export function listEchart(query) {
  return request({
    url: '/scada/echart/list',
    method: 'get',
    params: query,
  });
}

// 查询图表管理详细
export function getEchart(id) {
  return request({
    url: '/scada/echart/' + id,
    method: 'get',
  });
}

// 新增图表管理
export function addEchart(data) {
  return request({
    url: '/scada/echart',
    method: 'post',
    data: data,
  });
}

// 修改图表管理
export function updateEchart(data) {
  return request({
    url: '/scada/echart',
    method: 'put',
    data: data,
  });
}

// 删除图表管理
export function delEchart(id) {
  return request({
    url: '/scada/echart/' + id,
    method: 'delete',
  });
}
