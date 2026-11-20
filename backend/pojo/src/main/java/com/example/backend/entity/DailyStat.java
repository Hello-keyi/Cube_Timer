package com.example.backend.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyStat {
    private Integer id;
    private LocalDate statDate;
    private Integer userId;
    private Integer totalCount;
    private Integer bestMs;
    private Integer avgMs;
    private LocalDateTime createTime;
}