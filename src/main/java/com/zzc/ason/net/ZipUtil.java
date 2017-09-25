package com.zzc.ason.net;

import com.zzc.ason.common.Symbol;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ZipUtil
 * remark: 文件压缩工具
 */
@Slf4j
public final class ZipUtil {

    public static void zip(File file, String destFile) throws ZipException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        ZipFile zipFile = new ZipFile(destFile);
        zipFile.addFile(file, parameters);
    }

    public static void zip_encrypt(String file, String destFile, String key) throws ZipException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
//        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
//        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        parameters.setPassword(key);

        ZipFile zipFile = new ZipFile(destFile);
        zipFile.addFile(new File(file), parameters);
    }

    public static void unZip_all(String srcFile) throws ZipException {
        ZipFile zipFile = new ZipFile(srcFile);
        zipFile.extractAll(srcFile.substring(0, srcFile.lastIndexOf(Symbol.LINUX_SEPARATOR)));
    }

    public static void zip_list(ArrayList<File> files, String destFile) throws ZipException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        ZipFile zipFile = new ZipFile(destFile);
        zipFile.addFiles(files, parameters);
    }

    public static void zip_encrypt_list(ArrayList<File> files, String destFile, String key) throws ZipException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
//        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
//        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        parameters.setPassword(key);

        ZipFile zipFile = new ZipFile(destFile);
        zipFile.addFiles(files, parameters);
    }

    public static void unZip_decrypt_all(String srcFile, String key) throws ZipException {
        ZipFile zipFile = new ZipFile(srcFile);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(key);
        }
        List fileHeaderList = zipFile.getFileHeaders();
        for (int i = 0; i < fileHeaderList.size(); i++) {
            FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
            zipFile.extractFile(fileHeader, srcFile.substring(0, srcFile.lastIndexOf(Symbol.LINUX_SEPARATOR)));
        }
    }


}
