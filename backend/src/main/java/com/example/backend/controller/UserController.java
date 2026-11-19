package com.example.backend.controller;


import com.example.backend.common.Result;
import com.example.backend.dto.UserLoginDTO;
import com.example.backend.dto.UserRegisterDTO;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import com.example.backend.vo.UserLoginVO;
import com.example.backend.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "注册 / 登录")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "用户名唯一；密码用 BCrypt 加密存储")
    @PostMapping("/register")
    public Result<Void> register (@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @Operation(summary = "用户登录", description = "成功返回 JWT token；失败统一提示「用户名或密码错误」")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        UserLoginVO user = userService.login(dto.getUsername(), dto.getPassword());
        // 转成 VO 返回（不含 password）
        return Result.success(user);
    }

}
