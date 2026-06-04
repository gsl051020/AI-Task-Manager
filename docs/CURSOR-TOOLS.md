# Cursor 工具与 MCP 安装说明（AI 任务管理平台）

本文档汇总你要求的 GitHub 资源及在本项目中的安装状态。

> **可读性约定**：本仓库所有代码、MCP、Skills 均需带用途说明。  
> - MCP 逐项说明 → [`.cursor/README.md`](../.cursor/README.md)（因 `mcp.json` 不能写注释）  
> - 强制规则 → [`.cursor/rules/documentation-and-readability.mdc`](../.cursor/rules/documentation-and-readability.mdc)

---

## 一、资源对照表

| 名称 | GitHub / 官网 | 本项目中的形式 | 状态 |
|------|----------------|----------------|------|
| **awesome-cursor-rules** | https://github.com/PatrickJS/awesome-cursorrules | `.cursor/rules/spring-boot-ai-task-manager.mdc` | 已添加项目规则 |
| **Cursor Directory** | https://cursor.directory | 浏览插件/MCP 的目录站 | 见下方「如何发现更多」 |
| **awesome-mcp-servers** | https://github.com/wong2/awesome-mcp-servers | 文档索引 → https://mcpservers.org | 参考用 |
| **Playwright MCP** | https://github.com/microsoft/playwright-mcp | `.cursor/mcp.json` → `playwright` | 已配置（Edge） |
| **prompt-optimizer** | https://github.com/heyjustinai/prompt-ops-mcp | `.cursor/mcp.json` → `prompt-optimizer` | 已配置 |
| **real plan** | https://github.com/jalbarrang/task-guardian-mcp | MCP 名 `real-plan` + 技能 `real-plan` | 已配置 |
| **ultra QA** | https://github.com/AZANIR/qa-skills | 58 个 `qa-*` 技能 + `ultra-qa` 索引技能 | 已安装 |
| **AI Slack cleaner** | https://github.com/sgratzl/slack_cleaner2 | **非 MCP**，见下方说明 | 需自行配置 Token |

---

## 二、项目内文件位置

```
AI-Task-Manager/
├── .cursor/
│   ├── mcp.json              # 本项目 MCP（优先于全局）
│   ├── rules/
│   │   ├── spring-boot-ai-task-manager.mdc
│   │   └── qa-project-structure.mdc
│   └── skills/
│       ├── real-plan/        # 实训计划技能
│       ├── ultra-qa/         # QA 入口技能
│       ├── visual-qa/        # 视觉对比（Figma/截图）
│       └── qa-*/             # AZANIR QA 技能包（58 个）
├── AGENTS.md                 # QA 产物目录约定（qa-skills 生成）
└── docs/CURSOR-TOOLS.md      # 本文件
```

全局配置（所有项目共用）：`C:\Users\21516\.cursor\mcp.json`

---

## 三、启用步骤（必做）

1. **完全重启 Cursor**
2. 打开 **Settings → Tools & MCP**
3. 确认以下服务为 **已连接**（绿色）：
   - `playwright`
   - `prompt-optimizer`
   - `real-plan`
   - `figma`（需点击 Connect 完成 OAuth）
   - `github`（需配置 `GITHUB_TOKEN`，见下）
4. 在 Agent 对话中可用：`/real-plan`、`/ultra-qa`、`/visual-qa`

### GitHub MCP Token

1. 打开 https://github.com/settings/tokens
2. 创建 Fine-grained 或 Classic PAT（repo 读权限即可）
3. 在 Windows **用户环境变量** 中添加：`GITHUB_TOKEN=你的token`
4. 重启 Cursor

### Figma MCP

- 远程：https://mcp.figma.com/mcp（已在 `mcp.json`）
- 在 MCP 面板点击 **Connect** 登录 Figma

---

## 四、各工具用途（美术 / 开发）

| 工具 | 用途 |
|------|------|
| **Playwright** | 打开 `http://localhost:8080` 预览、截图、检查页面 |
| **Figma** | 读取设计稿，Day 6 对齐 UI |
| **prompt-optimizer** | 优化发给 AI 的提示词，减少含糊描述 |
| **real-plan** | 按 Day 3–7 拆解任务并跟踪 |
| **ultra-qa** | 生成测试、检查 Spring Boot 接口与页面 |
| **visual-qa** | 设计稿 vs 实际页面对比 |

---

## 五、AI Slack Cleaner（说明）

**这不是 Cursor MCP**，而是 Python 工具，用于批量清理 Slack 消息/文件：

- 仓库：https://github.com/sgratzl/slack_cleaner2
- 需要 Slack User OAuth Token 与相应权限
- **有删除风险**，请勿在未备份的工作区随意执行

若只需在 Cursor 里**读 Slack**（不清理），可用：

- https://github.com/slackapi/slack-mcp-plugin（官方远程 MCP）

安装 Slack 清理工具（可选，自行在终端执行）：

```powershell
pip install slack-cleaner2
```

---

## 六、发现更多 MCP / 插件

| 站点 | 链接 |
|------|------|
| Cursor Directory | https://cursor.directory |
| Awesome MCP（wong2） | https://github.com/wong2/awesome-mcp-servers |
| MCP 目录站 | https://mcpservers.org |
| 更大合集 | https://github.com/punkpeye/awesome-mcp-servers |

在 Cursor 中也可尝试：`/add-plugin figma`（Figma 官方插件）

---

## 七、故障排查

| 问题 | 处理 |
|------|------|
| MCP 显示 needsAuth | 点击 Connect 完成 OAuth（Figma / Slack） |
| `github` 失败 | 检查 `GITHUB_TOKEN` 环境变量 |
| `playwright` 失败 | 确认已安装 Edge；重启 Cursor |
| Figma 连上后又断 | 网络问题，重连或改用 Figma Desktop 本地 MCP `http://127.0.0.1:3845/mcp` |
| 技能太多 / 上下文大 | 对话里只 `@` 需要的技能，如 `@ultra-qa` |

---

## 八、与实训进度的关系

- **Day 2–5**：以写功能为主，QA / Playwright 辅助验证即可
- **Day 6**：重点用 **Figma + Playwright + visual-qa + Bootstrap**
- **Day 7**：**real-plan** 整理答辩要点；**Playwright** 辅助录屏前检查

继续开发请回复：**「开始 Day 3」**。
