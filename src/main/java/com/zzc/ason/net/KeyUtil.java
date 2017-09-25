package com.zzc.ason.net;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : KeyUtil
 * remark: 按序产生UUID工具
 */
@Slf4j
public class KeyUtil {

    public static String generatorTimeUUID() {
        TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        return timeBasedGenerator.generate().toString();
    }

    public static String generatorUUID() {
        return UUID.randomUUID().toString();
    }

}