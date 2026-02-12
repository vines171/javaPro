package com.example;

import com.example.config.AppConfig;
import com.example.config.ComponentConfig;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class, ComponentConfig.class);

        UserService userService = context.getBean(UserService.class);

        try {
            System.out.println("=== CRUD ОПЕРАЦИИ ===");
            System.out.println("\n1. Создание пользователей:");
            User user1 = userService.createUser("Алиса");
            System.out.println("Создан: " + user1);

            User user2 = userService.createUser("Алёна");
            System.out.println("Создан: " + user2);

            User user3 = userService.createUser("Глеб");
            System.out.println("Создан: " + user3);

            System.out.println("\n2. Все пользователи:");
            List<User> allUsers = userService.getAllUsers();
            allUsers.forEach(System.out::println);

            System.out.println("\n3. Поиск по ID:");
            userService.getUserById(user2.getId())
                    .ifPresent(user -> System.out.println("Найден: " + user));

            System.out.println("\n4. Поиск по имени:");
            userService.getUserByUsername("Глеб")
                    .ifPresent(user -> System.out.println("Найден: " + user));

            System.out.println("\n5. Обновление пользователя:");
            User updatedUser = userService.updateUser(user1.getId(), "Алёна_updated");
            System.out.println("Обновлен: " + updatedUser);

            // 6. Попытка создания пользователя с существующим именем
            System.out.println("\n6. Попытка создания дубликата:");
            try {
                userService.createUser("Алиса");
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }

            System.out.println("\n7. Все пользователи после обновления:");
            userService.getAllUsers().forEach(System.out::println);

            System.out.println("\n8. Удаление пользователя:");
            boolean deleted = userService.deleteUser(user3.getId());
            System.out.println("Пользователь удален: " + deleted);

            System.out.println("\n9. Финальный список пользователей:");
            userService.getAllUsers().forEach(System.out::println);

        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}