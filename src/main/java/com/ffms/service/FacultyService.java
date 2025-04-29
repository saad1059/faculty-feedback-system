package com.ffms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ffms.model.Faculty;
import com.ffms.repository.FacultyRepository;
import com.ffms.exception.RegistrationException;

import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
            .orElseThrow(() -> new RegistrationException("Faculty not found with id: " + id));
    }

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

    public Faculty updateFaculty(Faculty faculty) {
        // Validate input
        validateFacultyRegistration(faculty);

        // Check if faculty exists
        Faculty existingFaculty = getFacultyById(faculty.getId());

        // Check for duplicate email, but exclude the current faculty
        Faculty facultyWithEmail = facultyRepository.findByEmail(faculty.getEmail());
        if (facultyWithEmail != null && !facultyWithEmail.getId().equals(faculty.getId())) {
            throw new RegistrationException("Email already registered");
        }

        // Update the faculty
        existingFaculty.setName(faculty.getName());
        existingFaculty.setEmail(faculty.getEmail());
        if (faculty.getPassword() != null && !faculty.getPassword().trim().isEmpty()) {
            if (!isPasswordStrong(faculty.getPassword())) {
                throw new RegistrationException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            }
            existingFaculty.setPassword(faculty.getPassword());
        }

        return facultyRepository.save(existingFaculty);
    }

    public void deleteFaculty(Long id) {
        Faculty faculty = getFacultyById(id);
        facultyRepository.delete(faculty);
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

        if (faculty.getId() == null && (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty())) {
            throw new RegistrationException("Password is required");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isPasswordStrong(String password) {
        // At least 8 characters long
        // Contains at least one uppercase letter
        // Contains at least one lowercase letter
        // Contains at least one number
        // Contains at least one special character
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    public Faculty loginFaculty(String email, String password) {
        Faculty faculty = facultyRepository.findByEmail(email);
        if (faculty != null && faculty.getPassword().equals(password)) {
            return faculty;
        }
        return null;
    }
} 