/**
 * AI 能力封装层。
 * <p>
 * 本包负责对接外部 AI 服务（当前为 DeepSeek），并把复杂的 HTTP 请求细节隐藏起来。
 * Controller 或业务服务只需要调用简单方法，例如「提问」「分析任务」「优化日程」。
 */
package com.example.aitaskmanager.ai;