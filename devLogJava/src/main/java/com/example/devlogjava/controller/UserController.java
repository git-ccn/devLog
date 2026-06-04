package com.example.devlogjava.controller;

import com.example.devlogjava.dto.UserDTO;
import com.example.devlogjava.service.UserService;
import com.example.devlogjava.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/forgot-password")
    public Result<?> forgotPassword(@RequestBody UserDTO userDTO) {
        return userService.forgotPassword(userDTO);
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
