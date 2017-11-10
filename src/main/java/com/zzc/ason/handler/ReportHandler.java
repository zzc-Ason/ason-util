package com.zzc.ason.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zzc.ason.bean.DynamicBean;
import com.zzc.ason.bean.PatternBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : ReportHandler
 * remark: 日志文件读取助手
 */
@SuppressWarnings("Duplicates")
@Slf4j
public final class ReportHandler {

    private static final String UUID = "uuid";
    private static final String TIME = "time";
    private static final Pattern NEW_UUID_PATTERN = Pattern.compile("\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]");
    private static final Pattern OLD_UUID_PATTERN = Pattern.compile("\\[([0-9a-z]{8}[0-9a-z]{4}[0-9a-z]{4}[0-9a-z]{4}[0-9a-z]{12})\\]");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4}-[01]\\d-[0123]\\d [012]\\d:[0-6]\\d:[0-6]\\d)(,\\d{3})");

    /**
     * Describe : 读取日志到动态类集合：List<DynamicBean>
     */
    public static List<DynamicBean> acquireReportInfo(String path, String prefix, String suffix, Date startTime, Date endTime, Map<String, PatternBean> patternMap) {
        List<DynamicBean> reportList = Lists.newCopyOnWriteArrayList();
        try {
            Date nowTime = startTime;
            while (DateUtils.truncatedCompareTo(nowTime, endTime, Calendar.DATE) < 1) {
                String[] logPrefixS = prefix.split(",");
                String date = DateFormatUtils.format(nowTime, "yyyyMMdd");
                for (String _logPrefix : logPrefixS) {
                    String fullPath = path + _logPrefix + date + suffix;
                    File file = new File(fullPath);
                    if (!file.exists()) {
                        log.info("[log path is not exists: " + file.getAbsolutePath() + "]");
                        continue;
                    }
                    log.debug("[read log path is " + file.getAbsolutePath() + "]");
                    Map<String, Map<String, Object>> reportMap = readReport(file, patternMap);
                    compoundReportBean(reportList, reportMap.values());
                }
                nowTime = DateUtils.addDays(nowTime, 1);
            }
        } catch (Exception e) {
            log.error("[acquire report info failure]");
            throw new RuntimeException(e);
        }
        return reportList;
    }

    /**
     * Describe : 日志对象转换为动态类集合
     */
    private static void compoundReportBean(List<DynamicBean> reportList, Collection<Map<String, Object>> reportBeanList) throws Exception {
        if (CollectionUtils.isEmpty(reportBeanList)) return;
        for (Map<String, Object> reportBean : reportBeanList) {
            DynamicBean dynamicBean = new DynamicBean(reportBean);
            reportList.add(dynamicBean);
        }
    }

//    /**
//     * Describe : 读取日志到指定类集合：List<T>
//     */
//    public static <T> List<T> acquireReportInfo(String path, String prefix, String suffix, Date startTime, Date endTime, Map<String, PatternBean> patternMap, Class<T> cls) {
//        List<T> reportList = Lists.newCopyOnWriteArrayList();
//        try {
//            Date nowTime = startTime;
//            while (DateUtils.truncatedCompareTo(nowTime, endTime, Calendar.DATE) < 1) {
//                String[] logPrefixS = prefix.split(",");
//                String date = DateFormatUtils.format(nowTime, "yyyyMMdd");
//                for (String _logPrefix : logPrefixS) {
//                    String fullPath = path + _logPrefix + date + suffix;
//                    File file = new File(fullPath);
//                    if (!file.exists()) {
//                        log.debug("log path is not exists: " + file.getAbsolutePath());
//                        continue;
//                    }
//                    log.debug("read log: " + file.getAbsolutePath());
//                    Map<String, Map<String, Object>> reportMap = readReport(file, patternMap);
//                    compoundReportBean(reportList, reportMap.values(), cls);
//                }
//                nowTime = DateUtils.addDays(nowTime, 1);
//            }
//        } catch (Exception e) {
//            log.error("acquire report info failure", e);
//            throw new RuntimeException(e);
//        }
//        return reportList;
//    }
//
//    /**
//     * Describe : 日志对象转换为指定类集合
//     */
//    private static <T> void compoundReportBean(List<T> reportList, Collection<Map<String, Object>> reportBeanList, Class<T> cls) throws Exception {
//        if (CollectionUtils.isEmpty(reportBeanList)) return;
//        for (Map<String, Object> reportBean : reportBeanList) {
//            T t = MapBeanUtils.mapToObject(reportBean, cls);
//            reportList.add(t);
//        }
//    }

    /**
     * Describe : 逐行读取日志，工具：LineIterator
     */
    private static Map<String, Map<String, Object>> readReport(File file, Map<String, PatternBean> patternMap) {
        Map<String, Map<String, Object>> reportMap = Maps.newHashMap();      // 过滤器
        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator(file, "UTF-8");
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                // uuid
                String uuidNew = parseAttr(NEW_UUID_PATTERN, line, 1);
                String uuid = StringUtils.isBlank(uuidNew) ? parseAttr(OLD_UUID_PATTERN, line, 1) : uuidNew;
                // time
                String time = parseAttr(TIME_PATTERN, line, 1);
                // remark：reportBean
                if (StringUtils.isBlank(uuid) || StringUtils.isBlank(time)) continue;   // 过滤掉不存在uuid与time的日志，认为是坏日志
                if (!reportMap.containsKey(uuid)) {
                    reportMap.put(uuid, Maps.<String, Object>newHashMap());
                    Map<String, Object> reportBean = reportMap.get(uuid);
                    reportBean.put(UUID, uuid);
                    reportBean.put(TIME, time);
                }
                Map<String, Object> reportBean = reportMap.get(uuid);
                // pattern
                for (Map.Entry<String, PatternBean> entryPattern : patternMap.entrySet()) {
                    String key = entryPattern.getKey();

                    if (reportBean.get(key) != null) continue;

                    PatternBean patternBean = entryPattern.getValue();
                    String value = parseAttr(Pattern.compile(patternBean.getValue()), line, patternBean.getIndex());
                    reportBean.put(key, value);
                }
            }
        } catch (IOException e) {
            log.error("[read report failure]");
            throw new RuntimeException(e);
        } finally {
            if (lineIterator != null) lineIterator.close();
        }
        return reportMap;
    }

    /**
     * Describe : 全部加载到内存后，再读取日志
     */
    private static Map<String, Map<String, Object>> readReport(List<String> readLines, Map<String, PatternBean> patternMap) throws Exception {
        Map<String, Map<String, Object>> reportMap = Maps.newHashMap();      // 过滤器
        for (String line : readLines) {
            // uuid
            String uuidNew = parseAttr(NEW_UUID_PATTERN, line, 1);
            String uuid = StringUtils.isBlank(uuidNew) ? parseAttr(OLD_UUID_PATTERN, line, 1) : uuidNew;
            // time
            String time = parseAttr(TIME_PATTERN, line, 1);
            // remark：reportBean
            if (StringUtils.isBlank(uuid) || StringUtils.isBlank(time)) continue;   // 过滤掉不存在uuid与time的日志，认为是坏日志
            if (!reportMap.containsKey(uuid)) {
                reportMap.put(uuid, Maps.<String, Object>newHashMap());
                Map<String, Object> reportBean = reportMap.get(uuid);
                reportBean.put(UUID, uuid);
                reportBean.put(TIME, time);
            }
            Map<String, Object> reportBean = reportMap.get(uuid);
            // pattern
            for (Map.Entry<String, PatternBean> entryPattern : patternMap.entrySet()) {
                String key = entryPattern.getKey();

                if (reportBean.get(key) != null) continue;

                PatternBean patternBean = entryPattern.getValue();
                String value = parseAttr(Pattern.compile(patternBean.getValue()), line, patternBean.getIndex());
                reportBean.put(key, value);
            }
        }
        return reportMap;
    }

    /**
     * Describe : 获取文件内容到指定类集合，过滤行号：filterLineIndex
     */
    public static List<DynamicBean> acquireFileObject(String src, Map<String, PatternBean> patternMap, Integer... filterLineIndex) {
        File file = new File(src);
        if (!file.exists()) throw new RuntimeException("[log path is not exists: " + file.getAbsolutePath() + "]");
        log.debug("[read log path is " + file.getAbsolutePath() + "]");

        List<DynamicBean> fileList = Lists.newArrayList();
        try {
            Map<Integer, Map<String, Object>> fileMap = readFile(file, patternMap, filterLineIndex);
            compoundReportBean(fileList, fileMap.values());
        } catch (Exception e) {
            log.error("[read report failure]");
            throw new RuntimeException(e);
        }
        return fileList;
    }

    /**
     * Describe : 读取文件
     */
    private static Map<Integer, Map<String, Object>> readFile(File file, Map<String, PatternBean> patternMap, Integer... filterLineIndex) throws Exception {
        Map<Integer, Map<String, Object>> fileMap = Maps.newHashMap();      // 容器
        LineIterator lineIterator = null;
        lineIterator = FileUtils.lineIterator(file, "UTF-8");
        int row = 0;
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            if (ArrayUtils.contains(filterLineIndex, ++row)) continue;  // 过滤指定行号
            if (!fileMap.containsKey(row)) fileMap.put(row, Maps.<String, Object>newHashMap());
            Map<String, Object> fileBean = fileMap.get(row);
            // pattern
            for (Map.Entry<String, PatternBean> entryPattern : patternMap.entrySet()) {
                String key = entryPattern.getKey();
                if (fileBean.get(key) != null) continue;
                PatternBean patternBean = entryPattern.getValue();
                String value = parseAttr(Pattern.compile(patternBean.getValue()), line, patternBean.getIndex());
                if (StringUtils.isNotEmpty(value)) fileBean.put(key, value);
            }
        }
        return fileMap;
    }

    /**
     * Describe : 解析指定格式的行
     */
    public static String parseAttr(Pattern PATTERN, String line, int index) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            return matcher.group(index);
        }
        return null;
    }
}
