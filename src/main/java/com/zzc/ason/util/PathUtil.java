package com.zzc.ason.util;

import lombok.extern.slf4j.Slf4j;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
@Slf4j
public final class PathUtil {

    public static <T> String acquireItemPath(Class<T> cls) {
        final String basePath = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String filePath = basePath.substring(0, basePath.lastIndexOf("/") + 1);
        log.info("acquire item filePath: " + filePath);
        return filePath;
    }
}
