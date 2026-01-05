package com.school.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用程序主启动类
 *
 * <p>该类是应用程序的入口点，使用 @SpringBootApplication 注解标记，
 * 该注解包含了 @Configuration、@EnableAutoConfiguration 和 @ComponentScan 的功能。</p>
 *
 * <p>应用启动时会自动执行以下操作：
 * <ul>
 *   <li>扫描并注册所有 Spring Bean</li>
 *   <li>加载 application.properties 配置</li>
 *   <li>初始化数据库连接</li>
 *   <li>执行 DataInitializer 进行数据初始化</li>
 * </ul>
 * </p>
 *
 * @author School Homework Team
 * @version 1.0
 */
@SpringBootApplication
public class SchoolHomeworkApplication {

    /**
     * 应用程序主入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SchoolHomeworkApplication.class, args);
    }

}

