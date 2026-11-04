package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CubeRecordAddDTO {

    @NotNull(message = "成绩不能为空")
    @Min(value = 1, message = "成绩必须大于 0")
    @Max(value = 600000, message = "成绩不能超过 10 分钟")
    private Integer timeMs;

}
