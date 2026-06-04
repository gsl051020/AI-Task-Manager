# 国内访问友好的免费上线（推荐 Zeabur）

Render 在国外，国内打开可能较慢。下面用 **Zeabur 免费方案**（中文界面、连 GitHub 自动部署），适合实训答辩把链接发给老师同学。

> 说明：我无法代替你登录 Zeabur 账号，需要你在网页上点几次；代码已在 GitHub，按步骤做即可。

## 方案对比（都免费起步）

| 平台 | 国内访问 | 是否要信用卡 | 适合 |
|------|----------|--------------|------|
| **Zeabur**（推荐） | 较快 | 否 | GitHub 一键部署，步骤最少 |
| Render | 较慢 | 否 | 已配置 `render.yaml`，可作备选 |
| Sealos | 快 | 注册送少量余额，需实名 | 国内云，用完试用金可能需充值 |
| cpolar 穿透 | 快 | 否 | 本机运行 + 临时公网，适合短时演示 |

---

## 一、Zeabur 部署（推荐，约 10 分钟）

### 1. 注册并连接 GitHub

1. 打开 **https://zeabur.com**（可切换中文）
2. 用 **GitHub 登录**
3. 按提示安装 **Zeabur GitHub App**，授权仓库 `AI-Task-Manager`

### 2. 新建项目并部署

1. 控制台 → **新建项目**（Project）
2. 项目中点 **添加服务** → 选 **Git**
3. 搜索并选择：**`gsl051020/AI-Task-Manager`**
4. 进入该服务的 **设置 / 构建**：
   - **根目录（Root Directory）**：`ai-task-manager`（重要，否则找不到 Dockerfile）
   - 构建方式：自动识别 **Dockerfile**
5. 打开 **变量（Environment Variables）**，添加：

   | 变量名 | 值 |
   |--------|-----|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `DEEPSEEK_API_KEY` | 你的 DeepSeek Key（可选，不填则 AI 不可用） |

6. 点击 **部署**，等待日志出现 `Started AiTaskManagerApplication`

### 3. 绑定公网域名

1. 服务页 → **网络（Networking）** 或 **域名**
2. 点 **生成域名** / **Bind Domain**，会得到类似：  
   `https://ai-task-manager-xxxxx.zeabur.app`
3. 浏览器访问：
   - 首页：`/`
   - 工作台：`/tasks`

把 **zeabur.app** 链接发给他人即可；国内一般比 Render 更顺畅。

### 4. 以后更新

本地 `git push origin main` 后，Zeabur 会自动重新部署（与 Render 相同）。

---

## 二、Sealos 国内云（可选）

适合希望服务器在国内的场景；新用户通常有 **少量试用金**，用完需按量付费，不算「永久全免费」。

1. 打开 **https://cloud.sealos.run** 注册（需实名）
2. **应用管理** → **新建应用**
3. 若已用 GitHub Actions 构建镜像（见仓库 `.github/workflows/docker-publish.yml`），可填镜像：  
   `ghcr.io/gsl051020/ai-task-manager:latest`
4. 容器端口：**8080**，开启 **外网访问**
5. 环境变量：`SPRING_PROFILES_ACTIVE=prod`，`DEEPSEEK_API_KEY=你的key`
6. 部署后使用控制台给的 **公网地址**

---

## 三、cpolar 本机穿透（完全免费、临时演示）

不装云服务器，在你电脑上跑项目，用免费隧道暴露到公网：

1. 注册 **https://www.cpolar.com** ，下载客户端
2. 本机启动项目：

   ```powershell
   $env:DEEPSEEK_API_KEY="你的key"
   cd E:\AI-Task-Manager\ai-task-manager
   mvn spring-boot:run
   ```

3. cpolar 新建隧道：本地端口 **8080**，得到 `https://xxxx.cpolar.cn` 临时地址  
4. 电脑关机或关闭 cpolar 后链接失效；适合答辩当天临时用

---

## 环境变量说明（所有平台通用）

| 变量 | 必填 | 说明 |
|------|------|------|
| `SPRING_PROFILES_ACTIVE` | 建议 | 填 `prod` 开启线上模板缓存 |
| `DEEPSEEK_API_KEY` | 否 | 不填可浏览页面，AI 功能不可用 |
| `PORT` | 否 | 云平台自动注入，不要手动改 |

---

## 常见问题

**Zeabur 构建失败：找不到 Dockerfile**  
检查根目录是否填 **`ai-task-manager`**，不是仓库根目录。

**打开很慢**  
免费套餐闲置会休眠，等 30～60 秒再刷新。

**任务数据没了**  
当前是内存存储，重启后会清空，答辩前重新加几条演示任务即可。
