package com.ffms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ffms.controller.*;
import com.ffms.service.*;
import com.ffms.model.*;
import com.ffms.exception.RegistrationException;

import java.util.*;

public class ControllerLayerTests {

    @Mock
    private StudentService studentService;
    @Mock
    private FacultyService facultyService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private FeedbackService feedbackService;
    @Mock
    private AdminService adminService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController authController;
    @InjectMocks
    private StudentController studentController;
    @InjectMocks
    private FeedbackController feedbackController;

    private Student student;
    private Faculty faculty;
    private Subject subject;
    private Feedback feedback;

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

        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setStudent(student);
        feedback.setFaculty(faculty);
        feedback.setSubject(subject);
    }

    // Auth Controller Tests
    @Test
    void testShowFacultyRegistrationForm() {
        String viewName = authController.showFacultyRegistrationForm(model);
        assertEquals("faculty_register", viewName);
        verify(model).addAttribute(eq("faculty"), any(Faculty.class));
    }

    @Test
    void testRegisterFaculty() {
        when(facultyService.registerFaculty(any(Faculty.class))).thenReturn(faculty);

        String viewName = authController.registerFaculty(faculty, redirectAttributes);
        assertEquals("redirect:/faculty/login", viewName);
        verify(redirectAttributes).addFlashAttribute("success", "Registration successful! Please login.");
    }

    @Test
    void testRegisterFacultyWithError() {
        when(facultyService.registerFaculty(any(Faculty.class)))
            .thenThrow(new RegistrationException("Email already exists"));

        String viewName = authController.registerFaculty(faculty, redirectAttributes);
        assertEquals("redirect:/faculty/register", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Email already exists");
    }

    @Test
    void testShowStudentRegistrationForm() {
        String viewName = authController.showStudentRegistrationForm(model);
        assertEquals("student_register", viewName);
        verify(model).addAttribute(eq("student"), any(Student.class));
    }

    // Student Controller Tests
    @Test
    void testEnrollSubjects() {
        when(session.getAttribute("studentId")).thenReturn(1L);
        when(studentService.getStudentById(anyLong())).thenReturn(student);
        when(subjectService.getSubjectById(anyLong())).thenReturn(subject);

        Map<String, String> subjects = new HashMap<>();
        subjects.put("1", "on");
        Map<String, String> facultySelections = new HashMap<>();
        facultySelections.put("1", "1");

        String viewName = studentController.enrollSubjects(subjects, facultySelections, session, redirectAttributes);
        assertEquals("redirect:/student/enroll", viewName);
    }

    @Test
    void testEnrollSubjectsWithoutLogin() {
        when(session.getAttribute("studentId")).thenReturn(null);

        String viewName = studentController.enrollSubjects(new HashMap<>(), new HashMap<>(), session, redirectAttributes);
        assertEquals("redirect:/student/login", viewName);
    }

    // Feedback Controller Tests
    @Test
    void testSubmitFeedback() {
        when(session.getAttribute("studentId")).thenReturn(1L);
        when(studentService.getStudentById(anyLong())).thenReturn(student);
        when(facultyService.getFacultyById(anyLong())).thenReturn(faculty);
        when(subjectService.getSubjectById(anyLong())).thenReturn(subject);
        when(feedbackService.submitFeedback(any(Feedback.class))).thenReturn(feedback);

        // Set up student's enrollment in the subject
        student.getSubjects().add(subject);
        subject.getStudents().add(student);

        // Set up faculty's assignment to the subject
        faculty.getSubjects().add(subject);
        subject.getFaculties().add(faculty);

        String viewName = feedbackController.submitFeedback(feedback, 1L, 1L, session, redirectAttributes);
        assertEquals("redirect:/student/dashboard", viewName);
        verify(redirectAttributes).addFlashAttribute("success", "Feedback submitted successfully!");
    }

    @Test
    void testSubmitFeedbackWithoutLogin() {
        when(session.getAttribute("studentId")).thenReturn(null);

        String viewName = feedbackController.submitFeedback(feedback, 1L, 1L, session, redirectAttributes);
        assertEquals("redirect:/student/login", viewName);
    }

    // Edge Cases
    @Test
    void testHomePage() {
        String viewName = authController.home();
        assertEquals("index", viewName);
    }

    @Test
    void testShowFacultyLoginForm() {
        String viewName = authController.showFacultyLoginForm(model);
        assertEquals("faculty_login", viewName);
        verify(model).addAttribute(eq("faculty"), any(Faculty.class));
    }

    @Test
    void testShowStudentLoginForm() {
        String viewName = authController.showStudentLoginForm(model);
        assertEquals("student_login", viewName);
        verify(model).addAttribute(eq("student"), any(Student.class));
    }
} 