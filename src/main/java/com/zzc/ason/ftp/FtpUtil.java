package com.zzc.ason.ftp;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author songfeng
 * @version 1.0
 * @category com.feng.util
 * @since 2014-12-26
 */
@Slf4j
public class FtpUtil {

    private FTPClient ftp;
    private boolean is_connected;
    private int defaultTimeoutSecond = 30;
    private int connectTimeoutSecond = 3;
    private int dataTimeoutSecond = 30;

    public FtpUtil(String host, int port, String username, String password) {
        initFtp(host, port, username, password, defaultTimeoutSecond, connectTimeoutSecond, dataTimeoutSecond);
    }

    private void initFtp(String host, int port, String username, String password, int defaultTimeoutSecond, int connectTimeoutSecond, int dataTimeoutSecond) {
        ftp = new FTPClient();
        is_connected = false;
        ftp.setControlEncoding("UTF-8");
        ftp.setDefaultTimeout(defaultTimeoutSecond * 1000);
        ftp.setConnectTimeout(connectTimeoutSecond * 1000);
        ftp.setDataTimeout(dataTimeoutSecond * 1000);
        try {
            connect(host, port, username, password, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to FTP server.
     *
     * @param host       FTP server address or name
     * @param port       FTP server port
     * @param username   user name
     * @param password   user password
     * @param isTextMode text / binary mode switch
     * @throws IOException on I/O errors
     */
    private void connect(String host, int port, String username, String password, boolean isTextMode) throws IOException {
        // Connect to server.  
        try {
            ftp.connect(host, port);
        } catch (UnknownHostException ex) {
            throw new IOException("Can't find FTP server '" + host + "'");
        }
        // Check rsponse after connection attempt.  
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            disconnect();
            throw new IOException("Can't connect to server '" + host + "'");
        }
        // Login.  
        if (!ftp.login(username, password)) {
            is_connected = false;
            disconnect();
            throw new IOException("Can't login to server '" + host + "'");
        } else {
            is_connected = true;
        }
        // Set data transfer mode.  
        if (isTextMode) {
            ftp.setFileType(FTP.ASCII_FILE_TYPE);
        } else {
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        }
        log.info("[connected successfully to ftpHost = " + host + ",as ftpUserName = " + username + "]");
    }

    /**
     * Uploads the file to the FTP server.
     *
     * @param ftpFileName server file name (with absolute path)
     * @param localFile   local file to upload
     * @throws IOException on I/O errors
     */
    public void upload(String ftpFileName, File localFile) throws IOException {
        // File check.
        if (!localFile.exists()) {
            throw new IOException("Can't upload '" + localFile.getAbsolutePath() + "'. This file doesn't exist.");
        }
        // Upload.
        ftp.enterLocalPassiveMode();    // Use passive mode to pass firewalls.
        @Cleanup InputStream in = new BufferedInputStream(new FileInputStream(localFile));
        if (!ftp.storeFile(ftpFileName, in)) {
            throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions and path.");
        }
    }

    /**
     * Downloads the file from the FTP server.
     *
     * @param ftpFileName server file name (with absolute path)
     * @param localFile   local file to download into
     * @throws IOException on I/O errors
     */
    public void download(String ftpFileName, File localFile) throws IOException {
        // Use passive mode to pass firewalls.
        ftp.enterLocalPassiveMode();
        // Get file info.
        FTPFile[] fileInfoArray = ftp.listFiles(ftpFileName);
        if (fileInfoArray == null) {
            throw new FileNotFoundException("File " + ftpFileName + " was not found on FTP server.");
        }
        // Check file size.
        FTPFile fileInfo = fileInfoArray[0];
        long size = fileInfo.getSize();
        if (size > Integer.MAX_VALUE) {
            throw new IOException("File " + ftpFileName + " is too large.");
        }
        // Download file.
        @Cleanup OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
        if (!ftp.retrieveFile(ftpFileName, out)) {
            throw new IOException("Error loading file " + ftpFileName + " from FTP server. Check FTP permissions and path.");
        }
        out.flush();
    }

    /**
     * Removes the file from the FTP server.
     *
     * @param ftpFileName server file name (with absolute path)
     * @throws IOException on I/O errors
     */
    public void remove(String ftpFileName) throws IOException {
        if (!ftp.deleteFile(ftpFileName)) {
            throw new IOException("Can't remove file '" + ftpFileName + "' from FTP server.");
        }
    }

    /**
     * Lists the files in the given FTP directory.
     *
     * @param filePath absolute path on the server
     * @return files relative names list
     * @throws IOException on I/O errors
     */
    public List<String> list(String filePath) throws IOException {
        List<String> fileList = new ArrayList<String>();
        // Use passive mode to pass firewalls.  
        ftp.enterLocalPassiveMode();
        FTPFile[] ftpFiles = ftp.listFiles(filePath);
        int size = (ftpFiles == null) ? 0 : ftpFiles.length;
        for (int i = 0; i < size; i++) {
            FTPFile ftpFile = ftpFiles[i];
            if (ftpFile.isFile()) {
                fileList.add(ftpFile.getName());
            }
        }
        return fileList;
    }

    /**
     * Sends an FTP Server site specific command
     *
     * @param args site command arguments
     * @throws IOException on I/O errors
     */
    public void sendSiteCommand(String args) throws IOException {
        if (ftp.isConnected()) {
            try {
                ftp.sendSiteCommand(args);
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Disconnects from the FTP server
     *
     * @throws IOException on I/O errors
     */
    public void disconnect() throws IOException {
        if (ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
                is_connected = false;
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Makes the full name of the file on the FTP server by joining its path and the local
     * file name.
     *
     * @param ftpPath   file path on the server
     * @param localFile local file
     * @return full name of the file on the FTP server
     */
    public String makeFTPFileName(String ftpPath, File localFile) {
        if (ftpPath == "") {
            return localFile.getName();
        } else {
            String path = ftpPath.trim();
            if (path.charAt(path.length() - 1) != '/') {
                path = path + "/";
            }
            return path + localFile.getName();
        }
    }

    /**
     * Test coonection to ftp server
     *
     * @return true, if connected
     */
    public boolean isConnected() {
        return is_connected;
    }

    /**
     * Get current directory on ftp server
     *
     * @return current directory
     */
    public String getWorkingDirectory() {
        if (!is_connected) {
            return "";
        }
        try {
            return ftp.printWorkingDirectory();
        } catch (IOException e) {
        }
        return "";
    }

    /**
     * Set working directory on ftp server
     *
     * @param dir new working directory
     * @return true, if working directory changed
     */
    public boolean setWorkingDirectory(String dir) {
        if (!is_connected) {
            return false;
        }
        try {
            return ftp.changeWorkingDirectory(dir);
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * Change working directory on ftp server to parent directory
     *
     * @return true, if working directory changed
     */
    public boolean setParentDirectory() {
        if (!is_connected) {
            return false;
        }
        try {
            return ftp.changeToParentDirectory();
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * Get parent directory name on ftp server
     *
     * @return parent directory
     */
    public String getParentDirectory() {
        if (!is_connected) {
            return "";
        }
        String w = getWorkingDirectory();
        setParentDirectory();
        String p = getWorkingDirectory();
        setWorkingDirectory(w);
        return p;
    }

    /**
     * Get file from ftp server into given output stream
     *
     * @param ftpFileName file name on ftp server
     * @param out         OutputStream
     * @throws IOException
     */
    public void getFile(String ftpFileName, OutputStream out) throws IOException {
        try {
            // Use passive mode to pass firewalls.  
            ftp.enterLocalPassiveMode();
            // Get file info.  
            FTPFile[] fileInfoArray = ftp.listFiles(ftpFileName);
            if (fileInfoArray == null) {
                throw new FileNotFoundException("File '" + ftpFileName + "' was not found on FTP server.");
            }
            // Check file size.  
            FTPFile fileInfo = fileInfoArray[0];
            long size = fileInfo.getSize();
            if (size > Integer.MAX_VALUE) {
                throw new IOException("File '" + ftpFileName + "' is too large.");
            }
            // Download file.  
            if (!ftp.retrieveFile(ftpFileName, out)) {
                throw new IOException("Error loading file '" + ftpFileName + "' from FTP server. Check FTP permissions and path.");
            }
            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * Put file on ftp server from given input stream
     *
     * @param ftpFileName file name on ftp server
     * @param in          InputStream
     * @throws IOException
     */
    public void putFile(String ftpFileName, InputStream in) throws IOException {
        try {
            // Use passive mode to pass firewalls.  
            ftp.enterLocalPassiveMode();
            if (!ftp.storeFile(ftpFileName, in)) {
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions and path.");
            }
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
        }
    }

    public void getFileInfo(String filePath) {
        InputStream inputStream = null;
        BufferedReader in = null;
        try {
            ftp.enterLocalPassiveMode();
            inputStream = ftp.retrieveFileStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            in = new BufferedReader(inputStreamReader);
            String fileContent = "";
            String str = null;
            while ((str = in.readLine()) != null) {
                fileContent += str;
                fileContent += "\n";
            }
            System.out.println(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}