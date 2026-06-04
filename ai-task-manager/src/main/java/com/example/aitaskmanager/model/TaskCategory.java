package com.example.aitaskmanager.model;

/**
 * 任务分类枚举（实训 Day 3）。
 * <p>
 * 用途：限制任务只能属于固定的 5 个分类，避免页面和后端出现随意字符串。
 * 前端表单提交时使用枚举常量名（如 {@code DAILY}），页面展示时使用中文名称。
 */
public enum TaskCategory {

    /** 日常任务：默认分类，例如生活琐事、临时安排。 */
    DAILY("日常"),

    /** 学习任务：例如课程复习、作业、考试准备。 */
    STUDY("学习"),

    /** 工作任务：例如会议、报告、实习相关事项。 */
    WORK("工作"),

    /** 编程任务：例如写代码、调试、学习框架。 */
    PROGRAMMING("编程"),

    /** 运动任务：例如跑步、健身、球类活动。 */
    EXERCISE("运动");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取页面展示用的中文分类名称。
     *
     * @return 中文分类名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 将 AI 返回的文本解析为任务分类。
     * <p>
     * DeepSeek 可能返回中文（如「学习」）或枚举名（如 {@code STUDY}），
     * 本方法统一转换成系统内部使用的 {@link TaskCategory}。
     *
     * @param aiText AI 返回的分类文本
     * @return 解析成功的分类；无法识别时默认返回「日常」
     */
    public static TaskCategory fromAiText(String aiText) {
        if (aiText == null || aiText.isBlank()) {
            return DAILY;
        }

        String normalizedText = aiText.trim().toUpperCase();
        for (TaskCategory category : values()) {
            if (normalizedText.contains(category.name())
                    || aiText.contains(category.getDisplayName())) {
                return category;
            }
        }
        return DAILY;
    }
}
