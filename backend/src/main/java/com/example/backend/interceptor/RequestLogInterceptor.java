package com.example.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "req.start.time";

    @Override
    public boolean preHandle (HttpServletRequest request , HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion (HttpServletRequest request , HttpServletResponse response, Object handler, Exception ex) {
        Object start = request.getAttribute(START_TIME);
        if (start == null) {          // 没记到开始时间（不该发生）→ 直接跳过
            return;
        }
        long cost = System.currentTimeMillis() - (Long) start;   // 用刚取出的 start，别再取一次

        if (ex != null) {
            log.error("{} {} status={} cost={}ms error={}",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), cost, ex.getMessage());
        } else {
            log.info("{} {} status={} cost={}ms",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), cost);
        }
    }
}
