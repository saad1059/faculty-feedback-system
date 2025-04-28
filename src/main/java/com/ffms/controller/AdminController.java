package com.ffms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ffms.model.Admin;
import com.ffms.service.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin_login";
    }

    @PostMapping("/login")
    public String loginAdmin(@ModelAttribute Admin admin, HttpSession session, Model model) {
        Admin loggedInAdmin = adminService.loginAdmin(admin.getUsername(), admin.getPassword());
        if (loggedInAdmin != null) {
            session.setAttribute("adminId", loggedInAdmin.getId());
            session.setAttribute("adminName", loggedInAdmin.getName());
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "admin_login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("adminName", session.getAttribute("adminName"));
        return "admin_dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        Long adminId = (Long) session.getAttribute("adminId");
        Admin admin = adminService.getAdminById(adminId);
        model.addAttribute("admin", admin);
        return "admin_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Admin admin, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        try {
            adminService.updateAdmin(admin);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile");
        }
        return "redirect:/admin/profile";
    }
} 