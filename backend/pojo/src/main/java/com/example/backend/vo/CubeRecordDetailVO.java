package com.example.backend.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CubeRecordDetailVO {

    @Schema(description = "用户id", example = "张三")
    private Integer id;

    @Schema(description = "成绩（毫秒），范围 1 ~ 600000", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer timeMs;

    @Schema(description = "成绩（毫秒），范围 1 ~ 600000", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String timeText;

    @Schema(description = "成绩创建时间", example = "2026-10-25 18:34:10")
    private String createdText;

    @Schema(description = "成绩创建时间", example = "2026-10-25 18:34:10")
    private List<String> scrambles;
}
