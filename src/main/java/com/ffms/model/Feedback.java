package com.ffms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "selected_faculty_id")
    private Faculty selectedFaculty;

    private LocalDateTime submissionDate;

    // Feedback metrics
    private String punctuality;
    private String classParticipation;
    private String conceptDelivery;
    private String fairEvaluation;
    private String socialIssues;
    private String communication;
    private String teachingMethodology;
    private String subjectKnowledge;
    private String studentSupport;
    private String overallRating;
    private int rating;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Faculty getSelectedFaculty() {
        return selectedFaculty;
    }

    public void setSelectedFaculty(Faculty selectedFaculty) {
        this.selectedFaculty = selectedFaculty;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(String punctuality) {
        this.punctuality = punctuality;
    }

    public String getClassParticipation() {
        return classParticipation;
    }

    public void setClassParticipation(String classParticipation) {
        this.classParticipation = classParticipation;
    }

    public String getConceptDelivery() {
        return conceptDelivery;
    }

    public void setConceptDelivery(String conceptDelivery) {
        this.conceptDelivery = conceptDelivery;
    }

    public String getFairEvaluation() {
        return fairEvaluation;
    }

    public void setFairEvaluation(String fairEvaluation) {
        this.fairEvaluation = fairEvaluation;
    }

    public String getSocialIssues() {
        return socialIssues;
    }

    public void setSocialIssues(String socialIssues) {
        this.socialIssues = socialIssues;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    public String getTeachingMethodology() {
        return teachingMethodology;
    }

    public void setTeachingMethodology(String teachingMethodology) {
        if (teachingMethodology == null || teachingMethodology.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback cannot be empty");
        }
        this.teachingMethodology = teachingMethodology;
    }

    public String getSubjectKnowledge() {
        return subjectKnowledge;
    }

    public void setSubjectKnowledge(String subjectKnowledge) {
        if (subjectKnowledge == null || subjectKnowledge.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback cannot be empty");
        }
        this.subjectKnowledge = subjectKnowledge;
    }

    public String getStudentSupport() {
        return studentSupport;
    }

    public void setStudentSupport(String studentSupport) {
        if (studentSupport == null || studentSupport.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback cannot be empty");
        }
        this.studentSupport = studentSupport;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Invalid rating: Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
}
