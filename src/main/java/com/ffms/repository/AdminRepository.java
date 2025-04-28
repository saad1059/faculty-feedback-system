package com.ffms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ffms.model.Admin;
import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
    Admin findByEmail(String email);
    List<Admin> findAllByUsername(String username);
} 