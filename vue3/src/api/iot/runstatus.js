import request from '@/utils/request';

//查询设备实时数据
export function runStatus(params) {
  return request({
    url: '/iot/runtime/runState',
    method: 'get',
    params: params,
  });
}

//服务调用，等待设备响应
export function serviceInvokeReply(data) {
  return request({
    url: '/iot/runtime/service/invokeReply',
    method: 'post',
    data: data,
  });
}

//查询设备服务下发日志
export function funcLog(params) {
  return request({
    url: '/iot/runtime/funcLog',
    method: 'get',
    params: params,
  });
}

export function runningStatus(params) {
  return request({
    url: '/iot/runtime/running',
    method: 'get',
    params: params,
  });
}

export function propGet(params) {
  return request({
    url: '/iot/runtime/prop/get',
    method: 'get',
    params: params,
  });
}

//服务调用,不等待设备响应
export function serviceInvoke(data) {
  return request({
    url: '/iot/runtime/service/invoke',
    method: 'post',
    data: data,
  });
}
