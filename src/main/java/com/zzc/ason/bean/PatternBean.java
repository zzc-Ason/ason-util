package com.zzc.ason.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : PatternBean
 * remark: 日志pattern格式帮助类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatternBean {
    private String key;
    private String value;
    private Integer index;
}
