package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.service.SubjectService;
import com.ffms.service.FacultyService;
import com.ffms.exception.RegistrationException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/faculty")
public class FacultyCourseController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private FacultyService facultyService;

    @GetMapping("/courses")
    public String showCoursesPage(Model model, HttpSession session) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }
        Long facultyId = (Long) session.getAttribute("facultyId");
        model.addAttribute("teachingSubjects", subjectService.getSubjectsByFaculty(facultyId));
        model.addAttribute("availableSubjects", subjectService.getAvailableSubjectsForFaculty(facultyId));
        return "faculty/courses";
    }

    @PostMapping("/courses/assign/{subjectId}")
    public String assignSubject(@PathVariable Long subjectId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }
        Long facultyId = (Long) session.getAttribute("facultyId");
        try {
            subjectService.assignFacultyToSubject(subjectId, facultyId);
            redirectAttributes.addFlashAttribute("success", "Successfully assigned to the course");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/faculty/courses";
    }

    @PostMapping("/courses/unassign/{subjectId}")
    public String unassignSubject(@PathVariable Long subjectId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }
        Long facultyId = (Long) session.getAttribute("facultyId");
        try {
            subjectService.removeFacultyFromSubject(subjectId, facultyId);
            redirectAttributes.addFlashAttribute("success", "Successfully unassigned from the course");
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/faculty/courses";
    }
} 