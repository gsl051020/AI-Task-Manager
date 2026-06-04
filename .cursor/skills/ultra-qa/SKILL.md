---
name: ultra-qa
description: 对本项目进行测试与质量检查。在用户说「测试」「QA」「检查质量」「ultra QA」时使用；优先 Spring Boot / Thymeleaf 相关技能。
---

# Ultra QA — 本项目质量检查

## 用途

在写功能或答辩前，系统化检查：接口是否正常、页面是否可访问、是否需补测试用例。  
本仓库已安装 [AZANIR/qa-skills](https://github.com/AZANIR/qa-skills) 的 58 个子技能（目录 `.cursor/skills/qa-*`），本技能作为**入口索引**。

## 用法

1. 输入：`/ultra-qa` 或 `@ultra-qa`，并说明要检查的内容
2. 或直达子技能，例如：`/qa-spring-test-writer` 为 Controller 写测试
3. 视觉/UI 检查：配合 `/visual-qa` 与 **playwright** MCP

### 示例对话

```
/ultra-qa 检查 Day2：首页和 /api/hello 是否正常，给出测试清单
```

```
/qa-junit5-writer 为 HelloController 写一个最简单的单元测试
```

## 已安装的 QA 技能（`.cursor/skills/qa-*`）

### 与本项目最相关

| 技能 | 用途 |
|------|------|
| `qa-spring-test-writer` | Spring Boot 单元/集成测试 |
| `qa-junit5-writer` | JUnit 5 测试 |
| `qa-manual-test-designer` | 手工测试用例（答辩演示脚本） |
| `qa-playwright-ts-writer` | E2E（若加前端测试） |
| `qa-spec-writer` / `qa-spec-auditor` | 需求与规格 |
| `qa-orchestrator` | 编排完整 QA 流程 |
| `visual-qa` | 页面对比 Figma / 截图 diff |

## 推荐检查清单（实训）

1. `mvn test` 能通过
2. `http://localhost:8080/` 首页正常
3. `http://localhost:8080/api/hello` 返回 JSON
4. Day 3+：表单提交与展示无 500 错误
5. Day 4+：DeepSeek 调用失败时有友好错误提示

## 产物存放位置

见项目根目录 `AGENTS.md`（qa-skills 安装器生成，说明测试报告、用例写到哪里）。

## 依赖 MCP（可选）

| MCP | 用途 |
|-----|------|
| `playwright` | 打开 localhost 截图、点按钮 |
| `figma` | 读取设计稿（Day 6） |

说明见：`.cursor/README.md`、`docs/CURSOR-TOOLS.md`
