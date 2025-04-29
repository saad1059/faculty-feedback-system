package com.ffms.service;

import com.ffms.model.Faculty;
import com.ffms.model.Feedback;
import com.ffms.model.Student;
import com.ffms.model.Subject;
import com.ffms.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(Feedback feedback) {
        feedback.setSubmissionDate(LocalDateTime.now());
        
        // Set rating based on overall rating
        switch (feedback.getOverallRating()) {
            case "STRONGLY_AGREE":
                feedback.setRating(5);
                break;
            case "AGREE":
                feedback.setRating(4);
                break;
            case "NEUTRAL":
                feedback.setRating(3);
                break;
            case "DISAGREE":
                feedback.setRating(2);
                break;
            case "STRONGLY_DISAGREE":
                feedback.setRating(1);
                break;
            default:
                feedback.setRating(0);
        }
        
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByFaculty(Faculty faculty) {
        return feedbackRepository.findByFaculty(faculty);
    }

    public List<Feedback> getFeedbackByStudent(Student student) {
        return feedbackRepository.findByStudent(student);
    }

    public List<Feedback> getFeedbackBySubject(Subject subject) {
        return feedbackRepository.findBySubject(subject);
    }

    public List<Feedback> getFeedbackByFacultyAndSubject(Faculty faculty, Subject subject) {
        return feedbackRepository.findByFacultyAndSubject(faculty, subject);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public boolean hasSubmittedFeedback(Student student, Subject subject) {
        List<Feedback> feedbacks = feedbackRepository.findByStudentAndSubject(student, subject);
        if (feedbacks.isEmpty()) {
            return false;
        }
        
        // Get the most recent feedback
        Feedback latestFeedback = feedbacks.stream()
            .max((f1, f2) -> f1.getSubmissionDate().compareTo(f2.getSubmissionDate()))
            .orElse(null);
            
        if (latestFeedback == null) {
            return false;
        }
        
        // Check if the latest feedback was submitted within the last 5 minutes
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return latestFeedback.getSubmissionDate().isAfter(fiveMinutesAgo);
    }

    public Map<String, Map<String, Long>> getFeedbackMetricsByFaculty(Faculty faculty) {
        List<Feedback> feedbacks = feedbackRepository.findByFaculty(faculty);
        
        Map<String, Long> overallRatings = feedbacks.stream()
            .filter(f -> f.getOverallRating() != null)
            .collect(Collectors.groupingBy(
                Feedback::getOverallRating,
                Collectors.counting()
            ));

        Map<String, Map<String, Long>> result = new HashMap<>();
        result.put("overall", overallRatings);
        
        return result;
    }

    public Map<String, Map<String, Long>> getDetailedMetricsByFaculty(Faculty faculty) {
        List<Feedback> feedbacks = feedbackRepository.findByFaculty(faculty);
        
        Map<String, Map<String, Long>> metrics = new HashMap<>();
        
        // Punctuality
        metrics.put("punctuality", feedbacks.stream()
            .filter(f -> f.getPunctuality() != null)
            .collect(Collectors.groupingBy(
                Feedback::getPunctuality,
                Collectors.counting()
            )));
            
        // Class Participation
        metrics.put("classParticipation", feedbacks.stream()
            .filter(f -> f.getClassParticipation() != null)
            .collect(Collectors.groupingBy(
                Feedback::getClassParticipation,
                Collectors.counting()
            )));
            
        // Concept Delivery
        metrics.put("conceptDelivery", feedbacks.stream()
            .filter(f -> f.getConceptDelivery() != null)
            .collect(Collectors.groupingBy(
                Feedback::getConceptDelivery,
                Collectors.counting()
            )));
            
        // Fair Evaluation
        metrics.put("fairEvaluation", feedbacks.stream()
            .filter(f -> f.getFairEvaluation() != null)
            .collect(Collectors.groupingBy(
                Feedback::getFairEvaluation,
                Collectors.counting()
            )));
            
        // Social Issues
        metrics.put("socialIssues", feedbacks.stream()
            .filter(f -> f.getSocialIssues() != null)
            .collect(Collectors.groupingBy(
                Feedback::getSocialIssues,
                Collectors.counting()
            )));
            
        // Communication
        metrics.put("communication", feedbacks.stream()
            .filter(f -> f.getCommunication() != null)
            .collect(Collectors.groupingBy(
                Feedback::getCommunication,
                Collectors.counting()
            )));
            
        // Teaching Methodology
        metrics.put("teachingMethodology", feedbacks.stream()
            .filter(f -> f.getTeachingMethodology() != null)
            .collect(Collectors.groupingBy(
                Feedback::getTeachingMethodology,
                Collectors.counting()
            )));
            
        // Subject Knowledge
        metrics.put("subjectKnowledge", feedbacks.stream()
            .filter(f -> f.getSubjectKnowledge() != null)
            .collect(Collectors.groupingBy(
                Feedback::getSubjectKnowledge,
                Collectors.counting()
            )));
            
        // Student Support
        metrics.put("studentSupport", feedbacks.stream()
            .filter(f -> f.getStudentSupport() != null)
            .collect(Collectors.groupingBy(
                Feedback::getStudentSupport,
                Collectors.counting()
            )));
            
        return metrics;
    }

    public List<Feedback> getFeedbackByStudentAndSubject(Student student, Subject subject) {
        return feedbackRepository.findByStudentAndSubject(student, subject);
    }

    public Feedback getFeedbackByStudentFacultyAndSubject(Student student, Faculty faculty, Subject subject) {
        return feedbackRepository.findByStudentAndFacultyAndSubject(student, faculty, subject);
    }

    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
} 