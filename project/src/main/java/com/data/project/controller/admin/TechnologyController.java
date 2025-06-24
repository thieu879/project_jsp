package com.data.project.controller.admin;

import com.data.project.dto.admin.TechnologyDTO;
import com.data.project.entity.Auth;
import com.data.project.entity.Technology;
import com.data.project.service.admin.TechnologyService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class TechnologyController {
    private final ModelMapper modelMapper;
    private final TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService, ModelMapper modelMapper) {
        this.technologyService = technologyService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/admin/technology-management")
    public String technologyManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
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
                model.addAttribute("technologyDto", new TechnologyDTO());
                model.addAttribute("auth", admin);
                return "/admin/technology-add";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/admin/technology/save")
    public String saveTechnology(@Valid @ModelAttribute("technologyDto") TechnologyDTO technologyDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpServletRequest request) {

        // Kiểm tra trùng tên
        if (technologyDto.getName() != null && !technologyDto.getName().trim().isEmpty()) {
            if (technologyService.existsByName(technologyDto.getName().trim())) {
                bindingResult.rejectValue("name", "duplicate.name", "Tên công nghệ đã tồn tại trong hệ thống");
            }
        }

        if (bindingResult.hasErrors()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Auth admin = (Auth) session.getAttribute("admin");
                if (admin != null && admin.isRole()) {
                    model.addAttribute("auth", admin);
                    return "/admin/technology-add";
                }
            }
            return "redirect:/login";
        }

        try {
            Technology technology = modelMapper.map(technologyDto, Technology.class);
            technology.setStatus(true);
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
                    TechnologyDTO technologyDto = modelMapper.map(technology, TechnologyDTO.class);
                    model.addAttribute("technologyDto", technologyDto);
                    model.addAttribute("auth", admin);
                    return "/admin/technology-edit";
                }
            }
        }
        return "redirect:/admin/technology-management";
    }

    @PostMapping("/admin/technology/update")
    public String updateTechnology(@Valid @ModelAttribute("technologyDto") TechnologyDTO technologyDto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model,
                                   HttpServletRequest request) {

        // Kiểm tra trùng tên khi cập nhật
        if (technologyDto.getName() != null && !technologyDto.getName().trim().isEmpty() && technologyDto.getId() != null) {
            if (technologyService.existsByNameAndIdNot(technologyDto.getName().trim(), technologyDto.getId())) {
                bindingResult.rejectValue("name", "duplicate.name", "Tên công nghệ đã tồn tại trong hệ thống");
            }
        }

        if (bindingResult.hasErrors()) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Auth admin = (Auth) session.getAttribute("admin");
                if (admin != null && admin.isRole()) {
                    model.addAttribute("auth", admin);
                    return "/admin/technology-edit";
                }
            }
            return "redirect:/login";
        }

        try {
            Technology technology = modelMapper.map(technologyDto, Technology.class);
            technology.setStatus(true);
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
            technologyService.updateStatus(id, false);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa công nghệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/technology-management";
    }
}
