import { spawnSync } from 'node:child_process';
import { existsSync, mkdirSync, readFileSync, rmSync } from 'node:fs';
import { dirname, resolve } from 'node:path';

const checkOnly = process.argv.includes('--check');
const openapiUrl = process.env.FASTBEE_OPENAPI_URL || 'http://localhost:8080/v3/api-docs';
const schemaPath = resolve(process.env.FASTBEE_OPENAPI_SCHEMA_OUT || 'src/api/schema.d.ts');
const tempPath = resolve('node_modules/.tmp/fastbee-openapi-schema.d.ts');
const outputPath = checkOnly ? tempPath : schemaPath;
const runner = process.platform === 'win32' ? 'npx.cmd' : 'npx';

mkdirSync(dirname(outputPath), { recursive: true });

const result = spawnSync(runner, ['--yes', 'openapi-typescript@7.13.0', openapiUrl, '-o', outputPath], {
  stdio: 'inherit',
  shell: process.platform === 'win32',
});

if (result.status !== 0) {
  process.exit(result.status ?? 1);
}

if (checkOnly) {
  if (!existsSync(schemaPath)) {
    console.error('src/api/schema.d.ts does not exist. Run npm run openapi.');
    process.exit(1);
  }

  const currentSchema = readFileSync(schemaPath, 'utf8');
  const generatedSchema = readFileSync(tempPath, 'utf8');
  rmSync(tempPath, { force: true });

  if (currentSchema !== generatedSchema) {
    console.error('OpenAPI schema is out of date. Run npm run openapi and commit the result.');
    process.exit(1);
  }
}
