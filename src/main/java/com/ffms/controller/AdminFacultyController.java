package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.model.Faculty;
import com.ffms.service.FacultyService;
import com.ffms.service.SubjectService;
import com.ffms.exception.RegistrationException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/faculty")
public class AdminFacultyController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String listFaculty(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("faculties", facultyService.getAllFaculties());
        return "admin/faculty/list";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("faculty", new Faculty());
        return "admin/faculty/register";
    }

    @PostMapping("/register")
    public String registerFaculty(@ModelAttribute Faculty faculty, 
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        try {
            facultyService.registerFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Faculty member added successfully!");
            return "redirect:/admin/faculty";
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/faculty/register";
        }
    }

    @GetMapping("/{id}")
    public String viewFaculty(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("faculty", facultyService.getFacultyById(id));
        return "admin/faculty/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("faculty", facultyService.getFacultyById(id));
        return "admin/faculty/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateFaculty(@PathVariable Long id, 
                              @ModelAttribute Faculty faculty,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        try {
            faculty.setId(id);
            facultyService.updateFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Faculty member updated successfully!");
            return "redirect:/admin/faculty";
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/faculty/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteFaculty(@PathVariable Long id,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        try {
            facultyService.deleteFaculty(id);
            redirectAttributes.addFlashAttribute("success", "Faculty member deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting faculty member: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }
} 