package com.upidashboard.upi_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.upidashboard.upi_backend.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUpiId(String upiId);
}
