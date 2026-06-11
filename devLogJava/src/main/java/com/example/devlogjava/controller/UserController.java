package com.example.devlogjava.controller;

import com.example.devlogjava.service.UserService;
import com.example.devlogjava.common.Result;
import com.example.devlogjava.dto.UserReq;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody UserReq userReq) {
        return userService.login(userReq);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody UserReq userReq) {
        return userService.register(userReq);
    }

    @PostMapping("/forgot-password")
    public Result<?> forgotPassword(@RequestBody UserReq userReq) {
        return userService.forgotPassword(userReq);
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return userService.logout();
    }

    @GetMapping("/send-code")
    public void sendCode(@RequestParam String uuid, HttpServletResponse response) throws Exception {
        userService.sendCode(uuid, response);
    }
}
