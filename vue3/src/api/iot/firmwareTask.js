import request from '@/utils/request';

// 查询固件升级任务列表
export function listTask(query) {
  return request({
    url: '/iot/firmware/task/list',
    method: 'get',
    params: query,
  });
}

// 查询固件升级任务详细
export function getTask(id) {
  return request({
    url: '/iot/firmware/task/' + id,
    method: 'get',
  });
}

// 新增固件升级任务
export function addTask(data) {
  return request({
    url: '/iot/firmware/task',
    method: 'post',
    data: data,
  });
}

// 修改固件升级任务
export function updateTask(data) {
  return request({
    url: '/iot/firmware/task',
    method: 'put',
    data: data,
  });
}

// 删除固件升级任务
export function delTask(id) {
  return request({
    url: '/iot/firmware/task/' + id,
    method: 'delete',
  });
}

// 根据固件id查询下属设备列表
export function deviceList(query) {
  return request({
    url: '/iot/firmware/task/deviceList',
    method: 'get',
    params: query,
  });
}

// 固件升级设备统计
// 0:等待升级 1:已发送设备 2:设备收到  ===> 正在升级
// 3:升级成功 ===> 升级成功
// 4:升级失败 5:停止 ===> 升级失败
export function deviceStatistic(query) {
  return request({
    url: '/iot/firmware/task/deviceStatistic',
    method: 'get',
    params: query,
  });
}

// 固件重新升级
export function upgradeTask(data) {
  return request({
    url: '/iot/firmware/task/upgrade',
    method: 'post',
    data,
  });
}
