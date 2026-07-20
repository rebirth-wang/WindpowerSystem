import { existsSync, readFileSync } from 'node:fs';
import { resolve } from 'node:path';

const requiredFiles = [
  'src/api/iot/device.ts',
  'src/api/iot/device-contract.ts',
  'src/mocks/device.ts',
  'e2e/fixtures/fastbeeMock.ts',
  'e2e/device-management.spec.ts',
];

const requiredFragments = [
  ['src/api/iot/device.ts', "import type { paths } from '@/api/schema'"],
  ['src/api/iot/device-contract.ts', "import type { paths } from '@/api/schema'"],
  ['src/api/iot/device-contract.ts', 'export interface DeviceVO'],
  ['src/api/iot/device.ts', 'Promise<DevicePageResponse>'],
  ['src/api/iot/device.ts', 'Promise<DeviceDetailResponse>'],
  ['src/mocks/device.ts', 'deviceListSuccess'],
  ['src/mocks/device.ts', 'deviceListEmpty'],
  ['src/mocks/device.ts', 'deviceBusinessFailure'],
  ['src/mocks/device.ts', 'deviceUnauthorized'],
  ['e2e/fixtures/fastbeeMock.ts', 'mockFastBeeApis'],
  ['e2e/fixtures/fastbeeMock.ts', '/dev-api/iot/device/list'],
  ['e2e/device-management.spec.ts', 'lists devices from mock response'],
  ['e2e/device-management.spec.ts', 'opens device detail from mock response'],
  ['e2e/device-management.spec.ts', 'handles empty device list mock response'],
  ['e2e/device-management.spec.ts', 'creates edits and deletes a device through mocked APIs'],
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

console.log('Device phase 2/3 check passed.');
