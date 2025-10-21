package com.example.runner;

import com.example.model.User;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DatabaseRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseRunner.class);

    private final UserService userService;

    public DatabaseRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== STARTING DATABASE OPERATIONS ===");

        try {
            logger.info("\n0. ПРОВЕРКА НАЛИЧИЯ ДАННЫХ:");
            long initialCount = userService.getUsersCount();
            logger.info("   Найдено пользователей в базе: {}", initialCount);

            logger.info("\n1. ВСЕ ПОЛЬЗОВАТЕЛИ:");
            List<User> allUsers = userService.getAllUsers();
            if (allUsers.isEmpty()) {
                logger.info("   Нет пользователей в базе данных");
                createTestData();
                allUsers = userService.getAllUsers();
            }
            allUsers.forEach(user -> logger.info("   {}", user));

            logger.info("\n2. СОЗДАНИЕ НОВЫХ ПОЛЬЗОВАТЕЛЕЙ:");
            User newUser1 = userService.createUser("kate");
            logger.info("   Создан: {}", newUser1);

            User newUser2 = userService.createUser("mike");
            logger.info("   Создан: {}", newUser2);

            logger.info("\n3. ПОПЫТКА СОЗДАТЬ ДУБЛИКАТ:");
            try {
                userService.createUser("alice");
            } catch (IllegalArgumentException e) {
                logger.info("   Ошибка (ожидаемо): {}", e.getMessage());
            }

            logger.info("\n4. ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID:");
            Optional<User> userById = userService.getUserById(1L);
            if (userById.isPresent()) {
                logger.info("   Найден: {}", userById.get());
            } else {
                logger.info("   Пользователь с ID=1 не найден");
            }

            logger.info("\n5. ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ИМЕНИ:");
            Optional<User> userByName = userService.getUserByUsername("bob");
            if (userByName.isPresent()) {
                logger.info("   Найден: {}", userByName.get());
            } else {
                logger.info("   Пользователь 'bob' не найден");
            }

            logger.info("\n6. ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ:");
            try {
                User updatedUser = userService.updateUser(newUser1.getId(), "katherine");
                logger.info("   Обновлен: {}", updatedUser);
            } catch (IllegalArgumentException e) {
                logger.info("   Ошибка при обновлении: {}", e.getMessage());
            }

            logger.info("\n7. ПОИСК ПОЛЬЗОВАТЕЛЕЙ ПО ЧАСТИ ИМЕНИ ('a'):");
            List<User> usersWithA = userService.searchUsersByUsername("a");
            if (usersWithA.isEmpty()) {
                logger.info("   Пользователи с 'a' в имени не найдены");
            } else {
                usersWithA.forEach(user -> logger.info("   Найден: {}", user));
            }

            logger.info("\n8. СТАТИСТИКА:");
            long userCount = userService.getUsersCount();
            logger.info("   Всего пользователей: {}", userCount);

            logger.info("\n9. УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ:");
            boolean deleted = userService.deleteUser(newUser2.getId());
            logger.info("   Пользователь удален: {}", deleted);

            logger.info("\n10. ФИНАЛЬНЫЙ СПИСОК ПОЛЬЗОВАТЕЛЕЙ:");
            List<User> finalUsers = userService.getAllUsers();
            finalUsers.forEach(user -> logger.info("   {}", user));

            logger.info("\n=== ALL OPERATIONS COMPLETED SUCCESSFULLY ===");

        } catch (Exception e) {
            logger.error("Error during database operations", e);
            throw e;
        }
    }

    private void createTestData() {
        logger.info("   Создание тестовых данных...");
        String[] testUsernames = {"alice", "bob", "charlie", "diana", "eve", "frank", "grace", "henry", "ivan", "julia"};

        for (String username : testUsernames) {
            try {
                userService.createUser(username);
                logger.info("   Создан пользователь: {}", username);
            } catch (IllegalArgumentException e) {
                logger.info("   Пользователь {} уже существует", username);
            }
        }
    }
}