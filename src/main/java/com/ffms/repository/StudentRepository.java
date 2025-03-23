package com.ffms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ffms.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);
}
