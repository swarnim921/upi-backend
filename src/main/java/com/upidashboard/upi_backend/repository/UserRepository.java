package com.upidashboard.upi_backend.repository;

<<<<<<< HEAD
import com.upidashboard.upi_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
=======
import org.springframework.data.mongodb.repository.MongoRepository;
import com.upidashboard.upi_backend.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUpiId(String upiId);
}
>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
