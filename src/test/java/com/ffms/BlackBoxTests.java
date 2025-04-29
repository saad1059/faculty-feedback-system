package com.ffms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.ffms.model.Feedback;
import com.ffms.model.Student;
import com.ffms.model.Faculty;
import com.ffms.model.Subject;
import java.time.LocalDateTime;

public class BlackBoxTests {

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

    // Equivalence Class Partitioning Tests
    
    @Test
    void testValidRatings() {
        feedback.setRating(3);
        feedback.setTeachingMethodology("Good");
        feedback.setSubjectKnowledge("Excellent");
        feedback.setStudentSupport("Average");
        assertEquals(3, feedback.getRating());
    }

    @Test
    void testInvalidLowRating() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setRating(0);
        });
        assertTrue(exception.getMessage().contains("Invalid rating"));
    }

    @Test
    void testInvalidHighRating() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setRating(6);
        });
        assertTrue(exception.getMessage().contains("Invalid rating"));
    }

    // Boundary Value Analysis Tests
    
    @Test
    void testMinimumRating() {
        feedback.setRating(1);
        assertEquals(1, feedback.getRating());
    }

    @Test
    void testMaximumRating() {
        feedback.setRating(5);
        assertEquals(5, feedback.getRating());
    }

    @Test
    void testEmptyFeedback() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedback.setTeachingMethodology("");
        });
        assertTrue(exception.getMessage().contains("Feedback cannot be empty"));
    }

    // System Functionality Tests (10 test cases)
    
    @Test
    void testCompleteValidFeedback() {
        feedback.setStudent(student);
        feedback.setFaculty(faculty);
        feedback.setSubject(subject);
        feedback.setSubmissionDate(LocalDateTime.now());
        feedback.setRating(4);
        feedback.setTeachingMethodology("Good");
        feedback.setSubjectKnowledge("Excellent");
        feedback.setStudentSupport("Good");
        
        assertNotNull(feedback.getStudent());
        assertNotNull(feedback.getFaculty());
        assertNotNull(feedback.getSubject());
        assertNotNull(feedback.getSubmissionDate());
    }

    @Test
    void testPunctualityFeedback() {
        feedback.setPunctuality("Excellent");
        assertEquals("Excellent", feedback.getPunctuality());
    }

    @Test
    void testClassParticipationFeedback() {
        feedback.setClassParticipation("Good");
        assertEquals("Good", feedback.getClassParticipation());
    }

    @Test
    void testConceptDeliveryFeedback() {
        feedback.setConceptDelivery("Very Good");
        assertEquals("Very Good", feedback.getConceptDelivery());
    }
} 