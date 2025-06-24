package com.data.project.controller;

import com.data.project.entity.Auth;
import com.data.project.entity.Candidate;
import com.data.project.service.AuthService;
import com.data.project.service.admin.CandidateService;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final CandidateService candidateService;

    public GlobalControllerAdvice(AuthService authService, ModelMapper modelMapper, CandidateService candidateService) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.candidateService = candidateService;
    }
    @ModelAttribute
    public void addUserInfoToModel(HttpServletRequest request, Model model) {
        Auth currentUser = null;
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
                        }
                    } catch (Exception e) {
                    }
                    break;
                }
            }
        }
        model.addAttribute("isLoggedIn", isLoggedIn);
    }
}

