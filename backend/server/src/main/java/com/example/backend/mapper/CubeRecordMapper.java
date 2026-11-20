package com.example.backend.mapper;

import com.example.backend.annotation.AutoFill;
import com.example.backend.entity.CubeRecord;
import com.example.backend.enums.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CubeRecordMapper {

    List<CubeRecord> list(@Param("userId") Integer userId);

    @AutoFill(OperationType.INSERT)
    void insert(CubeRecord record);

    long count(@Param("userId") Integer userId);

    List<CubeRecord> listForPage(@Param("userId") Integer userId,
                                 @Param("minMs") Integer minMs,
                                 @Param("maxMs") Integer maxMs);

    List<CubeRecord> search(@Param("userId") Integer userId,
                            @Param("minMs") Integer minMs,
                            @Param("maxMs") Integer maxMs);

    @AutoFill(OperationType.UPDATE)
    int update(CubeRecord record);

    int deleteById(@Param("userId") Integer userId, @Param("id") Integer id);

    CubeRecord getById(@Param("userId") Integer userId, @Param("id") Integer id);

    Integer minTime(@Param("userId") Integer userId);

    Integer maxTime(@Param("userId") Integer userId);

    List<Integer> recentTimes(@Param("userId") Integer userId, @Param("n") int n);

    Integer todayBest(@Param("userId") Integer userId,
                      @Param("begin") LocalDateTime begin,
                      @Param("end") LocalDateTime end);

    int deleteBefore(@Param("before") LocalDateTime before);
}