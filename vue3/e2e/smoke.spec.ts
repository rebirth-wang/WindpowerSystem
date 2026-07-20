import { expect, test } from '@playwright/test';

test('login page renders for issue reproduction baseline', async ({ page }) => {
  const runtimeErrors: string[] = [];

  page.on('pageerror', (error) => {
    runtimeErrors.push(error.message);
  });

  await page.goto('/login');

  await expect(page).toHaveURL(/\/login/);
  await expect(page.locator('body')).toBeVisible();
  expect(runtimeErrors).toEqual([]);
});
