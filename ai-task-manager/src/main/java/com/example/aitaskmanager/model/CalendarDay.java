package com.example.aitaskmanager.model;

import java.time.LocalDate;
import java.util.List;

/**
 * 小日历中的单个日期格子（实训 Day 5）。
 * <p>
 * 用途：给左侧小日历提供渲染数据，例如日期数字、是否属于当前月、当天是否有任务和任务摘要。
 * 后续增加任务完成状态后，可用 {@code completed} 显示小勾。
 */
public class CalendarDay {

    /** 日期格子对应的真实日期。 */
    private final LocalDate date;

    /** 是否属于当前显示月份；月初/月末补齐的日期会是 false。 */
    private final boolean currentMonth;

    /** 该日期下是否存在任务；存在时页面显示小点。 */
    private final boolean hasTask;

    /** 该日期下任务是否全部完成；当前先预留，后续完成状态功能再启用。 */
    private final boolean completed;

    /** 展开日历时展示的任务标题摘要，避免在日历格子里塞入完整任务内容。 */
    private final List<String> taskSummaries;

    public CalendarDay(LocalDate date, boolean currentMonth, boolean hasTask, boolean completed, List<String> taskSummaries) {
        this.date = date;
        this.currentMonth = currentMonth;
        this.hasTask = hasTask;
        this.completed = completed;
        this.taskSummaries = taskSummaries == null ? List.of() : List.copyOf(taskSummaries);
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public boolean isHasTask() {
        return hasTask;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<String> getTaskSummaries() {
        return taskSummaries;
    }
}