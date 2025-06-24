package com.data.project.controller.admin;

import com.data.project.dto.PasswordChangeForm;
import com.data.project.dto.admin.CandidateDTO;
import com.data.project.entity.Auth;
import com.data.project.entity.Candidate;
import com.data.project.entity.Technology;
import com.data.project.service.AuthService;
import com.data.project.service.admin.CandidateService;
import com.data.project.service.admin.TechnologyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CandidateController {
    private Auth getCurrentUserFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authId".equals(cookie.getName())) {
                    try {
                        Long userId = Long.valueOf(cookie.getValue());
                        Auth auth = authService.findById(userId);
                        return (auth != null && auth.isStatus()) ? auth : null;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    private CandidateDTO convertToDTO(Candidate candidate) {
        CandidateDTO dto = new CandidateDTO();
        dto.setId(candidate.getId());
        dto.setName(candidate.getName());
        dto.setEmail(candidate.getEmail());
        dto.setPhone(candidate.getPhone());
        dto.setGender(candidate.isGender());
        dto.setExperience(candidate.getExperience());
        dto.setStatus(candidate.isStatus());
        dto.setDescription(candidate.getDescription());
        dto.setDob(candidate.getDob());
        dto.setTechnologies(null);

        return dto;
    }
    private final ModelMapper modelMapper;
    private final CandidateService candidateService;
    private final TechnologyService technologyService;
    private final AuthService authService;

    public CandidateController(CandidateService candidateService, ModelMapper modelMapper, TechnologyService technologyService, AuthService authService) {
        this.candidateService = candidateService;
        this.modelMapper = modelMapper;
        this.technologyService = technologyService;
        this.authService = authService;
    }

    @GetMapping("/admin/candidate-management")
    public String candidateManagement(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String technologys,
            Model model) {

        if (page < 1) page = 1;

        List<Candidate> candidates;
        long totalElements;

        Map<String, Object> filters = new HashMap<>();
        if (search != null && !search.trim().isEmpty()) {
            filters.put("search", search.trim());
        }
        if (gender != null && !gender.isEmpty() && !gender.equals("all")) {
            filters.put("gender", Boolean.valueOf(gender));
        }
        if (experience != null && !experience.isEmpty() && !experience.equals("all")) {
            filters.put("experience", experience);
        }
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            filters.put("status", Boolean.valueOf(status));
        }
        if (technologys != null && !technologys.isEmpty() && !technologys.equals("all")) {
            filters.put("technologys", Long.valueOf(technologys));
        }

        if (!filters.isEmpty()) {
            candidates = candidateService.findWithFilters(filters, page, size);
            totalElements = candidateService.countWithFilters(filters);
        } else {
            candidates = candidateService.findAll(page, size);
            totalElements = candidateService.countAll();
        }

        List<CandidateDTO> candidateDTOs = candidates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Đảm bảo page không vượt quá totalPages
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        // Thêm danh sách technologies vào model
        List<Technology> technologies = technologyService.findAll(0, 1000);
        model.addAttribute("technologys", technologies);
        model.addAttribute("selectedTechnology", technologys);

        model.addAttribute("candidates", candidateDTOs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalElements", totalElements);

        // Giữ lại các tham số filter
        model.addAttribute("search", search);
        model.addAttribute("gender", gender);
        model.addAttribute("experience", experience);
        model.addAttribute("status", status);

        return "/admin/candidate-management";
    }

    @PostMapping("/admin/candidate/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleCandidateStatus(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Candidate candidate = candidateService.findById(id);
            if (candidate == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy ứng viên");
                return ResponseEntity.badRequest().body(response);
            }

            boolean newStatus = !candidate.isStatus();
            candidateService.updateStatus(id, newStatus);

            response.put("success", true);
            response.put("newStatus", newStatus);
            response.put("message", newStatus ? "Đã kích hoạt tài khoản" : "Đã khóa tài khoản");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @GetMapping("/candidate/profile/{id}")
    public String candidateProfile(@PathVariable Long id,
                                   @RequestParam(value = "showPasswordForm", required = false) Boolean showPasswordForm,
                                   Model model,
                                   HttpServletRequest request) {
        Auth currentUser = getCurrentUserFromCookie(request);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Candidate candidate = candidateService.findById(id);
        if (candidate == null || !candidate.getEmail().equals(currentUser.getEmail())) {
            return "redirect:/home";
        }

        // Lấy danh sách tất cả technologies để hiển thị
        List<Technology> allTechnologies = technologyService.findAll(0, 100);

        model.addAttribute("candidate", candidate);
        model.addAttribute("candidateDTO", convertToDTO(candidate));
        model.addAttribute("passwordForm", new PasswordChangeForm());
        model.addAttribute("allTechnologies", allTechnologies);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("showEditForm", false);
        model.addAttribute("showPasswordForm", showPasswordForm != null && showPasswordForm);

        return "profile";
    }

    @GetMapping("/candidate/profile/{id}/edit")
    public String showEditProfile(@PathVariable Long id, Model model, HttpServletRequest request) {
        Auth currentUser = getCurrentUserFromCookie(request);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Candidate candidate = candidateService.findById(id);
        if (candidate == null || !candidate.getEmail().equals(currentUser.getEmail())) {
            return "redirect:/home";
        }

        // Lấy danh sách tất cả technologies
        List<Technology> allTechnologies = technologyService.findAll(0, 100);

        model.addAttribute("candidate", candidate);
        model.addAttribute("candidateDTO", convertToDTO(candidate));
        model.addAttribute("passwordForm", new PasswordChangeForm());
        model.addAttribute("allTechnologies", allTechnologies);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("showEditForm", true);
        model.addAttribute("showPasswordForm", false);

        return "profile";
    }


    @PostMapping("/candidate/profile/{id}/change-password")
    public String changePassword(@PathVariable Long id,
                                 @Valid @ModelAttribute("passwordForm") PasswordChangeForm passwordForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {

        Auth currentUser = getCurrentUserFromCookie(request);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Candidate candidate = candidateService.findById(id);
        if (candidate == null || !candidate.getEmail().equals(currentUser.getEmail())) {
            return "redirect:/home";
        }

        // Lấy thông tin Auth của user hiện tại
        Auth auth = authService.getAuthByEmail(candidate.getEmail());
        if (auth == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin tài khoản!");
            return "redirect:/candidate/profile/" + id;
        }

        // Kiểm tra mật khẩu cũ
        if (!authService.checkPassword(passwordForm.getOldPassword(), auth.getPassword())) {
            bindingResult.rejectValue("oldPassword", "error.oldPassword", "Mật khẩu cũ không đúng");
        }

        // Kiểm tra mật khẩu xác nhận
        if (!passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra mật khẩu mới không trùng với mật khẩu cũ
        if (passwordForm.getOldPassword().equals(passwordForm.getNewPassword())) {
            bindingResult.rejectValue("newPassword", "error.newPassword", "Mật khẩu mới phải khác mật khẩu cũ");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("candidate", candidate);
            model.addAttribute("candidateDTO", convertToDTO(candidate));
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("showEditForm", false);
            model.addAttribute("showPasswordForm", true);
            return "profile";
        }

        try {
            // Đổi mật khẩu
            authService.changePassword(candidate.getEmail(), passwordForm.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/candidate/profile/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/candidate/profile/" + id;
        }
    }

    @PostMapping("/candidate/profile/{id}/update")
    public String updateProfile(@PathVariable Long id,
                                @Valid @ModelAttribute("candidateDTO") CandidateDTO candidateDTO,
                                @RequestParam(value = "technologyIds", required = false) List<Long> technologyIds,
                                BindingResult bindingResult,
                                Model model,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {

        Auth currentUser = getCurrentUserFromCookie(request);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Candidate candidate = candidateService.findById(id);
        if (candidate == null || !candidate.getEmail().equals(currentUser.getEmail())) {
            return "redirect:/home";
        }

        // Kiểm tra email trùng lặp
        if (candidateService.existsByEmailAndIdNot(candidateDTO.getEmail(), id)) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            List<Technology> allTechnologies = technologyService.findAll(0, 100);
            model.addAttribute("candidate", candidate);
            model.addAttribute("passwordForm", new PasswordChangeForm());
            model.addAttribute("allTechnologies", allTechnologies);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("showEditForm", true);
            model.addAttribute("showPasswordForm", false);
            return "profile";
        }

        try {
            // Cập nhật thông tin cơ bản
            candidate.setName(candidateDTO.getName());
            candidate.setEmail(candidateDTO.getEmail());
            candidate.setPhone(candidateDTO.getPhone());
            candidate.setGender(candidateDTO.getGender());
            candidate.setExperience(candidateDTO.getExperience());
            candidate.setDescription(candidateDTO.getDescription());
            candidate.setDob(candidateDTO.getDob());

            // Cập nhật technologies
            if (technologyIds != null && !technologyIds.isEmpty()) {
                Set<Technology> selectedTechnologies = new HashSet<>();
                for (Long techId : technologyIds) {
                    Technology tech = technologyService.findById(techId);
                    if (tech != null) {
                        selectedTechnologies.add(tech);
                    }
                }
                candidate.setTechnologies(selectedTechnologies);
            } else {
                candidate.setTechnologies(new HashSet<>());
            }

            candidateService.save(candidate);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/candidate/profile/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/candidate/profile/" + id + "/edit";
        }
    }
}
