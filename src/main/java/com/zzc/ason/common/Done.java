package com.zzc.ason.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 07 月 28 日
 */
public class Done {
    private static final Logger LOGGER = Logger.getLogger(Done.class);

    public static String acquireValueByFieldName(Object obj, String fieldName) throws Exception {
        if (obj == null) return null;
        if (StringUtils.isBlank(fieldName)) return null;

        PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
        Method getMethod = pd.getReadMethod();
        Object value = getMethod.invoke(obj);

        return value == null ? null : value.toString();
    }

    public static String handlerTimeArgs(String arg) {
        if (StringUtils.isNotBlank(arg)) {
            try {
                DateUtils.parseDate(arg, DateFormat.DATE_FORMAT_1);
                return arg;
            } catch (ParseException e) {
                LOGGER.error("startTime or endTime is invalid.");
                throw new RuntimeException("args \"" + arg + "\" is invalid", e);
            }
        }
        return null;
    }

    public static String handlerNullArgs(String[] args, int index) {
        String arg;
        try {
            arg = args[index];
        } catch (Exception e) {
            arg = null;
        }
        return arg;
    }

    public static String trim(Object obj) {
        String back_str = "";
        if (obj != null) {
            if (obj instanceof Date) {
                back_str = DateFormatUtils.format((Date) obj, DateFormat.DATE_FORMAT_3);
            } else {
                String[] strs = obj.toString().trim().split("");
                for (String s : strs) {
                    if (!" ".equals(s) && !"\t".equals(s)) {
                        back_str += s;
                    }
                }
            }
        }
        return back_str;
    }

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
