package com.data.project.controller.admin;

import com.data.project.config.CloudinaryService;
import com.data.project.dto.admin.RecruitmentPositionDTO;
import com.data.project.entity.*;
import com.data.project.service.AuthService;
import com.data.project.service.admin.ApplicationService;
import com.data.project.service.admin.CandidateService;
import com.data.project.service.admin.RecruitmentPositionService;
import com.data.project.service.admin.TechnologyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class RecruitmentPositionController {
    private final ModelMapper modelMapper;
    private final RecruitmentPositionService recruitmentPositionService;
    private final TechnologyService technologyService;
    private final CandidateService candidateService;
    private final AuthService authService;
    private final ApplicationService applicationService;
    private final int ROW_PER_PAGE = 2;
    private final CloudinaryService cloudinaryService;

    public RecruitmentPositionController(RecruitmentPositionService recruitmentPositionService,
                                         ModelMapper modelMapper, TechnologyService technologyService,
                                         CandidateService candidateService, AuthService authService,
                                         ApplicationService applicationService, CloudinaryService cloudinaryService) {
        this.recruitmentPositionService = recruitmentPositionService;
        this.modelMapper = modelMapper;
        this.technologyService = technologyService;
        this.candidateService = candidateService;
        this.authService = authService;
        this.applicationService = applicationService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/admin/recruitment-position-management")
    public String recruitmentPositionManagement(
            @RequestParam(value = "page", defaultValue = "1") int pageNumber,
            @RequestParam(required = false) String search,
            Model model) {

        List<RecruitmentPosition> positions;
        long totalElements;

        if (search != null && !search.trim().isEmpty()) {
            positions = recruitmentPositionService.searchByName(search, pageNumber - 1, ROW_PER_PAGE);
            totalElements = recruitmentPositionService.countByName(search);
        } else {
            positions = recruitmentPositionService.findAll(pageNumber - 1, ROW_PER_PAGE);
            totalElements = recruitmentPositionService.countAll();
        }

        int totalPages = (int) Math.ceil((double) totalElements / ROW_PER_PAGE);
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < totalPages;

        model.addAttribute("positions", positions);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageNumber - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageNumber + 1);
        model.addAttribute("search", search);
        model.addAttribute("position", new RecruitmentPositionDTO());
        model.addAttribute("technologies", technologyService.findAll(0, 100));

        return "admin/recruitment-position-management";
    }

    @GetMapping("/admin/recruitment-position/add")
    public String showAddPosition(Model model) {
        model.addAttribute("position", new RecruitmentPositionDTO());
        model.addAttribute("technologies", technologyService.findAll(0, 100));
        model.addAttribute("add", true);
        return "admin/recruitment-position-form";
    }

    @PostMapping("/admin/recruitment-position/add")
    public String addPosition(@Valid @ModelAttribute("position") RecruitmentPositionDTO positionDTO,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("technologies", technologyService.findAll(0, 100));
            model.addAttribute("add", true);
            return "admin/recruitment-position-form";
        }

        try {
            RecruitmentPosition position = modelMapper.map(positionDTO, RecruitmentPosition.class);

            // Xử lý technologies từ technologyIds
            if (positionDTO.getTechnologyIds() != null && !positionDTO.getTechnologyIds().isEmpty()) {
                Set<Technology> technologies = new HashSet<>();
                for (Long techId : positionDTO.getTechnologyIds()) {
                    Technology tech = technologyService.findById(techId);
                    if (tech != null) {
                        technologies.add(tech);
                    }
                }
                position.setTechnologies(technologies);
            }

            position.setCreatedDate(LocalDate.now());
            position.setStatus(true);
            recruitmentPositionService.save(position);
            redirectAttributes.addFlashAttribute("success", "Thêm vị trí tuyển dụng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/recruitment-position-management";
    }

    @GetMapping("/admin/recruitment-position/{id}/edit")
    public String showEditPosition(@PathVariable Long id, Model model) {
        try {
            RecruitmentPosition position = recruitmentPositionService.findById(id);
            if (position != null) {
                RecruitmentPositionDTO dto = modelMapper.map(position, RecruitmentPositionDTO.class);

                // Đồng bộ technologyIds từ technologies để checkbox hiển thị đúng
                if (position.getTechnologies() != null) {
                    Set<Long> technologyIds = position.getTechnologies().stream()
                            .map(Technology::getId)
                            .collect(Collectors.toSet());
                    dto.setTechnologyIds(technologyIds);
                }

                model.addAttribute("position", dto);
                model.addAttribute("technologies", technologyService.findAll(0, 100));
                model.addAttribute("add", false);
                return "admin/recruitment-position-form";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không tìm thấy vị trí tuyển dụng!");
        }
        return "redirect:/admin/recruitment-position-management";
    }

    @PostMapping("/admin/recruitment-position/{id}/edit")
    public String updatePosition(@PathVariable Long id,
                                 @Valid @ModelAttribute("position") RecruitmentPositionDTO positionDTO,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("technologies", technologyService.findAll(0, 100));
            model.addAttribute("add", false);
            return "admin/recruitment-position-form";
        }

        try {
            RecruitmentPosition existingPosition = recruitmentPositionService.findById(id);
            if (existingPosition != null) {
                // Lưu lại giá trị cũ trước khi map
                boolean originalStatus = existingPosition.isStatus();
                LocalDate originalCreatedDate = existingPosition.getCreatedDate();

                modelMapper.map(positionDTO, existingPosition);

                // Khôi phục lại giá trị cũ
                existingPosition.setStatus(originalStatus);
                existingPosition.setCreatedDate(originalCreatedDate);

                // Xử lý technologies từ technologyIds
                if (positionDTO.getTechnologyIds() != null) {
                    Set<Technology> technologies = new HashSet<>();
                    for (Long techId : positionDTO.getTechnologyIds()) {
                        Technology tech = technologyService.findById(techId);
                        if (tech != null) {
                            technologies.add(tech);
                        }
                    }
                    existingPosition.setTechnologies(technologies);
                } else {
                    existingPosition.setTechnologies(new HashSet<>());
                }

                existingPosition.setId(id);
                recruitmentPositionService.updateRecruitmentPosition(existingPosition);
                redirectAttributes.addFlashAttribute("success", "Cập nhật vị trí tuyển dụng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy vị trí tuyển dụng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật vị trí tuyển dụng!");
        }

        return "redirect:/admin/recruitment-position-management";
    }


    @PostMapping("/admin/recruitment-position/{id}/delete")
    public String deletePosition(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            recruitmentPositionService.updateStatus(id, false);
            redirectAttributes.addFlashAttribute("success", "Xóa vị trí tuyển dụng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa vị trí tuyển dụng!");
        }

        return "redirect:/admin/recruitment-position-management";
    }

    @GetMapping("/recruitment-position/{id}/job-detail")
    public String jobDetail(@PathVariable Long id, Model model) {
        try {
            RecruitmentPosition position = recruitmentPositionService.findById(id);
            if (position != null) {
                model.addAttribute("position", position);
                return "jobDetail";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy vị trí tuyển dụng!");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi lấy thông tin vị trí tuyển dụng!");
        }
        return "redirect:/home";
    }

    @PostMapping("recruitment-position/{id}/apply")
    public String applyForPosition(@PathVariable Long id, RedirectAttributes redirectAttributes,
                                   @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
                                   HttpSession session, HttpServletRequest request) {
        Candidate candidate = null;
        boolean isLoggedIn = false;
        Auth currentUser = null;

        // Kiểm tra đăng nhập qua cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authId".equals(cookie.getName())) {
                    try {
                        Long userId = Long.valueOf(cookie.getValue());
                        currentUser = authService.findById(userId);

                        if (currentUser != null && currentUser.isStatus()) {
                            isLoggedIn = true;
                            List<Candidate> candidates = candidateService.findAll(1, 1000);
                            Auth finalCurrentUser = currentUser;
                            candidate = candidates.stream()
                                    .filter(c -> c.getEmail().equals(finalCurrentUser.getEmail()))
                                    .findFirst()
                                    .orElse(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Kiểm tra người dùng đã đăng nhập chưa
        if (!isLoggedIn || candidate == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để ứng tuyển!");
            return "redirect:/login";
        }

        try {
            RecruitmentPosition position = recruitmentPositionService.findById(id);
            if (position == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy vị trí tuyển dụng!");
                return "redirect:/home";
            }

            // Kiểm tra file CV
            if (cvFile == null || cvFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file CV!");
                return "redirect:/recruitment-position/" + id + "/job-detail";
            }

            // Upload CV lên Cloudinary
            String cvUrl = cloudinaryService.uploadImage(cvFile);

            // Tạo Application mới
            Application application = new Application();
            application.setCandidateId(candidate.getId());
            application.setRecruitmentPositionId(id);
            application.setCvUrl(cvUrl);
            application.setProgress(Progress.PENDING);
            applicationService.save(application);

            redirectAttributes.addFlashAttribute("success",
                    "Bạn đã ứng tuyển thành công vào vị trí: " + position.getName());

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi ứng tuyển vào vị trí!");
        }

        return "redirect:/home";
    }

}
