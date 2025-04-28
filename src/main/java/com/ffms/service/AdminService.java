package com.ffms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ffms.model.Admin;
import com.ffms.repository.AdminRepository;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin registerAdmin(Admin admin) {
        // Check if username or email already exists
        if (adminRepository.findByUsername(admin.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (adminRepository.findByEmail(admin.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        return adminRepository.save(admin);
    }

    public Admin loginAdmin(String username, String password) {
        List<Admin> admins = adminRepository.findAllByUsername(username);
        if (admins != null && !admins.isEmpty()) {
            Admin admin = admins.get(0); // Get the first admin with this username
            if (admin.getPassword().equals(password)) {
                return admin;
            }
        }
        return null;
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    public Admin updateAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
} 