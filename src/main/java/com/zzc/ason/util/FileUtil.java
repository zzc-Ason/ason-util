package com.zzc.ason.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : FileUtil
 * remark: 文件操作工具
 */
@Slf4j
public final class FileUtil {

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

    public static boolean deleteQuietly(String src) {
        File file = FileUtils.getFile(src);
        if (!file.exists()) return true;
        return FileUtils.deleteQuietly(file);
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static LinkedList<File> traverseFolder(String path) {
        File file = new File(path);
        LinkedList<File> list_folder = Lists.newLinkedList();
        LinkedList<File> list_file = Lists.newLinkedList();
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    list_folder.add(file2);
                } else {
                    list_file.add(file2);
                }
            }
            while (!list_folder.isEmpty()) {
                File temp_file_ok = list_folder.removeFirst();
                files = temp_file_ok.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        list_folder.add(file2);
                    } else {
                        list_file.add(file2);
                    }
                }
            }
        } else {
            log.info("[file is not exists]");
        }
        return list_file;
    }
}
