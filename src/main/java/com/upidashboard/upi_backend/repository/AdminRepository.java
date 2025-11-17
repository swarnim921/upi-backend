package com.upidashboard.upi_backend.repository;

import com.upidashboard.upi_backend.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByEmail(String email);
}
