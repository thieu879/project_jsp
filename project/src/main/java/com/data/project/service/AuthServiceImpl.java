package com.data.project.service;

import com.data.project.entity.Auth;
import com.data.project.repository.AuthRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private AuthRepository authRepository;
    public  AuthServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    @Override
    public void register(Auth auth) {
        authRepository.register(auth);
    }

    @Override
    public void login(String username, String password) {
        authRepository.login(username, password);
    }

    @Override
    public void updateAuth(Auth auth) {
        authRepository.updateAuth(auth);
    }

    @Override
    public void updateStatus(Long id, boolean status) {
        authRepository.updateStatus(id, status);
    }

    @Override
    public List<Auth> getAllAuths(int page, int size) {
        return authRepository.getAllAuths(page, size);
    }

    @Override
    public Auth getAuthByUsername(String username) {
        return authRepository.getAuthByUsername(username);
    }
    @Override
    public Auth getAuthByEmail(String email) {
        return authRepository.getAuthByEmail(email);
    }

    @Override
    public Auth findById(Long id) {
        return authRepository.findById(id);
    }
    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return authRepository.checkPassword(rawPassword, encodedPassword);
    }

    @Override
    public void changePassword(String email, String newPassword) {
        authRepository.changePassword(email, newPassword);
    }
}
