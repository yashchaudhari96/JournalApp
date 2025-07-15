package com.example.journalapp.controller;

import com.example.journalapp.Services.JournalEntryService;
import com.example.journalapp.Services.UserService;
import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    JournalEntryService journalService;
    @Autowired
    UserService userService;

    @GetMapping("/get-all/{userName}")
    public ResponseEntity<?> getAllJournalEntriesUser(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List<JournalEntry> entries = user.getJournalEntries();
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping("id/{userName}/{myid}")
    public ResponseEntity<?> getById(@PathVariable String userName, @PathVariable String myid) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Filter journal entries belonging to this user and matching the ID
        Optional<JournalEntry> match = user.getJournalEntries()
                .stream()
                .filter(x -> x.getId().equals(myid))
                .findFirst();

        if (match.isPresent()) {
            return new ResponseEntity<>(match.get(), HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found");
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myentry, @RequestParam String userName) {
        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            JournalEntry saved = journalService.SaveEntries(myentry, userName);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating journal entry");
        }
    }

    @DeleteMapping("id/{userName}/{myid}")
    public ResponseEntity<?> deleteEntry(@PathVariable String userName, @PathVariable String myid) {
        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            journalService.deleteById(myid, userName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found");
        }
    }

    @PutMapping("id/{userName}/{myid}")
    public ResponseEntity<?> updateJournalById(@PathVariable String userName, @PathVariable String myid, @RequestBody JournalEntry myentry) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        JournalEntry existing = journalService.getByID(myid);
        if (existing != null) {
            if (myentry.getTitle() != null && !myentry.getTitle().isEmpty()) {
                existing.setTitle(myentry.getTitle());
            }
            if (myentry.getContent() != null && !myentry.getContent().isEmpty()) {
                existing.setContent(myentry.getContent());
            }
            journalService.SaveEntries(existing, userName);
            return ResponseEntity.ok(existing);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found");
    }
}