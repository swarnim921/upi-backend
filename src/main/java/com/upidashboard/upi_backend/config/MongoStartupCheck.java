package com.upidashboard.upi_backend.config;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Simple startup check to verify MongoDB connectivity at application start.
 * Activated when running any profile (default) — it will attempt a ping and log the result.
 */
@Component
public class MongoStartupCheck implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoStartupCheck.class);

    private final MongoTemplate mongoTemplate;

    public MongoStartupCheck(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            Document ping = mongoTemplate.getDb().runCommand(new Document("ping", 1));
            log.info("MongoDB ping result: {}", ping.toJson());
        } catch (Exception e) {
            log.error("Failed to connect to MongoDB at startup — check your connection string, network or Atlas IP whitelist.", e);
        }
    }
}
