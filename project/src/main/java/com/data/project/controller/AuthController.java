package com.data.project.controller;

import com.data.project.dto.AuthDto;
import com.data.project.dto.LoginDto;
import com.data.project.dto.admin.CandidateDTO;
import com.data.project.entity.Auth;
import com.data.project.entity.Candidate;
import com.data.project.service.AuthService;
import com.data.project.service.admin.CandidateService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AuthController {
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final CandidateService candidateService;

    public AuthController(AuthService authService, ModelMapper modelMapper, CandidateService candidateService) {
            this.authService = authService;
            this.modelMapper = modelMapper;
            this.candidateService = candidateService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("authDto", new AuthDto());
        return "/register";
    }

    @PostMapping("/register")
    public String register(@Valid AuthDto authDto, BindingResult result,
                           CandidateDTO candidateDto,
                           @RequestParam("repeatPassword") String repeatPassword, Model model) {

        if (result.hasErrors()) {
            return "/register";
        }

        if (!authDto.getPassword().equals(repeatPassword)) {
            model.addAttribute("repeatPasswordError", "Mật khẩu nhập lại không khớp");
            return "/register";
        }

        try {
            if (authService.getAuthByUsername(authDto.getUsername()) != null) {
                model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại");
                return "/register";
            }
            Auth existingAuth = authService.getAuthByEmail(candidateDto.getEmail());
            if (existingAuth != null) {
                model.addAttribute("emailError", "Email đã được sử dụng trong hệ thống");
                return "/register";
            }
            if (candidateService.existsByEmail(authDto.getEmail())) {
                model.addAttribute("emailError", "Email đã được sử dụng trong hệ thống");
                return "/register";
            }

            Auth auth = modelMapper.map(authDto, Auth.class);
            auth.setRole(false);
            auth.setStatus(true);
            authService.register(auth);

            Candidate candidate = modelMapper.map(candidateDto, Candidate.class);
            candidate.setStatus(true);
            candidate.setEmail(authDto.getEmail());
            candidate.setPhone(authDto.getPhone());
            candidate.setExperience(0);
            candidate.setGender(true);
            candidate.setName(authDto.getUsername());
            candidate.setDescription(null);
            candidate.setDob(null);
            candidate.setTechnologies(null);
            candidateService.save(candidate);

            model.addAttribute("success", "Đăng ký thành công!");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "/register";
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        // Kiểm tra cookie để tự động đăng nhập
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authId".equals(cookie.getName())) {
                    String userId = cookie.getValue();
                    try {
                        Auth auth = authService.findById(Long.valueOf(userId));
                        if (auth != null && auth.isStatus()) {
                            // Tạo session mới
                            HttpSession session = request.getSession(true);
                            if (auth.isRole()) {
                                session.setAttribute("admin", auth);
                                return "redirect:/admin/technology-management";
                            } else {
                                session.setAttribute("user", auth);
                                return "redirect:/home";
                            }
                        }
                    } catch (Exception e) {
                        // Cookie không hợp lệ, bỏ qua
                    }
                }
            }
        }

        model.addAttribute("loginDto", new LoginDto());
        return "/login";
    }

    @PostMapping("/login")
    public String login(@Valid LoginDto loginDto, BindingResult result,
                        @RequestParam(value = "remember", required = false) boolean remember,
                        Model model, HttpServletRequest request, HttpServletResponse response) {

        if (result.hasErrors()) {
            return "/login";
        }

        try {
            authService.login(loginDto.getUsername(), loginDto.getPassword());
            Auth loggedInAuth = authService.getAuthByUsername(loginDto.getUsername());

            // Tạo cookie với thời gian sống khác nhau
            Cookie cookie = new Cookie("authId", String.valueOf(loggedInAuth.getId()));

            if (remember) {
                cookie.setMaxAge(30 * 24 * 3600); // 30 ngày nếu chọn "Remember me"
            } else {
                cookie.setMaxAge(7 * 24 * 3600); // 7 ngày mặc định
            }

            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            // Tạo session
            HttpSession session = request.getSession(true);
            if (loggedInAuth.isRole()) {
                session.setAttribute("admin", loggedInAuth);
                return "redirect:/admin/technology-management";
            } else {
                session.setAttribute("user", loggedInAuth);
                return "redirect:/home";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Xóa tất cả cookie liên quan
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authId".equals(cookie.getName())) {
                    Cookie deleteCookie = new Cookie("authId", null);
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setPath("/");
                    deleteCookie.setHttpOnly(true);
                    response.addCookie(deleteCookie);
                    break;
                }
            }
        }

        // Hủy session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login";
    }


}
