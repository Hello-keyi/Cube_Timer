package com.example.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name =  "连接状态提示" , description = "魔方后端状态")
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "后台连接成功";
    }
}
