package com.example.devlogjava.service.impl;

import com.example.devlogjava.service.UserService;
import com.example.devlogjava.dto.UserDTO;
import com.example.devlogjava.entity.User;
import com.example.devlogjava.mapper.UserMapper;
import com.example.devlogjava.common.Result;
import com.example.devlogjava.common.JwtUtils;
import com.wf.captcha.SpecCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Result<?> login(UserDTO userDTO) {
        // 1. 根据手机号查询用户
        User user = userMapper.findByPhone(userDTO.getPhone());
        
        // 2. 校验用户是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 3. 校验密码 (使用 BCrypt 校验)
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }
        
        // 4. 生成 Token
        String token = jwtUtils.createToken(user.getPhone());
        
        // 5. 脱敏处理：去掉密码和手机号
        user.setPassword(null);
        user.setPhone(null);
        
        // 6. 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success(data);
    }

    @Override
    public Result<?> register(UserDTO userDTO) {
         // 1. 根据手机号查询用户是否存在
        User existUser = userMapper.findByPhone(userDTO.getPhone());
        if (existUser != null) {
            return Result.error("该手机号已注册");
        }
        
        // 2. 生成随机昵称
        String randomNickname = "用户" + String.format("%06d", (int)(Math.random() * 1000000));
        
        // 3. 构建用户实体并保存
        User user = new User();
        user.setPhone(userDTO.getPhone());
        // 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setNickname(randomNickname);
        userMapper.save(user); // 注意：JpaRepository 使用 save 而不是 insert
        return Result.success("注册成功");
    }

    @Override
    public Result<?> forgotPassword(UserDTO userDTO, HttpServletRequest request) {
        // 1. 从 Session 中获取验证码
        String sessionCode = (String) request.getSession().getAttribute("captcha");
        
        // 2. 校验验证码 (忽略大小写)
        if (userDTO.getCode() == null || sessionCode == null || !sessionCode.equalsIgnoreCase(userDTO.getCode())) {
            request.getSession().removeAttribute("captcha");
            return Result.error("验证码错误");
        }

        // 3. 根据手机号查询用户
        User user = userMapper.findByPhone(userDTO.getPhone());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 4. 重置密码 (加密)
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userMapper.save(user);

        // 5. 清除验证码
        request.getSession().removeAttribute("captcha");

        return Result.success("密码重置成功");
    }

    @Override
    public Result<?> logout() {
        // TODO: 实现登出逻辑
        return Result.success("登出成功模板");
    }

    @Override
    public void sendCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置响应头
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 生成图形验证码 (宽, 高, 位数)
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        specCaptcha.setFont(SpecCaptcha.FONT_1);
        
        // 将验证码文本存入 Session (转为小写存入，方便校验)
        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());
        
        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }
}
