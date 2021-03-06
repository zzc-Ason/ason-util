package com.zzc.ason.sftp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ChannelFactory
 * remark: sftp操作助手ChannelFactory
 */
@Slf4j
public class ChannelFactory {

    private String ftpHost;
    private String ftpUserName;
    private String ftpPassword;
    private String ftpLocation;
    private String port = "22";

    private Session session = null;
    private Channel channel = null;

    public ChannelFactory(String ftpHost, String ftpUserName, String ftpPassword) {
        this.ftpHost = ftpHost;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
    }

    public ChannelFactory(String ftpHost, String ftpUserName, String ftpPassword, String port) throws JSchException {
        this.ftpHost = ftpHost;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.port = port;
        createSession(60000);
    }

    public ChannelFactory(String ftpHost, String ftpUserName, String ftpPassword, String port, String ftpLocation) throws Exception {
        this.ftpHost = ftpHost;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.port = port;
        this.ftpLocation = ftpLocation;
        createSession(60000);
    }

    private Session createSession(int timeout) throws JSchException {
        int ftpPort = 0;
        if (StringUtils.isNotBlank(port)) {
            ftpPort = Integer.valueOf(port);
        }
        JSch jsch = new JSch();         // 创建JSch对象
        session = jsch.getSession(ftpUserName, ftpHost, ftpPort);       // 根据用户名，主机ip，端口获取一个Session对象
        if (StringUtils.isNotBlank(ftpPassword)) {
            session.setPassword(ftpPassword);       // 设置密码
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);      // 为Session对象设置properties
        localUserInfo ui = new localUserInfo();
        session.setUserInfo(ui);
        session.setTimeout(timeout);        // 设置timeout时间
        log.info("[connected successfully to ftpHost = " + ftpHost + ",as ftpUserName = " + ftpUserName + "]");
        return session;
    }

    public Channel acquireChannel(String name) throws Exception {
        if (!session.isConnected()) {
            session.connect();
        }
        channel = session.openChannel(name);
        channel.connect();
        return channel;
    }

    // 登入SSH时的控制信息，设置不提示输入密码、不显示登入信息等
    public static class localUserInfo implements UserInfo {
        String password;

        public String getPassword() {
            return password;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {
        }
    }

    public void closeChannel() throws Exception {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }
}
