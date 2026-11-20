package com.example.backend.mapper;


import com.example.backend.entity.CubeScramble;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CubeScrambleMapper {

    List<CubeScramble> listByRecordId(@Param("recordId") int recordId);

    int insert (CubeScramble entity);

}





