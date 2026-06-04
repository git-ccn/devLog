package com.example.devlogjava.service;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    Result<?> login(UserDTO userDTO);
    Result<?> register(UserDTO userDTO);
    Result<?> forgotPassword(UserDTO userDTO, HttpServletRequest request);
    Result<?> logout();
    void sendCode(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
