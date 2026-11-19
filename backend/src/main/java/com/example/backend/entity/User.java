package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private Integer status;
    private LocalDateTime createTime;
}