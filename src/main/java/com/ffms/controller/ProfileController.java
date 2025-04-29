package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.service.FacultyService;
import com.ffms.service.StudentService;
import com.ffms.model.Faculty;
import com.ffms.model.Student;
import com.ffms.exception.RegistrationException;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    // ====================== Faculty Profile ======================

    @GetMapping("/faculty/profile")
    public String showFacultyProfile(Model model, HttpSession session) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }
        Long facultyId = (Long) session.getAttribute("facultyId");
        Faculty faculty = facultyService.getFacultyById(facultyId);
        model.addAttribute("faculty", faculty);
        return "faculty/profile";
    }

    @PostMapping("/faculty/profile")
    public String updateFacultyProfile(@ModelAttribute Faculty faculty, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }
        try {
            faculty.setId((Long) session.getAttribute("facultyId"));
            facultyService.updateFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/faculty/profile";
    }

    // ====================== Student Profile ======================

    @GetMapping("/student/profile")
    public String showStudentProfile(Model model, HttpSession session) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }
        Long studentId = (Long) session.getAttribute("studentId");
        Student student = studentService.getStudentById(studentId);
        model.addAttribute("student", student);
        return "student/profile";
    }

    @PostMapping("/student/profile")
    public String updateStudentProfile(@ModelAttribute Student student, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }
        try {
            student.setId((Long) session.getAttribute("studentId"));
            studentService.updateStudent(student);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/profile";
    }
} 