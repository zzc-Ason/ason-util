package com.zzc.ason.bean;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : PatternBean
 * remark: 日志pattern格式帮助类
 */
public class PatternBean {

    private String key;
    private String value;
    private Integer index;

    public PatternBean() {
    }

    public PatternBean(String value, Integer index) {
        this.value = value;
        this.index = index;
    }

    public PatternBean(String key, String value, Integer index) {
        this.key = key;
        this.value = value;
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "PatternBean{key='" + key + '\'' + ", value='" + value + '\'' + ", index=" + index + '}';
    }
}
