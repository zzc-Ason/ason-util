package com.zzc.ason.util;

import org.apache.log4j.Logger;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public final class PathUtil {
    private static final Logger LOGGER = Logger.getLogger(PathUtil.class);

    public static <T> String acquireItemPath(Class<T> cls) {
        final String basePath = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String filePath = basePath.substring(0, basePath.lastIndexOf("/") + 1);
        LOGGER.info("acquire item filePath: " + filePath);
        return filePath;
    }
}
