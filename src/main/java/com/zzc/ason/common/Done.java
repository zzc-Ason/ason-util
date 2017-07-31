package com.zzc.ason.common;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 07 月 28 日
 */
public class Done {
    private static final Logger LOGGER = Logger.getLogger(Done.class);

    public static String parseAttr(Pattern PATTERN, String line, int index) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            return matcher.group(index);
        }
        return null;
    }

    public static String parseStr(Object source) {
        return source == null ? null : source.toString();
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
            LOGGER.info("file is not exists!");
        }
        return list_file;
    }
}
