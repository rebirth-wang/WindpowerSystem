import type { paths } from '@/api/schema';

type DeviceListOpenApiPath = paths extends { '/iot/device/list': infer T } ? T : never;
type DeviceDetailOpenApiPath = paths extends { '/iot/device/{deviceId}': infer T } ? T : never;
type DeviceWriteOpenApiPath = paths extends { '/iot/device': infer T } ? T : never;
type DeviceDeleteOpenApiPath = paths extends { '/iot/device/{deviceIds}': infer T } ? T : never;

export type DeviceOpenApiContract = {
  list: DeviceListOpenApiPath;
  detail: DeviceDetailOpenApiPath;
  write: DeviceWriteOpenApiPath;
  remove: DeviceDeleteOpenApiPath;
};

export interface DeviceListQuery {
  pageNum?: number;
  pageSize?: number;
  deviceName?: string;
  serialNumber?: string;
  productId?: number | string;
  status?: number | string;
  [key: string]: unknown;
}

export interface DeviceVO {
  deviceId?: number | string;
  deviceName?: string;
  serialNumber?: string;
  productId?: number | string;
  productName?: string;
  status?: number;
  deviceType?: number | string;
  tenantId?: number | string;
  tenantName?: string;
  createTime?: string;
  [key: string]: unknown;
}

export interface DevicePageResponse {
  code: number;
  msg: string;
  rows: DeviceVO[];
  total: number;
}

export interface DeviceDetailResponse {
  code: number;
  msg: string;
  data: DeviceVO;
}

export interface DeviceWriteResponse {
  code: number;
  msg: string;
  data?: number;
}

export interface DeviceDeleteResponse {
  code: number;
  msg: string;
}
