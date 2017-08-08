package com.zzc.ason.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : FileUtil
 * remark: 文件操作工具
 */
public final class FileUtil {
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class);

    public static Collection<File> listFiles(String srcPath, String prefix, String suffix) {
        Collection<File> collection = CollectionUtils.EMPTY_COLLECTION;
        File file = FileUtils.getFile(srcPath);
        if (file.exists()) {
            IOFileFilter prefixFileFilter = null;
            if (StringUtils.isNotBlank(prefix)) prefixFileFilter = FileFilterUtils.prefixFileFilter(prefix);

            IOFileFilter suffixFileFilter = null;
            if (StringUtils.isNotBlank(suffix)) suffixFileFilter = FileFilterUtils.suffixFileFilter(suffix);

            collection.addAll(FileUtils.listFiles(file, prefixFileFilter, suffixFileFilter));
        }
        return collection;
    }
}
