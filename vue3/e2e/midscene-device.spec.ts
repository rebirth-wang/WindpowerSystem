import { expect, hasMidsceneModelConfig, test } from './fixtures/midscene';

test.describe('Midscene assisted device management', () => {
  test.skip(!hasMidsceneModelConfig(), 'Set Midscene model env vars to run AI-assisted e2e checks.');

  test('recognizes the mocked device list page', async ({ aiAssert, mockFastBee, page }) => {
    await mockFastBee();
    await page.goto('/login');

    await page.locator('.form-box input[type="text"]').first().fill('admin');
    await page.locator('.form-box input[type="password"]').first().fill('admin123');
    await page.locator('.form-box .btn').first().click();
    await page.waitForURL(/\/index/, { timeout: 15_000 });

    await page.goto('/iot/device/list');
    await expect(page.locator('body')).toContainText('Mock Gateway A');

    await aiAssert(
      'The current page is the Device Management page, and the device table includes Mock Gateway A with serial number FB-MOCK-GW-001.'
    );
  });
});
