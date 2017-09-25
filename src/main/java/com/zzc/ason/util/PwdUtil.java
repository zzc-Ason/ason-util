package com.zzc.ason.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : PwdUtil
 * remark: 单行密码文件读取助手
 */
@Slf4j
public final class PwdUtil {

    public static String acquirePwd(String filePath) {
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String value;
            while ((value = br.readLine()) != null) {
                if (StringUtils.isNotBlank(value)) return value.trim();
            }
        } catch (Exception e) {
            log.error("Failed to load file " + filePath, e);
            throw new RuntimeException(e);
        }
        return null;
    }
}
