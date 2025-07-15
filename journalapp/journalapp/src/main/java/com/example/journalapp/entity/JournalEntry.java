package com.example.journalapp.controller;

import com.example.journalapp.Services.JournalEntryService;
import com.example.journalapp.Services.UserService;
import com.example.journalapp.entity.JournalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalService;
    @Autowired
    private UserService userService;

    @GetMapping("/get-all/{username}")
    public ResponseEntity<?> getAllJournalEntriesUser(@PathVariable String username) {
        List<JournalEntry> entries = journalService.findByUsername(username);
        if (entries.isEmpty() && userService.findByUsername(username) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping("id/{username}/{myid}")
    public ResponseEntity<?> getById(@PathVariable String username, @PathVariable String myid) {
        JournalEntry entry = journalService.getByID(myid, username);
        if (entry != null) {
            return new ResponseEntity<>(entry, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found or does not belong to user");
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myentry, @RequestParam String username) {
        try {
            JournalEntry saved = journalService.SaveEntries(myentry, username);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating journal entry");
        }
    }

    @DeleteMapping("id/{username}/{myid}")
    public ResponseEntity<?> deleteEntry(@PathVariable String username, @PathVariable String myid) {
        try {
            journalService.deleteById(myid, username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting journal entry");
        }
    }

    @PutMapping("id/{username}/{myid}")
    public ResponseEntity<?> updateJournalById(@PathVariable String username, @PathVariable String myid, @RequestBody JournalEntry myentry) {
        try {
            JournalEntry existing = journalService.getByID(myid, username);
            if (existing != null) {
                if (myentry.getTitle() != null && !myentry.getTitle().isEmpty()) {
                    existing.setTitle(myentry.getTitle());
                }
                if (myentry.getContent() != null && !myentry.getContent().isEmpty()) {
                    existing.setContent(myentry.getContent());
                }
                JournalEntry updated = journalService.SaveEntries(existing, username);
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found or does not belong to user");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating journal entry");
        }
    }
}