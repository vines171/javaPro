package com.example.service;

import com.example.model.User;
import com.example.dao.UserDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String username) {
        Optional<User> existingUser = userDao.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with username '" + username + "' already exists");
        }

        User user = new User(username);
        return userDao.create(user);
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String newUsername) {
        Optional<User> existingUser = userDao.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        Optional<User> userWithSameUsername = userDao.findByUsername(newUsername);
        if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(id)) {
            throw new RuntimeException("Username '" + newUsername + "' is already taken");
        }

        User user = existingUser.get();
        user.setUsername(newUsername);
        return userDao.update(user);
    }

    public boolean deleteUser(Long id) {
        return userDao.delete(id);
    }

    public boolean deleteUserByUsername(String username) {
        Optional<User> user = userDao.findByUsername(username);
        return user.map(value -> userDao.delete(value.getId())).orElse(false);
    }
}
