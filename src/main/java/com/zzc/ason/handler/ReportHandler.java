package com.zzc.ason.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zzc.ason.bean.DynamicBean;
import com.zzc.ason.bean.PatternBean;
import com.zzc.ason.common.Done;
import com.zzc.ason.net.MapBeanUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : ReportHandler
 * remark: 日志文件读取助手
 */
public final class ReportHandler {
    private static final Logger LOGGER = Logger.getLogger(ReportHandler.class);

    private static final String UUID = "uuid";
    private static final String TIME = "time";
    private static final Pattern NEW_UUID_PATTERN = Pattern.compile("\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]");
    private static final Pattern OLD_UUID_PATTERN = Pattern.compile("\\[([0-9a-z]{8}[0-9a-z]{4}[0-9a-z]{4}[0-9a-z]{4}[0-9a-z]{12})\\]");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{4}-[01]\\d-[0123]\\d [012]\\d:[0-6]\\d:[0-6]\\d)(,\\d{3})");

    public static List<DynamicBean> acquireReportInfo(String path, String prefix, String suffix, Date startTime, Date endTime, Map<String, PatternBean> patternMap) {
        List<DynamicBean> reportList = Lists.newCopyOnWriteArrayList();
        try {
            Date nowTime = startTime;
            while (DateUtils.truncatedCompareTo(nowTime, endTime, Calendar.DATE) < 1) {
                String[] logPrefixS = prefix.split(",");
                String date = DateFormatUtils.format(nowTime, "yyyyMMdd");
                for (String _logPrefix : logPrefixS) {
                    String fullPath = path + _logPrefix + date + suffix;
                    File file = FileUtils.getFile(fullPath);
                    if (!file.exists()) {
                        LOGGER.info("log path is not exists: " + file.getAbsolutePath());
                        continue;
                    }
                    LOGGER.info("read log: " + file.getAbsolutePath());
                    List<String> readLines = FileUtils.readLines(file, "UTF-8");
                    Map<String, Map<String, Object>> reportMap = readReport(readLines, patternMap);
                    compoundReportBean(reportList, reportMap);
                }
                nowTime = DateUtils.addDays(nowTime, 1);
                LOGGER.info(date + " report list size: " + reportList.size());
            }
        } catch (Exception e) {
            LOGGER.error("acquire report info failure", e);
            throw new RuntimeException(e);
        }
        return reportList;
    }

    private static void compoundReportBean(List<DynamicBean> reportList, Map<String, Map<String, Object>> reportMap) throws Exception {
        if (MapUtils.isEmpty(reportMap)) return;
        for (Map<String, Object> reportBean : reportMap.values()) {
            DynamicBean dynamicBean = new DynamicBean(reportBean);
            reportList.add(dynamicBean);
        }
    }

    public static <T> List<T> acquireReportInfo(String path, String prefix, String suffix, Date startTime, Date endTime, Map<String, PatternBean> patternMap, Class<T> cls) {
        List<T> reportList = Lists.newCopyOnWriteArrayList();
        try {
            Date nowTime = startTime;
            while (DateUtils.truncatedCompareTo(nowTime, endTime, Calendar.DATE) < 1) {
                String[] logPrefixS = prefix.split(",");
                String date = DateFormatUtils.format(nowTime, "yyyyMMdd");
                for (String _logPrefix : logPrefixS) {
                    String fullPath = path + _logPrefix + date + suffix;
                    File file = FileUtils.getFile(fullPath);
                    if (!file.exists()) {
                        LOGGER.info("log path is not exists: " + file.getAbsolutePath());
                        continue;
                    }
                    LOGGER.info("read log: " + file.getAbsolutePath());
                    List<String> readLines = FileUtils.readLines(file, "UTF-8");
                    Map<String, Map<String, Object>> reportMap = readReport(readLines, patternMap);
                    compoundReportBean(reportList, reportMap, cls);
                }
                nowTime = DateUtils.addDays(nowTime, 1);
                LOGGER.info(date + " report list size: " + reportList.size());
            }
        } catch (Exception e) {
            LOGGER.error("acquire report info failure", e);
            throw new RuntimeException(e);
        }
        return reportList;
    }

    private static <T> void compoundReportBean(List<T> reportList, Map<String, Map<String, Object>> reportMap, Class<T> cls) throws Exception {
        if (MapUtils.isEmpty(reportMap)) return;
        for (Map<String, Object> entry : reportMap.values()) {
            T t = MapBeanUtils.mapToObject(entry, cls);
            reportList.add(t);
        }
    }

    private static Map<String, Map<String, Object>> readReport(List<String> readLines, Map<String, PatternBean> patternMap) throws Exception {
        Map<String, Map<String, Object>> reportMap = Maps.newHashMap();      // 过滤器
        for (String line : readLines) {
            // uuid
            String uuidNew = Done.parseAttr(NEW_UUID_PATTERN, line, 1);
            String uuid = StringUtils.isBlank(uuidNew) ? Done.parseAttr(OLD_UUID_PATTERN, line, 1) : uuidNew;
            // time
            String time = Done.parseAttr(TIME_PATTERN, line, 1);
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
                String value = Done.parseAttr(Pattern.compile(patternBean.getValue()), line, patternBean.getIndex());
                reportBean.put(key, value);
            }
        }
        return reportMap;
    }
}
