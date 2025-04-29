package com.ffms.repository;

import com.ffms.model.Faculty;
import com.ffms.model.Feedback;
import com.ffms.model.Student;
import com.ffms.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByFaculty(Faculty faculty);
    List<Feedback> findByStudent(Student student);
    List<Feedback> findBySubject(Subject subject);
    List<Feedback> findByFacultyAndSubject(Faculty faculty, Subject subject);
    List<Feedback> findByStudentAndSubject(Student student, Subject subject);
    Feedback findByStudentAndFacultyAndSubject(Student student, Faculty faculty, Subject subject);
    void deleteByStudent(Student student);
} 