package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.service.SubjectService;
import com.ffms.service.StudentService;
import com.ffms.exception.RegistrationException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentEnrollmentController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/enroll")
    public String showEnrollmentPage(Model model, HttpSession session) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }
        Long studentId = (Long) session.getAttribute("studentId");
        model.addAttribute("availableSubjects", subjectService.getAvailableSubjectsForStudent(studentId));
        model.addAttribute("enrolledSubjects", subjectService.getSubjectsByStudent(studentId));
        return "student/enroll";
    }

    @PostMapping("/unenroll/{subjectId}")
    public String unenrollFromSubject(@PathVariable Long subjectId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }
        Long studentId = (Long) session.getAttribute("studentId");
        try {
            subjectService.removeStudentFromSubject(subjectId, studentId);
            redirectAttributes.addFlashAttribute("success", "Successfully unenrolled from the course");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/enroll";
    }
} 