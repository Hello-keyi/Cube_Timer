package com.example.backend.interceptor;

import com.example.backend.context.BaseContext;
import com.example.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle (HttpServletRequest request ,HttpServletResponse response , Object handler) throws Exception{

        String auth =  request.getHeader(HEADER);
        if (auth == null || !auth.startsWith(PREFIX)) {
            reject(response, "未登录，请先登录");
            return false;
        }
        String token = auth.substring(PREFIX.length());
        try {
             Claims claims = jwtUtil.parseJWT(token);
             Integer userId = (Integer) claims.get("userId");
             if(userId == null){
                 reject(response , "系统异常");
                 return false;
             }
             BaseContext.setContextId(userId);
            log.debug("token 校验通过 userId={}", userId);
             return true;
        } catch (Exception e){
            log.warn("token 校验失败：{}", e.getMessage());
            reject(response,"登录已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion (HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeContextId();
    }

    private void reject(HttpServletResponse response, String msg) throws Exception{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":1,\"data\":null,\"msg\":\"" + msg + "\"}");
    }
}
