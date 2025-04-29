package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.model.Subject;
import com.ffms.service.SubjectService;
import com.ffms.service.FacultyService;
import com.ffms.service.StudentService;
import com.ffms.exception.RegistrationException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/subjects")
public class AdminSubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String listSubjects(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "admin/subjects/list";
    }

    @GetMapping("/{id}")
    public String viewSubject(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        model.addAttribute("faculties", subject.getFaculties());
        model.addAttribute("students", subject.getStudents());
        return "admin/subjects/view";
    }

    @GetMapping("/{id}/assign-faculty")
    public String showAssignFacultyForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        model.addAttribute("availableFaculties", facultyService.getAllFaculties());
        return "admin/subjects/assign-faculty";
    }

    @PostMapping("/{id}/assign-faculty")
    public String assignFaculty(@PathVariable Long id, @RequestParam Long facultyId, 
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        try {
            subjectService.assignFacultyToSubject(id, facultyId);
            redirectAttributes.addFlashAttribute("success", "Faculty assigned successfully");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/subjects/" + id;
    }

    @PostMapping("/{id}/remove-faculty")
    public String removeFaculty(@PathVariable Long id, @RequestParam Long facultyId, 
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        try {
            subjectService.removeFacultyFromSubject(id, facultyId);
            redirectAttributes.addFlashAttribute("success", "Faculty removed successfully");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/subjects/" + id;
    }

    @GetMapping("/{id}/enroll-student")
    public String showEnrollStudentForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        model.addAttribute("availableStudents", studentService.getAllStudents());
        return "admin/subjects/enroll-student";
    }

    @PostMapping("/{id}/enroll-student")
    public String enrollStudent(@PathVariable Long id, @RequestParam Long studentId, 
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        try {
            subjectService.enrollStudentInSubject(id, studentId);
            redirectAttributes.addFlashAttribute("success", "Student enrolled successfully");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/subjects/" + id;
    }

    @PostMapping("/{id}/remove-student")
    public String removeStudent(@PathVariable Long id, @RequestParam Long studentId, 
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        try {
            subjectService.removeStudentFromSubject(id, studentId);
            redirectAttributes.addFlashAttribute("success", "Student removed successfully");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/subjects/" + id;
    }
} 