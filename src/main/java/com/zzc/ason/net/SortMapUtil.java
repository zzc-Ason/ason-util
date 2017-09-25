package com.zzc.ason.net;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : SortMapUtil
 * remark: map排序工具
 */
@Slf4j
public final class SortMapUtil {

    public static <K, T> Map<K, T> sortMapByKey(Map<K, T> oriMap) {
        if (MapUtils.isEmpty(oriMap)) return null;
        Map<K, T> sortedMap = new TreeMap<K, T>(new Comparator<K>() {
            public int compare(K key1, K key2) {
                int intKey1 = 0, intKey2 = 0;
                try {
                    intKey1 = getInt(key1);
                    intKey2 = getInt(key2);
                } catch (Exception e) {
                    intKey1 = 0;
                    intKey2 = 0;
                }
                return intKey1 - intKey2;
            }
        });
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    public static <K, T> Map<K, T> sortMapByValue(Map<K, T> oriMap) {
        if (MapUtils.isEmpty(oriMap)) return null;
        List<Map.Entry<K, T>> entryList = new ArrayList<Map.Entry<K, T>>(oriMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<K, T>>() {
            public int compare(Map.Entry<K, T> entry1, Map.Entry<K, T> entry2) {
                int value1 = 0, value2 = 0;
                try {
                    value1 = getInt(entry1.getValue());
                    value2 = getInt(entry2.getValue());
                } catch (Exception e) {
                    value1 = 0;
                    value2 = 0;
                }
                return value2 - value1;
            }
        });
        Map<K, T> sortedMap = Maps.newHashMap();
        Iterator<Map.Entry<K, T>> iterator = entryList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, T> tmpEntry = iterator.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    private static <T> int getInt(T t) throws Exception {
        int i = 0;
        Pattern p = Pattern.compile("^\\d+");
        Matcher m = p.matcher(t.toString());
        if (m.find()) {
            i = Integer.valueOf(m.group());
        }
        return i;
    }
}
