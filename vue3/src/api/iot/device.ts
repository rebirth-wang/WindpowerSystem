import request from '@/utils/request';
import type {
  DeviceDeleteResponse,
  DeviceDetailResponse,
  DeviceListQuery,
  DevicePageResponse,
  DeviceVO,
  DeviceWriteResponse,
} from '@/api/iot/device-contract';
import type { paths } from '@/api/schema';

export type {
  DeviceDeleteResponse,
  DeviceDetailResponse,
  DeviceListQuery,
  DeviceOpenApiContract,
  DevicePageResponse,
  DeviceVO,
  DeviceWriteResponse,
} from '@/api/iot/device-contract';

type _DeviceSchemaPathBinding = paths;

// 查询设备列表
export function listDevice(query?: DeviceListQuery): Promise<DevicePageResponse> {
  return request({
    url: '/iot/device/list',
    method: 'get',
    params: query,
  });
}

// 查询未授权设备列表
export function listUnAuthDevice(query?: Record<string, unknown>) {
  return request({
    url: '/iot/device/unAuthlist',
    method: 'get',
    params: query,
  });
}

// 查询分组可添加设备分页列表
export function listDeviceByGroup(query?: Record<string, unknown>) {
  return request({
    url: '/iot/device/listByGroup',
    method: 'get',
    params: query,
  });
}

// 查询设备简短列表
export function listDeviceShort(query?: Record<string, unknown>) {
  return request({
    url: '/iot/device/shortList',
    method: 'get',
    params: query,
  });
}

// 查询所有设备简短列表
export function listAllDeviceShort(query?: Record<string, unknown>) {
  return request({
    url: '/iot/device/all',
    method: 'get',
    params: query,
  });
}

// 查询设备详细
export function getDevice(deviceId: number | string): Promise<DeviceDetailResponse> {
  return request({
    url: '/iot/device/' + deviceId,
    method: 'get',
  });
}

// 查询GB28181设备接入排障信息
export function getDeviceAccessDiagnostics(deviceId: number | string) {
  return request({
    url: '/iot/device/access-diagnostics/' + deviceId,
    method: 'get',
  });
}

// 设备数据同步
export function deviceSynchronization(serialNumber: string) {
  return request({
    url: '/iot/device/synchronization/' + serialNumber,
    method: 'get',
  });
}

// 根据设备编号查询设备详细
export function getDeviceBySerialNumber(serialNumber: string) {
  return request({
    url: '/iot/device/getDeviceBySerialNumber/' + serialNumber,
    method: 'get',
  });
}

// 查询监测数据和上报事件统计信息
export function getEventStatistic() {
  return request({
    url: '/iot/device/statistic',
    method: 'get',
  });
}

// 查询设备数量，产品数量,告警数量统计信息
export function getDeviceCountStatistic() {
  return request({
    url: '/iot/device/deviceProductAlertCount',
    method: 'get',
  });
}

// 查询操作记录统计信息
export function getFunctionStatistic() {
  return request({
    url: '/iot/device/functionLogCount',
    method: 'get',
  });
}

// 选择分配设备
export function distributionDevice(deptId: number | string, deviceIds: Array<number | string> | string) {
  return request({
    url: '/iot/device/assignment?deptId=' + deptId + '&deviceIds=' + deviceIds,
    method: 'post',
  });
}

// 用户分配设备
export function allotUserDevice(userId: number | string, deviceIds: Array<number | string> | string) {
  return request({
    url: '/iot/device/user/assignment?userId=' + userId + '&deviceIds=' + deviceIds,
    method: 'post',
  });
}
//回收设备
export function recycleDevice(query: Record<string, unknown>) {
  return request({
    url: '/iot/device/recovery',
    method: 'post',
    data: query,
  });
}
//查询设备导入记录
export function listImportRecord(params?: Record<string, unknown>) {
  return request({
    url: '/iot/record/list',
    method: 'get',
    params: params,
  });
}
//查询设备回收记录
export function listRecycleRecord(params?: Record<string, unknown>) {
  return request({
    url: '/iot/record/list',
    method: 'get',
    params: params,
  });
}
//查询设备分配记录
export function listAllotRecord(params?: Record<string, unknown>) {
  return request({
    url: '/iot/record/list',
    method: 'get',
    params: params,
  });
}
// 查询设备运行状态详细
export function getDeviceRunningStatus(params?: Record<string, unknown>) {
  return request({
    url: '/iot/device/runningStatus',
    method: 'get',
    params: params,
  });
}

// 查询设备物模型的值
export function getDeviceThingsModelValue(deviceId: number | string) {
  return request({
    url: '/iot/device/thingsModelValue/' + deviceId,
    method: 'get',
  });
}

// 新增设备
export function addDevice(data: DeviceVO): Promise<DeviceWriteResponse> {
  return request({
    url: '/iot/device',
    method: 'post',
    data: data,
  });
}

// 修改设备
export function updateDevice(data: DeviceVO): Promise<DeviceWriteResponse> {
  return request({
    url: '/iot/device',
    method: 'put',
    data: data,
  });
}

// 删除设备
export function deleteDevice(deviceId: number | string | Array<number | string>): Promise<DeviceDeleteResponse> {
  return request({
    url: '/iot/device/' + deviceId,
    method: 'delete',
  });
}

// 生成设备编号
export function generatorDeviceNum(params?: Record<string, unknown>) {
  return request({
    url: '/iot/device/generator',
    method: 'get',
    params: params,
  });
}

export function getGwDevCode(params?: Record<string, unknown>) {
  return request({
    url: '/iot/device/gwDevCount',
    method: 'get',
    params: params,
  });
}

//mqtt连接参数查看
export function getMqttConnect(params?: Record<string, unknown>) {
  return request({
    url: '/iot/device/getMqttConnectData',
    method: 'get',
    params: params,
  });
}

//获取国标配置信息
export function getSipConfig(deviceSipId: number | string) {
  return request({
    url: '/sip/sipconfig/auth/' + deviceSipId,
    method: 'get',
  });
}

//获取HTTP协议配置信息
export function getHttpConfig(params?: Record<string, unknown>) {
  return request({
    url: '/iot/device/getHttpAuthData',
    method: 'get',
    params: params,
  });
}
// 查询设备变量概况
export function listThingsModel(query?: Record<string, unknown>) {
  return request({
    url: '/iot/device/listThingsModel',
    method: 'get',
    params: query,
  });
}

// 查询modbusTcp协议网关设备
export function modbusTcpHostList() {
  return request({
    url: '/iot/device/pageModbusTcpHost',
    method: 'get',
  });
}

//添加子设备，获取数据
export function getSubDeviceData(gwProductId: number | string) {
  return request({
    url: '/productModbus/gateway/listSubProduct?gwProductId=' + gwProductId,
    method: 'get',
  });
}
