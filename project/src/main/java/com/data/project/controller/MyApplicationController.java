package com.data.project.controller;

import com.data.project.entity.*;
import com.data.project.service.AuthService;
import com.data.project.service.admin.ApplicationService;
import com.data.project.service.admin.CandidateService;
import com.data.project.service.admin.RecruitmentPositionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MyApplicationController {

    private final ApplicationService applicationService;
    private final CandidateService candidateService;
    private final RecruitmentPositionService recruitmentPositionService;
    private final AuthService authService;
    private final int ROW_PER_PAGE = 2;

    public MyApplicationController(ApplicationService applicationService,
                                   CandidateService candidateService,
                                   RecruitmentPositionService recruitmentPositionService,
                                   AuthService authService) {
        this.applicationService = applicationService;
        this.candidateService = candidateService;
        this.recruitmentPositionService = recruitmentPositionService;
        this.authService = authService;
    }

    @GetMapping("/applications/my-applications/{id}")
    public String myApplications(@PathVariable Long id,
                                 @RequestParam(value = "page", defaultValue = "1") int pageNumber,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String status,
                                 Model model,
                                 HttpServletRequest request) {
        try {
            // Kiểm tra xác thực
            if (!isValidUser(id, request)) {
                model.addAttribute("errorMessage", "Bạn không có quyền truy cập!");
                return "redirect:/login";
            }

            // Kiểm tra candidate có tồn tại không
            Candidate candidate = candidateService.findById(id);
            if (candidate == null) {
                model.addAttribute("errorMessage", "Không tìm thấy ứng viên!");
                return "redirect:/home";
            }

            List<Application> allApplications;

            // Lấy tất cả applications trước
            if (search != null && !search.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
                Progress progressEnum = Progress.valueOf(status);
                allApplications = applicationService.searchByNameAndStatus(search, progressEnum, 0, 1000);
            } else if (search != null && !search.trim().isEmpty()) {
                allApplications = applicationService.searchByName(search, 0, 1000);
            } else if (status != null && !status.trim().isEmpty()) {
                Progress progressEnum = Progress.valueOf(status);
                allApplications = applicationService.findByStatus(progressEnum, 0, 1000);
            } else {
                allApplications = applicationService.findAllApplications(0, 1000);
            }

            // Lọc chỉ lấy applications của candidate hiện tại
            List<Application> filteredApplications = allApplications.stream()
                    .filter(app -> app.getCandidateId() == id)
                    .collect(Collectors.toList());

            // Tính toán phân trang
            long totalElements = filteredApplications.size();
            int totalPages = (int) Math.ceil((double) totalElements / ROW_PER_PAGE);

            // Thực hiện phân trang thủ công
            int startIndex = (pageNumber - 1) * ROW_PER_PAGE;
            int endIndex = Math.min(startIndex + ROW_PER_PAGE, filteredApplications.size());

            List<Application> applications;
            if (startIndex < filteredApplications.size()) {
                applications = filteredApplications.subList(startIndex, endIndex);
            } else {
                applications = List.of();
            }

            // Thêm thông tin recruitment position name và technologies cho mỗi application
            for (Application app : applications) {
                RecruitmentPosition position = recruitmentPositionService.findById(app.getRecruitmentPositionId());
                if (position != null) {
                    app.setRecruitmentName(position.getName());
                    app.setTechnologies(position.getTechnologies());
                }
            }

            boolean hasPrev = pageNumber > 1;
            boolean hasNext = pageNumber < totalPages;

            // Truyền dữ liệu vào model
            model.addAttribute("applications", applications);
            model.addAttribute("candidateId", id);
            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalApplications", totalElements);
            model.addAttribute("pageSize", ROW_PER_PAGE);
            model.addAttribute("hasPrev", hasPrev);
            model.addAttribute("hasNext", hasNext);
            model.addAttribute("search", search);
            model.addAttribute("status", status);
            model.addAttribute("progressValues", Progress.values());

            return "myApplications";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải danh sách ứng tuyển!");
            return "redirect:/home";
        }
    }

    private boolean isValidUser(Long candidateId, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authId".equals(cookie.getName())) {
                    try {
                        Long userId = Long.valueOf(cookie.getValue());
                        Auth currentUser = authService.findById(userId);

                        if (currentUser != null && currentUser.isStatus()) {
                            Candidate candidate = candidateService.findById(candidateId);
                            return candidate != null && candidate.getEmail().equals(currentUser.getEmail());
                        }
                    } catch (Exception e) {
                        return false;
                    }
                    break;
                }
            }
        }
        return false;
    }
}
