package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.dto.UserProfile;
import com.upidashboard.upi_backend.model.User;
import com.upidashboard.upi_backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserProfile> getAllUserProfiles() {
        return userRepository.findAll()
            .stream()
            .map(this::toProfile)
            .collect(Collectors.toList());
    }

    public UserProfile getProfileByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(this::toProfile)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserProfile getUserByUpiId(String upiId) {
        return userRepository.findByUpiId(upiId)
            .map(this::toProfile)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with UPI: " + upiId));
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public User save(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserProfile toProfile(User user) {
        return UserProfile.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .upiId(user.getUpiId())
            .balance(user.getBalance())
            .roles(user.getRoles())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
