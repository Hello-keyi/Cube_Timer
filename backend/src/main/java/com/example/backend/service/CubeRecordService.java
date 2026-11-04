package com.example.backend.service;

import com.example.backend.common.PageResult;
import com.example.backend.vo.CubeRecordVO;


import java.util.List;

public interface CubeRecordService {
    List<CubeRecordVO> list();

    void add(Integer timeMs);

    PageResult<CubeRecordVO> page (int page, int size);

    List<CubeRecordVO> search(Integer minMs, Integer maxMs);

    // 接口
    void update(Integer id, Integer timeMs);

    void delete(Integer id);
}
