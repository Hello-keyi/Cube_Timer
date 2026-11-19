package com.example.backend.mapper;

import com.example.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /** 按用户名查（登录、查重用） */
    User getByUsername(@Param("username") String username);

    /** 新增用户 */
    void insert(User user);
}