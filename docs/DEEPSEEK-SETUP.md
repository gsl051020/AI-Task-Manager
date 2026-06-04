# DeepSeek 接入说明（Day 4）

本文档说明 AI任务管理平台 如何配置和验证 DeepSeek API。

---

## 1. 当前实现了什么

| 文件 | 用途 |
|------|------|
| `application.properties` | 读取 DeepSeek 地址、模型名、API Key |
| `DeepSeekService.java` | 封装 DeepSeek HTTP 调用 |
| `AiController.java` | 提供 `/api/ai/test` 测试接口 |

---

## 2. 为什么 API Key 不写进代码

DeepSeek API Key 属于密钥，如果写进代码或提交到 Git，别人拿到后可能会消耗你的额度。

本项目使用环境变量：

```text
DEEPSEEK_API_KEY
```

---

## 3. Windows PowerShell 临时配置方式

只对当前终端有效，适合今天测试：

```powershell
$env:DEEPSEEK_API_KEY="你的 DeepSeek API Key"
cd E:\AI-Task-Manager\ai-task-manager
mvn spring-boot:run
```

然后访问：

```text
http://localhost:8080/api/ai/test
```

---

## 4. Windows 永久配置方式

1. 打开 Windows 搜索：`环境变量`
2. 进入「编辑系统环境变量」→「环境变量」
3. 在「用户变量」中新建：
   - 变量名：`DEEPSEEK_API_KEY`
   - 变量值：你的 DeepSeek API Key
4. 保存后，**完全重启 Cursor**
5. 重新运行项目

---

## 5. 验证标准

### 未配置 API Key 时

访问 `/api/ai/test` 会返回提示：

```json
{
  "answer": "DeepSeek API Key 尚未配置..."
}
```

这说明接口本身正常，只是还没有密钥。

### 配置 API Key 后

访问 `/api/ai/test` 应返回一段 AI 生成的中文回答。

---

## 6. 下一步

Day 5 会基于 `DeepSeekService` 实现：

- 单个任务：AI 分析做法与建议
- 全部任务：AI 优化 1 天/多天日程
- 任务分类：AI 自动分类