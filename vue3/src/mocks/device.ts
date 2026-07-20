import type {
  DeviceDeleteResponse,
  DeviceDetailResponse,
  DevicePageResponse,
  DeviceVO,
  DeviceWriteResponse,
} from '@/api/iot/device-contract';

export const mockDevices: DeviceVO[] = [
  {
    deviceId: 1001,
    deviceName: 'Mock Gateway A',
    serialNumber: 'FB-MOCK-GW-001',
    productId: 2001,
    productName: 'Gateway Product',
    status: 3,
    deviceType: 2,
    tenantId: 1,
    tenantName: 'FastBee',
    createTime: '2026-05-20 10:00:00',
  },
  {
    deviceId: 1002,
    deviceName: 'Mock Sensor B',
    serialNumber: 'FB-MOCK-SENSOR-002',
    productId: 2002,
    productName: 'Sensor Product',
    status: 1,
    deviceType: 1,
    tenantId: 1,
    tenantName: 'FastBee',
    createTime: '2026-05-20 10:10:00',
  },
];

export const deviceListSuccess: DevicePageResponse = {
  code: 200,
  msg: 'success',
  rows: mockDevices,
  total: mockDevices.length,
};

export const deviceListEmpty: DevicePageResponse = {
  code: 200,
  msg: 'success',
  rows: [],
  total: 0,
};

export const deviceBusinessFailure = {
  code: 500,
  msg: 'Mock business failure: duplicated serial number',
  data: null,
};

export const deviceUnauthorized = {
  code: 401,
  msg: 'Mock unauthorized',
};

export const deviceForbidden = {
  code: 403,
  msg: 'Mock forbidden: missing iot:device:list',
};

export const deviceDetailSuccess: DeviceDetailResponse = {
  code: 200,
  msg: 'success',
  data: mockDevices[0],
};

export const deviceCreateSuccess: DeviceWriteResponse = {
  code: 200,
  msg: 'success',
  data: 1,
};

export const deviceUpdateSuccess: DeviceWriteResponse = {
  code: 200,
  msg: 'success',
  data: 1,
};

export const deviceDeleteSuccess: DeviceDeleteResponse = {
  code: 200,
  msg: 'success',
};
