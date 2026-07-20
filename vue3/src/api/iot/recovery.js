import request from '@/utils/request';
//查询回收设备
export function deletedDeviceList(params) {
  return request({
    url: '/iot/device/list/deleted',
    method: 'get',
    params: params,
  });
}
// 还原设备
export function restoreDevice(data) {
  return request({
    url: '/iot/device/restore',
    method: 'put',
    params: data,
  });
}
// 删除设备
export function deldeletedDevice(deviceId) {
  return request({
    url: '/iot/device/physical/delete/' + deviceId,
    method: 'delete',
    //params: data,
  });
}

//查询回收产品
export function deletedProductList(params) {
  return request({
    url: '/iot/product/deleted/list',
    method: 'get',
    params: params,
  });
}
// 还原产品
export function restoreProduct(data) {
  return request({
    url: '/iot/product/restore',
    method: 'put',
    params: data,
  });
}
// 删除产品
export function deldeletedProduct(productId) {
  return request({
    url: '/iot/product/remove/' + productId,
    method: 'delete',
  });
}
//查询回收场景
export function deletedSceneList(params) {
  return request({
    url: '/scene/model/delList',
    method: 'get',
    params: params,
  });
}
// 还原场景
export function restoreScene(data) {
  return request({
    url: '/scene/model/restore',
    method: 'put',
    params: data,
  });
}
// 删除场景
export function deldeletedScene(sceneId) {
  return request({
    url: '/scene/model/physicDel/' + sceneId,
    method: 'delete',
  });
}
