package com.example.journalapp.Services;

import com.example.journalapp.Repository.JournalEntryRepo;
import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class JournalEntryService {

    private final JournalEntryRepo journalRepo;
    private final UserService userService;

    public JournalEntryService(JournalEntryRepo journalRepo, UserService userService) {
        this.journalRepo = journalRepo;
        this.userService = userService;
    }

    @Transactional
    public JournalEntry SaveEntries(JournalEntry journalEntry, String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            log.error("User not found for username: {}", userName);
            throw new IllegalArgumentException("User not found: " + userName);
        }
        try {
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalRepo.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveNewUser(user);
            return saved;
        } catch (Exception e) {
            log.error("Error saving journal entry for user: {}", userName, e);
            throw new RuntimeException("Failed to save journal entry", e);
        }
    }

    public List<JournalEntry> findByUserName(String userName) {
        User user = userService.findByUserName(userName);
        if (user != null) {
            return user.getJournalEntries();
        }
        log.warn("User not found for username: {}", userName);
        return Collections.emptyList();
    }

    @Transactional
    public void deleteById(String id, String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            log.error("User not found for username: {}", userName);
            throw new IllegalArgumentException("User not found: " + userName);
        }
        boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        if (removed) {
            userService.saveNewUser(user);
            journalRepo.deleteById(id);
        } else {
            log.warn("Journal entry with ID {} not found for user {}", id, userName);
            throw new IllegalArgumentException("Journal entry not found: " + id);
        }
    }

    public JournalEntry getByID(String id) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            log.error("User not found for username: {}", userName);
            throw new IllegalArgumentException("User not found: " + userName);
        }
        JournalEntry entry = journalRepo.findById(id).orElse(null);
        if (entry != null && user.getJournalEntries().stream().anyMatch(e -> e.getId().equals(id))) {
            return entry;
        }
        log.warn("Journal entry with ID {} not found for user {}", id, userName);
        return null;
    }
}