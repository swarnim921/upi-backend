package com.upidashboard.upi_backend.controller;

import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ✅ Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Create (register) a new user
    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // ✅ Get user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userRepository.findById(id).orElse(null);
    }

    // ✅ Delete user by ID
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }

    // ✅ Simple test endpoint
    @GetMapping("/demo")
    public String demo() {
        return "UserController is working fine!";
    }
}
