package com.zzc.ason.util;

import com.zzc.ason.common.PrintOut;
import com.zzc.ason.test.bean.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * author : Ason
 * createTime : 2017 年 07 月 27 日
 */
public class ReflectionUtilTest {

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(1, "JOKE", "男");
    }

    @Test
    public void newInstance() throws Exception {
        Object u = ReflectionUtil.newInstance(User.class);
        PrintOut.object(u);
    }

    @Test
    public void newInstance1() throws Exception {
        Object u = ReflectionUtil.newInstance("com.zzc.ason.test.bean.User");
        PrintOut.object(u);
    }

    @Test
    public void invokeMethod() throws Exception {
        Method method = User.class.getMethod("setName", String.class);
        Object result = ReflectionUtil.invokeMethod(user, method, "JIM");
        PrintOut.object(result);
        PrintOut.object(user);
    }

    @Test
    public void setField() throws Exception {
        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            PrintOut.object(field);
        }
        Field field = User.class.getField("name");
        ReflectionUtil.setField(user, field, "KATE");
        PrintOut.object(user);
    }

}