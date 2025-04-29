package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.service.StudentService;
import com.ffms.service.SubjectService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String listStudents(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("student", studentService.getStudentById(id));
        return "admin/students/view";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id, 
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }
} 