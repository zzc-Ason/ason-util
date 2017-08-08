package com.zzc.ason.bean;

import com.google.common.collect.Maps;
import com.zzc.ason.common.DateFormat;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public class DynamicBeanTest {

    private DynamicBean dynamicBean;
    private Map<String, Object> map = Maps.newHashMap();

    @Before
    public void setUp() throws Exception {
        map.put("1", "a");
        map.put("2", 2);
        map.put("3", null);
        map.put("4", new Date());
        dynamicBean = new DynamicBean(map);
    }

    @Test(expected = ClassCastException.class)
    public void setValue() throws Exception {
        Assert.assertEquals(Date.class, dynamicBean.getValue("4").getClass());
        dynamicBean.setValue("4", DateUtils.parseDate("2017-07-26 10:48:00", DateFormat.DATE_FORMAT_PATTERN));

        Date date = (Date) dynamicBean.getValue("4");
        Assert.assertEquals(DateFormatUtils.format(new Date(), DateFormat.DATE_FORMAT_1), DateFormatUtils.format(date, DateFormat.DATE_FORMAT_1));

        dynamicBean.setValue("5", "e");
        Assert.assertEquals("e", dynamicBean.getValue("5"));

        dynamicBean.setValue("4", "f");
    }

}