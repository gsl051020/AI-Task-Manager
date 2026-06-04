package com.example.aitaskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Hello 接口控制器（实训 Day 2）。
 * <p>
 * 用途：提供第一个 REST API，返回 JSON，用于验证 Spring Web 是否正常工作。
 * 与 {@link HomeController} 的区别：本类使用 {@code @RestController}，直接返回数据而非 HTML 页面。
 * <p>
 * 验证：访问 <a href="http://localhost:8080/api/hello">http://localhost:8080/api/hello</a>，
 * 应看到 {@code {"message":"Hello, AI任务管理平台!"} }。
 */
@RestController
public class HelloController {

	/**
	 * Day 2 验收用接口：返回固定问候 JSON。
	 *
	 * @return 包含 {@code message} 字段的 Map，Spring 自动序列化为 JSON
	 */
	@GetMapping("/api/hello")
	public Map<String, String> hello() {
		return Map.of("message", "Hello, AI任务管理平台!");
	}
}
