package com.data.project.controller;

import com.data.project.entity.Auth;
import com.data.project.entity.Candidate;
import com.data.project.entity.RecruitmentPosition;
import com.data.project.service.AuthService;
import com.data.project.service.admin.CandidateService;
import com.data.project.service.admin.RecruitmentPositionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HomeController {
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final CandidateService candidateService;
    private final RecruitmentPositionService recruitmentPositionService;

    public HomeController(AuthService authService, ModelMapper modelMapper, CandidateService candidateService, RecruitmentPositionService recruitmentPositionService) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.candidateService = candidateService;
        this.recruitmentPositionService = recruitmentPositionService;
    }
    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        Auth currentUser = null;
        Candidate candidate = null;
        List<RecruitmentPosition> recruitmentPositions = null;
        boolean isLoggedIn = false;

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
                            recruitmentPositions = recruitmentPositionService.findAll(0, 10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recruitmentPositions", recruitmentPositions);
        model.addAttribute("candidate", candidate);
        model.addAttribute("isLoggedIn", isLoggedIn);

        return "/home";
    }

}
