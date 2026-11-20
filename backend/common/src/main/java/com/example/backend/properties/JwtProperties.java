package com.example.backend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "login.jwt")
public class JwtProperties {
    private String secret;
    private long ttl;
}
