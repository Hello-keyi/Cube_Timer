package com.example.backend.service;

import com.example.backend.dto.UserRegisterDTO;
import com.example.backend.entity.User;
import com.example.backend.vo.UserLoginVO;
import com.example.backend.vo.UserVO;

public interface UserService {

    void register(UserRegisterDTO dto) ;

    UserLoginVO login(String username, String password);

    /** Entity -> VO（转出去给前端时去掉 password） */
    UserVO toVO(User user);
}
