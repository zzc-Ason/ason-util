package com.zzc.ason.net;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;
import java.util.Map.Entry;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : EmailUtil
 * remark: 邮件发送助手
 */
@Slf4j
public final class EmailUtil {

    private String protocol = "smtp"; // 邮件传输协议
    private String smtp;               // 邮件服务器主机名
    private String username;           // 登录用户名
    private String password;           // 登录密码
    private String from;               // 发件人地址
    private String to;                 // 收件人地址
    private String subject;            // 邮件主题
    private String body;               // 邮件内容

    Map<String, String> imageMap = Maps.newHashMap();               // 图片附件
    List<String> fileList = Lists.newArrayList();                   // war、jar等附件

    private EmailUtil() {
    }

    public EmailUtil(String smtp, String username, String password, String from, String to, String subject, String body) {
        this.smtp = smtp;
        this.username = username;
        this.password = password;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public EmailUtil(String protocol, String smtp, String username, String password, String from, String to, String subject, String body) {
        this.protocol = protocol;
        this.smtp = smtp;
        this.username = username;
        this.password = password;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public EmailUtil(String smtp, String username, String password, String from, String to, String subject, String body, List<String> fileList) {
        this.smtp = smtp;
        this.username = username;
        this.password = password;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.fileList = fileList;
    }

    public EmailUtil(String smtp, String username, String password, String from, String to, String subject, String body, Map<String, String> imageMap) {
        this.smtp = smtp;
        this.username = username;
        this.password = password;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.imageMap = imageMap;
    }

    public EmailUtil(Map<String, String> map, List<String> fileList, Map<String, String> imageMap) {
        String _protocol = map.get("protocol");
        if (StringUtils.isNotBlank(_protocol)) this.protocol = _protocol;
        this.smtp = map.get("smtp");
        this.username = map.get("username");
        this.password = map.get("password");
        this.from = map.get("from");
        this.to = map.get("to");
        this.subject = map.get("subject");
        this.body = map.get("body");
        this.fileList = fileList;
        this.imageMap = imageMap;
    }

    public void send() {
        Properties pros = new Properties();
        pros.setProperty("mail.transport.protocol", this.protocol);
        pros.setProperty("mail.host", this.smtp);
        pros.put("mail.smtp.auth", "true");
        try {
            MySendMailAuthenticator ma = new MySendMailAuthenticator(this.username, this.password);
            Session session = Session.getInstance(pros, ma);
            session.setDebug(false);
            MimeMessage msg = createMessage(session);
            Transport ts = session.getTransport();
            ts.connect();
            ts.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
            ts.close();
        } catch (Exception e) {
            log.error("[send email failure]");
            throw new RuntimeException(e);
        }
    }

    public MimeMessage createMessage(Session session) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(this.from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(this.subject);
        MimeMultipart allMultipart = new MimeMultipart();
        MimeBodyPart contentPart = createContent(this.body);
        allMultipart.addBodyPart(contentPart);
        for (int i = 0; i < fileList.size(); i++) {
            allMultipart.addBodyPart(createAttachment(fileList.get(i)));
        }
        message.setContent(allMultipart);
        message.saveChanges();
        return message;
    }

    public MimeBodyPart createContent(String body) throws Exception {
        MimeBodyPart contentPart = new MimeBodyPart();
        MimeMultipart contentMultipart = new MimeMultipart("related");
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(body, "text/html;charset=UTF-8");
        contentMultipart.addBodyPart(htmlBodyPart);
        if (imageMap != null && imageMap.size() > 0) {
            Set<Entry<String, String>> set = imageMap.entrySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                Entry<String, String> entry = (Entry<String, String>) iterator.next();
                MimeBodyPart gifBodyPart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(entry.getValue());//图片所在的目录的绝对路径
                gifBodyPart.setDataHandler(new DataHandler(fds));
                gifBodyPart.setContentID(entry.getKey());   //cid的值
                contentMultipart.addBodyPart(gifBodyPart);
            }
        }
        contentPart.setContent(contentMultipart);
        return contentPart;
    }

    public MimeBodyPart createAttachment(String filename) throws Exception {
        MimeBodyPart attachPart = new MimeBodyPart();
        FileDataSource fsd = new FileDataSource(filename);
        attachPart.setDataHandler(new DataHandler(fsd));
        BASE64Encoder enc = new BASE64Encoder();
        attachPart.setFileName("=?GBK?B?" + enc.encode(fsd.getName().getBytes("GBK")) + "?=");
        return attachPart;
    }

    class MySendMailAuthenticator extends Authenticator {
        String username = "";
        String password = "";

        public MySendMailAuthenticator(String user, String pass) {
            this.username = user;
            this.password = pass;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
