package com.example.backend.mapper;

import com.example.backend.entity.DailyStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DailyStatMapper {

    List<DailyStat> selectStatByDate(@Param("begin") LocalDateTime begin,
                                     @Param("end") LocalDateTime end);

    void insertOrUpdate(DailyStat stat);
}