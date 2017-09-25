package com.zzc.ason.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * author : Ason
 * createTime : 2017 年 07 月 27 日
 * className : CodecUtil
 * remark: URLEncoder加密解密助手
 */
@Slf4j
public final class CodecUtil {

    public static String encodeURL(String source) {
        String target;
        try {
            target = URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("encode url failure", e);
            throw new RuntimeException(e);
        }
        return target;
    }

    public static String decodeURL(String source) {
        String target;
        try {
            target = URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("decode url failure", e);
            throw new RuntimeException(e);
        }
        return target;
    }

    public static String md5(String source) {
        return DigestUtils.md5Hex(source);
    }
}
