package com.example.backend.util;


import java.util.List;
import java.util.Locale;

public final class StatsUtils {

    private StatsUtils() {}

    public static String calcAvg(List<Integer> times, int n) {
        if (times == null || times.size() < n) {
            return null;
        }
        double sum = 0;
        // 初值用第一条成绩，不要写死 60000（否则所有成绩都 > 60 秒时 min 就永远不更新）
        double max = times.getFirst();
        double min = times.getFirst();

        for (int i = 0; i < n; i++) {
            double now = times.get(i);
            sum += now;
            max = Math.max(max, now);
            min = Math.min(min, now);
        }
        double avgMs = (sum - max - min) / (n - 2);
        return String.format(Locale.ROOT, "%.3f", avgMs / 1000.0);
    }

    public static String toText(Integer ms) {
        return ms == null ? null : String.format(Locale.ROOT, "%.3f", ms / 1000.0);
    }

}
