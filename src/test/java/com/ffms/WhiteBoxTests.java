package com.ffms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.ffms.model.Feedback;
import com.ffms.model.Student;
import com.ffms.model.Faculty;
import com.ffms.model.Subject;
import java.time.LocalDateTime;

public class WhiteBoxTests {
    private Feedback feedback;
    private Student student;
    private Faculty faculty;
    private Subject subject;

    @BeforeEach
    void setUp() {
        feedback = new Feedback();
        student = new Student();
        faculty = new Faculty();
        subject = new Subject();
        
        // Setup basic test data
        student.setId(1L);
        faculty.setId(1L);
        subject.setId(1L);
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
} 