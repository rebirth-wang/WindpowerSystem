export interface ListResponse<T> {
  code: number;
  msg: string;
  rows: T[];
  total: number;
}

export interface DetailResponse<T> {
  code: number;
  msg: string;
  data: T;
}

export interface WriteResponse {
  code: number;
  msg: string;
  data?: number;
}

export const systemUserListSuccess: ListResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  rows: [{ userId: 1, userName: 'mock-admin', nickName: 'Mock Admin', status: '0' }],
  total: 1,
};

export const systemUserDetailSuccess: DetailResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  data: { userId: 1, userName: 'mock-admin', nickName: 'Mock Admin', status: '0' },
};

export const systemRoleListSuccess: ListResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  rows: [{ roleId: 1, roleName: 'Mock Admin Role', roleKey: 'admin', status: '0' }],
  total: 1,
};

export const systemRoleDetailSuccess: DetailResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  data: { roleId: 1, roleName: 'Mock Admin Role', roleKey: 'admin', status: '0' },
};

export const systemMenuListSuccess = {
  code: 200,
  msg: 'success',
  data: [{ menuId: 1, menuName: 'Device Management', path: 'device/list', perms: 'iot:device:list' }],
};

export const systemMenuDetailSuccess: DetailResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  data: { menuId: 1, menuName: 'Device Management', path: 'device/list', perms: 'iot:device:list' },
};

export const productListSuccess: ListResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  rows: [{ productId: 2001, productName: 'Gateway Product', categoryName: 'Gateway', status: 1 }],
  total: 1,
};

export const productDetailSuccess: DetailResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  data: { productId: 2001, productName: 'Gateway Product', categoryName: 'Gateway', status: 1 },
};

export const alertListSuccess: ListResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  rows: [{ alertId: 3001, alertName: 'Mock Temperature Alert', status: 1 }],
  total: 1,
};

export const alertDetailSuccess: DetailResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  data: { alertId: 3001, alertName: 'Mock Temperature Alert', status: 1 },
};

export const workOrderListSuccess: ListResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  rows: [{ id: 4001, title: 'Mock Maintenance Order', status: 1 }],
  total: 1,
};

export const workOrderDetailSuccess: DetailResponse<Record<string, unknown>> = {
  code: 200,
  msg: 'success',
  data: { id: 4001, title: 'Mock Maintenance Order', status: 1 },
};

export const commonWriteSuccess: WriteResponse = {
  code: 200,
  msg: 'success',
  data: 1,
};
