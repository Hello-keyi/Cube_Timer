package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CubeRecordVO {
    private Integer id;
    private Integer timeMs;
    private String timeText;
    private String createdText;
}
