package com.ffms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.ffms.model.Feedback;
import com.ffms.model.Student;
import com.ffms.model.Faculty;
import com.ffms.model.Subject;
import com.ffms.model.Admin;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class WhiteBoxTests {
    private Feedback feedback;
    private Student student;
    private Faculty faculty;
    private Subject subject;
    private Admin admin;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        student = new Student();
        faculty = new Faculty();
        subject = new Subject();
        admin = new Admin();
        
        // Setup basic test data
        student.setId(1L);
        faculty.setId(1L);
        subject.setId(1L);
        admin.setId(1L);
    }

    // Statement Coverage Tests
    @Test
    void testAllGettersAndSetters() {
        // Test all getters and setters
        feedback.setId(1L);
        assertEquals(1L, feedback.getId());

        feedback.setStudent(student);
        assertEquals(student, feedback.getStudent());

        feedback.setFaculty(faculty);
        assertEquals(faculty, feedback.getFaculty());

        feedback.setSubject(subject);
        assertEquals(subject, feedback.getSubject());

        feedback.setSelectedFaculty(faculty);
        assertEquals(faculty, feedback.getSelectedFaculty());

        LocalDateTime now = LocalDateTime.now();
        feedback.setSubmissionDate(now);
        assertEquals(now, feedback.getSubmissionDate());

        feedback.setPunctuality("Good");
        assertEquals("Good", feedback.getPunctuality());

        feedback.setClassParticipation("Excellent");
        assertEquals("Excellent", feedback.getClassParticipation());

        feedback.setConceptDelivery("Very Good");
        assertEquals("Very Good", feedback.getConceptDelivery());

        feedback.setFairEvaluation("Good");
        assertEquals("Good", feedback.getFairEvaluation());

        feedback.setSocialIssues("None");
        assertEquals("None", feedback.getSocialIssues());

        feedback.setCommunication("Excellent");
        assertEquals("Excellent", feedback.getCommunication());

        feedback.setOverallRating("Very Good");
        assertEquals("Very Good", feedback.getOverallRating());
    }

    // Branch Coverage Tests
    @Test
    void testTeachingMethodologyValidation() {
        // Test null value
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setTeachingMethodology(null);
        });
        assertTrue(nullException.getMessage().contains("Feedback cannot be empty"));

        // Test empty string
        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setTeachingMethodology("");
        });
        assertTrue(emptyException.getMessage().contains("Feedback cannot be empty"));

        // Test valid value
        feedback.setTeachingMethodology("Good");
        assertEquals("Good", feedback.getTeachingMethodology());
    }

    @Test
    void testSubjectKnowledgeValidation() {
        // Test null value
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setSubjectKnowledge(null);
        });
        assertTrue(nullException.getMessage().contains("Feedback cannot be empty"));

        // Test empty string
        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setSubjectKnowledge("");
        });
        assertTrue(emptyException.getMessage().contains("Feedback cannot be empty"));

        // Test valid value
        feedback.setSubjectKnowledge("Excellent");
        assertEquals("Excellent", feedback.getSubjectKnowledge());
    }

    @Test
    void testStudentSupportValidation() {
        // Test null value
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setStudentSupport(null);
        });
        assertTrue(nullException.getMessage().contains("Feedback cannot be empty"));

        // Test empty string
        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setStudentSupport("");
        });
        assertTrue(emptyException.getMessage().contains("Feedback cannot be empty"));

        // Test valid value
        feedback.setStudentSupport("Good");
        assertEquals("Good", feedback.getStudentSupport());
    }

    // Path Coverage Tests
    @Test
    void testRatingValidationPaths() {
        // Path 1: Valid rating (1-5)
        feedback.setRating(3);
        assertEquals(3, feedback.getRating());

        // Path 2: Invalid rating (< 1)
        Exception lowException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setRating(0);
        });
        assertTrue(lowException.getMessage().contains("Invalid rating"));

        // Path 3: Invalid rating (> 5)
        Exception highException = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setRating(6);
        });
        assertTrue(highException.getMessage().contains("Invalid rating"));
    }

    // Condition Coverage Tests
    @Test
    void testFeedbackValidationConditions() {
        // Test all conditions for teaching methodology
        feedback.setTeachingMethodology("Valid feedback");
        assertEquals("Valid feedback", feedback.getTeachingMethodology());

        // Test all conditions for subject knowledge
        feedback.setSubjectKnowledge("Valid knowledge");
        assertEquals("Valid knowledge", feedback.getSubjectKnowledge());

        // Test all conditions for student support
        feedback.setStudentSupport("Valid support");
        assertEquals("Valid support", feedback.getStudentSupport());
    }

    // Multiple Condition Coverage Tests
    @Test
    void testMultipleConditions() {
        // Test multiple conditions together
        feedback.setTeachingMethodology("Good");
        feedback.setSubjectKnowledge("Excellent");
        feedback.setStudentSupport("Very Good");
        feedback.setRating(4);

        assertEquals("Good", feedback.getTeachingMethodology());
        assertEquals("Excellent", feedback.getSubjectKnowledge());
        assertEquals("Very Good", feedback.getStudentSupport());
        assertEquals(4, feedback.getRating());
    }

    // Additional Model Tests
    @Test
    void testStudentModel() {
        student.setName("John Doe");
        student.setEmail("john@example.com");
        student.setPassword("password123");
        
        assertEquals("John Doe", student.getName());
        assertEquals("john@example.com", student.getEmail());
        assertEquals("password123", student.getPassword());
        
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        student.setSubjects(subjects);
        
        assertTrue(student.getSubjects().contains(subject));
    }

    @Test
    void testFacultyModel() {
        faculty.setName("Dr. Smith");
        faculty.setEmail("smith@example.com");
        faculty.setPassword("faculty123");
        
        assertEquals("Dr. Smith", faculty.getName());
        assertEquals("smith@example.com", faculty.getEmail());
        assertEquals("faculty123", faculty.getPassword());
        
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        faculty.setSubjects(subjects);
        
        assertTrue(faculty.getSubjects().contains(subject));
    }

    @Test
    void testSubjectModel() {
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Introduction to Programming");
        subject.setDescription("Basic programming concepts");
        subject.setCredits(3);
        
        assertEquals("CS101", subject.getSubjectCode());
        assertEquals("Introduction to Programming", subject.getSubjectName());
        assertEquals("Basic programming concepts", subject.getDescription());
        assertEquals(3, subject.getCredits());
        
        Set<Faculty> faculties = new HashSet<>();
        faculties.add(faculty);
        subject.setFaculties(faculties);
        
        Set<Student> students = new HashSet<>();
        students.add(student);
        subject.setStudents(students);
        
        assertTrue(subject.getFaculties().contains(faculty));
        assertTrue(subject.getStudents().contains(student));
    }

    @Test
    void testAdminModel() {
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setEmail("admin@example.com");
        admin.setName("System Admin");
        
        assertEquals("admin", admin.getUsername());
        assertEquals("admin123", admin.getPassword());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("System Admin", admin.getName());
    }

    // Relationship Tests
    @Test
    void testStudentSubjectRelationship() {
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        student.setSubjects(subjects);
        
        Set<Student> students = new HashSet<>();
        students.add(student);
        subject.setStudents(students);
        
        assertTrue(student.getSubjects().contains(subject));
        assertTrue(subject.getStudents().contains(student));
    }

    @Test
    void testFacultySubjectRelationship() {
        Set<Subject> subjects = new HashSet<>();
        subjects.add(subject);
        faculty.setSubjects(subjects);
        
        Set<Faculty> faculties = new HashSet<>();
        faculties.add(faculty);
        subject.setFaculties(faculties);
        
        assertTrue(faculty.getSubjects().contains(subject));
        assertTrue(subject.getFaculties().contains(faculty));
    }

    // Edge Cases
    @Test
    void testEmptySets() {
        student.setSubjects(new HashSet<>());
        faculty.setSubjects(new HashSet<>());
        subject.setStudents(new HashSet<>());
        subject.setFaculties(new HashSet<>());
        
        assertTrue(student.getSubjects().isEmpty());
        assertTrue(faculty.getSubjects().isEmpty());
        assertTrue(subject.getStudents().isEmpty());
        assertTrue(subject.getFaculties().isEmpty());
    }

    @Test
    void testNullValues() {
        student.setName(null);
        faculty.setName(null);
        subject.setSubjectName(null);
        admin.setName(null);
        
        assertNull(student.getName());
        assertNull(faculty.getName());
        assertNull(subject.getSubjectName());
        assertNull(admin.getName());
    }
} 