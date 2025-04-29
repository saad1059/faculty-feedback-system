package com.ffms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ffms.model.Subject;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findBySubjectCode(String subjectCode);
    List<Subject> findByFacultiesId(Long facultyId);
    List<Subject> findByStudentsId(Long studentId);
} 