import { expect, test } from '@playwright/test';

const mainFeatureModules = [
  {
    area: 'shell and auth',
    modules: [
      { name: 'login', path: '/src/views/login.vue' },
      { name: 'register', path: '/src/views/register.vue' },
      { name: 'dashboard', path: '/src/views/index.vue' },
    ],
  },
  {
    area: 'system management',
    modules: [
      { name: 'users', path: '/src/views/system/user/index.vue' },
      { name: 'roles', path: '/src/views/system/role/index.vue' },
      { name: 'menus', path: '/src/views/system/menu/index.vue' },
      { name: 'departments', path: '/src/views/system/dept/index.vue' },
      { name: 'dictionaries', path: '/src/views/system/dict/index.vue' },
      { name: 'client config', path: '/src/views/system/sysclient/index.vue' },
    ],
  },
  {
    area: 'iot operations',
    modules: [
      { name: 'products', path: '/src/views/iot/product/index.vue' },
      { name: 'devices', path: '/src/views/iot/device/index.vue' },
      { name: 'device detail', path: '/src/views/iot/device/device-edit.vue' },
      { name: 'groups', path: '/src/views/iot/group/index.vue' },
      { name: 'alerts', path: '/src/views/iot/alert/index.vue' },
      { name: 'firmware', path: '/src/views/iot/firmware/index.vue' },
      { name: 'scenes', path: '/src/views/iot/scene/index.vue' },
      { name: 'sip video', path: '/src/views/iot/sip/index.vue' },
      { name: 'sim cards', path: '/src/views/iot/card/sim/index.vue' },
    ],
  },
  {
    area: 'automation and reporting',
    modules: [
      { name: 'rule editor', path: '/src/views/ruleengine/editor/index.vue' },
      { name: 'drag editor', path: '/src/views/dragEditor/index.vue' },
      { name: 'scene detail', path: '/src/views/scene/list/detail/index.vue' },
      { name: 'scene edit', path: '/src/views/scene/list/edit.vue' },
      { name: 'report edit', path: '/src/views/dataCenter/report/report-edit.vue' },
      { name: 'job log', path: '/src/views/monitor/job/log.vue' },
    ],
  },
];

test.describe('main feature modules', () => {
  test.describe.configure({ mode: 'serial' });

  for (const featureArea of mainFeatureModules) {
    test.describe(featureArea.area, () => {
      for (const featureModule of featureArea.modules) {
        test(`${featureModule.name} compiles through the Vite dev server`, async ({ page }) => {
          const runtimeErrors: string[] = [];
          const failedRequests: string[] = [];
          const failedResponses: string[] = [];

          page.on('pageerror', (error) => {
            runtimeErrors.push(error.message);
          });
          page.on('requestfailed', (request) => {
            failedRequests.push(`${request.failure()?.errorText || 'request failed'} ${request.url()}`);
          });
          page.on('response', (response) => {
            if (!response.ok() && response.url().includes('/src/')) {
              failedResponses.push(`${response.status()} ${response.url()}`);
            }
          });

          await page.goto('/login', { waitUntil: 'domcontentloaded' });

          const importedModule = await page
            .evaluate(async (modulePath) => {
              const mod = await import(modulePath);

              return {
                hasDefaultExport: Boolean(mod.default),
              };
            }, featureModule.path)
            .catch((error) => {
              throw new Error(
                [
                  `Failed to import ${featureModule.path}: ${error.message}`,
                  failedResponses.length ? `Failed responses:\n${failedResponses.join('\n')}` : '',
                  failedRequests.length ? `Failed requests:\n${failedRequests.join('\n')}` : '',
                  runtimeErrors.length ? `Runtime errors:\n${runtimeErrors.join('\n')}` : '',
                ]
                  .filter(Boolean)
                  .join('\n\n')
              );
            });

          expect(importedModule.hasDefaultExport).toBe(true);
          expect(runtimeErrors).toEqual([]);
        });
      }
    });
  }
});
