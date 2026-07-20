import request from '@/utils/request';

// 查询组件管理列表
export function listDeviceBind(query) {
  return request({
    url: '/scada/center/listDeviceBind',
    method: 'get',
    params: query,
  });
}

// 保存组态关联设备
export function saveDeviceBind(data) {
  return request({
    url: '/scada/center/saveDeviceBind',
    method: 'post',
    data: data,
  });
}

// 移除组态关联设备
export function removeDeviceBind(ids) {
  return request({
    url: '/scada/center/removeDeviceBind/' + ids,
    method: 'delete',
  });
}

// 查询设备统计信息
export function getDeviceStatistic(query) {
  return request({
    url: '/scada/center/statistic',
    method: 'get',
    params: query,
  });
}

// 选择变量
export function getListVariable(query) {
  return request({
    url: '/scada/center/listVariable',
    method: 'get',
    params: query,
  });
}

// 获取组态详情
export function getByGuid(query) {
  return request({
    url: '/scada/center/getByGuid',
    method: 'get',
    params: query,
  });
}

//保存组态详细数据
export function saveDetailData(data) {
  return request({
    url: '/scada/center/save',
    method: 'post',
    data: data,
  });
}

// 删除收藏图库管理
export function delFavoritesGallery(id) {
  return request({
    url: '/scada/center/deleteGalleryFavorites/' + id,
    method: 'delete',
  });
}

// 收藏图标
export function favoritesGallery(data) {
  return request({
    url: '/scada/center/saveGalleryFavorites',
    method: 'post',
    data: data,
  });
}

// 查询图库管理列表
export function getFavoriteGallerys(query) {
  return request({
    url: '/scada/center/listGalleryFavorites',
    method: 'get',
    params: query,
  });
}

// 获取物模历史数据
export function getListVariableHistory(data) {
  return request({
    url: '/scada/center/listVariableHistory',
    method: 'post',
    data: data,
  });
}

// 获取设备状态
export function getDeviceStatus(query) {
  return request({
    url: '/scada/center/getDeviceStatus',
    method: 'get',
    params: query,
  });
}

//获取用户密码
export function verifyUserPassword(query) {
  return request({
    url: '/scada/center/verifyUserPassword',
    method: 'get',
    params: query,
  });
}

// 获取变量历史数据
export function listVariableHistoryTable(data) {
  return request({
    url: '/scada/center/listVariableHistoryTable',
    method: 'post',
    data: data,
  });
}

// 获取页面告警数据
export function getPageAlertLog(query) {
  return request({
    url: '/scada/center/pageAlertLog',
    method: 'get',
    params: query,
  });
}
