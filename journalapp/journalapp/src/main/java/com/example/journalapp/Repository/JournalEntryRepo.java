package com.example.journalapp.Repository;

import com.example.journalapp.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepo extends MongoRepository<JournalEntry, String> {

}
