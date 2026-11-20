package com.example.backend.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatsUtilsTest {

    @Test
    void avg5_去掉一个最好一个最差() {
        // 1000, 2000, 3000, 4000, 5000
        // 排序后去掉 1000 和 5000 → 剩 2000+3000+4000 = 9000
        // 9000 / 3 = 3000ms = 3.000 秒
        assertEquals("3.000", StatsUtils.calcAvg(List.of(1000, 2000, 3000, 4000, 5000), 5));
    }

    @Test
    void avg5_数据不足返回null() {
        assertNull(StatsUtils.calcAvg(List.of(1000, 2000, 3000), 5));
        assertNull(StatsUtils.calcAvg(List.of(), 5));
        assertNull(StatsUtils.calcAvg(null, 5));
    }

    // ✍️ 你来写这个：验证「最大值出现两次时，只去掉一个」
    @Test
    void avg5_最大值重复时只去掉一个() {
        // 输入：8888, 8888, 1234, 5555, 1111
        assertEquals("5.226", StatsUtils.calcAvg(List.of(8888, 8888, 1234, 5555, 1111), 5));
        // 排序：1111, 1234, 5555, 8888, 8888
        // 去掉 1111 和 8888 → 剩 1234 + 5555 + 8888 = 15677
        // 15677 / 3 = 5225.667ms = 5.226 秒
        // 你来写断言
    }

    @Test
    void toText_null安全() {
        assertNull(StatsUtils.toText(null));
        assertEquals("1.234", StatsUtils.toText(1234));
        assertEquals("600.000", StatsUtils.toText(600000));
    }
}