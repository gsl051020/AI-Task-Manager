package com.example.aitaskmanager.service;

import com.example.aitaskmanager.model.CalendarDay;
import com.example.aitaskmanager.model.Task;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小日历服务（实训 Day 5）。
 * <p>
 * 用途：根据当前月份和任务日期生成左侧小日历数据。
 * 页面只负责展示，日期计算逻辑放在服务层，便于后续扩展动画、完成状态、按日期过滤。
 */
@Service
public class CalendarService {

    /**
     * 生成指定月份的小日历格子。
     * <p>
     * 返回结果会按周补齐，例如月初不是周一时，会补上上个月的几天，方便页面按 7 列展示。
     *
     * @param tasks 当前所有任务，用于判断哪些日期有任务
     * @param month 目标年月；为空时自动使用当前年月
     * @return 日历格子列表，按日期顺序排列
     */
    public List<CalendarDay> buildMonthCalendar(List<Task> tasks, YearMonth month) {
        YearMonth targetMonth = month == null ? YearMonth.now() : month;
        LocalDate firstDay = targetMonth.atDay(1);
        LocalDate lastDay = targetMonth.atEndOfMonth();

        Map<LocalDate, List<String>> taskTitlesByDate = tasks.stream()
                .filter(task -> task.getTaskDate() != null)
                .collect(Collectors.groupingBy(
                        Task::getTaskDate,
                        Collectors.mapping(Task::getTitle, Collectors.toList())
                ));
        Map<LocalDate, Boolean> completedByDate = tasks.stream()
                .filter(task -> task.getTaskDate() != null)
                .collect(Collectors.groupingBy(
                        Task::getTaskDate,
                        Collectors.collectingAndThen(Collectors.toList(), dailyTasks ->
                                !dailyTasks.isEmpty() && dailyTasks.stream().allMatch(Task::isCompleted))
                ));

        LocalDate start = firstDay.minusDays(daysFromMonday(firstDay.getDayOfWeek()));
        LocalDate end = lastDay.plusDays(6L - daysFromMonday(lastDay.getDayOfWeek()));

        return start.datesUntil(end.plusDays(1))
                .map(date -> new CalendarDay(
                        date,
                        YearMonth.from(date).equals(targetMonth),
                        taskTitlesByDate.containsKey(date),
                        completedByDate.getOrDefault(date, false),
                        summarizeTitles(taskTitlesByDate.getOrDefault(date, List.of()))
                ))
                .toList();
    }

    /**
     * 兼容旧调用：生成当前月份的小日历格子。
     *
     * @param tasks 当前所有任务
     * @return 当前月份日历格子
     */
    public List<CalendarDay> buildCurrentMonthCalendar(List<Task> tasks) {
        return buildMonthCalendar(tasks, YearMonth.now());
    }

    /**
     * 获取指定月份的小日历标题，例如 2026-06。
     *
     * @param month 目标年月；为空时自动使用当前年月
     * @return 年月文本
     */
    public String getMonthTitle(YearMonth month) {
        return (month == null ? YearMonth.now() : month).toString();
    }

    /**
     * 兼容旧调用：获取当前小日历标题，例如 2026-06。
     *
     * @return 当前年月文本
     */
    public String getCurrentMonthTitle() {
        return getMonthTitle(YearMonth.now());
    }

    /**
     * 将星期转换成从周一开始的偏移量：周一为 0，周日为 6。
     *
     * @param dayOfWeek Java 的星期枚举
     * @return 从周一开始的偏移量
     */
    private int daysFromMonday(DayOfWeek dayOfWeek) {
        return dayOfWeek.getValue() - 1;
    }

    /**
     * 为展开日历生成简短任务摘要。
     * <p>
     * 每天最多展示 3 条任务标题，避免展开日历过长；更多任务用省略提示表示。
     *
     * @param titles 当天全部任务标题
     * @return 用于页面展示的任务标题摘要
     */
    private List<String> summarizeTitles(List<String> titles) {
        if (titles.size() <= 3) {
            return titles;
        }
        return List.of(titles.get(0), titles.get(1), titles.get(2), "还有 " + (titles.size() - 3) + " 条任务...");
    }
}