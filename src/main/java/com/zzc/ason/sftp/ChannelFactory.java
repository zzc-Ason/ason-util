package com.zzc.ason.sftp;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by Administrator on 2017/6/28 0028.
 */
public class ChannelFactory {
    private static final Logger LOGGER = Logger.getLogger(ChannelFactory.class);

    private String ftpHost;
    private String ftpUserName;
    private String ftpPassword;
    private String ftpLocation;
    private String port = "22";

    private Session session = null;
    private Channel channel = null;

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
        LOGGER.info("Connected successfully to ftpHost = " + ftpHost + ",as ftpUserName = " + ftpUserName);
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
