package com.example.aitaskmanager.controller;

import com.example.aitaskmanager.ai.DeepSeekService;
import com.example.aitaskmanager.model.Task;
import com.example.aitaskmanager.model.TaskCategory;
import com.example.aitaskmanager.service.CalendarService;
import com.example.aitaskmanager.service.ScheduleService;
import com.example.aitaskmanager.service.TaskClassificationService;
import com.example.aitaskmanager.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * 任务页面控制器（实训 Day 3）。
 * <p>
 * 用途：处理任务输入页、任务提交、分类过滤。
 * 当前阶段任务保存在内存中；后续接入 MySQL 后，Controller 的页面逻辑可以基本不变。
 * <p>
 * 验证：启动项目后访问 <a href="http://localhost:8080/tasks">http://localhost:8080/tasks</a>。
 */
@Controller
public class TaskController {

    private final TaskService taskService;
    private final DeepSeekService deepSeekService;
    private final ScheduleService scheduleService;
    private final TaskClassificationService taskClassificationService;
    private final CalendarService calendarService;

    /**
     * 通过构造方法注入任务服务。
     *
     * @param taskService     任务保存与查询服务
     * @param deepSeekService DeepSeek AI 调用服务
     * @param scheduleService 正式日程保存服务
     * @param taskClassificationService 本地关键词分类服务
     * @param calendarService 小日历数据服务
     */
    public TaskController(
            TaskService taskService,
            DeepSeekService deepSeekService,
            ScheduleService scheduleService,
            TaskClassificationService taskClassificationService,
            CalendarService calendarService) {
        this.taskService = taskService;
        this.deepSeekService = deepSeekService;
        this.scheduleService = scheduleService;
        this.taskClassificationService = taskClassificationService;
        this.calendarService = calendarService;
    }

    /**
     * 显示任务页面，并可按分类过滤任务。
     *
     * @param category     当前选中的分类；为空表示「全部」
     * @param selectedDate 当前选中的日历日期；为空时不按日期筛选
     * @param calendarView 日历视图状态；值为 {@code expanded} 时展示整页日历
     * @param model        Thymeleaf 页面数据容器
     * @return 模板名 {@code tasks}
     */
    @GetMapping("/tasks")
    public String taskPage(
            @RequestParam(required = false) TaskCategory category,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            Model model) {
        model.addAttribute("tasks", taskService.findByCategoryAndDate(category, selectedDate));
        model.addAttribute("categories", TaskCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedMonth", resolveCalendarMonth(selectedMonth).toString());
        model.addAttribute("calendarExpanded", "expanded".equals(calendarView));
        model.addAttribute("confirmedSchedule", scheduleService.getConfirmedSchedule());
        model.addAttribute("defaultTaskDate", LocalDate.now());
        addCalendarAttributes(model, selectedMonth);
        return "tasks";
    }

    /**
     * 接收任务表单提交，保存到内存后重定向回任务页面。
     *
     * @param title              任务标题，来自表单 input[name=title]
     * @param content            任务描述，来自表单 textarea[name=content]
     * @param category           任务分类，来自表单 select[name=category]
     * @param taskDate           任务所属日期，来自表单 input[name=taskDate]
     * @param redirectAttributes 重定向时传递一次性提示消息
     * @return 重定向到任务列表页，防止刷新页面重复提交表单
     */
    @PostMapping("/tasks")
    public String createTask(
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "DAILY") TaskCategory category,
            @RequestParam(required = false) LocalDate taskDate,
            @RequestParam(required = false) TaskCategory selectedCategory,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            RedirectAttributes redirectAttributes) {
        taskService.createTask(title, content, category, taskDate);
        redirectAttributes.addFlashAttribute("message", "任务已保存");
        redirectAttributes.addAttribute("category", selectedCategory);
        redirectAttributes.addAttribute("selectedDate", selectedDate);
        String monthAfterSave = selectedMonth;
        if ((monthAfterSave == null || monthAfterSave.isBlank()) && taskDate != null) {
            monthAfterSave = YearMonth.from(taskDate).toString();
        }
        redirectAttributes.addAttribute("selectedMonth", monthAfterSave);
        redirectAttributes.addAttribute("calendarView", calendarView);
        return "redirect:/tasks";
    }

    /**
     * 切换任务完成状态。
     * <p>
     * 当前阶段使用内存保存，点击后会重定向回任务页，并尽量保留当前分类、日期筛选和日历展开状态。
     *
     * @param id                 任务编号
     * @param category           当前分类筛选条件
     * @param selectedDate       当前日期筛选条件
     * @param calendarView       当前日历视图状态
     * @param redirectAttributes 重定向时传递一次性提示消息
     * @return 重定向回任务页面
     */
    @PostMapping("/tasks/{id}/toggle-completed")
    public String toggleCompleted(
            @PathVariable Long id,
            @RequestParam(required = false) TaskCategory category,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            RedirectAttributes redirectAttributes) {
        Task task = taskService.toggleCompleted(id).orElse(null);
        if (task == null) {
            redirectAttributes.addFlashAttribute("message", "没有找到要更新的任务，请刷新页面后重试。");
        } else {
            redirectAttributes.addFlashAttribute("message", task.isCompleted() ? "任务已标记完成" : "任务已取消完成");
        }

        redirectAttributes.addAttribute("category", category);
        redirectAttributes.addAttribute("selectedDate", selectedDate);
        redirectAttributes.addAttribute("selectedMonth", selectedMonth);
        redirectAttributes.addAttribute("calendarView", calendarView);
        return "redirect:/tasks";
    }

    /**
     * 根据当前输入内容进行 AI 自动分类，并把推荐分类预填到表单。
     * <p>
     * 该方法不会保存任务，只帮助用户选择分类；用户仍需点击「提交任务」完成保存。
     *
     * @param title   当前表单中的任务标题
     * @param content 当前表单中的任务描述
     * @param taskDate 当前表单中的任务日期
     * @param model   Thymeleaf 页面数据容器
     * @return 模板名 {@code tasks}
     */
    @PostMapping("/tasks/classify")
    public String classifyTask(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) LocalDate taskDate,
            @RequestParam(required = false) TaskCategory selectedCategory,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            Model model) {
        TaskCategory recommendedCategory = taskClassificationService.classifyByKeywords(title, content)
                .orElseGet(() -> deepSeekService.classifyTask(title, content));

        model.addAttribute("tasks", taskService.findByCategoryAndDate(selectedCategory, selectedDate));
        model.addAttribute("categories", TaskCategory.values());
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedMonth", resolveCalendarMonth(selectedMonth).toString());
        model.addAttribute("calendarExpanded", "expanded".equals(calendarView));
        model.addAttribute("confirmedSchedule", scheduleService.getConfirmedSchedule());
        model.addAttribute("draftTitle", title);
        model.addAttribute("draftContent", content);
        model.addAttribute("draftTaskDate", taskDate == null ? LocalDate.now() : taskDate);
        model.addAttribute("defaultTaskDate", LocalDate.now());
        model.addAttribute("recommendedCategory", recommendedCategory);
        model.addAttribute("message", "AI 推荐分类：" + recommendedCategory.getDisplayName() + "。确认无误后请点击「提交任务」。");
        addCalendarAttributes(model, selectedMonth);
        return "tasks";
    }

    /**
     * 对选中的单个任务进行 AI 分析，并把结果展示回任务页面。
     * <p>
     * 当前用于替换页面中的「AI 分析做法与建议（待接入）」占位按钮。
     *
     * @param id       被分析的任务编号
     * @param category     当前分类过滤条件；用于分析后仍停留在同一分类页
     * @param selectedDate 当前日期过滤条件；用于分析后仍停留在同一天
     * @param calendarView 日历视图状态；值为 {@code expanded} 时展示整页日历
     * @param model        Thymeleaf 页面数据容器
     * @return 模板名 {@code tasks}
     */
    @GetMapping("/tasks/{id}/analyze")
    public String analyzeTask(
            @PathVariable Long id,
            @RequestParam(required = false) TaskCategory category,
            @RequestParam(required = false) LocalDate selectedDate,
            @RequestParam(required = false) String selectedMonth,
            @RequestParam(required = false) String calendarView,
            Model model) {
        Task task = taskService.findById(id).orElse(null);
        model.addAttribute("tasks", taskService.findByCategoryAndDate(category, selectedDate));
        model.addAttribute("categories", TaskCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("selectedMonth", resolveCalendarMonth(selectedMonth).toString());
        model.addAttribute("calendarExpanded", "expanded".equals(calendarView));
        model.addAttribute("confirmedSchedule", scheduleService.getConfirmedSchedule());
        model.addAttribute("defaultTaskDate", LocalDate.now());
        addCalendarAttributes(model, selectedMonth);

        if (task == null) {
            model.addAttribute("aiAnalysis", "没有找到编号为 " + id + " 的任务，请刷新页面后重试。");
            return "tasks";
        }

        model.addAttribute("selectedTask", task);
        model.addAttribute("aiAnalysis", deepSeekService.analyzeTask(task));
        return "tasks";
    }

    /**
     * 为任务页面补充左侧小日历数据。
     * <p>
     * 多个页面入口都会返回 {@code tasks.html}，因此统一在这里填充，避免遗漏。
     *
     * @param model Thymeleaf 页面数据容器
     */
    private void addCalendarAttributes(Model model, String selectedMonth) {
        YearMonth calendarMonth = resolveCalendarMonth(selectedMonth);
        model.addAttribute("selectedMonth", calendarMonth.toString());
        model.addAttribute("calendarMonthTitle", calendarService.getMonthTitle(calendarMonth));
        model.addAttribute("calendarDays", calendarService.buildMonthCalendar(taskService.findAll(), calendarMonth));
    }

    /**
     * 解析页面传回的年月参数（yyyy-MM）。
     * <p>
     * 若参数为空或格式非法，回退为当前年月，避免用户手动改 URL 导致页面报错。
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