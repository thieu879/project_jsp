package com.data.project.controller;

import com.data.project.dto.AuthDto;
import com.data.project.dto.LoginDto;
import com.data.project.entity.Auth;
import com.data.project.service.AuthService;
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

@Controller
public class AuthController {
    private final ModelMapper modelMapper;
    private final AuthService authService;

    public AuthController(AuthService authService, ModelMapper modelMapper) {
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("authDto", new AuthDto());
        return "/register";
    }

    @PostMapping("/register")
    public String register(@Valid AuthDto authDto, BindingResult result,
                           @RequestParam("repeatPassword") String repeatPassword, Model model) {

        if (result.hasErrors()) {
            return "/register";
        }

        // Kiểm tra mật khẩu nhập lại
        if (!authDto.getPassword().equals(repeatPassword)) {
            model.addAttribute("repeatPasswordError", "Mật khẩu nhập lại không khớp");
            return "/register";
        }

        try {
            // Kiểm tra username đã tồn tại
            if (authService.getAuthByUsername(authDto.getUsername()) != null) {
                model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại");
                return "/register";
            }

            Auth auth = modelMapper.map(authDto, Auth.class);
            auth.setRole(false);
            auth.setStatus(true);

            authService.register(auth);
            model.addAttribute("success", "Đăng ký thành công!");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            return "/register";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "/login";
    }

    @PostMapping("/login")
    public String login(@Valid LoginDto loginDto, BindingResult result,
                        Model model, HttpServletRequest request, HttpServletResponse response) {

        if (result.hasErrors()) {
            return "/login";
        }

        try {
            authService.login(loginDto.getUsername(), loginDto.getPassword());
            Auth loggedInAuth = authService.getAuthByUsername(loginDto.getUsername());

            // Tạo cookie để lưu thông tin đăng nhập
            Cookie cookie = new Cookie("authId", String.valueOf(loggedInAuth.getId()));
            cookie.setMaxAge(3600); // 1 giờ
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            // Phân quyền dựa trên role
            if (loggedInAuth.isRole()) {
                HttpSession session = request.getSession();
                session.setAttribute("admin", loggedInAuth);
                return "redirect:/admin/technology-management";
            }

            HttpSession session = request.getSession();
            session.setAttribute("user", loggedInAuth);
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Xóa cookie
        Cookie cookie = new Cookie("authId", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // Hủy session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Auth admin = (Auth) session.getAttribute("admin");
            Auth user = (Auth) session.getAttribute("user");

            if (admin != null) {
                model.addAttribute("auth", admin);
            } else if (user != null) {
                model.addAttribute("auth", user);
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
        return "/home";
    }
}
