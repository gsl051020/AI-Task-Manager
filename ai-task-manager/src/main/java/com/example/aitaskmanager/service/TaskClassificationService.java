package com.example.aitaskmanager.service;

import com.example.aitaskmanager.model.TaskCategory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * 任务分类规则服务（实训 Day 4）。
 * <p>
 * 用途：先用本地关键词做稳定分类，再把不确定的任务交给 DeepSeek。
 * 这样即使 API Key 没有配置，常见任务也不会全部兜底成「日常」。
 */
@Service
public class TaskClassificationService {

    /**
     * 根据标题和描述进行本地关键词分类。
     * <p>
     * 规则设计：如果明确出现「学习、复习、作业」等学习意图，优先归为学习；
     * 否则再根据 C++、Java、代码等技术关键词归为编程。
     *
     * @param title   任务标题
     * @param content 任务描述
     * @return 能识别时返回分类；无法识别时返回 Optional.empty()
     */
    public Optional<TaskCategory> classifyByKeywords(String title, String content) {
        String text = ((title == null ? "" : title) + " " + (content == null ? "" : content)).toLowerCase(Locale.ROOT);

        if (containsAny(text, "学习", "复习", "预习", "作业", "考试", "课程", "背单词", "读书", "笔记", "知识点")) {
            return Optional.of(TaskCategory.STUDY);
        }
        if (containsAny(text, "c++", "cpp", "java", "python", "spring", "springboot", "代码", "编程", "程序", "开发", "bug", "debug", "算法")) {
            return Optional.of(TaskCategory.PROGRAMMING);
        }
        if (containsAny(text, "工作", "会议", "汇报", "报告", "客户", "项目进度", "实习", "面试", "简历")) {
            return Optional.of(TaskCategory.WORK);
        }
        if (containsAny(text, "运动", "跑步", "健身", "篮球", "足球", "游泳", "瑜伽", "锻炼", "训练")) {
            return Optional.of(TaskCategory.EXERCISE);
        }
        if (containsAny(text, "买", "购物", "做饭", "洗衣", "打扫", "整理房间", "缴费", "取快递")) {
            return Optional.of(TaskCategory.DAILY);
        }

        return Optional.empty();
    }

    /**
     * 判断文本是否包含任一关键词。
     *
     * @param text     被检查的任务文本
     * @param keywords 关键词列表
     * @return 包含任意关键词时返回 true
     */
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}