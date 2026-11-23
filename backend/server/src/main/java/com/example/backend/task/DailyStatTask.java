package com.example.backend.task;

import com.example.backend.entity.DailyStat;
import com.example.backend.mapper.CubeRecordMapper;
import com.example.backend.mapper.DailyStatMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class DailyStatTask {

    @Autowired
    private DailyStatMapper dailyStatMapper;

    @Autowired
    private CubeRecordMapper cubeRecordMapper;
    /**
     * 每天 02:00 统计「昨天」的数据并落库
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyStat() {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime begin = yesterday.atStartOfDay();
        LocalDateTime end = LocalDate.now().atStartOfDay();
        List<DailyStat> list = dailyStatMapper.selectStatByDate(begin,end);
        for (DailyStat dailyStat : list) {
            dailyStat.setStatDate(yesterday);
            dailyStatMapper.insertOrUpdate(dailyStat);
        }
        log.info("每日统计完成 日期={} 用户数={}", yesterday, list.size());
    }

    /**
     * 每天 03:00 清理 30 天前的成绩
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldData() {

        LocalDateTime before = LocalDateTime.now().minusDays(30);
        cubeRecordMapper.deleteBefore(before);
        log.info("清理完成，删除截止时间={}", before);

    }
}