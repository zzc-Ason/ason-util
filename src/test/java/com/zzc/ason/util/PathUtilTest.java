package com.zzc.ason.util;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public class PathUtilTest {
    private static final Logger LOGGER = Logger.getLogger(PathUtilTest.class);

    @Test
    public void acquireItemPath() throws Exception {
        final String filePath = PathUtil.acquireItemPath(PathUtilTest.class);
        LOGGER.info(filePath);
    }

}