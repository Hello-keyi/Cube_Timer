package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CubeRecord {
    private Integer id;
    private Integer timeMs;
    private LocalDateTime createdAt;
}
