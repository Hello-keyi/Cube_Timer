package com.example.backend.service;

import java.io.IOException;
import java.io.OutputStream;

public interface RecordExportService {

    /**
     * 把当前用户的成绩导出成 xlsx，写进传入的输出流
     * ★ 注意：参数是 OutputStream，不是 HttpServletResponse（不污染 Service 层）
     */
    void exportTo(OutputStream out) throws IOException;
}