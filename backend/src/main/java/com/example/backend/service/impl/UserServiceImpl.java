package com.example.backend.service.impl;


import com.example.backend.dto.UserRegisterDTO;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import com.example.backend.util.JwtUtil;
import com.example.backend.vo.UserLoginVO;
import com.example.backend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserLoginVO login(String username, String password) {
        // ① 原有校验逻辑不变（查用户 → 校验密码 → 校验 status）
        User user = userMapper.getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!encoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("该用户被禁用");
        }

        // ② 生成 token：把 userId 放进 claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = jwtUtil.createJWT(claims);

        // ③ 组装 VO 返回（不要返回 password）
        UserLoginVO vo = new UserLoginVO();
        BeanUtils.copyProperties(user, vo);
        vo.setToken(token);

        log.info("用户登录成功 id={} username={}", user.getId(), user.getUsername());
        return vo;
    }

    public void register(UserRegisterDTO dto) {
        // ① 先查重（给友好提示；并发时由数据库唯一索引兜底）
        if (userMapper.getByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // ② 新建对象（不要复用查询结果 —— 查不到时它是 null）
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));   // BCrypt 加密（自带盐）
        user.setNickname(dto.getNickname() == null || dto.getNickname().isBlank()
                ? dto.getUsername()
                : dto.getNickname());
        user.setStatus(1);                                     // 1 = 正常（0 是禁用！）

        // ③ 插入（并发场景由 uk_username 唯一索引兜底）
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("用户名已存在");
        }

        log.info("用户注册成功 id={} username={}", user.getId(), user.getUsername());
    }

    @Override
    public UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);   // 只复制同名字段；VO 没有 password
        return vo;
    }


}
