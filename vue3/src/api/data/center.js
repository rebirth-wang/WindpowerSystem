import request from '@/utils/request';

// 查询设备的历史数据
export function getDataCenterDeviceHistory(data) {
  return request({
    url: '/data/center/deviceHistory',
    method: 'post',
    data: data,
  });
}

// 查询场景变量历史数据
export function getDataCenterSceneHistory(query) {
  return request({
    url: '/data/center/sceneHistory',
    method: 'get',
    params: query,
  });
}

// 统计告警处理信息
export function getDataCenterCountAlertProcess(query) {
  return request({
    url: '/data/center/countAlertProcess',
    method: 'get',
    params: query,
  });
}

// 统计告警级别信息
export function getDataCenterCountAlertLevel(query) {
  return request({
    url: '/data/center/countAlertLevel',
    method: 'get',
    params: query,
  });
}

// 统计设备物模型指令下发数量
export function getDataCenterCountThingsModelInvoke(query) {
  return request({
    url: '/data/center/countThingsModelInvoke',
    method: 'get',
    params: query,
  });
}
