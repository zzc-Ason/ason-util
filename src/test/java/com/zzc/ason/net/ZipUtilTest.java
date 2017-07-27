package com.zzc.ason.net;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public class ZipUtilTest {

    private String srcFileName;
    private String zipFileName;
    private ArrayList<File> fileList;

    @Before
    public void setUp() throws Exception {
        String basePath = "D:/Project/SourceCode/ason-util/test/";
        srcFileName = basePath + "src.csv";
        zipFileName = basePath + "zip.zip";

        fileList = Lists.newArrayList();
        fileList.add(new File(srcFileName));
        fileList.add(new File(basePath + "src1.csv"));
    }

    @Test
    public void zip() throws Exception {
        ZipUtil.zip(new File(srcFileName), zipFileName);
    }

    @Test
    public void zip_encrypt() throws Exception {
        ZipUtil.zip_encrypt(srcFileName, zipFileName, "123");
    }

    @Test
    public void unZip_all() throws Exception {
        ZipUtil.unZip_all(zipFileName);
    }

    @Test
    public void zip_list() throws Exception {
        ZipUtil.zip_list(fileList, zipFileName);
    }

    @Test
    public void zip_encrypt_list() throws Exception {
        ZipUtil.zip_encrypt_list(fileList, zipFileName, "123");
    }

    @Test
    public void unZip_decrypt_all() throws Exception {
        ZipUtil.unZip_decrypt_all(zipFileName, "123");
    }
}