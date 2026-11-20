package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDTO {

    @Schema(description = "用户名", example = "郑理海")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度 3-20 位")
    private String username;

    @Schema(description = "用户密码", example = "sf27sf8aer")
    @NotBlank
    @Size(min = 6, max = 32, message = "密码长度 6-32 位")
    private String password;

}