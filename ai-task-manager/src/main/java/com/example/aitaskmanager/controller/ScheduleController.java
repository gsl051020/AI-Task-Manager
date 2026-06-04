package com.example.aitaskmanager.controller;

import com.example.aitaskmanager.ai.DeepSeekService;
import com.example.aitaskmanager.model.TaskCategory;
import com.example.aitaskmanager.service.CalendarService;
import com.example.aitaskmanager.service.ScheduleService;
import com.example.aitaskmanager.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * 日程控制器（实训 Day 4/5）。
 * <p>
 * 用途：处理「AI 日程优化」相关操作。
 * 设计原则：AI 生成的内容先进入预览区，用户可以修改；只有用户点击确认，才保存到正式日程。
 */
@Controller
public class ScheduleController {

    private final TaskService taskService;
    private final ScheduleService scheduleService;
    private final DeepSeekService deepSeekService;
    private final CalendarService calendarService;

    /**
     * 构造日程控制器。
     *
     * @param taskService     任务查询服务，用于获取所有任务
     * @param scheduleService 日程保存服务，用于保存用户确认后的日程
     * @param deepSeekService AI 服务，用于生成日程优化预览
     * @param calendarService 小日历数据服务
     */
    public ScheduleController(
            TaskService taskService,
            ScheduleService scheduleService,
            DeepSeekService deepSeekService,
            CalendarService calendarService) {
        this.taskService = taskService;
        this.scheduleService = scheduleService;
        this.deepSeekService = deepSeekService;
        this.calendarService = calendarService;
    }

    /**
     * 生成 AI 日程优化预览。
     * <p>
     * 该方法不会保存正式日程，只把 AI 生成结果放到页面预览区。
     *
     * @param days  希望优化的天数，最少 1 天
     * @param model Thymeleaf 页面数据容器
     * @return 回到任务页面并展示可编辑预览
     */
    @PostMapping("/schedule/preview")
    public String previewSchedule(
            @RequestParam(defaultValue = "1") int days,
            @RequestParam(required = false) TaskCategory selectedCategory,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            Model model) {
        int safeDays = Math.max(days, 1);
        YearMonth calendarMonth = resolveCalendarMonth(selectedMonth);
        model.addAttribute("tasks", taskService.findByCategoryAndDate(selectedCategory, selectedDate));
        model.addAttribute("categories", TaskCategory.values());
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedMonth", calendarMonth.toString());
        model.addAttribute("calendarExpanded", "expanded".equals(calendarView));
        model.addAttribute("schedulePreview", deepSeekService.optimizeSchedule(taskService.findAll(), safeDays));
        model.addAttribute("scheduleDays", safeDays);
        model.addAttribute("confirmedSchedule", scheduleService.getConfirmedSchedule());
        model.addAttribute("defaultTaskDate", LocalDate.now());
        model.addAttribute("calendarMonthTitle", calendarService.getMonthTitle(calendarMonth));
        model.addAttribute("calendarDays", calendarService.buildMonthCalendar(taskService.findAll(), calendarMonth));
        return "tasks";
    }

    /**
     * 保存用户确认后的正式日程。
     * <p>
     * 用户可以在页面预览区修改 AI 草稿，本方法保存的是修改后的最终内容。
     *
     * @param scheduleContent     用户确认保存的日程内容
     * @param redirectAttributes  重定向时展示一次性提示消息
     * @return 重定向回任务页面，避免刷新导致重复提交
     */
    @PostMapping("/schedule/confirm")
    public String confirmSchedule(
            @RequestParam String scheduleContent,
            @RequestParam(required = false) TaskCategory selectedCategory,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            RedirectAttributes redirectAttributes) {
        scheduleService.saveConfirmedSchedule(scheduleContent);
        redirectAttributes.addFlashAttribute("message", "日程已保存");
        redirectAttributes.addAttribute("category", selectedCategory);
        redirectAttributes.addAttribute("selectedDate", selectedDate);
        redirectAttributes.addAttribute("selectedMonth", selectedMonth);
        redirectAttributes.addAttribute("calendarView", calendarView);
        return "redirect:/tasks";
    }

    /**
     * 解析页面传回的年月参数（yyyy-MM），异常时回退当前年月。
     */
    private YearMonth resolveCalendarMonth(String selectedMonth) {
        if (selectedMonth == null || selectedMonth.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(selectedMonth);
        } catch (DateTimeParseException ignored) {
            return YearMonth.now();
        }
    }
}