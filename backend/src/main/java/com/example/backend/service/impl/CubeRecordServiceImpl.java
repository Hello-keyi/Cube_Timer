package com.example.backend.service.impl;

import com.example.backend.common.PageResult;
import com.example.backend.entity.CubeRecord;
import com.example.backend.entity.CubeScramble;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.CubeRecordMapper;
import com.example.backend.mapper.CubeScrambleMapper;
import com.example.backend.service.CubeRecordService;
import com.example.backend.util.StatsUtils;
import com.example.backend.vo.CubeRecordDetailVO;
import com.example.backend.vo.CubeRecordVO;
import com.example.backend.vo.RecordStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
public class CubeRecordServiceImpl implements CubeRecordService {

    private static final String STATS_KEY = "cube:stats";
    private static final long STATS_TTL_SECONDS = 60;

    @Autowired
    private CubeScrambleMapper cubeScrambleMapper;

    @Autowired
    private CubeRecordMapper cubeRecordMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<CubeRecordVO> list() {
        return cubeRecordMapper.list().stream().map(this::toCubeRecordVO).toList();
    }


    @Override
    public void add(Integer timeMs) {

        CubeRecord record = new CubeRecord();
        record.setTimeMs(timeMs);
        cubeRecordMapper.insert(record);
        log.info("新增成绩成功 timeMs={}", timeMs);
        evictStatsCache();
    }

    @Override
    public PageResult<CubeRecordVO> page(int page, int size) {

        if (page <= 0 || size <= 0) {
            throw new BusinessException("查询数据错误");
        }

        long total = cubeRecordMapper.count();
        int offset = (page - 1) * size;
        List<CubeRecord> records = cubeRecordMapper.page(offset, size);
        List<CubeRecordVO> vos = records.stream().map(this::toCubeRecordVO).toList();// ③ 拿当页
        return new PageResult<>(total, vos);
    }

    @Override
    public List<CubeRecordVO> search(Integer minMs , Integer maxMs) {
        return cubeRecordMapper.search(minMs, maxMs).stream().map(this::toCubeRecordVO).toList();
    }

    @Override
    public void update(Integer id, Integer timeMs){
        int rows = cubeRecordMapper.update(id, timeMs);
        if ( rows == 0 ) {
            throw new BusinessException("记录不存在");
        }
        log.info("更新成绩成功 id = {} , time = {}", id , timeMs);
        evictStatsCache();
    }

    @Override
    public void delete(Integer id){
        if (id == null || id <= 0) {
            throw new BusinessException("id应该大于0");
        }
        int row = cubeRecordMapper.deleteById(id);
        if ( row == 0 ) {
            throw new BusinessException("记录不存在");
        }
        log.info("删除成绩成功 id={}", id);
        evictStatsCache();
    }

    @Override
    public CubeRecordDetailVO getDetail(Integer id) {

        if (id == null || id <= 0) {
            throw new BusinessException("id应该大于0");
        }

        CubeRecord entity = cubeRecordMapper.getById(id);

        if (entity == null) {
            throw new BusinessException("记录不存在");
        }

        List<CubeScramble> list = cubeScrambleMapper.listByRecordId(id);
        CubeRecordDetailVO vos = toCubeScrambleVO(entity , list);

        log.info("查询成绩详情 id={}", id);
        return vos;
    }

    @Override
    public void addScramble(Integer recordId, String scramble) {
        if (recordId == null || recordId <= 0) {
            throw new BusinessException("recordId 应该大于 0");
        }

        // ① 先确认这条成绩真的存在（外键约束会抛异常，不如提前给友好提示）
        if (cubeRecordMapper.getById(recordId) == null) {
            throw new BusinessException("记录不存在");
        }

        // ② seq = 当前已有打乱条数 + 1，保证顺序不乱
        List<CubeScramble> exists = cubeScrambleMapper.listByRecordId(recordId);

        CubeScramble entity = new CubeScramble();
        entity.setRecordId(recordId);
        entity.setScramble(scramble);
        entity.setSeq(exists.size() + 1);

        cubeScrambleMapper.insert(entity);
        log.info("新增打乱成功 recordId={} scramble={}", recordId, scramble);
    }

    /** 真正的查库逻辑（被 getStats 调用；缓存未命中时才走这里） */
    private RecordStatsVO buildStats() {
        long total = cubeRecordMapper.count();
        Integer best  = cubeRecordMapper.minTime();
        Integer worst = cubeRecordMapper.maxTime();
        List<Integer> recent5  = cubeRecordMapper.recentTimes(5);
        List<Integer> recent12 = cubeRecordMapper.recentTimes(12);
        LocalDateTime begin = LocalDate.now().atStartOfDay();
        LocalDateTime end   = LocalDate.now().atTime(LocalTime.MAX);
        Integer todayBest = cubeRecordMapper.todayBest(begin, end);
        log.info("查询统计（查库） total={} best={} worst={}", total, best, worst);
        return toStatsVO(total, best, worst, recent5, recent12, todayBest);
    }

    @Override
    public RecordStatsVO getStats() {
        // ① 先看缓存
        try {
            String cached = stringRedisTemplate.opsForValue().get(STATS_KEY);
            if (cached != null) {
                log.info("统计命中缓存");
                return objectMapper.readValue(cached, RecordStatsVO.class);
            }
        } catch (Exception e) {
            // 缓存挂了不能影响主流程，降级查库
            log.warn("读缓存失败，回退查库：{}", e.getMessage());
        }

        // ② 没命中 → 查库
        RecordStatsVO vo = buildStats();

        // ③ 写回缓存，60 秒后自动过期
        try {
            stringRedisTemplate.opsForValue().set(
                    STATS_KEY,
                    objectMapper.writeValueAsString(vo),
                    Duration.ofSeconds(STATS_TTL_SECONDS));
            log.info("统计已写入缓存 TTL={}s", STATS_TTL_SECONDS);
        } catch (Exception e) {
            log.warn("写缓存失败：{}", e.getMessage());
        }

        return vo;
    }

    /** 数据变了就把统计缓存删掉，下次访问自然重建 */
    private void evictStatsCache() {
        try {
            stringRedisTemplate.delete(STATS_KEY);
            log.info("已清除统计缓存");
        } catch (Exception e) {
            log.warn("清除缓存失败：{}", e.getMessage());
        }
    }

    private RecordStatsVO toStatsVO(
            long total, Integer best,
            Integer worst, List<Integer> recent5,
            List<Integer> recent12, Integer todayBest) {

        RecordStatsVO vo  = new RecordStatsVO();

        vo.setTotal(total);

        vo.setBestMs(best);
        vo.setBestText(StatsUtils.toText(best));

        vo.setAvg5Text(StatsUtils.calcAvg(recent5, 5));
        vo.setAvg12Text(StatsUtils.calcAvg(recent12, 12));

        vo.setWorstMs(worst);
        vo.setWorstText(StatsUtils.toText(worst));

        vo.setTodayBestText(StatsUtils.toText(todayBest));
        vo.setTodayBestMs(todayBest);
        return vo;
    }

    private CubeRecordVO toCubeRecordVO(CubeRecord entity) {
        CubeRecordVO vo = new CubeRecordVO();
        BeanUtils.copyProperties(entity, vo);

        double seconds = entity.getTimeMs() / 1000.0;
        vo.setTimeText(String.format(Locale.ROOT, "%.3f", seconds));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        vo.setCreatedText(entity.getCreatedAt().format(formatter));
        return vo;
    }

    private CubeRecordDetailVO toCubeScrambleVO(CubeRecord cubeRecord, List<CubeScramble> cubeScramble) {
        CubeRecordDetailVO vo = new CubeRecordDetailVO();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        vo.setCreatedText(cubeRecord.getCreatedAt().format(formatter));

        double seconds = cubeRecord.getTimeMs() / 1000.0;
        vo.setTimeText(String.format(Locale.ROOT, "%.3f", seconds));

        vo.setScrambles(cubeScramble.stream().map(CubeScramble::getScramble).toList());
        vo.setTimeMs(cubeRecord.getTimeMs());
        vo.setId(cubeRecord.getId());

        return vo;
    }
}
