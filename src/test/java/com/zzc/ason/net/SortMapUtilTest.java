package com.zzc.ason.net;

import com.google.common.collect.Maps;
import com.zzc.ason.common.PrintOut;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public class SortMapUtilTest {

    private Map<String, Object> map;

    @Before
    public void setUp() throws Exception {
        map = Maps.newHashMap();
        map.put("2", "b");
        map.put("1", "a");
        map.put("3", "c");
    }

    @Test
    public void sortMapByKey() throws Exception {
        Map<String, Object> r = SortMapUtil.sortMapByKey(map);
        PrintOut.map(r);
    }

    @Test
    public void sortMapByValue() throws Exception {
        Map<String, Object> r = SortMapUtil.sortMapByKey(map);
        PrintOut.map(r);
    }
}