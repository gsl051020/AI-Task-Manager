package com.example.aitaskmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务数据模型（实训 Day 3）。
 * <p>
 * 用途：表示用户输入的一条任务，是后续「提交保存、分类过滤、AI 分析、日程优化」的基础数据结构。
 * 当前阶段先保存在内存中；后期接入 MySQL 时，本类可继续作为页面和业务层的数据对象。
 */
public class Task {

    /** 任务唯一编号；内存阶段由服务类自增生成。 */
    private Long id;

    /** 任务标题，表单必填，例如「完成数据库设计」。 */
    private String title;

    /** 任务描述，记录更详细的背景、要求或备注。 */
    private String content;

    /** 任务分类，默认应为 {@link TaskCategory#DAILY}。 */
    private TaskCategory category;

    /** 任务所属日期，默认是今天；页面使用 date 输入框选择。 */
    private LocalDate taskDate;

    /** 任务是否已完成；新建任务默认未完成。 */
    private boolean completed;

    /** 任务创建时间，用于后续展示和日程优化。 */
    private LocalDateTime createdAt;

    /**
     * 无参构造方法。
     * <p>
     * Spring MVC 处理表单提交时，需要先创建对象再填充字段，因此保留无参构造。
     */
    public Task() {
        this.category = TaskCategory.DAILY;
        this.taskDate = LocalDate.now();
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 创建一条完整任务。
     *
     * @param id        任务编号
     * @param title     任务标题
     * @param content   任务描述
     * @param category  任务分类；如果为 null，则默认使用「日常」
     * @param taskDate  任务所属日期；如果为 null，则默认使用今天
     * @param completed 是否完成；新任务通常为 false
     * @param createdAt 创建时间；如果为 null，则使用当前时间
     */
    public Task(Long id, String title, String content, TaskCategory category, LocalDate taskDate, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category == null ? TaskCategory.DAILY : category;
        this.taskDate = taskDate == null ? LocalDate.now() : taskDate;
        this.completed = completed;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    /**
     * 创建一条用户新提交的任务，编号由后续服务层补充。
     *
     * @param title    任务标题
     * @param content  任务描述
     * @param category 任务分类；如果为 null，则默认使用「日常」
     * @param taskDate 任务所属日期；如果为 null，则默认使用今天
     */
    public Task(String title, String content, TaskCategory category, LocalDate taskDate) {
        this(null, title, content, category, taskDate, false, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category == null ? TaskCategory.DAILY : category;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate == null ? LocalDate.now() : taskDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}