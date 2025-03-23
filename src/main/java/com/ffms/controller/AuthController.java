package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ffms.model.Faculty;
import com.ffms.model.Student;
import com.ffms.repository.FacultyRepository;
import com.ffms.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

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
    public String registerFaculty(@ModelAttribute Faculty faculty) {
        facultyRepository.save(faculty);
        return "redirect:/faculty/login";
    }

    @GetMapping("/faculty/login")
    public String showFacultyLoginForm(Model model) {
        model.addAttribute("faculty", new Faculty());
        return "faculty_login";
    }

    @PostMapping("/faculty/login")
    public String loginFaculty(@ModelAttribute Faculty faculty, HttpSession session, Model model) {
        Faculty registered = facultyRepository.findByEmail(faculty.getEmail());
        if (registered != null && registered.getPassword().equals(faculty.getPassword())) {
            session.setAttribute("facultyName", registered.getName());
            return "redirect:/faculty/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "faculty_login";
        }
    }

    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(HttpSession session, Model model) {
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
    public String registerStudent(@ModelAttribute Student student) {
        studentRepository.save(student);
        return "redirect:/student/login";
    }

    @GetMapping("/student/login")
    public String showStudentLoginForm(Model model) {
        model.addAttribute("student", new Student());
        return "student_login";
    }

    @PostMapping("/student/login")
    public String loginStudent(@ModelAttribute Student student, HttpSession session, Model model) {
        Student registered = studentRepository.findByEmail(student.getEmail());
        if (registered != null && registered.getPassword().equals(student.getPassword())) {
            session.setAttribute("studentName", registered.getName());
            return "redirect:/student/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "student_login";
        }
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        model.addAttribute("studentName", session.getAttribute("studentName"));
        return "student_dashboard";
    }
}
