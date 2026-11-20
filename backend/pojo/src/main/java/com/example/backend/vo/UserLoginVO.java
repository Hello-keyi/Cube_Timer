package com.example.backend.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private Integer id;
    private String username;
    private String nickname;
    private String token;        // ← 新增：JWT 字符串
}