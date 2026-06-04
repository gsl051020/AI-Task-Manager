# 免费上线部署（Render）

用 **Render 免费套餐** 把项目发布到公网，别人可通过 `https://xxx.onrender.com` 访问。无需信用卡（选 Free 方案时）。

## 特点与限制

| 项目 | 说明 |
|------|------|
| 费用 | $0 / 月（Free Web Service） |
| 访问 | 全球 HTTPS 链接，可发给同学/老师 |
| 冷启动 | 约 15 分钟无人访问后会休眠，首次打开需等待约 30～60 秒 |
| 数据 | 当前为**内存存储**，服务重启后任务数据会清空 |
| AI | 需在 Render 配置环境变量 `DEEPSEEK_API_KEY`（与本地相同） |

## 第 1 步：把代码推到 GitHub

确保仓库 **https://github.com/gsl051020/AI-Task-Manager** 已包含本仓库中的：

- `ai-task-manager/Dockerfile`
- `render.yaml`（仓库根目录）

在 PowerShell 中（先提交再推送）：

```powershell
cd E:\AI-Task-Manager
git add ai-task-manager/Dockerfile ai-task-manager/.dockerignore render.yaml `
  ai-task-manager/src/main/resources/application.properties `
  ai-task-manager/src/main/resources/application-prod.properties docs/DEPLOY-FREE.md
git commit -m "chore: add free Render deployment"
git push origin main
```

若尚未推送过，请先按 [GITHUB-PUSH.md](GITHUB-PUSH.md) 创建远程仓库。

## 第 2 步：在 Render 创建服务

1. 打开 **https://render.com** ，用 GitHub 账号注册/登录。  
2. 点击 **New +** → **Blueprint**（或 **Web Service**）。  
3. **Connect** 你的仓库 `gsl051020/AI-Task-Manager`。  
4. 若选 **Blueprint**：Render 会读取根目录 `render.yaml` 自动创建服务。  
5. 若选手动 **Web Service**：
   - **Language**：Docker  
   - **Root Directory**：`ai-task-manager`  
   - **Instance Type**：**Free**  
   - **Region**：Singapore（离国内相对近）  

## 第 3 步：配置环境变量（可选但推荐）

在服务的 **Environment** 中添加：

| Key | Value |
|-----|--------|
| `DEEPSEEK_API_KEY` | 你的 DeepSeek Key（勿提交到 Git） |
| `SPRING_PROFILES_ACTIVE` | `prod`（Blueprint 已包含可跳过） |

保存后 Render 会自动重新部署。

## 第 4 步：等待部署完成

- **Logs** 里出现 `Started AiTaskManagerApplication` 即成功。  
- 打开页面上的 **https://ai-task-manager-xxxx.onrender.com** 。  
- 首页：`/` ，工作台：`/tasks` 。

把该链接发给他人即可访问。

## 常见问题

**打开很慢**  
免费实例休眠后的冷启动属正常现象，多等一会儿或刷新一次。

**AI 功能报错**  
检查 Environment 里是否配置了 `DEEPSEEK_API_KEY`，并确认 Key 有效（见 [DEEPSEEK-SETUP.md](DEEPSEEK-SETUP.md)）。

**部署失败：Docker build**  
本地可先验证：

```powershell
cd E:\AI-Task-Manager\ai-task-manager
mvn -DskipTests clean package
```

**想换域名**  
Render 控制台 → 服务 → **Settings** → 可改子域名（仍免费）。

## 其他免费方案（备选）

- **Fly.io**：需 CLI，免费额度按用量计。  
- **Oracle Cloud 永久免费 VPS**：完全免费但需自己装 Java、配防火墙，步骤较多。  

本仓库默认推荐 **Render + Docker**，步骤最少、与 GitHub 联动最好。
