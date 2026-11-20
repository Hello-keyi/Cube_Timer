package com.example.backend.service;

import com.example.backend.common.PageResult;
import com.example.backend.dto.CubeRecordFullDTO;
import com.example.backend.vo.CubeRecordDetailVO;
import com.example.backend.vo.CubeRecordVO;
import com.example.backend.vo.RecordStatsVO;


import java.util.List;

public interface CubeRecordService {
    List<CubeRecordVO> list();

    void add(Integer timeMs);

    PageResult<CubeRecordVO> page(int page, int size, Integer minMs, Integer maxMs);

    List<CubeRecordVO> search(Integer minMs, Integer maxMs);

    // 接口
    void update(Integer id, Integer timeMs);

    void delete(Integer id);

    CubeRecordDetailVO getDetail(Integer id);

    void addScramble(Integer recordId, String scramble);

    RecordStatsVO getStats();

    void addFull(CubeRecordFullDTO dto);
}
