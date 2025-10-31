# 代码生成类型枚举 (CodeGenTypeEnum)

## 说明

该枚举用于指定 AI 代码生成的模式类型，在创建应用时需要传入 `codeGenType` 参数。

## 枚举值

| 枚举名称 | 描述 | value 值 | 说明 |
|---------|------|---------|------|
| `HTML` | 原生 HTML 模式 | `"html"` | 生成单文件 HTML 代码，适合简单的页面应用 |
| `MULTI_FILE` | 原生多文件模式 | `"multi_file"` | 生成多个文件的代码结构，适合较复杂的项目 |
| `VUE_PROJECT` | Vue 工程模式 | `"vue_project"` | 生成完整的 Vue 项目结构，包含构建配置和依赖 |

## 使用示例

### 在 TypeScript 中使用

```typescript
// 创建应用时传入生成模式
const createAppRequest = {
  appName: "我的应用",
  initPrompt: "创建一个待办事项应用",
  codeGenType: "html" // 使用 html、multi_file 或 vue_project
};

// 或者定义常量
const CODE_GEN_TYPE = {
  HTML: "html",
  MULTI_FILE: "multi_file",
  VUE_PROJECT: "vue_project"
} as const;

type CodeGenType = typeof CODE_GEN_TYPE[keyof typeof CODE_GEN_TYPE];
```

### API 请求示例

```typescript
import { createApp } from '@/api/appController';

// HTML 模式
await createApp({
  appName: "HTML 应用",
  initPrompt: "创建一个简单的页面",
  codeGenType: "html"
});

// Vue 项目模式
await createApp({
  appName: "Vue 应用",
  initPrompt: "创建一个 Vue 项目",
  codeGenType: "vue_project"
});
```

## 注意事项

- `codeGenType` 参数为可选，如果不传或为空，后端会默认使用 `"multi_file"` 模式
- 创建应用后，该应用的生成模式将固定，后续生成代码都会使用该模式
- 生成代码接口 `/app/chat/gen/code` 不需要传入生成模式参数，会自动使用应用创建时设置的模式




