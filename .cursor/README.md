# .cursor 目录说明

本目录存放 Cursor 的 **项目级** 配置：MCP 服务器、Rules、Skills。  
（JSON 不能写 `//` 注释，因此 MCP 逐项说明写在本文件。）

---

## mcp.json — 各服务器用途

| 名称 | 用途 | 何时用 | 你需要准备 |
|------|------|--------|------------|
| **playwright** | 用 Edge 自动打开网页、点击、截图 | Day 6 检查页面效果；录屏前自测 | 已安装 Edge |
| **figma** | 读取 Figma 设计稿（颜色、布局、组件） | Day 6 按设计还原页面 | MCP 面板点 Connect 登录 Figma |
| **prompt-optimizer** | 优化发给 AI 的提示词，减少含糊描述 | 复杂需求前先优化 prompt | 无，npx 自动下载 |
| **real-plan** | 任务拆解与跟踪（task-guardian） | 规划 Day 3–7、列待办 | 无 |
| **github** | 读仓库、Issue、PR（GitHub API） | 提交代码、查远程仓库 | 环境变量 `GITHUB_TOKEN` |

**全局另有**（见 `C:\Users\21516\.cursor\mcp.json`）：`unity-editor-mcp`、`jetbrains-pycharm` 等（与本实训无关可忽略）。

### 在对话中的用法示例

```
用 playwright 打开 http://localhost:8080 并截图
/real-plan 帮我规划 Day 3 最小步骤
```

### 修改配置后

1. 保存 `mcp.json`
2. **完全重启 Cursor**
3. Settings → Tools & MCP → 确认绿色已连接

---

## rules/

| 文件 | 用途 |
|------|------|
| `spring-boot-ai-task-manager.mdc` | Spring Boot 技术栈与包结构约定 |
| `documentation-and-readability.mdc` | **强制** 代码/MCP/Skills 必须可读、有注释 |
| `qa-project-structure.mdc` | QA 技能生成文件的存放目录（qa-skills 安装器生成） |

---

## skills/

| 目录 | 用途 | 调用方式 |
|------|------|----------|
| `real-plan/` | 7 天实训计划与下一步拆解 | `/real-plan` 或 `@real-plan` |
| `ultra-qa/` | QA 入口，指向 58 个 `qa-*` 技能 | `/ultra-qa` |
| `visual-qa/` | 页面对比 Figma / 截图 diff | `/visual-qa` |
| `qa-*` | 各类测试/规格技能（来自 AZANIR/qa-skills） | `/qa-junit5-writer` 等 |

更多链接见：`docs/CURSOR-TOOLS.md`
