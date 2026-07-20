import { existsSync, readFileSync } from 'node:fs';
import { resolve } from 'node:path';

const requiredFiles = [
  'src/mocks/integration.ts',
  'e2e/integration-rollout.spec.ts',
  'e2e/fixtures/fastbeeMock.ts',
  '../docs/frontend-integration.md',
];

const requiredFragments = [
  ['src/mocks/integration.ts', 'systemUserListSuccess'],
  ['src/mocks/integration.ts', 'systemRoleListSuccess'],
  ['src/mocks/integration.ts', 'systemMenuListSuccess'],
  ['src/mocks/integration.ts', 'productListSuccess'],
  ['src/mocks/integration.ts', 'alertListSuccess'],
  ['src/mocks/integration.ts', 'workOrderListSuccess'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/system/user/list'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/system/role/list'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/system/menu/list'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/iot/product/list'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/iot/alert/list'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/iot/workOrder/list'],
  ['e2e/integration-rollout.spec.ts', 'validates common module list contracts'],
  ['e2e/integration-rollout.spec.ts', 'validates common module detail contracts'],
  ['../docs/frontend-integration.md', 'Stage 4/5 rollout gate'],
];

const missingFiles = requiredFiles.filter((file) => !existsSync(resolve(file)));

if (missingFiles.length) {
  console.error(`Missing required files:\n${missingFiles.join('\n')}`);
  process.exit(1);
}

const missingFragments = requiredFragments.filter(([file, fragment]) => {
  const content = readFileSync(resolve(file), 'utf8');
  return !content.includes(fragment);
});

if (missingFragments.length) {
  console.error(
    `Missing required fragments:\n${missingFragments.map(([file, fragment]) => `${file}: ${fragment}`).join('\n')}`
  );
  process.exit(1);
}

console.log('Integration rollout check passed.');
