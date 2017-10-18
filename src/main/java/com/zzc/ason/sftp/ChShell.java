package com.zzc.ason.sftp;

import com.jcraft.jsch.ChannelShell;
import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.oro.text.regex.MalformedPatternException;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Ason
 * createTime : 2017 年 08 月 08 日
 * className : ChShell
 * remark: sftp操作助手ChannelShell
 */
@Slf4j
public class ChShell {

    private ChannelShell chShell = null;
    private static Expect4j expect = null;

    private StringBuffer buffer = new StringBuffer();
    public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;

    public static String[] linuxPromptRegEx = new String[]{"~]#", "~#", "#", ":~#", "/$", ">"};     // 正则匹配，用于处理服务器返回的结果
    public static String[] errorMsg = new String[]{"could not acquire the config lock "};

    public ChShell(ChannelFactory channelFactory) throws Exception {
        chShell = (ChannelShell) channelFactory.acquireChannel("shell");
        this.expect = new Expect4j(chShell.getInputStream(), chShell.getOutputStream());
    }

    public void startChShell() throws Exception {
        if (chShell.isClosed()) chShell.start();
        if (!chShell.isConnected()) chShell.connect();
    }

    public boolean executeCommands(String[] commands, int timeOut) {
        if (expect == null) {       // 如果expect返回为0，说明登入没有成功
            return false;
        }
        log.info("[start to upload file to seafile]");
        Closure closure = new Closure() {
            public void run(ExpectState expectState) throws Exception {
                buffer.append(expectState.getBuffer());     // buffer is string buffer for appending output of executed command
                expectState.exp_continue();
            }
        };
        List<Match> lstPattern = new ArrayList<Match>();
        String[] regEx = linuxPromptRegEx;
        if (regEx != null && regEx.length > 0) {
            synchronized (regEx) {
                for (String regexElement : regEx) {     // list of regx like, :>, /> etc. it is possible command prompts of your remote machine
                    try {
                        RegExpMatch mat = new RegExpMatch(regexElement, closure);
                        lstPattern.add(mat);
                    } catch (MalformedPatternException e) {
                        return false;
                    } catch (Exception e) {
                        return false;
                    }
                }
                lstPattern.add(new EofMatch(new Closure() {         // should cause entire page to be collected
                    public void run(ExpectState state) {
                    }
                }));
                lstPattern.add(new TimeoutMatch(timeOut, new Closure() {
                    public void run(ExpectState state) {
                    }
                }));
            }
        }
        try {
            boolean isSuccess = true;
            for (String strCmd : commands) {
                isSuccess = isSuccess(lstPattern, strCmd);
            }
            isSuccess = !checkResult(expect.expect(lstPattern));            // 防止最后一个命令执行不了
            String response = buffer.toString().toLowerCase();              // 找不到错误信息标示成功
            for (String msg : errorMsg) {
                if (response.indexOf(msg) > -1) {
                    return false;
                }
            }
            return isSuccess;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 检查执行是否成功
    private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
        try {
            boolean isFailed = checkResult(expect.expect(objPattern));
            if (!isFailed) {
                expect.send(strCommandPattern);
                expect.send("\r\n");
                return true;
            }
            return false;
        } catch (MalformedPatternException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    // 检查执行返回的状态
    private boolean checkResult(int intRetVal) {
        if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
            return true;
        }
        return false;
    }

    public void closeChShell() {
        if (expect != null) {
            expect.close();
        }
        if (chShell != null) {
            if (chShell.isConnected()) chShell.disconnect();
            chShell = null;
        }
    }
}