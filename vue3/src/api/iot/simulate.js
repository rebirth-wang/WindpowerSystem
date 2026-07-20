import request from '@/utils/request';

// 查询模拟设备日志列表
export function listSimulateLog(query) {
  return request({
    url: '/iot/simulate/list',
    method: 'get',
    params: query,
  });
}
