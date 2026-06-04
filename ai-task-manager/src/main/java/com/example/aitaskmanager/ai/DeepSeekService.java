package com.example.aitaskmanager.ai;

import com.example.aitaskmanager.model.Task;
import com.example.aitaskmanager.model.TaskCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * DeepSeek 调用服务（实训 Day 4）。
 * <p>
 * 用途：把项目中的 AI 请求统一封装在这里，Controller 不直接关心 HTTP 请求格式。
 * 当前先提供一个通用的 {@link #ask(String)} 方法，后续 Day 5 会基于它实现任务分析、自动分类和日程优化。
 * <p>
 * 安全说明：API Key 从环境变量 {@code DEEPSEEK_API_KEY} 读取，不写进代码。
 */
@Service
public class DeepSeekService {

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;
    private final String model;

    /**
     * 构造 DeepSeek 服务。
     *
     * @param builder RestClient 构建器，由 Spring Boot 自动提供
     * @param apiUrl  DeepSeek API 地址，来自 application.properties
     * @param apiKey  DeepSeek API Key，来自环境变量 DEEPSEEK_API_KEY
     * @param model   DeepSeek 模型名称，例如 deepseek-chat
     */
    public DeepSeekService(
            RestClient.Builder builder,
            @Value("${deepseek.api-url}") String apiUrl,
            @Value("${deepseek.api-key:}") String apiKey,
            @Value("${deepseek.model}") String model) {
        this.restClient = builder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
    }

    /**
     * 向 DeepSeek 发送一个普通文本问题，并返回 AI 回复。
     * <p>
     * 如果还没有配置 API Key，返回友好提示而不是抛异常，便于 Day 4 先验证接口流程。
     *
     * @param userPrompt 用户问题或任务描述
     * @return DeepSeek 回复文本，或配置提示
     */
    public String ask(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "DeepSeek API Key 尚未配置。请先设置环境变量 DEEPSEEK_API_KEY，然后重启应用。";
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "你是 AI任务管理平台 的任务规划助手，回答要简洁、具体、适合大学实训项目。"),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.7
        );

        Map<?, ?> response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return extractContent(response);
    }

    /**
     * 分析单个任务，生成具体做法和建议（实训 Day 4）。
     * <p>
     * 用途：任务列表中的「AI 分析做法与建议」按钮会调用此方法。
     *
     * @param task 用户选中的任务
     * @return AI 给出的具体执行建议
     */
    public String analyzeTask(Task task) {
        String prompt = """
                请分析下面这个任务，并给出具体做法和建议。
                
                要求：
                1. 用中文回答
                2. 分成「执行步骤」「注意事项」「预计耗时」三部分
                3. 适合大学生实训项目使用，建议要具体、可执行
                
                任务标题：%s
                任务分类：%s
                任务描述：%s
                """.formatted(
                task.getTitle(),
                task.getCategory().getDisplayName(),
                task.getContent() == null || task.getContent().isBlank() ? "无" : task.getContent()
        );
        return ask(prompt);
    }

    /**
     * 根据任务标题和描述自动推荐分类（实训 Day 4）。
     * <p>
     * 用途：页面点击「AI 自动分类」后，AI 从固定 5 类中选择一个分类并预填到表单。
     * 如果 DeepSeek API Key 未配置或返回无法识别的内容，会默认返回「日常」。
     *
     * @param title   任务标题
     * @param content 任务描述
     * @return AI 推荐的任务分类
     */
    public TaskCategory classifyTask(String title, String content) {
        String prompt = """
                请根据下面的任务，判断它最适合哪个分类。
                
                只能从下面 5 个分类中选择一个：
                DAILY（日常）
                STUDY（学习）
                WORK（工作）
                PROGRAMMING（编程）
                EXERCISE（运动）
                
                输出要求：
                只输出一个分类英文枚举名，不要解释。
                
                任务标题：%s
                任务描述：%s
                """.formatted(
                title == null || title.isBlank() ? "无标题" : title,
                content == null || content.isBlank() ? "无描述" : content
        );

        String aiAnswer = ask(prompt);
        return TaskCategory.fromAiText(aiAnswer);
    }

    /**
     * 根据全部任务生成日程优化预览草稿（实训 Day 4/5）。
     * <p>
     * 用途：页面点击「优化 1 天安排」或「优化多天安排」后，先生成可编辑预览。
     * 用户确认后才会保存为正式日程。
     *
     * @param tasks 当前所有任务
     * @param days  希望安排的天数，最少为 1 天
     * @return AI 生成的日程草稿文本
     */
    public String optimizeSchedule(List<Task> tasks, int days) {
        if (tasks == null || tasks.isEmpty()) {
            return "当前没有任务，无法生成日程安排。请先添加任务。";
        }

        int safeDays = Math.max(days, 1);
        StringBuilder taskText = new StringBuilder();
        for (Task task : tasks) {
            taskText.append("- 编号：")
                    .append(task.getId())
                    .append("；标题：")
                    .append(task.getTitle())
                    .append("；分类：")
                    .append(task.getCategory().getDisplayName())
                    .append("；描述：")
                    .append(task.getContent() == null || task.getContent().isBlank() ? "无" : task.getContent())
                    .append(System.lineSeparator());
        }

        String prompt = """
                请根据下面的任务列表，生成一个 %d 天的日程优化预览草稿。
                
                要求：
                1. 用中文回答
                2. 按「第 1 天」「第 2 天」这样的格式分组
                3. 每个任务给出建议时间段、任务标题、执行建议
                4. 优先把学习/编程类任务安排在精力较好的时间段
                5. 运动类任务尽量安排在早晚
                6. 输出内容要适合放进页面预览区，方便用户继续手动修改
                
                任务列表：
                %s
                """.formatted(safeDays, taskText);

        return ask(prompt);
    }

    /**
     * 从 DeepSeek 的 OpenAI 兼容响应中取出 choices[0].message.content。
     *
     * @param response DeepSeek 原始 JSON 响应，已被 Spring 转成 Map
     * @return AI 回复内容；如果结构异常则返回兜底提示
     */
    private String extractContent(Map<?, ?> response) {
        if (response == null) {
            return "DeepSeek 没有返回内容。";
        }

        Object choicesObject = response.get("choices");
        if (!(choicesObject instanceof List<?> choices) || choices.isEmpty()) {
            return "DeepSeek 返回格式异常：缺少 choices。";
        }

        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map<?, ?> choiceMap)) {
            return "DeepSeek 返回格式异常：choice 不是对象。";
        }

        Object messageObject = choiceMap.get("message");
        if (!(messageObject instanceof Map<?, ?> messageMap)) {
            return "DeepSeek 返回格式异常：缺少 message。";
        }

        Object content = messageMap.get("content");
        return content == null ? "DeepSeek 返回内容为空。" : content.toString();
    }
}