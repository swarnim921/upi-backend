package com.upidashboard.upi_backend.repository;

import com.upidashboard.upi_backend.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
<<<<<<< HEAD
}
=======
    // You can add custom queries later, for example:
    Admin findByEmail(String email);
}
>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
