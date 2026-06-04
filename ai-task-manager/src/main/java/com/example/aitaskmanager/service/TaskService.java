package com.example.aitaskmanager.service;

import com.example.aitaskmanager.model.Task;
import com.example.aitaskmanager.model.TaskCategory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务服务类（实训 Day 3）。
 * <p>
 * 用途：在接入 MySQL 之前，先用内存 List 保存用户提交的任务。
 * Controller 只负责接收请求，真正的任务编号、保存、查询逻辑放在这里。
 * <p>
 * 注意：当前数据会在应用重启后清空；Day 后期接入 MySQL 时会替换为数据库持久化。
 */
@Service
public class TaskService {

    /** 内存任务列表：临时充当数据库。 */
    private final List<Task> tasks = new ArrayList<>();

    /** 自增编号生成器：保证每条任务都有唯一 id。 */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 创建并保存一条任务。
     *
     * @param title    任务标题，不能为空
     * @param content  任务描述，可为空
     * @param category 任务分类；为空时默认日常
     * @param taskDate 任务所属日期；为空时默认今天
     * @return 保存后的任务对象，包含 id 和 createdAt
     */
    public synchronized Task createTask(String title, String content, TaskCategory category, LocalDate taskDate) {
        Task task = new Task(
                idGenerator.getAndIncrement(),
                title == null ? "" : title.trim(),
                content == null ? "" : content.trim(),
                category,
                taskDate,
                false,
                LocalDateTime.now()
        );
        tasks.add(task);
        return task;
    }

    /**
     * 查询全部任务。
     *
     * @return 任务列表副本，避免外部直接修改内存数据
     */
    public synchronized List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    /**
     * 按分类查询任务。
     *
     * @param category 选中的分类；为空时返回全部任务
     * @return 符合分类的任务列表
     */
    public synchronized List<Task> findByCategory(TaskCategory category) {
        if (category == null) {
            return findAll();
        }
        return tasks.stream()
                .filter(task -> category.equals(task.getCategory()))
                .toList();
    }

    /**
     * 按任务日期查询任务。
     * <p>
     * 用途：用户点击左侧小日历某一天后，主区域只显示这一天的任务。
     *
     * @param taskDate 选中的任务日期；为空时返回全部任务
     * @return 属于该日期的任务列表
     */
    public synchronized List<Task> findByDate(LocalDate taskDate) {
        if (taskDate == null) {
            return findAll();
        }
        return tasks.stream()
                .filter(task -> taskDate.equals(task.getTaskDate()))
                .toList();
    }

    /**
     * 按分类和日期组合查询任务。
     * <p>
     * 用途：用户同时使用左侧分类和小日历日期时，主区域只显示符合当前条件的任务。
     *
     * @param category 选中的分类；为空时不过滤分类
     * @param taskDate 选中的任务日期；为空时不过滤日期
     * @return 符合分类和日期条件的任务列表
     */
    public synchronized List<Task> findByCategoryAndDate(TaskCategory category, LocalDate taskDate) {
        return tasks.stream()
                .filter(task -> category == null || category.equals(task.getCategory()))
                .filter(task -> taskDate == null || taskDate.equals(task.getTaskDate()))
                .toList();
    }

    /**
     * 根据任务编号查找单条任务。
     * <p>
     * 用途：点击「AI 分析做法与建议」时，需要先找到用户选中的具体任务。
     *
     * @param id 任务编号
     * @return 找到时返回任务；找不到时返回空 Optional
     */
    public synchronized Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    /**
     * 切换任务完成状态。
     * <p>
     * 用途：用户点击任务卡片上的「标记完成/取消完成」按钮后，更新内存中的任务状态。
     *
     * @param id 任务编号
     * @return 找到并切换后的任务；找不到时返回空 Optional
     */
    public synchronized Optional<Task> toggleCompleted(Long id) {
        Optional<Task> optionalTask = findById(id);
        optionalTask.ifPresent(task -> task.setCompleted(!task.isCompleted()));
        return optionalTask;
    }
}