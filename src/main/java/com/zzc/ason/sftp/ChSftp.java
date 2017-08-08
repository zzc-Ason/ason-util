package com.zzc.ason.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.zzc.ason.common.Done;
import com.zzc.ason.common.Symbol;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

public class ChSftp {
    private static final Logger LOGGER = Logger.getLogger(ChSftp.class);

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
            LOGGER.info("src file path is not exists.");
            return;
        }
        String dstFullPath = dstBaseFile + dst;
        if (!isDirExist(dstFullPath)) {
            LOGGER.info("create remote server file path: " + dstFullPath);
            chSftp.mkdir(dstFullPath);
        }
        LOGGER.info("start to upload file \"" + srcFullPath + "\" to \"" + dstFullPath + "\"");
        if (file.isDirectory()) {
            LinkedList<File> srcFiles = Done.traverseFolder(srcFullPath);
            for (File srcFile : srcFiles) {
                LOGGER.info("upload file: " + srcFile.getAbsolutePath());
                chSftp.put(srcFile.getAbsolutePath(), dstFullPath, ChannelSftp.OVERWRITE);
            }
        }
        if (file.isFile()) {
            LOGGER.info("upload file: " + file.getAbsolutePath());
            chSftp.put(srcFullPath, dstFullPath, ChannelSftp.OVERWRITE);
        }
        LOGGER.info("file upload over.");
    }

    public void rmDir(String dst) throws Exception {
        String fullPath = dstBaseFile + dst;
        if (!isDirExist(fullPath)) {
            LOGGER.info("remote server file path is not exists.");
            return;
        }
        LOGGER.info("start to remove remote file: " + fullPath);
        Vector ls = chSftp.ls(fullPath);
        if (!ls.isEmpty()) {
            ListIterator listIterator = ls.listIterator();
            while (listIterator.hasNext()) {
                Object next = listIterator.next();
                String[] files = next.toString().split(Symbol.BLANK);
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
        LOGGER.info("remove dir over: " + fullPath);
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