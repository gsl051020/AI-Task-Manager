# AI 任务管理平台

基于 **Spring Boot 3**、**Thymeleaf** 与 **DeepSeek API** 的智能任务工作台，适用于大学 Java Web 七天实训：任务分类、日历追踪、完成状态、AI 分析与日程优化。

## 技术栈

- Java 17
- Spring Boot 3.5
- Thymeleaf + Bootstrap 5
- DeepSeek Chat API（环境变量配置，不写进代码）

## 快速启动

```powershell
# 1. 配置 DeepSeek（仅当前终端，测试用）
$env:DEEPSEEK_API_KEY="你的 DeepSeek API Key"

# 2. 启动应用
cd ai-task-manager
mvn spring-boot:run
```

浏览器访问：

- 首页：http://localhost:8080/
- 工作台：http://localhost:8080/tasks
- AI 连通测试：http://localhost:8080/api/ai/test

## 免费上线（公网访问）

按 **[docs/DEPLOY-FREE.md](docs/DEPLOY-FREE.md)** 使用 **Render 免费套餐** + GitHub 自动部署，无需信用卡。部署后把 `https://xxx.onrender.com` 发给他人即可访问。

## 文档

| 文档 | 说明 |
|------|------|
| [docs/DEPLOY-FREE.md](docs/DEPLOY-FREE.md) | 免费公网部署（Render） |
| [docs/FEATURE-SPEC.md](docs/FEATURE-SPEC.md) | 功能规格与 7 天实训映射 |
| [docs/DEEPSEEK-SETUP.md](docs/DEEPSEEK-SETUP.md) | DeepSeek API 配置与验证 |
| [docs/实训报告.md](docs/实训报告.md) | 实训报告（Markdown） |

## 项目结构

```
AI-Task-Manager/
├── ai-task-manager/     # Spring Boot 主工程
├── docs/                # 规格与实训文档
└── .cursor/             # Cursor 技能与 MCP 配置
```

## 安全说明

请勿将 `DEEPSEEK_API_KEY` 或任何 API Key 提交到仓库。本项目通过环境变量 `DEEPSEEK_API_KEY` 读取密钥。

## 许可证

实训教学项目，按课程要求使用。
