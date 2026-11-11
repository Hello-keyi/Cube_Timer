package com.example.backend.mapper;

import com.example.backend.entity.CubeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    CubeRecord getById(@Param("id") Integer id);

    Integer minTime();

    Integer maxTime();

    List<Integer> recentTimes(@Param("n") int n);

    Integer todayBest(@Param("begin") LocalDateTime begin,
                      @Param("end") LocalDateTime end);
}
