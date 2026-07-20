import { expect, test as base, type Page, type Route } from '@playwright/test';
import {
  deviceCreateSuccess,
  deviceDeleteSuccess,
  deviceDetailSuccess,
  deviceListEmpty,
  deviceListSuccess,
  deviceUpdateSuccess,
} from '../../src/mocks/device';
import {
  alertDetailSuccess,
  alertListSuccess,
  commonWriteSuccess,
  productDetailSuccess,
  productListSuccess,
  systemMenuDetailSuccess,
  systemMenuListSuccess,
  systemRoleDetailSuccess,
  systemRoleListSuccess,
  systemUserDetailSuccess,
  systemUserListSuccess,
  workOrderDetailSuccess,
  workOrderListSuccess,
} from '../../src/mocks/integration';

type DeviceListScenario = 'success' | 'empty';

interface MockFastBeeOptions {
  deviceListScenario?: DeviceListScenario;
}

const jsonHeaders = {
  'access-control-allow-origin': '*',
  'content-type': 'application/json',
};

function fulfillJson(route: Route, body: unknown) {
  return route.fulfill({
    status: 200,
    headers: jsonHeaders,
    body: JSON.stringify(body),
  });
}

export async function mockFastBeeApis(page: Page, options: MockFastBeeOptions = {}) {
  const deviceListResponse = options.deviceListScenario === 'empty' ? deviceListEmpty : deviceListSuccess;

  await page.route('**/dev-api/**', (route) => fulfillJson(route, { code: 200, msg: 'success', data: [], rows: [], total: 0 }));

  await page.route('**/dev-api/captchaImage', (route) =>
    fulfillJson(route, {
      code: 200,
      msg: 'success',
      img: '',
      uuid: 'mock-captcha',
      captchaEnabled: false,
    })
  );

  await page.route('**/dev-api/login', (route) =>
    fulfillJson(route, {
      code: 200,
      msg: 'success',
      token: 'mock-token',
      expireTime: '2099-01-01T00:00:00',
    })
  );

  await page.route('**/dev-api/getInfo', (route) =>
    fulfillJson(route, {
      code: 200,
      msg: 'success',
      user: {
        userId: 1,
        userName: 'mock-admin',
        nickName: 'Mock Admin',
        avatar: '',
        dept: { deptId: 1, deptName: 'FastBee' },
      },
      roles: ['admin'],
      permissions: ['*:*:*', 'iot:device:list', 'iot:device:query', 'iot:device:add', 'iot:device:edit', 'iot:device:remove'],
      mqtt: false,
      dataScope: 'ALL',
    })
  );

  await page.route('**/dev-api/getRouters', (route) =>
    fulfillJson(route, {
      code: 200,
      msg: 'success',
      data: [
        {
          name: 'Iot',
          path: '/iot',
          hidden: false,
          redirect: 'noRedirect',
          component: 'Layout',
          alwaysShow: true,
          meta: { title: 'IoT', icon: 'device' },
          children: [
            {
              name: 'DeviceList',
              path: 'device/list',
              hidden: false,
              component: 'iot/device/index',
              meta: { title: 'Device Management', icon: 'device' },
            },
          ],
        },
      ],
    })
  );

  await page.route('**/dev-api/iot/device/list**', (route) => fulfillJson(route, deviceListResponse));
  await page.route('**/dev-api/iot/device/1001', (route) => fulfillJson(route, deviceDetailSuccess));
  await page.route('**/dev-api/iot/device', (route) => {
    if (route.request().method() === 'POST') {
      return fulfillJson(route, deviceCreateSuccess);
    }
    if (route.request().method() === 'PUT') {
      return fulfillJson(route, deviceUpdateSuccess);
    }
    return route.fallback();
  });
  await page.route('**/dev-api/iot/device/1001,1002', (route) => fulfillJson(route, deviceDeleteSuccess));
  await page.route('**/dev-api/iot/device/1002', (route) => fulfillJson(route, deviceDeleteSuccess));

  await page.route('**/dev-api/system/user/list**', (route) => fulfillJson(route, systemUserListSuccess));
  await page.route('**/dev-api/system/user/1', (route) => fulfillJson(route, systemUserDetailSuccess));
  await page.route('**/dev-api/system/user', (route) => fulfillJson(route, commonWriteSuccess));

  await page.route('**/dev-api/system/role/list**', (route) => fulfillJson(route, systemRoleListSuccess));
  await page.route('**/dev-api/system/role/1', (route) => fulfillJson(route, systemRoleDetailSuccess));
  await page.route('**/dev-api/system/role', (route) => fulfillJson(route, commonWriteSuccess));

  await page.route('**/dev-api/system/menu/list**', (route) => fulfillJson(route, systemMenuListSuccess));
  await page.route('**/dev-api/system/menu/1', (route) => fulfillJson(route, systemMenuDetailSuccess));
  await page.route('**/dev-api/system/menu', (route) => fulfillJson(route, commonWriteSuccess));

  await page.route('**/dev-api/iot/product/list**', (route) => fulfillJson(route, productListSuccess));
  await page.route('**/dev-api/iot/product/2001', (route) => fulfillJson(route, productDetailSuccess));
  await page.route('**/dev-api/iot/product', (route) => fulfillJson(route, commonWriteSuccess));

  await page.route('**/dev-api/iot/alert/list**', (route) => fulfillJson(route, alertListSuccess));
  await page.route('**/dev-api/iot/alert/3001', (route) => fulfillJson(route, alertDetailSuccess));
  await page.route('**/dev-api/iot/alert', (route) => fulfillJson(route, commonWriteSuccess));

  await page.route('**/dev-api/iot/workOrder/list**', (route) => fulfillJson(route, workOrderListSuccess));
  await page.route('**/dev-api/iot/workOrder/4001', (route) => fulfillJson(route, workOrderDetailSuccess));
  await page.route('**/dev-api/iot/workOrder', (route) => fulfillJson(route, commonWriteSuccess));
}

export const test = base.extend<{
  mockFastBee: (options?: MockFastBeeOptions) => Promise<void>;
}>({
  mockFastBee: async ({ page }, use) => {
    await use((options?: MockFastBeeOptions) => mockFastBeeApis(page, options));
  },
});

export { expect };
