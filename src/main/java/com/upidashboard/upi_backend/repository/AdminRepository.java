package com.upidashboard.upi_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.upidashboard.upi_backend.model.Admin;

public interface AdminRepository extends MongoRepository<Admin, String> {
    // You can add custom query methods here later if needed
}
