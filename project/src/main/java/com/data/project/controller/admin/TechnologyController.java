package com.data.project.controller.admin;

import com.data.project.entity.Auth;
import com.data.project.entity.Technology;
import com.data.project.service.admin.TechnologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class TechnologyController {

    @Autowired
    private TechnologyService technologyService;

    @GetMapping("/admin/technology-management")
    public String technologyManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            Auth admin = (Auth) session.getAttribute("admin");
            if (admin != null && admin.isRole()) {
                List<Technology> technologies;
                long totalElements;

                if (search != null && !search.trim().isEmpty()) {
                    technologies = technologyService.searchByName(search.trim(), page, size);
                    totalElements = technologyService.countByName(search.trim());
                    model.addAttribute("searchKeyword", search);
                } else {
                    technologies = technologyService.findAll(page, size);
                    totalElements = technologyService.countAll();
                }

                int totalPages = (int) Math.ceil((double) totalElements / size);

                model.addAttribute("technologies", technologies);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("pageSize", size);
                model.addAttribute("auth", admin);

                return "/admin/technology-management";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/admin/technology/add")
    public String showAddForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Auth admin = (Auth) session.getAttribute("admin");
            if (admin != null && admin.isRole()) {
                model.addAttribute("technology", new Technology());
                model.addAttribute("auth", admin);
                return "/admin/technology-add";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/admin/technology/save")
    public String saveTechnology(@ModelAttribute Technology technology,
                                 RedirectAttributes redirectAttributes) {
        try {
            technologyService.save(technology);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm công nghệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/technology-management";
    }

    @GetMapping("/admin/technology/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Auth admin = (Auth) session.getAttribute("admin");
            if (admin != null && admin.isRole()) {
                Technology technology = technologyService.findById(id);
                if (technology != null) {
                    model.addAttribute("technology", technology);
                    model.addAttribute("auth", admin);
                    return "/admin/technology-edit";
                }
            }
        }
        return "redirect:/admin/technology-management";
    }

    @PostMapping("/admin/technology/update")
    public String updateTechnology(@ModelAttribute Technology technology,
                                   RedirectAttributes redirectAttributes) {
        try {
            technologyService.updateTechnology(technology);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật công nghệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/technology-management";
    }

    @GetMapping("/admin/technology/delete/{id}")
    public String deleteTechnology(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            technologyService.updateStatus(id, false); // Soft delete
            redirectAttributes.addFlashAttribute("successMessage", "Xóa công nghệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/technology-management";
    }
}

