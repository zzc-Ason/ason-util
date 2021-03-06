package com.zzc.ason.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.zzc.ason.util.FileUtil;
import com.zzc.ason.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ChSftp
 * remark: sftp操作助手ChannelSftp
 */
@Slf4j
public class ChSftp {

    private String srcBaseFile;
    private String dstBaseFile;

    private ChannelSftp chSftp = null;

    public ChSftp(ChannelFactory channelFactory, String srcBaseFile, String dstBaseFile) throws Exception {
        this.srcBaseFile = srcBaseFile;
        this.dstBaseFile = dstBaseFile;
        chSftp = (ChannelSftp) channelFactory.acquireChannel("sftp");
    }

    public void startCHSftp() throws Exception {
        if (chSftp.isClosed()) chSftp.start();
        if (!chSftp.isConnected()) chSftp.connect();
        chSftp.cd(this.dstBaseFile);
    }

    public void batchUpload(String src, String dst) throws Exception {
        String srcFullPath = srcBaseFile + src;
        File file = FileUtils.getFile(srcFullPath);
        if (!file.exists()) {
            log.info("[file path is not exists: " + src + "]");
            return;
        }
        String dstFullPath = dstBaseFile + dst;
        if (!isDirExist(dstFullPath)) {
            log.info("[create remote server file path: " + dstFullPath + "]");
            chSftp.mkdir(dstFullPath);
        }
        log.info("[start to upload file \"" + srcFullPath + "\" to \"" + dstFullPath + "\"]");
        if (file.isDirectory()) {
            LinkedList<File> srcFiles = FileUtil.traverseFolder(srcFullPath);
            for (File srcFile : srcFiles) {
                log.info("[upload file path is " + srcFile.getAbsolutePath() + "]");
                chSftp.put(srcFile.getAbsolutePath(), dstFullPath, ChannelSftp.OVERWRITE);
            }
        }
        if (file.isFile()) {
            log.info("[upload file path is " + file.getAbsolutePath() + "]");
            chSftp.put(srcFullPath, dstFullPath, ChannelSftp.OVERWRITE);
        }
        log.info("[file upload over]");
    }

    public void rmDir(String dst) throws Exception {
        String fullPath = dstBaseFile + dst;
        if (!isDirExist(fullPath)) {
            log.info("[remote server file path is not exists]");
            return;
        }
        log.info("[start to remove remote file is " + fullPath + "]");
        Vector ls = chSftp.ls(fullPath);
        if (!ls.isEmpty()) {
            ListIterator listIterator = ls.listIterator();
            while (listIterator.hasNext()) {
                Object next = listIterator.next();
                String[] files = next.toString().split(StringUtil.BLANK);
                String fileName = files[files.length - 1];
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                }
                char fileAttr = files[0].charAt(0);
                if (fileAttr == 100) {
                    rmDir(dst + "/" + fileName);
                }
                if (fileAttr == 45 || fileAttr == 108) {
                    chSftp.rm(fullPath + fileName);
                }
            }
        }
        log.info("[remove dir over is " + fullPath + "]");
    }

    public boolean isDirExist(String directory) throws Exception {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = chSftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    public void closeChSftp() {
        if (chSftp != null) {
            if (chSftp.isConnected()) chSftp.quit();
            chSftp = null;
        }
    }
}