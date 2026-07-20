# E2E issue reproduction

本目录用于把用户 issue 转成可重复执行的 Playwright 用例。

## 常用命令

```bash
npm run test:e2e
npm run test:e2e:integration
npm run test:e2e:debug
npm run test:e2e:ui
npx playwright show-report
```

## Agent 处理 issue 的最小流程

1. 先把 issue 的复现步骤写成 `e2e/<issue-id>.spec.ts`。
2. 执行 `npm run test:e2e -- e2e/<issue-id>.spec.ts`，确认测试先失败。
3. 根据失败截图、trace、video、控制台错误和源码定位问题。
4. 修复后再次执行同一个 E2E 用例，确认变绿。
5. 保留复现用例，作为该 issue 的回归测试。

## 环境变量

- `PLAYWRIGHT_BASE_URL`：指定已有前端服务地址；未设置时默认启动本地 Vite 服务 `http://127.0.0.1:5173`。

## Playwright codegen 录制用例

`codegen` 用于在真实浏览器里操作页面，并把操作过程生成 Playwright 代码。适合快速生成 issue 复现步骤的初稿，录完后需要人工整理断言、等待条件和 mock 数据。

先启动本地前端服务：

```bash
npm run dev -- --host 127.0.0.1 --port 5173
```

在另一个终端进入 `vue3` 目录后开始录制：

```bash
npx playwright codegen http://127.0.0.1:5173/login
```

直接把录制结果写入文件：

```bash
npx playwright codegen http://127.0.0.1:5173/login -o e2e/generated-login.spec.ts
```

指定浏览器窗口尺寸，便于复现桌面端布局：

```bash
npx playwright codegen --viewport-size=1440,900 http://127.0.0.1:5173/login
```

保存登录态：

```bash
npx playwright codegen http://127.0.0.1:5173/login --save-storage=e2e/.auth/admin.json
```

加载已保存的登录态继续录制业务页面：

```bash
npx playwright codegen http://127.0.0.1:5173/iot/device/list --load-storage=e2e/.auth/admin.json
```

录制完成后建议：

1. 将生成文件重命名为具体 issue 或业务场景，例如 `e2e/<issue-id>.spec.ts`。
2. 优先复用 `e2e/fixtures/fastbeeMock.ts` 中的 mock 后端能力，避免依赖外部测试环境。
3. 把脆弱的 CSS/XPath 选择器替换为更稳定的文本、角色或业务可见内容断言。
4. 删除无意义的点击、悬停和自动等待，只保留能复现问题的最小步骤。
5. 使用 `npm run test:e2e -- e2e/<file>.spec.ts` 验证录制用例可以稳定执行。

## Midscene.js AI-assisted checks

Midscene tests live beside normal Playwright specs and should be used for high-level UI recognition, not low-level API contract checks.

```bash
npm run test:e2e:ai
```

Required model environment:

- `MIDSCENE_MODEL_NAME`: model name used by Midscene.
- `MIDSCENE_MODEL_API_KEY` or `OPENAI_API_KEY`: model API key.
- `MIDSCENE_MODEL_BASE_URL`: optional OpenAI-compatible endpoint.
- `MIDSCENE_MODEL_FAMILY`: recommended for visual element localization.
- `MIDSCENE_CACHE=true`: optional local Midscene cache for repeated development runs.

If the required model variables are missing, the AI spec is skipped so regular E2E verification remains deterministic.
