package com.upidashboard.upi_backend.dto;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import com.upidashboard.upi_backend.model.Role;

@Data
@Builder
public class UserProfile {
    private String id;
    private String name;
    private String email;
    private String upiId;
    private double balance;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


