package com.zzc.ason.net;

import org.junit.Before;
import org.junit.Test;

/**
 * author : Ason
 * createTime : 2017 年 07 月 26 日
 */
public class EmailUtilTest {

    private EmailUtil emailUtil;

    @Before
    public void setUp() throws Exception {
//        String protocol = "smtp";       // remark：默认为smtp
        String smtp = "smtp.intellicredit.cn";
        String username = "dev@intellicredit.cn";
        String password = "nMJnpq9ZVws4Ce";
        String from = "dev@intellicredit.cn";
        String to = "wangxinsen@intellicredit.cn";
        String subject = "邮件主题";
        String body = "内容展示";
        emailUtil = new EmailUtil(smtp, username, password, from, to, subject, body);
    }

    @Test
    public void send() throws Exception {
        emailUtil.send();
    }
}