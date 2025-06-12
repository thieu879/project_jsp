package com.data.project.service;

import com.data.project.entity.Auth;

import java.util.List;

public interface AuthService {
    void register(Auth auth);
    void login(String username, String password);
    void updateAuth(Auth auth);
    void updateStatus(Long id, boolean status);
    List<Auth> getAllAuths(int page, int size);
    Auth getAuthByUsername(String username);
}
