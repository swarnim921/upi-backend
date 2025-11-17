package com.upidashboard.upi_backend.repository;

import com.upidashboard.upi_backend.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUpiId(String upiId);
    boolean existsByEmail(String email);
}
