package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CubeRecordUpdateDTO {

    @Schema(description = "成绩（毫秒），范围 1 ~ 600000", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "成绩不能为空")
    @Min(value = 1, message = "成绩必须大于 0")
    @Max(value = 600000, message = "成绩不能超过 10 分钟")
    private Integer timeMs;

}
