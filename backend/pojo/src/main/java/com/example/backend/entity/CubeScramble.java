package com.example.backend.entity;

import lombok.Data;

@Data
public class CubeScramble {
    private Integer id;
    private Integer recordId;
    private String scramble;
    private Integer seq;
}
