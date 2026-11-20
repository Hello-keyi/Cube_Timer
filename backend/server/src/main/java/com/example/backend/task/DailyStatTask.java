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
        //      LocalDate yesterday = LocalDate.now().minusDays(1);
        //      LocalDateTime begin = yesterday.atStartOfDay();
        //      LocalDateTime end   = LocalDate.now().atStartOfDay();   // 今天 0 点（左闭右开）
        // ② 查：List<DailyStat> list = dailyStatMapper.selectStatByDate(begin, end);
        // ③ 遍历，给每条补上 statDate = yesterday，然后 dailyStatMapper.insertOrUpdate(stat);
        // ④ log.info("每日统计完成 日期={} 用户数={}", yesterday, list.size());
        //
        // 注意：如果 list 为空，也要能正常结束（不要 NPE）
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
        // ✍️ 你来写：
        // ① 30 天前的那个时刻：LocalDateTime before = LocalDateTime.now().minusDays(30);
        // ② 调 Mapper 删除（需要你在 cubeRecordMapper 里加一个方法，或者新建一个方法）
        // ③ log.info("清理完成，删除截止时间={}", before);
        //
        // 提示：可以给 CubeRecordMapper 加：
        //   int deleteBefore(@Param("before") LocalDateTime before);
        // XML: delete from cube_record where create_time < #{before}
        //   （打乱公式会级联删除）

        LocalDateTime before = LocalDateTime.now().minusDays(30);
        cubeRecordMapper.deleteBefore(before);
        log.info("清理完成，删除截止时间={}", before);

    }
}