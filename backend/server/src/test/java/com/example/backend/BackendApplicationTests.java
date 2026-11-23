package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 冒烟测试：验证 Spring 上下文能正常启动
 *
 * ★ webEnvironment = RANDOM_PORT 是必须的：
 *   WebSocket 的 ServerEndpointExporter 依赖真实的 Servlet 容器（ServerContainer），
 *   而 @SpringBootTest 默认的 MOCK 环境没有容器 → 会报
 *   "jakarta.websocket.server.ServerContainer not available"
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
