package com.ffms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ffms.model.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Faculty findByEmail(String email);
}
