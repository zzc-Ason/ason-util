package com.zzc.ason.handler;

import com.google.common.collect.Lists;
import com.zzc.ason.net.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ZipHandler
 * remark: 压缩工具
 */
@Slf4j
public class ZipHandler {

    public static void zipEncrypt(String srcPath, String zipFile, String secret) {
        try {
            File file = new File(srcPath);
            if (!file.exists()) return;
            ArrayList<File> files = Lists.newArrayList();

            if (file.isFile()) {
                files.add(file);
            }
            if (file.isDirectory()) {
                Collection<File> zipFiles = FileUtils.listFiles(file, FileFilterUtils.fileFileFilter(), null);
                files.addAll(zipFiles);
            }

            if (!CollectionUtils.isEmpty(files)) {
                ZipUtil.zip_encrypt_list(files, zipFile, secret);
            }
        } catch (Exception e) {
            log.error("[zip and encrypt file failure]");
            throw new RuntimeException(e);
        }
    }

    public static void zipEncrypt(String srcPath, String zipFile) {
        try {
            File file = new File(srcPath);
            if (!file.exists()) return;
            ArrayList<File> files = new ArrayList<File>();

            if (file.isFile()) {
                files.add(file);
            }
            if (file.isDirectory()) {
                Collection<File> zipFiles = FileUtils.listFiles(file, FileFilterUtils.fileFileFilter(), null);
                files.addAll(zipFiles);
            }

            if (!CollectionUtils.isEmpty(files)) {
                ZipUtil.zip_list(files, zipFile);
            }
        } catch (Exception e) {
            log.error("[zip and encrypt file failure]");
            throw new RuntimeException(e);
        }
    }
}
