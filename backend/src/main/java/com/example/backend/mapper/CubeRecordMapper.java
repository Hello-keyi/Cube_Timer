package com.example.backend.mapper;

import com.example.backend.entity.CubeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CubeRecordMapper {

    List<CubeRecord> list();

    void insert(CubeRecord record);

    long count();

    List<CubeRecord> page(@Param("offset") int offset, @Param("size") int size);

    List<CubeRecord> search(@Param("minMs") Integer minMs,
                            @Param("maxMs") Integer maxMs);

    int update(@Param("id") Integer id, @Param("timeMs") Integer timeMs);

    int deleteById(@Param("id") Integer id);
}
