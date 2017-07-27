package com.zzc.ason.util;

import com.zzc.ason.common.PrintOut;
import com.zzc.ason.test.bean.User;
import org.junit.Before;
import org.junit.Test;

/**
 * author : Ason
 * createTime : 2017 年 07 月 27 日
 */
public class JsonUtilTest {

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(1, "JOKE", "男");
    }

    @Test
    public void toJson() throws Exception {
        String s = JsonUtil.toJson(user);
        PrintOut.str(s);
    }

    @Test
    public void fromJson() throws Exception {
        User u = JsonUtil.fromJson("{\"id\":2,\"name\":\"ROSE\",\"gender\":\"女\"}", User.class);
        PrintOut.object(u);
    }

}