package org.example.todolistservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 待办清单应用主类
 * 支持跨终端（Web/iOS/Android）的轻量型个人待办清单工具
 */
@SpringBootApplication
@EnableScheduling
public class TodoListServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoListServiceApplication.class, args);
    }
}
