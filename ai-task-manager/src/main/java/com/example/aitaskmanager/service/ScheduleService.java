package com.example.aitaskmanager.service;

import org.springframework.stereotype.Service;

/**
 * 日程服务类（实训 Day 4/5）。
 * <p>
 * 用途：保存用户确认后的正式日程。
 * AI 生成的内容先只是「预览草稿」，只有用户点击确认后，才会调用本服务保存。
 * <p>
 * 当前阶段使用内存保存，应用重启后会清空；后续接入 MySQL 时可替换为数据库存储。
 */
@Service
public class ScheduleService {

    /** 用户确认后的正式日程文本；为空表示还没有保存日程。 */
    private String confirmedSchedule = "";

    /**
     * 保存用户确认后的正式日程。
     *
     * @param scheduleContent 用户在预览区编辑后的最终日程内容
     */
    public synchronized void saveConfirmedSchedule(String scheduleContent) {
        this.confirmedSchedule = scheduleContent == null ? "" : scheduleContent.trim();
    }

    /**
     * 读取当前正式日程。
     *
     * @return 正式日程文本；如果尚未确认保存，则返回空字符串
     */
    public synchronized String getConfirmedSchedule() {
        return confirmedSchedule;
    }

    /**
     * 判断是否已有正式日程。
     *
     * @return 已保存正式日程时返回 true
     */
    public synchronized boolean hasConfirmedSchedule() {
        return confirmedSchedule != null && !confirmedSchedule.isBlank();
    }
}