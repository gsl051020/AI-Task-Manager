package com.example.aitaskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器（实训 Day 2）。
 * <p>
 * 用途：处理浏览器访问网站根路径时的请求，返回 Thymeleaf 渲染的 HTML 页面（非 JSON）。
 * 任务输入、提交与分类过滤从 Day 3 起交给 {@link TaskController} 处理。
 * <p>
 * 验证：启动项目后访问 <a href="http://localhost:8080/">http://localhost:8080/</a>，
 * 应看到「AI任务管理平台」标题。
 */
@Controller
public class HomeController {

    /**
     * 显示网站首页。
     *
     * @return 模板名 {@code index}，对应文件 {@code templates/index.html}
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}