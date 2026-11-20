package com.example.backend.controller;


import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.dto.CubeRecordAddDTO;
import com.example.backend.dto.CubeRecordFullDTO;
import com.example.backend.dto.CubeRecordUpdateDTO;
import com.example.backend.service.CubeRecordService;
import com.example.backend.vo.CubeRecordDetailVO;
import com.example.backend.vo.CubeRecordVO;
import com.example.backend.vo.RecordStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "成绩管理", description = "魔方成绩的增删改查与统计")
@RestController
@RequestMapping("/record")
public class CubeRecordController {
    @Autowired
    private CubeRecordService cubeRecordService;

    @Operation(summary = "新增成绩", description = "只保存成绩，不含打乱公式")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CubeRecordAddDTO dto){
        cubeRecordService.add(dto.getTimeMs());
        return Result.success();
    }

    @Operation(summary = "成绩列表", description = "返回当前用户的全部成绩，按 id 倒序")
    @GetMapping("/list")
    public Result<List<CubeRecordVO>> list() {
        return Result.success(cubeRecordService.list());
    }

    @Operation(summary = "分页查询", description = "支持按耗时区间筛选；页码越界会自动回到最后一页")
    @GetMapping("/page")
    public Result<PageResult<CubeRecordVO>> page(
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "最小耗时（毫秒），可选")
            @RequestParam(required = false) Integer minMs,
            @Parameter(description = "最大耗时（毫秒），可选")
            @RequestParam(required = false) Integer maxMs) {
        return Result.success(cubeRecordService.page(page, size, minMs, maxMs));
    }

    @Operation(summary = "按耗时区间筛选", description = "返回当前用户在该区间内的全部成绩")
    @GetMapping("/search")
    public Result<List<CubeRecordVO>> search(
            @RequestParam(required = false) Integer minMs,
            @RequestParam(required = false) Integer maxMs) {
        return Result.success(cubeRecordService.search(minMs, maxMs));
    }

    @Operation(summary = "修改成绩", description = "只能修改自己的成绩；id 不属于当前用户时返回「记录不存在」")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "成绩 id") @PathVariable Integer id,
                               @Valid @RequestBody CubeRecordUpdateDTO dto) {
        cubeRecordService.update(id, dto.getTimeMs());
        return Result.success();
    }

    @Operation(summary = "删除成绩", description = "关联的打乱公式会级联删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "成绩 id") @PathVariable Integer id) {
        cubeRecordService.delete(id);
        return Result.success();
    }

    @Operation(summary = "成绩详情", description = "返回成绩 + 它的全部打乱公式")
    @GetMapping("/{id}")
    public Result<CubeRecordDetailVO> detail(@Parameter(description = "成绩 id") @PathVariable Integer id) {
        return Result.success(cubeRecordService.getDetail(id));
    }

    @Operation(summary = "新增打乱公式", description = "给指定成绩追加一条打乱；seq 自动 +1")
    @PostMapping("/{id}/scramble")
    public Result<Void> addScramble(@Parameter(description = "成绩 id") @PathVariable Integer id,
                                    @Parameter(description = "打乱公式", example = "R U R2 F2")
                                    @RequestParam String scramble) {
        cubeRecordService.addScramble(id, scramble);
        return Result.success();
    }

    @Operation(summary = "统计数据", description = "总次数 / 最佳 / 最差 / AVG5 / AVG12 / 今日最佳；Redis 缓存 60 秒")
    @GetMapping("/stats")
    public Result<RecordStatsVO> stats() {
        return Result.success(cubeRecordService.getStats());
    }

    @Operation(summary = "新增成绩（含打乱，事务）", description = "成绩与多条打乱一次写入，任一失败全部回滚")
    @PostMapping("/full")
    public Result<Void> addFull(@Valid @RequestBody CubeRecordFullDTO dto) {
        cubeRecordService.addFull(dto);
        return Result.success();
    }
}
