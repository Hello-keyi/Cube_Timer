package com.example.backend.vo;


import lombok.Data;

import java.util.List;

@Data
public class CubeRecordDetailVO {
    private Integer id;
    private Integer timeMs;
    private String timeText;
    private String createdText;
    private List<String> scrambles;
}
