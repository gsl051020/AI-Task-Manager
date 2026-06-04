package com.example.aitaskmanager.controller;

import com.example.aitaskmanager.ai.DeepSeekService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AI 接口控制器（实训 Day 4）。
 * <p>
 * 用途：提供后端 AI 能力的测试入口，先确认 DeepSeek 是否能被项目调用。
 * 后续 Day 5 会继续扩展为「单任务分析」「AI 自动分类」「日程优化」等接口。
 * <p>
 * 验证：访问 <a href="http://localhost:8080/api/ai/test">http://localhost:8080/api/ai/test</a>。
 */
@RestController
public class AiController {

    private final DeepSeekService deepSeekService;

    /**
     * 构造 AI 控制器。
     *
     * @param deepSeekService DeepSeek 调用服务
     */
    public AiController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    /**
     * Day 4 验收接口：向 DeepSeek 发送一段测试问题。
     *
     * @param prompt 测试问题；不传时使用默认问题
     * @return JSON，包含用户输入和 AI 回复
     */
    @GetMapping("/api/ai/test")
    public Map<String, String> testDeepSeek(
            @RequestParam(defaultValue = "请用一句话介绍 AI任务管理平台 可以帮助我做什么") String prompt) {
        return Map.of(
                "prompt", prompt,
                "answer", deepSeekService.ask(prompt)
        );
    }
}