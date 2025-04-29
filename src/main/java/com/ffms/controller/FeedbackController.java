package com.ffms.controller;

import com.ffms.model.Faculty;
import com.ffms.model.Feedback;
import com.ffms.model.Student;
import com.ffms.model.Subject;
import com.ffms.service.FacultyService;
import com.ffms.service.FeedbackService;
import com.ffms.service.StudentService;
import com.ffms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    // Student feedback form
    @GetMapping("/student/form/{facultyId}/{subjectId}")
    public String showFeedbackForm(@PathVariable Long facultyId, 
                                 @PathVariable Long subjectId,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        Faculty faculty = facultyService.getFacultyById(facultyId);
        Subject subject = subjectService.getSubjectById(subjectId);

        // Check if student has already submitted feedback for this faculty and subject
        if (feedbackService.hasSubmittedFeedback(student, subject)) {
            redirectAttributes.addFlashAttribute("error", "You have already submitted feedback for this subject in the last 5 minutes. Please try again later.");
            return "redirect:/student/dashboard";
        }

        // Check if student is enrolled in the subject
        if (!student.getSubjects().contains(subject)) {
            redirectAttributes.addFlashAttribute("error", "You are not enrolled in this subject.");
            return "redirect:/student/dashboard";
        }

        // Check if faculty is assigned to the subject
        if (!subject.getFaculties().contains(faculty)) {
            redirectAttributes.addFlashAttribute("error", "This faculty is not assigned to this subject.");
            return "redirect:/student/dashboard";
        }

        model.addAttribute("student", student);
        model.addAttribute("faculty", faculty);
        model.addAttribute("subject", subject);
        model.addAttribute("feedback", new Feedback());

        return "student/feedback_form";
    }

    // Submit feedback
    @PostMapping("/student/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback,
                               @RequestParam("faculty.id") Long facultyId,
                               @RequestParam("subject.id") Long subjectId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        Faculty faculty = facultyService.getFacultyById(facultyId);
        Subject subject = subjectService.getSubjectById(subjectId);

        // Check if student has already submitted feedback for this faculty and subject
        if (feedbackService.hasSubmittedFeedback(student, subject)) {
            redirectAttributes.addFlashAttribute("error", "You have already submitted feedback for this subject in the last 5 minutes. Please try again later.");
            return "redirect:/student/dashboard";
        }

        // Check if student is enrolled in the subject
        if (!student.getSubjects().contains(subject)) {
            redirectAttributes.addFlashAttribute("error", "You are not enrolled in this subject.");
            return "redirect:/student/dashboard";
        }

        // Check if faculty is assigned to the subject
        if (!subject.getFaculties().contains(faculty)) {
            redirectAttributes.addFlashAttribute("error", "This faculty is not assigned to this subject.");
            return "redirect:/student/dashboard";
        }

        feedback.setStudent(student);
        feedback.setFaculty(faculty);
        feedback.setSubject(subject);
        feedback.setSubmissionDate(LocalDateTime.now());

        feedbackService.submitFeedback(feedback);
        redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully!");
        return "redirect:/student/dashboard";
    }

    // Faculty view all feedback
    @GetMapping("/faculty/view")
    public String viewFeedback(HttpSession session, Model model) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }

        Long facultyId = (Long) session.getAttribute("facultyId");
        Faculty faculty = facultyService.getFacultyById(facultyId);
        if (faculty == null) {
            return "redirect:/faculty/login";
        }

        // Get all feedback for this faculty
        List<Feedback> feedbacks = feedbackService.getFeedbackByFaculty(faculty);
        
        // Calculate metrics
        Map<String, Map<String, Long>> feedbackMetrics = feedbackService.getFeedbackMetricsByFaculty(faculty);
        Map<String, Map<String, Long>> detailedMetrics = feedbackService.getDetailedMetricsByFaculty(faculty);
        
        // Calculate subject-wise metrics with subject IDs
        Map<String, Map<String, Object>> subjectWiseMetrics = new HashMap<>();
        for (Subject subject : faculty.getSubjects()) {
            List<Feedback> subjectFeedbacks = feedbackService.getFeedbackByFacultyAndSubject(faculty, subject);
            Map<String, Long> subjectRatings = subjectFeedbacks.stream()
                .filter(f -> f.getOverallRating() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getOverallRating,
                    Collectors.counting()
                ));
            
            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("ratings", subjectRatings);
            subjectData.put("id", subject.getId());
            subjectWiseMetrics.put(subject.getSubjectName(), subjectData);
        }

        model.addAttribute("faculty", faculty);
        model.addAttribute("feedbackMetrics", feedbackMetrics != null ? feedbackMetrics.get("overall") : new HashMap<>());
        model.addAttribute("detailedMetrics", detailedMetrics != null ? detailedMetrics : new HashMap<>());
        model.addAttribute("subjectWiseMetrics", subjectWiseMetrics);
        model.addAttribute("totalFeedbacks", feedbacks.size());

        return "faculty/feedback_analysis";
    }

    // Faculty view feedback for specific subject
    @GetMapping("/faculty/feedback/{subjectId}")
    public String viewSubjectFeedback(@PathVariable Long subjectId, 
                                    HttpSession session, 
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (session.getAttribute("facultyId") == null) {
                return "redirect:/faculty/login";
            }

            Long facultyId = (Long) session.getAttribute("facultyId");
            Faculty faculty = facultyService.getFacultyById(facultyId);
            if (faculty == null) {
                return "redirect:/faculty/login";
            }

            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject == null) {
                redirectAttributes.addFlashAttribute("error", "Subject not found");
                return "redirect:/feedback/faculty/view";
            }

            // Verify that the faculty is assigned to this subject
            if (!subject.getFaculties().contains(faculty)) {
                redirectAttributes.addFlashAttribute("error", "You are not assigned to this subject");
                return "redirect:/feedback/faculty/view";
            }

            // Get feedback for this faculty and subject
            List<Feedback> feedbacks = feedbackService.getFeedbackByFacultyAndSubject(faculty, subject);
            
            // Calculate metrics for this subject
            Map<String, Map<String, Long>> feedbackMetrics = new HashMap<>();
            Map<String, Long> overallRatings = feedbacks.stream()
                .filter(f -> f.getOverallRating() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getOverallRating,
                    Collectors.counting()
                ));
            feedbackMetrics.put("overall", overallRatings);

            Map<String, Map<String, Long>> detailedMetrics = new HashMap<>();
            // Punctuality
            detailedMetrics.put("punctuality", feedbacks.stream()
                .filter(f -> f.getPunctuality() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getPunctuality,
                    Collectors.counting()
                )));
            // Class Participation
            detailedMetrics.put("classParticipation", feedbacks.stream()
                .filter(f -> f.getClassParticipation() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getClassParticipation,
                    Collectors.counting()
                )));
            // Concept Delivery
            detailedMetrics.put("conceptDelivery", feedbacks.stream()
                .filter(f -> f.getConceptDelivery() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getConceptDelivery,
                    Collectors.counting()
                )));
            // Fair Evaluation
            detailedMetrics.put("fairEvaluation", feedbacks.stream()
                .filter(f -> f.getFairEvaluation() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getFairEvaluation,
                    Collectors.counting()
                )));
            // Social Issues
            detailedMetrics.put("socialIssues", feedbacks.stream()
                .filter(f -> f.getSocialIssues() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getSocialIssues,
                    Collectors.counting()
                )));
            // Communication
            detailedMetrics.put("communication", feedbacks.stream()
                .filter(f -> f.getCommunication() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getCommunication,
                    Collectors.counting()
                )));
            // Teaching Methodology
            detailedMetrics.put("teachingMethodology", feedbacks.stream()
                .filter(f -> f.getTeachingMethodology() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getTeachingMethodology,
                    Collectors.counting()
                )));
            // Subject Knowledge
            detailedMetrics.put("subjectKnowledge", feedbacks.stream()
                .filter(f -> f.getSubjectKnowledge() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getSubjectKnowledge,
                    Collectors.counting()
                )));
            // Student Support
            detailedMetrics.put("studentSupport", feedbacks.stream()
                .filter(f -> f.getStudentSupport() != null)
                .collect(Collectors.groupingBy(
                    Feedback::getStudentSupport,
                    Collectors.counting()
                )));

            model.addAttribute("faculty", faculty);
            model.addAttribute("subject", subject);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("feedbackMetrics", feedbackMetrics.get("overall"));
            model.addAttribute("detailedMetrics", detailedMetrics);
            model.addAttribute("totalFeedbacks", feedbacks.size());

            return "faculty/subject_feedback";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error viewing feedback: " + e.getMessage());
            return "redirect:/feedback/faculty/view";
        }
    }

    // Admin view all feedback
    @GetMapping("/admin/view")
    public String adminViewFeedback(Model model) {
        model.addAttribute("allFeedback", feedbackService.getAllFeedback());
        return "admin/feedback_list";
    }

    // Student view their feedback
    @GetMapping("/student/view/{facultyId}/{subjectId}")
    public String viewStudentFeedback(@PathVariable Long facultyId, 
                                    @PathVariable Long subjectId,
                                    Model model,
                                    HttpSession session) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            return "redirect:/student/login";
        }

        Faculty faculty = facultyService.getFacultyById(facultyId);
        Subject subject = subjectService.getSubjectById(subjectId);
        
        if (faculty == null || subject == null) {
            return "redirect:/student/dashboard";
        }

        Feedback feedback = feedbackService.getFeedbackByStudentFacultyAndSubject(student, faculty, subject);
        model.addAttribute("feedback", feedback);
        model.addAttribute("faculty", faculty);
        model.addAttribute("subject", subject);
        
        return "student/view_feedback";
    }
} 