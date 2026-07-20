import { expect, test } from './fixtures/fastbeeMock';

const listCases = [
  {
    moduleName: 'system user',
    importPath: '/src/api/system/user.js',
    functionName: 'listUser',
    expectedField: 'userName',
    expectedValue: 'mock-admin',
  },
  {
    moduleName: 'system role',
    importPath: '/src/api/system/role.js',
    functionName: 'listRole',
    expectedField: 'roleName',
    expectedValue: 'Mock Admin Role',
  },
  {
    moduleName: 'system menu',
    importPath: '/src/api/system/menu.js',
    functionName: 'listMenu',
    expectedField: 'menuName',
    expectedValue: 'Device Management',
    dataKey: 'data',
  },
  {
    moduleName: 'iot product',
    importPath: '/src/api/iot/product.js',
    functionName: 'listProduct',
    expectedField: 'productName',
    expectedValue: 'Gateway Product',
  },
  {
    moduleName: 'iot alert',
    importPath: '/src/api/iot/alert.js',
    functionName: 'listAlert',
    expectedField: 'alertName',
    expectedValue: 'Mock Temperature Alert',
  },
  {
    moduleName: 'iot work order',
    importPath: '/src/api/iot/workOrder.js',
    functionName: 'listWorkOrder',
    expectedField: 'title',
    expectedValue: 'Mock Maintenance Order',
  },
];

const detailCases = [
  {
    moduleName: 'system user',
    importPath: '/src/api/system/user.js',
    functionName: 'getUser',
    id: 1,
    expectedField: 'userName',
    expectedValue: 'mock-admin',
  },
  {
    moduleName: 'system role',
    importPath: '/src/api/system/role.js',
    functionName: 'getRole',
    id: 1,
    expectedField: 'roleName',
    expectedValue: 'Mock Admin Role',
  },
  {
    moduleName: 'system menu',
    importPath: '/src/api/system/menu.js',
    functionName: 'getMenu',
    id: 1,
    expectedField: 'menuName',
    expectedValue: 'Device Management',
  },
  {
    moduleName: 'iot product',
    importPath: '/src/api/iot/product.js',
    functionName: 'getProduct',
    id: 2001,
    expectedField: 'productName',
    expectedValue: 'Gateway Product',
  },
  {
    moduleName: 'iot alert',
    importPath: '/src/api/iot/alert.js',
    functionName: 'getAlert',
    id: 3001,
    expectedField: 'alertName',
    expectedValue: 'Mock Temperature Alert',
  },
  {
    moduleName: 'iot work order',
    importPath: '/src/api/iot/workOrder.js',
    functionName: 'getWorkOrder',
    id: 4001,
    expectedField: 'title',
    expectedValue: 'Mock Maintenance Order',
  },
];

test.describe('stage 4/5 integration rollout', () => {
  test('validates common module list contracts', async ({ page, mockFastBee }) => {
    await mockFastBee();
    await page.goto('/login');

    for (const testCase of listCases) {
      const result = await page.evaluate(async ({ importPath, functionName }) => {
        // @ts-ignore - resolved by the Vite dev server in the browser context.
        const mod = await import(importPath);
        return mod[functionName]({ pageNum: 1, pageSize: 10 });
      }, testCase);

      const rows = testCase.dataKey === 'data' ? result.data : result.rows;
      expect.soft(result.code, testCase.moduleName).toBe(200);
      expect.soft(rows[0][testCase.expectedField], testCase.moduleName).toBe(testCase.expectedValue);
    }
  });

  test('validates common module detail contracts', async ({ page, mockFastBee }) => {
    await mockFastBee();
    await page.goto('/login');

    for (const testCase of detailCases) {
      const result = await page.evaluate(async ({ importPath, functionName, id }) => {
        // @ts-ignore - resolved by the Vite dev server in the browser context.
        const mod = await import(importPath);
        return mod[functionName](id);
      }, testCase);

      expect.soft(result.code, testCase.moduleName).toBe(200);
      expect.soft(result.data[testCase.expectedField], testCase.moduleName).toBe(testCase.expectedValue);
    }
  });
});
