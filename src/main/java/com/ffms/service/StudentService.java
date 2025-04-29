package com.ffms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ffms.model.Student;
import com.ffms.repository.StudentRepository;
import com.ffms.exception.RegistrationException;
import com.ffms.model.Subject;
import com.ffms.model.Faculty;
import com.ffms.repository.SubjectRepository;
import com.ffms.repository.FeedbackRepository;
import com.ffms.model.Feedback;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student registerStudent(Student student) {
        // Validate input
        validateStudentRegistration(student);

        // Check for duplicate email
        if (studentRepository.findByEmail(student.getEmail()) != null) {
            throw new RegistrationException("Email already registered");
        }

        // Validate password strength
        if (!isPasswordStrong(student.getPassword())) {
            throw new RegistrationException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        // Save the student
        return studentRepository.save(student);
    }

    private void validateStudentRegistration(Student student) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new RegistrationException("Name is required");
        }

        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new RegistrationException("Email is required");
        }

        if (!isValidEmail(student.getEmail())) {
            throw new RegistrationException("Invalid email format");
        }

        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            throw new RegistrationException("Password is required");
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isPasswordStrong(String password) {
        // Password must be at least 8 characters long
        if (password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        boolean hasUpper = false;
        // Check for at least one lowercase letter
        boolean hasLower = false;
        // Check for at least one number
        boolean hasNumber = false;
        // Check for at least one special character
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        return hasUpper && hasLower && hasNumber && hasSpecial;
    }

    public Student loginStudent(String email, String password) {
        Student student = studentRepository.findByEmail(email);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        return null;
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Student student) {
        // Validate input
        validateStudentRegistration(student);

        // Check if student exists
        Student existingStudent = studentRepository.findById(student.getId())
            .orElseThrow(() -> new RegistrationException("Student not found"));

        // Check for duplicate email (excluding current student)
        Student studentWithEmail = studentRepository.findByEmail(student.getEmail());
        if (studentWithEmail != null && !studentWithEmail.getId().equals(student.getId())) {
            throw new RegistrationException("Email already registered");
        }

        // Update password only if it's changed
        if (student.getPassword() != null && !student.getPassword().isEmpty()) {
            if (!isPasswordStrong(student.getPassword())) {
                throw new RegistrationException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            }
        } else {
            // Keep existing password if no new password provided
            student.setPassword(existingStudent.getPassword());
        }

        return studentRepository.save(student);
    }

    public void enrollStudentInSubject(Student student, Subject subject, Faculty faculty) {
        // Check if student is already enrolled in this subject
        if (student.getSubjects().contains(subject)) {
            throw new IllegalArgumentException("Student is already enrolled in this subject");
        }

        // If faculty is provided, validate it
        if (faculty != null && !subject.getFaculties().contains(faculty)) {
            throw new IllegalArgumentException("Faculty is not assigned to this subject");
        }

        // Add the subject to student's subjects
        student.getSubjects().add(subject);
        
        // Add the student to subject's students
        subject.getStudents().add(student);
        
        // Only create feedback entry if faculty is selected
        if (faculty != null) {
            // Create a new feedback entry to store the selected faculty
            Feedback feedback = new Feedback();
            feedback.setStudent(student);
            feedback.setSubject(subject);
            feedback.setFaculty(faculty);
            feedback.setSelectedFaculty(faculty);
            feedback.setSubmissionDate(LocalDateTime.now());
            feedbackRepository.save(feedback);
        }
        
        // Save the changes
        studentRepository.save(student);
        subjectRepository.save(subject);
    }

    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // Remove student from all subjects
        for (Subject subject : student.getSubjects()) {
            subject.getStudents().remove(student);
            subjectRepository.save(subject);
        }

        // Clear student's subjects
        student.getSubjects().clear();
        studentRepository.save(student);

        // Delete all feedback associated with this student
        feedbackRepository.deleteByStudent(student);

        // Finally, delete the student
        studentRepository.delete(student);
    }
} 