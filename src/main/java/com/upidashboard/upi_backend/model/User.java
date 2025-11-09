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
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String upiId;
    private String email;
}
=======

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String upiId;
    private double balance;

    public User() {}

    public User(String name, String email, String upiId, double balance) {
        this.name = name;
        this.email = email;
        this.upiId = upiId;
        this.balance = balance;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
>>>>>>> a9fde5b945b9b33778f4479bfb2c9c257e0e31fa
