package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.model.Faculty;
import com.ffms.model.Student;
import com.ffms.service.FacultyService;
import com.ffms.service.StudentService;
import com.ffms.exception.RegistrationException;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ====================== Faculty ======================

    @GetMapping("/faculty/register")
    public String showFacultyRegistrationForm(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "faculty_register";
    }

    @PostMapping("/faculty/register")
    public String registerFaculty(@ModelAttribute Faculty faculty, RedirectAttributes redirectAttributes) {
        try {
            facultyService.registerFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/faculty/login";
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/faculty/register";
        }
    }

    @GetMapping("/faculty/login")
    public String showFacultyLoginForm(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "faculty_login";
    }

    @PostMapping("/faculty/login")
    public String loginFaculty(@ModelAttribute Faculty faculty, HttpSession session, Model model) {
        Faculty loggedInFaculty = facultyService.loginFaculty(faculty.getEmail(), faculty.getPassword());
        if (loggedInFaculty != null) {
            session.setAttribute("facultyId", loggedInFaculty.getId());
            session.setAttribute("facultyName", loggedInFaculty.getName());
            return "redirect:/faculty/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "faculty_login";
        }
    }

    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(HttpSession session, Model model) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login";
        }
        model.addAttribute("facultyName", session.getAttribute("facultyName"));
        return "faculty_dashboard";
    }

    // ====================== Student ======================

    @GetMapping("/student/register")
    public String showStudentRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "student_register";
    }

    @PostMapping("/student/register")
    public String registerStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        try {
            studentService.registerStudent(student);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/student/login";
        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/student/register";
        }
    }

    @GetMapping("/student/login")
    public String showStudentLoginForm(Model model) {
        model.addAttribute("student", new Student());
        return "student_login";
    }

    @PostMapping("/student/login")
    public String loginStudent(@ModelAttribute Student student, HttpSession session, Model model) {
        Student loggedInStudent = studentService.loginStudent(student.getEmail(), student.getPassword());
        if (loggedInStudent != null) {
            session.setAttribute("studentId", loggedInStudent.getId());
            session.setAttribute("studentName", loggedInStudent.getName());
            return "redirect:/student/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "student_login";
        }
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student/login";
        }
        model.addAttribute("studentName", session.getAttribute("studentName"));
        return "student_dashboard";
    }

    // ====================== Logout ======================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
