import { expect, test } from './fixtures/fastbeeMock';

test.describe('device management', () => {
  test('lists devices from mock response', async ({ page, mockFastBee }) => {
    await mockFastBee();
    await page.goto('/login');

    const result = await page.evaluate(async () => {
      // @ts-ignore - resolved by the Vite dev server in the browser context.
      const { listDevice } = await import('/src/api/iot/device.ts');
      return listDevice({ pageNum: 1, pageSize: 10 });
    });

    expect(result.total).toBe(2);
    expect(result.rows[0].deviceName).toBe('Mock Gateway A');
    expect(result.rows[0].serialNumber).toBe('FB-MOCK-GW-001');
  });

  test('opens device detail from mock response', async ({ page, mockFastBee }) => {
    await mockFastBee();
    await page.goto('/login');

    const result = await page.evaluate(async () => {
      // @ts-ignore - resolved by the Vite dev server in the browser context.
      const { getDevice } = await import('/src/api/iot/device.ts');
      return getDevice(1001);
    });

    expect(result.data.deviceId).toBe(1001);
    expect(result.data.deviceName).toBe('Mock Gateway A');
  });

  test('handles empty device list mock response', async ({ page, mockFastBee }) => {
    await mockFastBee({ deviceListScenario: 'empty' });
    await page.goto('/login');

    const result = await page.evaluate(async () => {
      // @ts-ignore - resolved by the Vite dev server in the browser context.
      const { listDevice } = await import('/src/api/iot/device.ts');
      return listDevice({ pageNum: 1, pageSize: 10 });
    });

    expect(result.total).toBe(0);
    expect(result.rows).toEqual([]);
  });

  test('creates edits and deletes a device through mocked APIs', async ({ page, mockFastBee }) => {
    await mockFastBee();
    await page.goto('/login');

    const result = await page.evaluate(async () => {
      // @ts-ignore - resolved by the Vite dev server in the browser context.
      const { addDevice, updateDevice, deleteDevice } = await import('/src/api/iot/device.ts');
      const createResult = await addDevice({
        deviceName: 'Mock Created Device',
        serialNumber: 'FB-MOCK-CREATED-001',
        productId: 2001,
      });
      const updateResult = await updateDevice({
        deviceId: 1001,
        deviceName: 'Mock Gateway A Updated',
        serialNumber: 'FB-MOCK-GW-001',
        productId: 2001,
      });
      const deleteResult = await deleteDevice([1001, 1002]);

      return { createResult, updateResult, deleteResult };
    });

    expect(result.createResult.code).toBe(200);
    expect(result.updateResult.code).toBe(200);
    expect(result.deleteResult.code).toBe(200);
  });
});
