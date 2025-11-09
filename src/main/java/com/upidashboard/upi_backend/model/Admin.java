package com.upidashboard.upi_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
<<<<<<< HEAD
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
=======

>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
@Document(collection = "admins")
public class Admin {
    @Id
    private String id;
    private String name;
    private String email;
<<<<<<< HEAD
    private String role;
}
=======
    private String password; // plain text for now, hashed later

    // Getters and Setters
}
>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
