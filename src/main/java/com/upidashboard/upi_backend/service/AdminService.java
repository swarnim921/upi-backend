package com.upidashboard.upi_backend.service;

import com.upidashboard.upi_backend.model.Admin;
import com.upidashboard.upi_backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // ✅ Get all admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // ✅ Register a new admin
    public Admin addAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // ✅ Get admin by email (used later for login)
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    // ✅ Delete admin by ID
    public void deleteAdmin(String id) {
        adminRepository.deleteById(id);
    }

    // ✅ Get admin by ID (optional)
    public Optional<Admin> getAdminById(String id) {
        return adminRepository.findById(id);
    }
}
