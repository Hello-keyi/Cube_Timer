package com.example.backend.service.impl;

import com.example.backend.common.PageResult;
import com.example.backend.entity.CubeRecord;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.CubeRecordMapper;
import com.example.backend.service.CubeRecordService;
import com.example.backend.vo.CubeRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
public class CubeRecordServiceImpl implements CubeRecordService {


    @Autowired
    private CubeRecordMapper cubeRecordMapper;

    @Override
    public List<CubeRecordVO> list() {
        return cubeRecordMapper.list().stream().map(this::toVo).toList();
    }

    @Override
    public void add(Integer timeMs) {

        CubeRecord record = new CubeRecord();
        record.setTimeMs(timeMs);
        cubeRecordMapper.insert(record);
        log.info("新加成绩成功 {}"  , timeMs);
    }

    @Override
    public PageResult<CubeRecordVO> page(int page, int size) {

        if (page <= 0 || size <= 0) {
            throw new BusinessException("查询数据错误");
        }

        long total = cubeRecordMapper.count();
        int offset = (page - 1) * size;
        List<CubeRecord> records = cubeRecordMapper.page(offset, size);
        List<CubeRecordVO> vos = records.stream().map(this::toVo).toList();// ③ 拿当页
        return new PageResult<>(total, vos);
    }

    @Override
    public List<CubeRecordVO> search(Integer minMs , Integer maxMs) {
        return cubeRecordMapper.search(minMs, maxMs).stream().map(this::toVo).toList();
    }

    @Override
    public void update(Integer id, Integer timeMs){
        int rows = cubeRecordMapper.update(id, timeMs);
        if ( rows == 0 ) {
            throw new BusinessException("记录不存在");
        }
        log.info("更新成绩成功 id = {} , time = {}", id , timeMs );
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
    }

    private CubeRecordVO toVo(CubeRecord entity) {
        CubeRecordVO vo = new CubeRecordVO();
        BeanUtils.copyProperties(entity, vo);

        double seconds = entity.getTimeMs() / 1000.0;
        vo.setTimeText(String.format(Locale.ROOT, "%.3f", seconds));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        vo.setCreatedText(entity.getCreatedAt().format(formatter));
        return vo;
    }
}
