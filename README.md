# ai-code-creator
ai代码生成平台


应用部署需要自行配置nginx访问路径，在     {运行目录}/tmp/code_deploy/   下

## 项目亮点

### 后端亮点
- 基于 Spring WebFlux 的流式输出：`SSE + Flux<String>` 实现连续推送，接口 `GET /app/chat/gen/code` 返回 `ServerSentEvent`，前端可边生成边预览。
- LangChain4j 深度集成：`AiServices` 构建多形态 AI 能力，支持普通流 `Flux<String>` 与推理流 `TokenStream`；通过适配器将 `TokenStream` 转换为 `Flux<String>` 并携带工具调用事件，兼顾易用性与扩展性。
- 会话记忆与冷启动优化：使用 `RedisChatMemoryStore + MessageWindowChatMemory` 按 `appId` 维度加载与持久化历史对话，提升上下文质量。
- Caffeine 本地缓存：对 `AiCodeGeneratorService` 进行实例级缓存（最大 1000、写入 30 分钟、访问 10 分钟），减少重复构建开销。
- 可插拔的解析与保存策略：
  - 解析器策略接口 `CodeParser` + 执行器 `CodeParserExecutor`，按类型（HTML / MULTI_FILE / VUE_PROJECT）切换解析流程。
  - 保存模板 `CodeFileSaverTemplate` 统一落盘流程，便于扩展新代码类型与部署逻辑。
- 流处理器解耦：`StreamHandlerExecutor` 按类型切换 `SimpleTextStreamHandler` 与 `JsonMessageStreamHandler`，分别处理纯文本与包含工具调用的复杂流。
- 分布式限流：基于 Redisson `RRateLimiter` 自研注解 `@RateLimit` + AOP 切面，支持 USER / API / IP 级别限流，按需配置窗口与频率，保护热点接口与模型资源。
- 健壮的异常与权限体系：统一异常码与全局异常处理，`@AuthCheck` 注解保护管理员接口，参数校验与业务校验清晰。
- 代码生成全链路：从提示词到解析、保存、预览与一键部署（输出目录 `{运行目录}/tmp/code_deploy/`），形成闭环。

参考要点（来自`测试`与源码）：
- 模板模块与策略接口拆分
- 工具调用采用覆盖源码方式兼容流式输出
- `AiCodeGeneratorFacade` 中将 `Flux<String>` 与 `TokenStream` 做适配转换
- 使用 Redisson 实现分布式限流

### 前端亮点
- 技术栈现代化：Vue 3 + TypeScript + Vite 7 + Pinia + Vue Router，工程化脚手架完善。
- 流式渲染体验：使用 `fetch + ReadableStream` 解析 `SSE`，支持携带 Cookie 的会话鉴权，消息逐字更新、即时滚动与完成事件处理。
- 可视化编辑：`visualEditor.ts` 注入可视化编辑脚本，支持在预览中直接修改样式与节点，所见即所得。
- Markdown 友好展示：`marked + highlight.js` 渲染对话中的 Markdown/代码块，暗色主题样式优化。
- UI 与生态：集成 Ant Design Vue 与 Tailwind（按需启用），支持更快的页面搭建与主题扩展。
- API 工具链：OpenAPI 到 TS 类型生成命令，保持前后端类型一致性，降低接口联调成本。