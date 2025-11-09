package com.upidashboard.upi_backend.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ 1. Actual database users (from MongoDB)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ 2. Get a specific user by UPI ID
    @GetMapping("/{upiId}")
    public User getUserByUpiId(@PathVariable String upiId) {
        return userService.getUserByUpiId(upiId);
    }

    // ✅ 3. Register a new user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // ✅ 4. Delete a user by ID
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }

    // ✅ 5. Demo endpoint (for frontend & Postman testing)
    @GetMapping("/demo")
    public List<Map<String, String>> getDemoUsers() {
        List<Map<String, String>> users = new ArrayList<>();

        Map<String, String> user1 = new HashMap<>();
        user1.put("name", "Swarnim Singh");
        user1.put("upiId", "swarnim@upi");

        Map<String, String> user2 = new HashMap<>();
        user2.put("name", "Shaqib Iqbal");
        user2.put("upiId", "shaqib@upi");

        users.add(user1);
        users.add(user2);

        return users;
    }
}
