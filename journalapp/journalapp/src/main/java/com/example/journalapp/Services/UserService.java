package com.example.journalapp.Services;

import com.example.journalapp.Repository.UserRepository;
import com.example.journalapp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepo.save(user);
            log.info("Successfully saved new user: {}", user.getUserName());
        } catch (Exception e) {
            log.error("Error saving new user: {}", user.getUserName(), e);
            throw new RuntimeException("Failed to save new user: " + user.getUserName(), e);
        }
    }

    public void saveAdmin(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER", "ADMIN"));
            userRepo.save(user);
            log.info("Successfully saved admin user: {}", user.getUserName());
        } catch (Exception e) {
            log.error("Error saving admin user: {}", user.getUserName(), e);
            throw new RuntimeException("Failed to save admin user: " + user.getUserName(), e);
        }
    }

    public void saveUser(User user) {
        try {
            userRepo.save(user);
            log.info("Successfully updated user: {}", user.getUserName());
        } catch (Exception e) {
            log.error("Error updating user: {}", user.getUserName(), e);
            throw new RuntimeException("Failed to update user: " + user.getUserName(), e);
        }
    }

    public List<User> getAll() {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    public void deleteById(String id) {
        try {
            if (userRepo.existsById(id)) {
                userRepo.deleteById(id);
                log.info("Successfully deleted user with ID: {}", id);
            } else {
                log.warn("User with ID {} not found", id);
                throw new IllegalArgumentException("User not found: " + id);
            }
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", id, e);
            throw new RuntimeException("Failed to delete user: " + id, e);
        }
    }

    public User getByID(String id) {
        try {
            User user = userRepo.findById(id).orElse(null);
            if (user == null) {
                log.warn("User with ID {} not found", id);
            }
            return user;
        } catch (Exception e) {
            log.error("Error retrieving user with ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve user: " + id, e);
        }
    }

    public User findByUserName(String userName) {
        try {
            User user = userRepo.findByUserName(userName);
            if (user == null) {
                log.warn("User with username {} not found", userName);
            }
            return user;
        } catch (Exception e) {
            log.error("Error retrieving user with username: {}", userName, e);
            throw new RuntimeException("Failed to retrieve user: " + userName, e);
        }
    }
}