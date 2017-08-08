package com.zzc.ason.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ChExec
 * remark: sftp操作助手ChannelExec
 */
public class ChExec {
    private static final Logger LOGGER = Logger.getLogger(ChExec.class);

    private Channel channel;
    private ChannelExec chExec;

    public ChExec(ChannelFactory channelFactory) throws Exception {
        channel = channelFactory.acquireChannel("exec");
        chExec = (ChannelExec) channel;
    }

    public void startChExec() throws Exception {
        if (chExec.isClosed()) chExec.start();
        if (!chExec.isConnected()) chExec.connect();
    }

    public void execCmd(String command) throws JSchException, IOException {
        BufferedReader reader = null;
        try {
            if (StringUtils.isNotBlank(command)) {
                channel.setInputStream(null);
                chExec.setCommand(command);
                chExec.setErrStream(System.err);
                reader = new BufferedReader(new InputStreamReader(chExec.getInputStream()));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    System.out.println(buf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
        }
    }

    public void closeChExec() {
        if (chExec != null) {
            if (chExec.isConnected()) chExec.disconnect();
            chExec = null;
        }
    }
}
