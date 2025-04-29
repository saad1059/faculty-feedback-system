package com.ffms.controller;

import com.ffms.model.Faculty;
import com.ffms.model.Student;
import com.ffms.model.Subject;
import com.ffms.model.Feedback;
import com.ffms.service.FacultyService;
import com.ffms.service.StudentService;
import com.ffms.service.SubjectService;
import com.ffms.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/enroll")
    public String enrollSubjects(@RequestParam Map<String, String> subjects,
                               @RequestParam(required = false) Map<String, String> facultySelections,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);

        try {
            // If no subjects selected, return with error
            if (subjects == null || subjects.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a subject to enroll");
                return "redirect:/student/enroll";
            }

            // Process the selected subject
            for (Map.Entry<String, String> entry : subjects.entrySet()) {
                String subjectIdStr = entry.getValue(); // The value is the subject ID
                if (subjectIdStr == null || subjectIdStr.isEmpty()) {
                    continue; // Skip if no subject ID
                }

                Long subjectId = Long.parseLong(subjectIdStr);
                Subject subject = subjectService.getSubjectById(subjectId);
                
                // Check if student is already enrolled in this subject
                if (student.getSubjects().contains(subject)) {
                    redirectAttributes.addFlashAttribute("error", "Already enrolled in subject: " + subject.getSubjectName());
                    return "redirect:/student/enroll";
                }
                
                // Get faculty selection for this subject if any
                Faculty faculty = null;
                if (facultySelections != null) {
                    String facultyIdStr = facultySelections.get("faculty_" + subjectId);
                    if (facultyIdStr != null && !facultyIdStr.isEmpty()) {
                        Long facultyId = Long.parseLong(facultyIdStr);
                        faculty = facultyService.getFacultyById(facultyId);
                        
                        // Check if the faculty is assigned to this subject
                        if (!subject.getFaculties().contains(faculty)) {
                            redirectAttributes.addFlashAttribute("error", "Invalid faculty selection for subject: " + subject.getSubjectName());
                            return "redirect:/student/enroll";
                        }
                    }
                }
                
                // Enroll student in the subject with optional faculty
                studentService.enrollStudentInSubject(student, subject, faculty);
                
                // Return success message for the enrolled subject
                redirectAttributes.addFlashAttribute("success", "Successfully enrolled in " + subject.getSubjectName());
                return "redirect:/student/dashboard";
            }
            
            return "redirect:/student/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during enrollment: " + e.getMessage());
            return "redirect:/student/enroll";
        }
    }

    @PostMapping("/feedback/student/submit")
    public String provideFeedback(@ModelAttribute Feedback feedback,
                                @RequestParam("faculty.id") Long facultyId,
                                @RequestParam("subject.id") Long subjectId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        Subject subject = subjectService.getSubjectById(subjectId);
        Faculty faculty = facultyService.getFacultyById(facultyId);

        try {
            feedback.setStudent(student);
            feedback.setSubject(subject);
            feedback.setFaculty(faculty);
            feedback.setSelectedFaculty(faculty);
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

            // Save feedback
            feedbackService.submitFeedback(feedback);
            
            redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting feedback: " + e.getMessage());
        }

        return "redirect:/student/dashboard";
    }

    @GetMapping("/feedback")
    public String showFeedbackPage(HttpSession session, Model model) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        
        // Get all subjects the student is enrolled in and convert to List
        List<Subject> enrolledSubjects = new ArrayList<>(student.getSubjects());
        
        model.addAttribute("student", student);
        model.addAttribute("enrolledSubjects", enrolledSubjects);
        
        return "student/feedback";
    }

    @GetMapping("/give-feedback")
    public String showFeedbackForm(@RequestParam Long subjectId,
                                 @RequestParam Long facultyId,
                                 HttpSession session,
                                 Model model) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }

        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        Subject subject = subjectService.getSubjectById(subjectId);
        Faculty faculty = facultyService.getFacultyById(facultyId);

        // Check if student is enrolled in the subject
        if (!student.getSubjects().contains(subject)) {
            return "redirect:/student/dashboard";
        }

        // Check if faculty is assigned to the subject
        if (!subject.getFaculties().contains(faculty)) {
            return "redirect:/student/dashboard";
        }

        // Check if feedback already exists
        if (feedbackService.hasSubmittedFeedback(student, subject)) {
            return "redirect:/student/dashboard";
        }

        model.addAttribute("student", student);
        model.addAttribute("subject", subject);
        model.addAttribute("faculty", faculty);
        model.addAttribute("feedback", new Feedback());

        return "student/feedback_form";
    }
} 