package com.ffms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ffms.model.Faculty;
import com.ffms.repository.FacultyRepository;
import com.ffms.exception.RegistrationException;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    public Faculty registerFaculty(Faculty faculty) {
        // Validate input
        validateFacultyRegistration(faculty);

        // Check for duplicate email
        if (facultyRepository.findByEmail(faculty.getEmail()) != null) {
            throw new RegistrationException("Email already registered");
        }

        // Validate password strength
        if (!isPasswordStrong(faculty.getPassword())) {
            throw new RegistrationException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        // Save the faculty
        return facultyRepository.save(faculty);
    }

    private void validateFacultyRegistration(Faculty faculty) {
        if (faculty.getName() == null || faculty.getName().trim().isEmpty()) {
            throw new RegistrationException("Name is required");
        }

        if (faculty.getEmail() == null || faculty.getEmail().trim().isEmpty()) {
            throw new RegistrationException("Email is required");
        }

        if (!isValidEmail(faculty.getEmail())) {
            throw new RegistrationException("Invalid email format");
        }

        if (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty()) {
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

    public Faculty loginFaculty(String email, String password) {
        Faculty faculty = facultyRepository.findByEmail(email);
        if (faculty != null && faculty.getPassword().equals(password)) {
            return faculty;
        }
        return null;
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty) {
        // Validate input
        validateFacultyRegistration(faculty);

        // Check if faculty exists
        Faculty existingFaculty = facultyRepository.findById(faculty.getId())
            .orElseThrow(() -> new RegistrationException("Faculty not found"));

        // Check for duplicate email (excluding current faculty)
        Faculty facultyWithEmail = facultyRepository.findByEmail(faculty.getEmail());
        if (facultyWithEmail != null && !facultyWithEmail.getId().equals(faculty.getId())) {
            throw new RegistrationException("Email already registered");
        }

        // Update password only if it's changed
        if (faculty.getPassword() != null && !faculty.getPassword().isEmpty()) {
            if (!isPasswordStrong(faculty.getPassword())) {
                throw new RegistrationException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            }
        } else {
            // Keep existing password if no new password provided
            faculty.setPassword(existingFaculty.getPassword());
        }

        return facultyRepository.save(faculty);
    }
} 