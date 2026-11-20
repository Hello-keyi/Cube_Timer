package com.example.backend.vo;

import lombok.Data;


@Data
public class RecordStatsVO {
    private long total;

    private Integer bestMs;
    private String bestText;

    private Integer worstMs;
    private String worstText;

    private String avg5Text;
    private String avg12Text;

    private Integer todayBestMs;
    private String todayBestText;
}
