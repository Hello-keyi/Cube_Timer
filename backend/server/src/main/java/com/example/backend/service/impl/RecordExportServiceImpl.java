package com.example.backend.service.impl;

import com.example.backend.context.BaseContext;
import com.example.backend.entity.CubeRecord;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.CubeRecordMapper;
import com.example.backend.service.RecordExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class RecordExportServiceImpl implements RecordExportService {

    @Autowired
    private CubeRecordMapper cubeRecordMapper;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void exportTo(OutputStream out) throws IOException {

        Integer userId = BaseContext.getContextId();
        if (userId == null) {
            throw new BusinessException("未登录");
        }

        List<CubeRecord> list = cubeRecordMapper.list(userId);
        try(XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("成绩");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "成绩(毫秒)", "成绩(秒)", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            int rowIdx = 1;
            for (CubeRecord r : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getId());
                row.createCell(1).setCellValue(r.getTimeMs());
                row.createCell(2).setCellValue(r.getTimeMs() / 1000.0);
                row.createCell(3).setCellValue(
                        r.getCreateTime() == null ? "" : r.getCreateTime().format(FMT));
            }
            wb.write(out);
            log.info("成绩导出成功 userId={} 行数={}", userId, list.size());
        }
    }
}
