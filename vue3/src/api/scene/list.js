import request from '@/utils/request';

// 查询场景管理列表
export function getSceneModelList(query) {
  return request({
    url: '/scene/model/list',
    method: 'get',
    params: query,
  });
}

// 新增场景管理
export function addSceneModel(data) {
  return request({
    url: '/scene/model',
    method: 'post',
    data: data,
  });
}

// 修改场景管理
export function updateSceneModel(data) {
  return request({
    url: '/scene/model',
    method: 'put',
    data: data,
  });
}

// 删除场景管理
export function deleteSceneModel(id) {
  return request({
    url: '/scene/model/' + id,
    method: 'delete',
  });
}

// 获取场景管理详细信息
export function getSceneModelDetail(id) {
  return request({
    url: '/scene/model/' + id,
    method: 'get',
  });
}

// 点击查看进入后查询变量列表
export function getSceneModelDataList(query) {
  return request({
    url: '/scene/modelData/list',
    method: 'get',
    params: query,
  });
}

// 查询场景关联设备列表
export function getSceneModelDeviceList(query) {
  return request({
    url: '/scene/modelDevice/list',
    method: 'get',
    params: query,
  });
}

// 新增场景关联设备
export function addModelDevice(data) {
  return request({
    url: '/scene/modelDevice',
    method: 'post',
    data: data,
  });
}

// 删除场景关联设备
export function deleteModelDevice(id) {
  return request({
    url: '/scene/modelDevice/' + id,
    method: 'delete',
  });
}

// 编辑场景关联设备
export function updateModelDevice(data) {
  return request({
    url: '/scene/modelDevice',
    method: 'put',
    data: data,
  });
}

// 点击编辑进入后根据类型查询变量列表
export function getSceneModelDataListByType(query) {
  return request({
    url: '/scene/modelData/listByType',
    method: 'get',
    params: query,
  });
}

// 全部启用
export function enableModelDevice(data) {
  return request({
    url: '/scene/modelDevice/editEnable',
    method: 'post',
    data: data,
  });
}

// 启用变量
export function enableModelData(data) {
  return request({
    url: '/scene/modelData/editEnable',
    method: 'post',
    data: data,
  });
}

// 获取场景录入运算变量列表
export function getSceneModelTagList(query) {
  return request({
    url: '/scene/modelTag/list',
    method: 'get',
    params: query,
  });
}

// 新增场景录入运算变量
export function addSceneModelTag(data) {
  return request({
    url: '/scene/modelTag',
    method: 'post',
    data: data,
  });
}

// 编辑场景录入运算变量
export function updateSceneModelTag(data) {
  return request({
    url: '/scene/modelTag',
    method: 'put',
    data: data,
  });
}

// 删除场景录入运算变量
export function deleteSceneModelTag(id) {
  return request({
    url: '/scene/modelTag/' + id,
    method: 'delete',
  });
}

// 获取场景录入运算变量详情
export function getSceneModelTag(id) {
  return request({
    url: '/scene/modelTag/' + id,
    method: 'get',
  });
}

//根据场景查询绑定的告警配置列表
export function getSceneAlertList(query) {
  return request({
    url: '/iot/scene/alerts',
    method: 'get',
    params: query,
  });
}

//为场景绑定告警配置
export function bindSceneAlert(data) {
  return request({
    url: '/iot/scene/bindAlerts',
    method: 'post',
    data: data,
  });
}

//解除场景与告警配置的绑定关系
export function unbindSceneAlert(data) {
  return request({
    url: '/iot/scene/unbindAlerts',
    method: 'delete',
    data: data,
  });
}
