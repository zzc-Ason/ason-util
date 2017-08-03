package com.zzc.ason.net;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;

/**
 * author : Ason
 * createTime : 2017 年 07 月 30 日 上午 9:24
 */
public class KeyUtil {

    public static String generatorTimeUUID() {
        TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        return timeBasedGenerator.generate().toString();
    }

    public static String generatorUUID() {
        return UUID.randomUUID().toString();
    }

}