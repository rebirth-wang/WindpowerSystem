import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { resolve } from 'node:path';

const root = resolve(fileURLToPath(new URL('..', import.meta.url)));
const controllerPath = resolve(root, 'fastbee-open-api/src/main/java/com/fastbee/controller/device/DeviceController.java');
const schemaPath = resolve(root, 'fastbee-open-api/src/main/java/com/fastbee/controller/device/DeviceContractSchemas.java');

const controller = readFileSync(controllerPath, 'utf8');
let schemas = '';
try {
  schemas = readFileSync(schemaPath, 'utf8');
} catch {
  schemas = '';
}

const requiredControllerFragments = [
  'import io.swagger.annotations.ApiImplicitParam;',
  'import io.swagger.annotations.ApiImplicitParams;',
  'import io.swagger.annotations.ApiResponse;',
  'import io.swagger.annotations.ApiResponses;',
  'permission: iot:device:list',
  'permission: iot:device:query',
  'permission: iot:device:add',
  'permission: iot:device:edit',
  'permission: iot:device:remove',
  'DeviceContractSchemas.DevicePageResponse.class',
  'DeviceContractSchemas.DeviceDetailResponse.class',
  'DeviceContractSchemas.DeviceWriteResponse.class',
  'DeviceContractSchemas.DeviceDeleteResponse.class',
  'code = 401',
  'code = 403',
  'code = 500',
];

const requiredSchemaFragments = [
  'class DevicePageResponse',
  'class DeviceDetailResponse',
  'class DeviceWriteResponse',
  'class DeviceDeleteResponse',
  'public Integer code',
  'public String msg',
  'public List<DeviceVO> rows',
  'public Long total',
  'public DeviceVO data',
];

const missing = [
  ...requiredControllerFragments.filter((fragment) => !controller.includes(fragment)),
  ...requiredSchemaFragments.filter((fragment) => !schemas.includes(fragment)),
];

if (missing.length) {
  console.error('Device contract is incomplete. Missing fragments:');
  for (const fragment of missing) {
    console.error(`- ${fragment}`);
  }
  process.exit(1);
}

console.log('Device contract check passed.');
