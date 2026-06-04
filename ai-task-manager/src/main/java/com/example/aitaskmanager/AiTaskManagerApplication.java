package com.example.aitaskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI 任务管理平台 — 应用启动入口（实训 Day 2）。
 * <p>
 * 作用：启动内嵌 Tomcat，扫描本包及子包下的 Controller、配置等组件。
 * 运行：在项目根目录执行 {@code mvn spring-boot:run}，默认端口 8080。
 */
@SpringBootApplication
public class AiTaskManagerApplication {

	/**
	 * 程序主入口，由 JVM 或 IDE 直接运行此类即可启动 Web 服务。
	 *
	 * @param args 命令行参数（本实训暂未使用，可留空）
	 */
	public static void main(String[] args) {
		SpringApplication.run(AiTaskManagerApplication.class, args);
	}

}
