package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CubeRecord {
    private Integer id;
    private Integer userId;
    private Integer timeMs;
    private LocalDateTime createTime;      // ← 原来叫 createdAt
    private LocalDateTime updateTime;      // ← 新增
    private Integer createUser;            // ← 新增
    private Integer updateUser;            // ← 新增
}

