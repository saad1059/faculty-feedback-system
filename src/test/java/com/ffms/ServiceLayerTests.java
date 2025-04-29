package com.ffms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ffms.model.*;
import com.ffms.service.*;
import com.ffms.repository.*;
import com.ffms.exception.RegistrationException;

import java.util.*;

public class ServiceLayerTests {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private StudentService studentService;
    @InjectMocks
    private FacultyService facultyService;
    @InjectMocks
    private SubjectService subjectService;
    @InjectMocks
    private AdminService adminService;

    private Student student;
    private Faculty faculty;
    private Subject subject;
    private Admin admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test data
        student = new Student();
        student.setId(1L);
        student.setName("John Doe");
        student.setEmail("john@example.com");
        student.setPassword("Password123!");

        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Dr. Smith");
        faculty.setEmail("smith@example.com");
        faculty.setPassword("Faculty123!");

        subject = new Subject();
        subject.setId(1L);
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Introduction to Programming");
        subject.setDescription("Basic programming concepts");
        subject.setCredits(3);

        admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("Admin123!");
        admin.setEmail("admin@example.com");
        admin.setName("System Admin");
    }

    // Student Service Tests
    @Test
    void testRegisterStudent() {
        when(studentRepository.findByEmail(anyString())).thenReturn(null);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student registeredStudent = studentService.registerStudent(student);
        assertNotNull(registeredStudent);
        assertEquals("John Doe", registeredStudent.getName());
    }

    @Test
    void testRegisterStudentWithDuplicateEmail() {
        when(studentRepository.findByEmail(anyString())).thenReturn(student);

        assertThrows(RegistrationException.class, () -> {
            studentService.registerStudent(student);
        });
    }

    @Test
    void testLoginStudent() {
        when(studentRepository.findByEmail(anyString())).thenReturn(student);

        Student loggedInStudent = studentService.loginStudent("john@example.com", "Password123!");
        assertNotNull(loggedInStudent);
        assertEquals("John Doe", loggedInStudent.getName());
    }

    // Faculty Service Tests
    @Test
    void testRegisterFaculty() {
        when(facultyRepository.findByEmail(anyString())).thenReturn(null);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        Faculty registeredFaculty = facultyService.registerFaculty(faculty);
        assertNotNull(registeredFaculty);
        assertEquals("Dr. Smith", registeredFaculty.getName());
    }

    @Test
    void testRegisterFacultyWithDuplicateEmail() {
        when(facultyRepository.findByEmail(anyString())).thenReturn(faculty);

        assertThrows(RegistrationException.class, () -> {
            facultyService.registerFaculty(faculty);
        });
    }

    @Test
    void testLoginFaculty() {
        when(facultyRepository.findByEmail(anyString())).thenReturn(faculty);

        Faculty loggedInFaculty = facultyService.loginFaculty("smith@example.com", "Faculty123!");
        assertNotNull(loggedInFaculty);
        assertEquals("Dr. Smith", loggedInFaculty.getName());
    }

    // Subject Service Tests
    @Test
    void testCreateSubject() {
        when(subjectRepository.findBySubjectCode(anyString())).thenReturn(null);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        Subject createdSubject = subjectService.createSubject(subject);
        assertNotNull(createdSubject);
        assertEquals("CS101", createdSubject.getSubjectCode());
    }

    @Test
    void testCreateSubjectWithDuplicateCode() {
        when(subjectRepository.findBySubjectCode(anyString())).thenReturn(subject);

        assertThrows(RegistrationException.class, () -> {
            subjectService.createSubject(subject);
        });
    }

    @Test
    void testAssignFacultyToSubject() {
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(subject));
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        subjectService.assignFacultyToSubject(1L, 1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void testEnrollStudentInSubject() {
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(subject));
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        subjectService.enrollStudentInSubject(1L, 1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    // Admin Service Tests
    @Test
    void testRegisterAdmin() {
        when(adminRepository.findByUsername(anyString())).thenReturn(null);
        when(adminRepository.findByEmail(anyString())).thenReturn(null);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Admin registeredAdmin = adminService.registerAdmin(admin);
        assertNotNull(registeredAdmin);
        assertEquals("admin", registeredAdmin.getUsername());
    }

    @Test
    void testRegisterAdminWithDuplicateUsername() {
        when(adminRepository.findByUsername(anyString())).thenReturn(admin);

        assertThrows(RuntimeException.class, () -> {
            adminService.registerAdmin(admin);
        });
    }

    @Test
    void testLoginAdmin() {
        List<Admin> admins = Collections.singletonList(admin);
        when(adminRepository.findAllByUsername(anyString())).thenReturn(admins);

        Admin loggedInAdmin = adminService.loginAdmin("admin", "Admin123!");
        assertNotNull(loggedInAdmin);
        assertEquals("admin", loggedInAdmin.getUsername());
    }

    // Edge Cases
    @Test
    void testInvalidEmailFormat() {
        student.setEmail("invalid-email");
        assertThrows(RegistrationException.class, () -> {
            studentService.registerStudent(student);
        });
    }

    @Test
    void testWeakPassword() {
        student.setPassword("weak");
        assertThrows(RegistrationException.class, () -> {
            studentService.registerStudent(student);
        });
    }

    @Test
    void testNullRequiredFields() {
        student.setName(null);
        assertThrows(RegistrationException.class, () -> {
            studentService.registerStudent(student);
        });
    }
} 