import { expect, test } from '@playwright/test';

test('axios requests include X-Request-Id for backend log correlation', async ({ page }) => {
  let requestIdHeader: string | null = null;

  await page.route('**/dev-api/integration/request-id-check', async (route) => {
    requestIdHeader = route.request().headers()['x-request-id'] || null;
    await route.fulfill({
      contentType: 'application/json',
      body: JSON.stringify({ code: 200, msg: 'ok', data: true }),
    });
  });

  await page.goto('/login', { waitUntil: 'domcontentloaded' });

  await page.evaluate(async () => {
    const mod = await import('/src/utils/request.ts');
    await mod.default({
      url: '/integration/request-id-check',
      method: 'get',
      headers: { isToken: false },
    });
  });

  expect(requestIdHeader).toMatch(/^fb-[0-9a-z]+-[0-9a-f]{8}$/);
});
