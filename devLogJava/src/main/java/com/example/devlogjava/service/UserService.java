package com.example.devlogjava.service;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.dto.UserReq;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    Result<?> login(UserReq userReq);
    Result<?> register(UserReq userReq);
    Result<?> forgotPassword(UserReq userReq);
    Result<?> logout();
    void sendCode(String uuid, HttpServletResponse response) throws Exception;
}
