---
name: real-plan
description: 为 AI任务管理平台 制定可执行的多步开发计划，并用 task-guardian MCP 拆解与跟踪任务。在用户说「做计划」「real plan」「按7天实训」时使用。
---

# Real Plan — 实训任务规划

## 用途

把 7 天实训目标拆成**下一步就能做、能验证**的小任务，避免一次写完整项目。  
可配合 MCP **real-plan**（底层为 [task-guardian-mcp](https://github.com/jalbarrang/task-guardian-mcp)）在 `.task/` 目录记录任务。

## 用法

1. 在 Agent 对话输入：`/real-plan` 或 `@real-plan`
2. 说明当前进度，例如：「Day 2 已完成，帮我规划 Day 3」
3. Agent 只给出**一个**最小下一步 + 验证方式 + 后续预览

### 示例对话

```
/real-plan Day2 已完成，请规划 Day3 任务输入页，只要第一步
```

```
@real-plan 按实训表列出我还缺什么
```

## 何时使用

- 用户要开始新的一天（Day 3–7）
- 用户要求列出步骤、里程碑或任务拆解
- 用户说「real plan」「帮我规划」

## 7 天实训对照（本项目）

| 天 | 目标 |
|----|------|
| Day 2 | Spring Boot 启动 + Hello API + 首页 |
| Day 3 | 任务输入页、提交、结果展示（内存） |
| Day 4 | 接入 DeepSeek |
| Day 5 | AI 任务拆解 |
| Day 6 | Bootstrap 美化 |
| Day 7 | 部署 + 答辩 PPT + 录屏 |

## 工作流程

1. 确认当前已完成项（不要重复做）
2. 只规划**下一步最小可验证任务**（1–3 个子步骤）
3. 若已启用 **real-plan** MCP，可用 `create_task` / `list_tasks` 记录任务
4. 每步写清：**为什么、如何验证、失败时怎么排查**

## 输出格式

```markdown
## 当前进度
- [x] ...
- [ ] ...

## 下一步（仅一步）
1. ...
验证：...

## 再之后（预览，今天不做）
- ...
```

## 依赖 MCP

| MCP 名称 | 作用 |
|----------|------|
| `real-plan` | 任务 CRUD、依赖关系（配置见 `.cursor/mcp.json`） |

说明见：`.cursor/README.md`
