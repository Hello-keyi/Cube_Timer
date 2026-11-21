package com.example.backend.service.impl;

import com.example.backend.common.PageResult;
import com.example.backend.context.BaseContext;
import com.example.backend.dto.CubeRecordFullDTO;
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
import com.example.backend.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.net.http.WebSocket;
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

    private String statsKey(Integer userId) {
        return "cube:stats:" + userId;
    }
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
        return cubeRecordMapper.list(currentUserId()).stream().map(this::toCubeRecordVO).toList();
    }

    @Operation(summary = "新增成绩", description = "只保存成绩，不含打乱公式")
    @Override
    public void add(Integer timeMs) {

        CubeRecord record = new CubeRecord();
        record.setTimeMs(timeMs);
        record.setUserId(currentUserId());
        cubeRecordMapper.insert(record);
        log.info("新增成绩成功 timeMs={}", timeMs);
        evictStatsCache();
        WebSocketServer.broadcast(
                String.format("{\"type\":\"NEW_RECORD\",\"timeMs\":%d}", timeMs)
        );
    }

    @Operation(summary = "成绩列表", description = "返回当前用户的全部成绩，按 id 倒序")
    @Override
    public PageResult<CubeRecordVO> page(int page, int size, Integer minMs, Integer maxMs) {
        if (page <= 0 || size <= 0) {
            throw new BusinessException("查询数据错误");
        }
        Integer userId = currentUserId();
        PageHelper.startPage(page, size);
        List<CubeRecord> records = cubeRecordMapper.listForPage(userId, minMs, maxMs);
        PageInfo<CubeRecord> info = new PageInfo<>(records);
        List<CubeRecordVO> vos = info.getList().stream().map(this::toCubeRecordVO).toList();
        return new PageResult<>(info.getTotal(), vos);
    }

    @Operation(summary = "分页查询", description = "支持时间区间筛选")
    @Override
    public List<CubeRecordVO> search(Integer minMs , Integer maxMs) {
        return cubeRecordMapper.search(currentUserId(),minMs, maxMs).stream().map(this::toCubeRecordVO).toList();
    }

    @Operation(summary = "成绩更新" , description = "根据id查用户，只改成绩")
    @Override
    public void update(Integer id, Integer timeMs){
        CubeRecord record = new CubeRecord();
        record.setId(id);
        record.setUserId(currentUserId());
        record.setTimeMs(timeMs);

        int rows = cubeRecordMapper.update(record);
        if ( rows == 0 ) {
            throw new BusinessException("记录不存在");
        }
        log.info("更新成绩成功 id = {} , time = {}", id , timeMs);
        evictStatsCache();
    }

    @Operation(summary = "根据id删除成绩"  , description = "id查数据，删除相关数据")
    @Override
    public void delete(Integer id){
        if (id == null || id <= 0) {
            throw new BusinessException("id应该大于0");
        }
        int row = cubeRecordMapper.deleteById(currentUserId() , id);
        if ( row == 0 ) {
            throw new BusinessException("记录不存在");
        }
        log.info("删除成绩成功 id={}", id);
        evictStatsCache();
    }

    @Operation(summary = "查询详细数据" , description = "根据id查询所有详细数据")
    @Override
    public CubeRecordDetailVO getDetail(Integer id) {

        if (id == null || id <= 0) {
            throw new BusinessException("id应该大于0");
        }

        CubeRecord entity = cubeRecordMapper.getById(currentUserId(),id);

        if (entity == null) {
            throw new BusinessException("记录不存在");
        }

        List<CubeScramble> list = cubeScrambleMapper.listByRecordId(id);
        CubeRecordDetailVO vos = toCubeScrambleVO(entity , list);

        log.info("查询成绩详情 id={}", id);
        return vos;
    }

    @Operation(summary = "添加成绩的打乱公式"  , description = "根据recordId查询数据，添加公式")
    @Override
    public void addScramble(Integer recordId, String scramble) {
        if (recordId == null || recordId <= 0) {
            throw new BusinessException("recordId 应该大于 0");
        }

        // ① 先确认这条成绩真的存在（外键约束会抛异常，不如提前给友好提示）
        if (cubeRecordMapper.getById(currentUserId(),recordId) == null) {
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
    @Operation(summary = "缓存未命中查询"  , description = "查询数据")
    private RecordStatsVO buildStats() {
        long total = cubeRecordMapper.count(currentUserId());
        Integer best  = cubeRecordMapper.minTime(currentUserId());
        Integer worst = cubeRecordMapper.maxTime(currentUserId());
        List<Integer> recent5  = cubeRecordMapper.recentTimes(currentUserId(),5);
        List<Integer> recent12 = cubeRecordMapper.recentTimes(currentUserId(),12);
        LocalDateTime begin = LocalDate.now().atStartOfDay();
        LocalDateTime end   = LocalDate.now().atTime(LocalTime.MAX);
        Integer todayBest = cubeRecordMapper.todayBest(currentUserId(),begin, end);
        log.info("查询统计（查库） total={} best={} worst={}", total, best, worst);
        return toStatsVO(total, best, worst, recent5, recent12, todayBest);
    }

    @Operation(summary = "查询，判断是否命中缓存"  , description = "查询数据")
    @Override
    public RecordStatsVO getStats() {
        // ① 先看缓存
        try {
            String cached = stringRedisTemplate.opsForValue().get(statsKey(currentUserId()));
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
                    statsKey(currentUserId()),
                    objectMapper.writeValueAsString(vo),
                    Duration.ofSeconds(STATS_TTL_SECONDS));
            log.info("统计已写入缓存 TTL={}s", STATS_TTL_SECONDS);
        } catch (Exception e) {
            log.warn("写缓存失败：{}", e.getMessage());
        }

        return vo;
    }

    @Operation(summary = "添加数据，完整"  , description = "添加数据，用dto")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addFull(CubeRecordFullDTO dto) {
        CubeRecord record = new CubeRecord();
        record.setTimeMs(dto.getTimeMs());
        record.setUserId(currentUserId());
        cubeRecordMapper.insert(record);

        Integer recordId = record.getId();
        if (recordId == null) {
            throw new BusinessException("新添成绩失败");
        }

        List<String> scrambles = dto.getScrambles();
        if ( scrambles != null && !scrambles.isEmpty()) {
            for ( int i = 0; i < scrambles.size(); i++ ) {
                CubeScramble cs = new CubeScramble();
                cs.setScramble(scrambles.get(i));
                cs.setSeq(i + 1);
                cs.setRecordId(recordId);
                cubeScrambleMapper.insert(cs);
            }
        }
        evictStatsCache();
        log.info("新增成绩(含打乱)成功 recordId={} 打乱数={}", recordId,
                scrambles == null ? 0 : scrambles.size());
    }

    /** 数据变了就把统计缓存删掉，下次访问自然重建 */
    @Operation(summary = "缓存清理"  , description = "缓存数据管理")
    private void evictStatsCache() {
        try {
            stringRedisTemplate.delete(statsKey(currentUserId()));
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
        vo.setCreatedText(entity.getCreateTime().format(formatter));
        return vo;
    }

    private CubeRecordDetailVO toCubeScrambleVO(CubeRecord cubeRecord, List<CubeScramble> cubeScramble) {
        CubeRecordDetailVO vo = new CubeRecordDetailVO();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        vo.setCreatedText(cubeRecord.getCreateTime().format(formatter));

        double seconds = cubeRecord.getTimeMs() / 1000.0;
        vo.setTimeText(String.format(Locale.ROOT, "%.3f", seconds));

        vo.setScrambles(cubeScramble.stream().map(CubeScramble::getScramble).toList());
        vo.setTimeMs(cubeRecord.getTimeMs());
        vo.setId(cubeRecord.getId());

        return vo;
    }

    private Integer currentUserId() {
        Integer id = BaseContext.getContextId();
        if (id == null) {
            throw new BusinessException("未登录");
        }
        return id;
    }
}
