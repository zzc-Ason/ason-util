package com.zzc.ason.handler;

import com.google.common.collect.Maps;
import com.zzc.ason.bean.DynamicBean;
import com.zzc.ason.bean.PatternBean;
import com.zzc.ason.common.Constant;
import com.zzc.ason.common.PrintOut;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public class ReportHandlerTest {

    private String path;
    private String prefix;
    private String suffix;
    private Date startTime;
    private Date endTime;
    private Map<String, PatternBean> patternBeanMap;

    @Before
    public void setUp() throws Exception {
        path = "D:/Project/task21_integrateReport/log/gongan_log/";
        prefix = "proxy_proxy1_,proxy_proxy2_";
        suffix = ".log";
        startTime = DateUtils.parseDate("20170530", Constant.DATE_FORMAT_1);
        endTime = DateUtils.parseDate("20170531", Constant.DATE_FORMAT_1);
        patternBeanMap = Maps.newHashMap();
        patternBeanMap.put("email", new PatternBean("\\[((\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3}))\\]", 1));
        patternBeanMap.put("requestPath", new PatternBean("\\[id_check\\]", 0));
        patternBeanMap.put("charge", new PatternBean("charge success for ([0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12})", 0));
        patternBeanMap.put("billingException", new PatternBean("\\[Billing failed with exception:\\]", 0));
    }

    @Test
    public void acquireReportInfo() throws Exception {
        List<DynamicBean> reportList = ReportHandler.acquireReportInfo(path, prefix, suffix, startTime, endTime, patternBeanMap);
        PrintOut.list(reportList);
    }

    @Test
    public void acquireReportInfo1() throws Exception {
        List<DynamicBean> reportList = ReportHandler.acquireReportInfo(path, prefix, suffix, startTime, endTime, patternBeanMap);
        PrintOut.list(reportList);
    }

}