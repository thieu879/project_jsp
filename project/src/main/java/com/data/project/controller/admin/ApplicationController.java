package com.data.project.controller.admin;

import com.data.project.entity.Application;
import com.data.project.entity.Candidate;
import com.data.project.entity.Progress;
import com.data.project.entity.RecruitmentPosition;
import com.data.project.service.admin.ApplicationService;
import com.data.project.service.admin.CandidateService;
import com.data.project.service.admin.RecruitmentPositionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@Transactional
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CandidateService candidateService;
    private final RecruitmentPositionService recruitmentPositionService;

    public ApplicationController(ApplicationService applicationService,
                                 CandidateService candidateService,
                                 RecruitmentPositionService recruitmentPositionService) {
        this.applicationService = applicationService;
        this.candidateService = candidateService;
        this.recruitmentPositionService = recruitmentPositionService;
    }

    @GetMapping("/application-management")
    public String applicationManagement(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "2") int size,
                                        @RequestParam(required = false) String search,
                                        @RequestParam(required = false) String status,
                                        Model model) {
        try {
            if (page < 1) page = 1;
            List<Application> applications;
            long totalApplications;
            Progress statusEnum = null;

            // Chuyển đổi string status thành enum
            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
                try {
                    statusEnum = Progress.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    statusEnum = null;
                }
            }

            // Logic lọc dựa trên search và status
            if (search != null && !search.trim().isEmpty() && statusEnum != null) {
                applications = applicationService.searchByNameAndStatus(search, statusEnum, page, size);
                totalApplications = applicationService.countByNameAndStatus(search, statusEnum);
            } else if (search != null && !search.trim().isEmpty()) {
                applications = applicationService.searchByName(search, page, size);
                totalApplications = applicationService.countByName(search);
            } else if (statusEnum != null) {
                applications = applicationService.findByStatus(statusEnum, page, size);
                totalApplications = applicationService.countByStatus(statusEnum);
            } else {
                applications = applicationService.findAll(page, size);
                totalApplications = applicationService.countAll();
            }

            // Tải thông tin candidate và recruitment position
            for (Application app : applications) {
                try {
                    Candidate candidate = candidateService.findById(app.getCandidateId());
                    RecruitmentPosition position = recruitmentPositionService.findById(app.getRecruitmentPositionId());

                    app.setCandidateName(candidate != null ? candidate.getName() : "N/A");
                    app.setRecruitmentName(position != null ? position.getName() : "N/A");
                } catch (Exception e) {
                    System.err.println("Error loading data for application " + app.getId() + ": " + e.getMessage());
                    app.setCandidateName("Error loading");
                    app.setRecruitmentName("Error loading");
                }
            }

            int totalPages = (int) Math.ceil((double) totalApplications / size);

            // Đảm bảo page không vượt quá totalPages
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            // Thêm các thuộc tính cần thiết cho phân trang
            model.addAttribute("applications", applications);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalApplications", totalApplications);
            model.addAttribute("search", search);
            model.addAttribute("status", status);
            model.addAttribute("progressValues", Progress.values());

            return "admin/application-management";
        } catch (Exception e) {
            System.err.println("Error in applicationManagement: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("applications", List.of());
            model.addAttribute("totalApplications", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 0);
            model.addAttribute("hasPrev", false);
            model.addAttribute("hasNext", false);

            return "admin/application-management";
        }
    }

    @PostMapping("/applications/interview")
    public String scheduleInterview(@RequestParam(required = false) String applicationId,
                                    @RequestParam(required = false) String interviewLink,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime interviewTime,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Validate applicationId
            if (applicationId == null || applicationId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "ID đơn ứng tuyển không được để trống!");
                return "redirect:/admin/application-management";
            }

            Long appId;
            try {
                appId = Long.parseLong(applicationId.trim());
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "ID đơn ứng tuyển không hợp lệ!");
                return "redirect:/admin/application-management";
            }

            // Validate interview link
            if (interviewLink == null || interviewLink.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Link phỏng vấn không được để trống!");
                return "redirect:/admin/application-management";
            }

            // Validate URL format
            if (!interviewLink.matches("^(https?://).*")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Link phỏng vấn phải là URL hợp lệ (bắt đầu bằng http:// hoặc https://)!");
                return "redirect:/admin/application-management";
            }

            // Validate interview time
            if (interviewTime == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Thời gian phỏng vấn không được để trống!");
                return "redirect:/admin/application-management";
            }

            // Check if interview time is in the future
            if (interviewTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Thời gian phỏng vấn phải trong tương lai!");
                return "redirect:/admin/application-management";
            }

            Application application = applicationService.findById(appId);
            if (application != null) {
                // Check current status - chỉ cho phép lên lịch khi HANDLING
                if (application.getProgress() != Progress.HANDLING) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể lên lịch phỏng vấn cho đơn ứng tuyển đang ở trạng thái HANDLING!");
                    return "redirect:/admin/application-management";
                }

                application.setInterviewLink(interviewLink.trim());
                application.setInterviewTime(interviewTime);
                application.setInterviewRequestDate(LocalDateTime.now());
                application.setProgress(Progress.INTERVIEWING);
                applicationService.save(application);

                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã lên lịch phỏng vấn thành công! Trạng thái đã chuyển thành INTERVIEWING.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn ứng tuyển!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/application-management";
    }

    @PostMapping("/applications/updateInterview/{id}")
    public String updateInterview(@PathVariable Long id,
                                  @RequestParam(required = false) String interviewLink,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime interviewTime,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Validate inputs
            if (interviewLink == null || interviewLink.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Link phỏng vấn không được để trống!");
                return "redirect:/admin/application-management";
            }

            if (!interviewLink.trim().matches("^(https?://).*")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Link phỏng vấn phải là URL hợp lệ!");
                return "redirect:/admin/application-management";
            }

            if (interviewTime == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Thời gian phỏng vấn không được để trống!");
                return "redirect:/admin/application-management";
            }

            if (interviewTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Thời gian phỏng vấn phải trong tương lai!");
                return "redirect:/admin/application-management";
            }

            Application application = applicationService.findById(id);
            if (application == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn ứng tuyển!");
                return "redirect:/admin/application-management";
            }

            // Only allow update if status is INTERVIEWING
            if (application.getProgress() != Progress.INTERVIEWING) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể cập nhật lịch phỏng vấn cho đơn ứng tuyển đang ở trạng thái INTERVIEWING!");
                return "redirect:/admin/application-management";
            }

            application.setInterviewLink(interviewLink.trim());
            application.setInterviewTime(interviewTime);
            applicationService.updateApplication(application);

            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật thông tin phỏng vấn thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/application-management";
    }



    @PostMapping("/applications/approve")
    public String approveInterview(@RequestParam Long applicationId,
                                   @RequestParam String interviewResult,
                                   @RequestParam String interviewNote,
                                   RedirectAttributes redirectAttributes) {
        try {
            Application application = applicationService.findById(applicationId);
            if (application != null) {
                application.setInterviewResult(interviewResult);
                application.setInterviewResultNote(interviewNote);

                // Logic mới: PASS -> DONE, FAIL -> CANCEL
                if ("PASS".equals(interviewResult)) {
                    application.setProgress(Progress.DONE);
                    redirectAttributes.addFlashAttribute("successMessage", "Ứng viên đã PASS - chuyển trạng thái thành DONE!");
                } else {
                    application.setProgress(Progress.CANCEL);
                    redirectAttributes.addFlashAttribute("successMessage", "Ứng viên không đạt - chuyển trạng thái thành CANCEL!");
                }

                applicationService.save(application);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/application-management";
    }

    @PostMapping("/applications/destroy")
    public String destroyApplication(@RequestParam Long applicationId,
                                     @RequestParam String destroyReason,
                                     RedirectAttributes redirectAttributes) {
        try {
            Application application = applicationService.findById(applicationId);
            if (application != null) {
                application.setDestroyReason(destroyReason);
                application.setDestroyAt(LocalDateTime.now());
                application.setProgress(Progress.CANCEL);
                applicationService.save(application);

                redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn ứng tuyển!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/application-management";
    }
    @PostMapping("/applications/view-cv")
    public String viewCvAndUpdateStatus(@RequestParam Long applicationId,
                                        RedirectAttributes redirectAttributes) {
        try {
            Application application = applicationService.findById(applicationId);
            if (application != null) {
                application.setProgress(Progress.HANDLING);
                applicationService.save(application);
                redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái thành HANDLING!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn ứng tuyển!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/application-management";
    }

//    @GetMapping("/applications/my-applications/{id}")
//    public String myApplications(@RequestParam(defaultValue = "1") int page,
//                                        @RequestParam(defaultValue = "2") int size,
//                                        @RequestParam(required = false) String search,
//                                        @RequestParam(required = false) String status,
//                                        Model model) {
//        try {
//            if (page < 1) page = 1;
//            List<Application> applications;
//            long totalApplications;
//            Progress statusEnum = null;
//
//            // Chuyển đổi string status thành enum
//            if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
//                try {
//                    statusEnum = Progress.valueOf(status.toUpperCase());
//                } catch (IllegalArgumentException e) {
//                    statusEnum = null;
//                }
//            }
//
//            // Logic lọc dựa trên search và status
//            if (search != null && !search.trim().isEmpty() && statusEnum != null) {
//                applications = applicationService.searchByNameAndStatus(search, statusEnum, page, size);
//                totalApplications = applicationService.countByNameAndStatus(search, statusEnum);
//            } else if (search != null && !search.trim().isEmpty()) {
//                applications = applicationService.searchByName(search, page, size);
//                totalApplications = applicationService.countByName(search);
//            } else if (statusEnum != null) {
//                applications = applicationService.findByStatus(statusEnum, page, size);
//                totalApplications = applicationService.countByStatus(statusEnum);
//            } else {
//                applications = applicationService.findAll(page, size);
//                totalApplications = applicationService.countAll();
//            }
//
//            // Tải thông tin candidate và recruitment position
//            for (Application app : applications) {
//                try {
//                    Candidate candidate = candidateService.findById(app.getCandidateId());
//                    RecruitmentPosition position = recruitmentPositionService.findById(app.getRecruitmentPositionId());
//
//                    app.setCandidateName(candidate != null ? candidate.getName() : "N/A");
//                    app.setRecruitmentName(position != null ? position.getName() : "N/A");
//                } catch (Exception e) {
//                    System.err.println("Error loading data for application " + app.getId() + ": " + e.getMessage());
//                    app.setCandidateName("Error loading");
//                    app.setRecruitmentName("Error loading");
//                }
//            }
//
//            int totalPages = (int) Math.ceil((double) totalApplications / size);
//
//            // Đảm bảo page không vượt quá totalPages
//            if (page > totalPages && totalPages > 0) {
//                page = totalPages;
//            }
//
//            // Thêm các thuộc tính cần thiết cho phân trang
//            model.addAttribute("applications", applications);
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", totalPages);
//            model.addAttribute("pageSize", size);
//            model.addAttribute("totalApplications", totalApplications);
//            model.addAttribute("search", search);
//            model.addAttribute("status", status);
//            model.addAttribute("progressValues", Progress.values());
//
//            return "myApplications";
//        } catch (Exception e) {
//            System.err.println("Error in applicationManagement: " + e.getMessage());
//            e.printStackTrace();
//            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
//            model.addAttribute("applications", List.of());
//            model.addAttribute("totalApplications", 0);
//            model.addAttribute("currentPage", 1);
//            model.addAttribute("totalPages", 0);
//            model.addAttribute("hasPrev", false);
//            model.addAttribute("hasNext", false);
//
//            return "myApplications";
//        }
//    }
}
