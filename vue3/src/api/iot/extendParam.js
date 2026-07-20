import request from '@/utils/request';

// 产品扩展参数
export function listProductExtendParams(productId) {
  return request({
    url: '/iot/productExtParam/list',
    method: 'get',
    params: { productId },
  });
}

export function addProductExtendParam(data) {
  return request({
    url: '/iot/productExtParam',
    method: 'post',
    data,
  });
}

export function updateProductExtendParam(data) {
  return request({
    url: '/iot/productExtParam',
    method: 'put',
    data,
  });
}

export function delProductExtendParam(id) {
  return request({
    url: `/iot/productExtParam/${id}`,
    method: 'delete',
  });
}

// 设备扩展参数值
export function listDeviceExtendParams(deviceId) {
  return request({
    url: '/iot/deviceExtValue/list',
    method: 'get',
    params: { deviceId },
  });
}

// 更新设备参数值
export function updateDeviceExtendValue(data) {
  return request({
    url: '/iot/deviceExtValue',
    method: 'put',
    data,
  });
}
