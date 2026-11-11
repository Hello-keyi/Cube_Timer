package com.example.backend.controller;


import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.dto.CubeRecordAddDTO;
import com.example.backend.dto.CubeRecordUpdateDTO;
import com.example.backend.service.CubeRecordService;
import com.example.backend.vo.CubeRecordDetailVO;
import com.example.backend.vo.CubeRecordVO;
import com.example.backend.vo.RecordStatsVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/record")
public class CubeRecordController {
    @Autowired
    private CubeRecordService cubeRecordService;

    @PostMapping
    public Result<Void> add(@Valid @RequestBody CubeRecordAddDTO dto){
        cubeRecordService.add(dto.getTimeMs());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<CubeRecordVO>> list() {
        return Result.success(cubeRecordService.list());
    }

    @GetMapping("/page")
    public Result<PageResult<CubeRecordVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(cubeRecordService.page(page, size));
    }

    @GetMapping("/search")
    public Result<List<CubeRecordVO>> search(
            @RequestParam(required = false) Integer minMs,
            @RequestParam(required = false) Integer maxMs) {
        return Result.success(cubeRecordService.search(minMs, maxMs));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id,
                               @Valid @RequestBody CubeRecordUpdateDTO dto) {
        cubeRecordService.update(id, dto.getTimeMs());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        cubeRecordService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CubeRecordDetailVO> detail(@PathVariable Integer id) {
        return Result.success(cubeRecordService.getDetail(id));
    }

    @PostMapping("/{id}/scramble")
    public Result<Void> addScramble(@PathVariable Integer id,
                                    @RequestParam String scramble) {
        cubeRecordService.addScramble(id, scramble);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<RecordStatsVO> stats() {
        return Result.success(cubeRecordService.getStats());
    }
}
