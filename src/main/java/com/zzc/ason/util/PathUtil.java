package com.zzc.ason.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
@Slf4j
public final class PathUtil {

    public static <T> String acquireItemPath(Class<T> cls) {
        final String basePath = cls.getProtectionDomain().getCodeSource().getLocation().getPath();
        final String filePath = basePath.substring(0, basePath.lastIndexOf("/") + 1);
        log.info("[acquire item filePath is " + filePath + "]");
        return filePath;
    }

    public static void deleteQuietly(String fillPath) {
        File file = FileUtils.getFile(fillPath);
        if (file.exists()) FileUtils.deleteQuietly(file);
        log.info("[delete dir over] [dir is: " + fillPath + "]");
    }
}
