# 推送到 GitHub（一次性操作）

本地已完成：`git init`、首次提交（`main`）、远程地址已配置。

你的 GitHub 账号：**gsl051020**

## 第 1 步：在网页创建空仓库

1. 打开：https://github.com/new  
2. **Repository name** 填：`AI-Task-Manager`（必须与下面远程地址一致）  
3. 选择 **Public**  
4. **不要**勾选 “Add a README file”（本地已有提交）  
5. 点击 **Create repository**

## 第 2 步：推送代码

在 PowerShell 中执行：

```powershell
cd E:\AI-Task-Manager
git push -u origin main
```

若提示登录，使用 GitHub 账号 + Personal Access Token（不是密码）。

## 第 3 步：验证

浏览器打开：

https://github.com/gsl051020/AI-Task-Manager

应能看到 `README.md`、`ai-task-manager/`、`docs/` 等目录，且 **没有** `ai-task-manager/target/`。

## 已配置的远程地址

```
origin  https://github.com/gsl051020/AI-Task-Manager.git
```

## 若仓库名想改成别的

```powershell
git remote set-url origin https://github.com/gsl051020/<你的仓库名>.git
git push -u origin main
```

## 可选：安装 GitHub CLI 后一键创建

```powershell
winget install GitHub.cli
# 重启终端后
gh auth login
cd E:\AI-Task-Manager
gh repo create AI-Task-Manager --public --source=. --remote=origin --push
```
