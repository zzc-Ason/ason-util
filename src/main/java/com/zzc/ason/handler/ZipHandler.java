package com.zzc.ason.handler;

import com.zzc.ason.net.ZipUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ZipHandler
 * remark: 压缩工具
 */
public class ZipHandler {
    private static final Logger LOGGER = Logger.getLogger(ZipHandler.class);

    public static void zipEncrypt(String srcPath, String zipFile, String secret) {
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
                ZipUtil.zip_encrypt_list(files, zipFile, secret);
            }
        } catch (Exception e) {
            LOGGER.error("zip and encrypt file failure", e);
            throw new RuntimeException(e);
        }
    }
}
